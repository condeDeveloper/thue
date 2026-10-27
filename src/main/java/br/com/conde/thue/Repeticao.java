package br.com.conde.thue;

/**
 * O juiz. Procura repeticoes dentro de uma palavra do jeito mais burro
 * possivel: todo pedaco contra todo pedaco.
 *
 * <p>Um <b>quadrado</b> e um pedaco da forma {@code xx}, como o {@code anan} de
 * "banana". Um <b>cubo</b> e {@code xxx}. Uma <b>sobreposicao</b> e um pedaco
 * que se repete e ainda emenda mais uma letra, como {@code axaxa}: o
 * {@code alfalfa} tem uma, com x = {@code lf}.
 *
 * <p>Nada aqui e esperto. A busca tem uma versao rapida que so olha o fim da
 * palavra, e o teste confronta as duas em tudo que a busca produz.
 */
public final class Repeticao {

    private Repeticao() {
    }

    /** Onde comeca o primeiro quadrado e qual o tamanho do pedaco repetido. */
    public static int[] achaPotencia(Palavra palavra, int expoente) {
        int n = palavra.tamanho();
        for (int inicio = 0; inicio < n; inicio++) {
            for (int periodo = 1; inicio + expoente * periodo <= n; periodo++) {
                if (ehPotencia(palavra, inicio, periodo, expoente)) {
                    return new int[] {inicio, periodo};
                }
            }
        }
        return null;
    }

    private static boolean ehPotencia(Palavra palavra, int inicio, int periodo, int expoente) {
        for (int i = periodo; i < expoente * periodo; i++) {
            if (palavra.em(inicio + i) != palavra.em(inicio + i - periodo)) {
                return false;
            }
        }
        return true;
    }

    public static int[] achaQuadrado(Palavra palavra) {
        return achaPotencia(palavra, 2);
    }

    public static int[] achaCubo(Palavra palavra) {
        return achaPotencia(palavra, 3);
    }

    public static boolean livreDeQuadrados(Palavra palavra) {
        return achaQuadrado(palavra) == null;
    }

    public static boolean livreDeCubos(Palavra palavra) {
        return achaCubo(palavra) == null;
    }

    /**
     * Uma sobreposicao: um pedaco de periodo p com pelo menos 2p+1 letras.
     * Devolve onde comeca e qual o periodo.
     */
    public static int[] achaSobreposicao(Palavra palavra) {
        int n = palavra.tamanho();
        for (int inicio = 0; inicio < n; inicio++) {
            for (int periodo = 1; inicio + 2 * periodo + 1 <= n; periodo++) {
                boolean bate = true;
                for (int i = periodo; i < 2 * periodo + 1 && bate; i++) {
                    bate = palavra.em(inicio + i) == palavra.em(inicio + i - periodo);
                }
                if (bate) {
                    return new int[] {inicio, periodo};
                }
            }
        }
        return null;
    }

    public static boolean livreDeSobreposicoes(Palavra palavra) {
        return achaSobreposicao(palavra) == null;
    }

    /**
     * O atalho da busca: supondo que a palavra sem a ultima letra ja esta
     * limpa, basta olhar as potencias que terminam na ultima letra.
     */
    public static boolean oFimFechaPotencia(int[] letras, int tamanho, int expoente) {
        for (int periodo = 1; expoente * periodo <= tamanho; periodo++) {
            int inicio = tamanho - expoente * periodo;
            boolean bate = true;
            for (int i = inicio + periodo; i < tamanho && bate; i++) {
                bate = letras[i] == letras[i - periodo];
            }
            if (bate) {
                return true;
            }
        }
        return false;
    }
}
