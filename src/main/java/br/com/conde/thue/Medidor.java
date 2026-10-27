package br.com.conde.thue;

import java.util.List;
import java.util.Locale;

/** As tabelas do README. Cada uma roda sozinha: {@code exec:java -Dexec.args="nome"}. */
public final class Medidor {

    /** O que se sabe sobre o crescimento das livres de quadrados em tres letras. */
    static final double CRESCIMENTO_CONHECIDO = 1.30176;

    private static final List<String> TABELAS =
            List.of("contagem", "crescimento", "poda", "morfismo", "morse");

    public static void main(String[] argumentos) {
        String tabela = argumentos.length > 0 ? argumentos[0] : "todas";
        if (tabela.equals("todas")) {
            for (String nome : TABELAS) {
                roda(nome);
                System.out.println();
            }
            return;
        }
        roda(tabela);
    }

    private static void roda(String tabela) {
        switch (tabela) {
            case "contagem" -> contagem();
            case "crescimento" -> crescimento();
            case "poda" -> poda();
            case "morfismo" -> morfismo();
            case "morse" -> morse();
            default -> {
                System.out.println("tabela desconhecida: " + tabela);
                System.out.println("tabelas: " + String.join(", ", TABELAS));
            }
        }
    }

    // ---------------------------------------------------------------- tabelas

    private static void contagem() {
        titulo("Quantas palavras escapam da repeticao");
        System.out.println("n   2 letras sem quadrado  3 letras sem quadrado  2 letras sem cubo");
        Busca duasQuadrado = new Busca(2, 2);
        Busca tresQuadrado = new Busca(3, 2);
        Busca duasCubo = new Busca(2, 3);
        for (int n = 1; n <= 24; n++) {
            System.out.printf(Locale.ROOT, "%-3d %-22s %-22s %s%n",
                    n, comPontos(duasQuadrado.conta(n)),
                    comPontos(tresQuadrado.conta(n)), comPontos(duasCubo.conta(n)));
        }
        System.out.println();
        System.out.printf(Locale.ROOT,
                "Com duas letras e proibindo quadrado, a maior palavra tem %d letras.%n",
                duasQuadrado.maiorTamanho(10));
        System.out.println("Sao estas:");
        for (int n = 1; n <= 3; n++) {
            StringBuilder linha = new StringBuilder("  ");
            duasQuadrado.todas(n, palavra -> linha.append(palavra).append(' '));
            System.out.println(linha);
        }
    }

    private static void crescimento() {
        titulo("O crescimento das livres de quadrados em tres letras");
        System.out.println("n    palavras      nos escritos   letras por palavra  raiz n-esima  falta");
        Busca busca = new Busca(3, 2);
        for (int n : new int[] {10, 20, 24, 30, 35, 40, 45, 50}) {
            long[] quantas = {0};
            long nos = busca.todas(n, palavra -> quantas[0]++);
            double raiz = Math.pow(quantas[0], 1.0 / n);
            System.out.printf(Locale.ROOT, "%-4d %-13s %-14s %-19.2f %-13.5f %+.5f%n",
                    n, comPontos(quantas[0]), comPontos(nos), (double) nos / quantas[0],
                    raiz, raiz - CRESCIMENTO_CONHECIDO);
        }
        System.out.println();
        System.out.printf(Locale.ROOT,
                "O valor para onde a raiz caminha e %.5f, conhecido com muito mais trabalho%n",
                CRESCIMENTO_CONHECIDO);
        System.out.println("do que uma varredura. Em 50 letras ela ainda esta 5% acima.");
    }

    private static void poda() {
        titulo("O que a poda joga fora");
        System.out.println("n    com poda  por forca bruta  3^n         nos escritos  fracao de 3^n");
        Busca busca = new Busca(3, 2);
        for (int n = 1; n <= 13; n++) {
            long comPoda = busca.conta(n);
            long bruta = busca.contaPorForcaBruta(n);
            if (comPoda != bruta) {
                throw new IllegalStateException("a poda e a forca bruta discordaram em n=" + n);
            }
            long tresElevado = (long) Math.pow(3, n);
            long[] ignora = {0};
            long nos = busca.todas(n, palavra -> ignora[0]++);
            System.out.printf(Locale.ROOT, "%-4d %-9s %-16s %-11s %-13s %.6f%n",
                    n, comPontos(comPoda), comPontos(bruta), comPontos(tresElevado),
                    comPontos(nos), (double) comPoda / tresElevado);
        }
        System.out.println();
        System.out.println("A poda e a forca bruta nunca discordam: a tabela quebra se discordarem.");
    }

