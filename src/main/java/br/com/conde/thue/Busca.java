package br.com.conde.thue;

import java.util.function.Consumer;

/**
 * Monta as palavras letra por letra, voltando atras assim que o fim fecha uma
 * potencia proibida.
 *
 * <p>Conta tambem quantos nos abriu, que e quantas letras chegou a escrever,
 * inclusive as que teve de apagar. E esse numero, comparado com {@code k^n},
 * que mostra o tamanho do que a poda jogou fora.
 */
public final class Busca {

    private final int letras;
    private final int expoente;

    public Busca(int letras, int expoente) {
        if (letras < 1 || expoente < 2) {
            throw new IllegalArgumentException("alfabeto ou expoente fora da faixa");
        }
        this.letras = letras;
        this.expoente = expoente;
    }

    /** Visita toda palavra de tamanho n sem potencia de expoente dado. */
    public long todas(int tamanho, Consumer<Palavra> achou) {
        int[] atual = new int[tamanho];
        long[] nos = {0};
        estende(atual, 0, tamanho, nos, achou);
        return nos[0];
    }

    private void estende(int[] atual, int escritas, int tamanho, long[] nos, Consumer<Palavra> achou) {
        if (escritas == tamanho) {
            achou.accept(new Palavra(atual.length == tamanho ? atual : java.util.Arrays.copyOf(atual, tamanho)));
            return;
        }
        for (int letra = 0; letra < letras; letra++) {
            atual[escritas] = letra;
            nos[0]++;
            if (!Repeticao.oFimFechaPotencia(atual, escritas + 1, expoente)) {
                estende(atual, escritas + 1, tamanho, nos, achou);
            }
        }
    }

    public long conta(int tamanho) {
        long[] quantas = {0};
        todas(tamanho, palavra -> quantas[0]++);
        return quantas[0];
    }

    /** O maior tamanho que ainda tem alguma palavra, ou -1 se nao acaba nunca. */
    public int maiorTamanho(int limite) {
        for (int tamanho = 1; tamanho <= limite; tamanho++) {
            if (conta(tamanho) == 0) {
                return tamanho - 1;
            }
        }
        return -1;
    }

    /** O mesmo resultado sem poda nenhuma: escreve as k^n palavras e julga cada uma. */
    public long contaPorForcaBruta(int tamanho) {
        long quantas = 0;
        long total = 1;
        for (int i = 0; i < tamanho; i++) {
            total *= letras;
        }
        int[] atual = new int[tamanho];
        for (long codigo = 0; codigo < total; codigo++) {
            long resto = codigo;
            for (int i = tamanho - 1; i >= 0; i--) {
                atual[i] = (int) (resto % letras);
                resto /= letras;
            }
            if (Repeticao.achaPotencia(new Palavra(atual), expoente) == null) {
                quantas++;
            }
        }
        return quantas;
    }

    public int letras() {
        return letras;
    }

    public int expoente() {
        return expoente;
    }
}
