package br.com.conde.thue;

import java.util.Arrays;

/**
 * Uma substituicao de letras por palavras, aplicada muitas vezes.
 *
 * <p>Thue mostrou que {@code a -> abc, b -> ac, c -> b} leva de {@code a} a
 * palavras livres de quadrados de qualquer tamanho. A substituicao de
 * Thue-Morse, {@code 0 -> 01, 1 -> 10}, nao escapa dos quadrados, mas escapa
 * das sobreposicoes.
 *
 * <p>Aqui nao ha prova nenhuma: o {@link Repeticao} julga cada palavra gerada.
 */
public final class Morfismo {

    /** a -> abc, b -> ac, c -> b. */
    public static final Morfismo DE_THUE = new Morfismo(new int[][] {
        {0, 1, 2},
        {0, 2},
        {1},
    });

    /** 0 -> 01, 1 -> 10. */
    public static final Morfismo DE_THUE_MORSE = new Morfismo(new int[][] {
        {0, 1},
        {1, 0},
    });

    private final int[][] imagens;

    public Morfismo(int[][] imagens) {
        this.imagens = new int[imagens.length][];
        for (int i = 0; i < imagens.length; i++) {
            this.imagens[i] = imagens[i].clone();
        }
    }

    public Palavra aplica(Palavra palavra) {
        int tamanho = 0;
        for (int i = 0; i < palavra.tamanho(); i++) {
            tamanho += imagens[palavra.em(i)].length;
        }
        int[] saida = new int[tamanho];
        int onde = 0;
        for (int i = 0; i < palavra.tamanho(); i++) {
            int[] imagem = imagens[palavra.em(i)];
            System.arraycopy(imagem, 0, saida, onde, imagem.length);
            onde += imagem.length;
        }
        return new Palavra(saida);
    }

    /** Aplica a substituicao {@code vezes} vezes a partir da primeira letra. */
    public Palavra iterado(int vezes) {
        Palavra palavra = Palavra.de("a");
        for (int i = 0; i < vezes; i++) {
            palavra = aplica(palavra);
        }
        return palavra;
    }

    /** O tamanho que a palavra tera depois de tantas aplicacoes, sem gerar nada. */
    public long tamanhoDepoisDe(int vezes) {
        long[] contagem = new long[imagens.length];
        contagem[0] = 1;
        for (int i = 0; i < vezes; i++) {
            long[] proxima = new long[imagens.length];
            for (int letra = 0; letra < imagens.length; letra++) {
                for (int saida : imagens[letra]) {
                    proxima[saida] += contagem[letra];
                }
            }
            contagem = proxima;
        }
        return Arrays.stream(contagem).sum();
    }

    /** A letra de indice dado da sequencia de Thue-Morse, pela paridade dos bits. */
    public static int thueMorse(int indice) {
        return Integer.bitCount(indice) % 2;
    }

    /** Os primeiros tantos termos da sequencia de Thue-Morse. */
    public static Palavra thueMorseAte(int tamanho) {
        int[] letras = new int[tamanho];
        for (int i = 0; i < tamanho; i++) {
            letras[i] = thueMorse(i);
        }
        return new Palavra(letras);
    }
}