    private static void morfismo() {
        titulo("A substituicao de Thue: a -> abc, b -> ac, c -> b");
        System.out.println("vezes  tamanho  livre de quadrados  palavra");
        for (int vezes = 1; vezes <= 9; vezes++) {
            Palavra palavra = Morfismo.DE_THUE.iterado(vezes);
            if (palavra.tamanho() != Morfismo.DE_THUE.tamanhoDepoisDe(vezes)) {
                throw new IllegalStateException("o tamanho previsto nao bate com o gerado");
            }
            System.out.printf(Locale.ROOT, "%-6d %-8s %-19s %s%n",
                    vezes, comPontos(palavra.tamanho()),
                    Repeticao.livreDeQuadrados(palavra) ? "sim" : "NAO",
                    palavra.tamanho() <= 24 ? palavra.toString() : "");
        }
        System.out.println();
        System.out.println("Cada palavra dessas foi julgada pelo juiz burro, que procura todo");
        System.out.println("pedaco repetido contra todo pedaco. Nenhuma prova foi usada.");
        System.out.println();
        System.out.println("Quantas das livres de quadrados de 12 letras o morfismo alcanca:");
        Palavra longa = Morfismo.DE_THUE.iterado(9);
        java.util.Set<Palavra> pedacos = new java.util.HashSet<>();
        for (int i = 0; i + 12 <= longa.tamanho(); i++) {
            pedacos.add(longa.pedaco(i, i + 12));
        }
        long todas = new Busca(3, 2).conta(12);
        System.out.printf(Locale.ROOT, "  %s pedacos diferentes, de %s palavras possiveis (%.1f%%)%n",
                comPontos(pedacos.size()), comPontos(todas), 100.0 * pedacos.size() / todas);
    }

    private static void morse() {
        titulo("A sequencia de Thue-Morse, com duas letras");
        System.out.println("primeiras 32 letras: " + Morfismo.thueMorseAte(32));
        System.out.println();
        System.out.println("tamanho  tem quadrado  tem cubo  tem sobreposicao");
        for (int tamanho : new int[] {4, 16, 64, 256, 1024, 4096}) {
            Palavra palavra = Morfismo.thueMorseAte(tamanho);
            System.out.printf(Locale.ROOT, "%-8s %-13s %-9s %s%n",
                    comPontos(tamanho),
                    Repeticao.achaQuadrado(palavra) != null ? "sim" : "nao",
                    Repeticao.achaCubo(palavra) != null ? "sim" : "nao",
                    Repeticao.achaSobreposicao(palavra) != null ? "sim" : "nao");
        }
        int[] onde = Repeticao.achaQuadrado(Morfismo.thueMorseAte(32));
        System.out.printf(Locale.ROOT,
                "%nO primeiro quadrado comeca na letra %d e o pedaco repetido tem %d letra.%n",
                onde[0], onde[1]);
        System.out.println("Duas letras nao escapam do quadrado, nem com a sequencia mais famosa");
        System.out.println("que existe. Escapam do cubo, e escapam da sobreposicao.");
        System.out.println();
        System.out.println("E a substituicao que gera Thue-Morse, 0 -> 01, 1 -> 10:");
        Palavra gerada = Morfismo.DE_THUE_MORSE.iterado(12);
        Palavra pelaParidade = Morfismo.thueMorseAte(gerada.tamanho());
        System.out.printf(Locale.ROOT, "  %s letras, igual a contagem de bits: %s%n",
                comPontos(gerada.tamanho()), gerada.equals(pelaParidade) ? "sim" : "NAO");
    }

    // ------------------------------------------------------------------ apoio

    private static void titulo(String texto) {
        System.out.println(texto);
        System.out.println("=".repeat(texto.length()));
    }

    /** Agrupamento a mao: String.format com virgula muda com o idioma da maquina. */
    static String comPontos(long valor) {
        String digitos = Long.toString(Math.abs(valor));
        StringBuilder saida = new StringBuilder();
        for (int i = 0; i < digitos.length(); i++) {
            if (i > 0 && (digitos.length() - i) % 3 == 0) {
                saida.append('.');
            }
            saida.append(digitos.charAt(i));
        }
        return (valor < 0 ? "-" : "") + saida;
    }

    private Medidor() {
    }
}
