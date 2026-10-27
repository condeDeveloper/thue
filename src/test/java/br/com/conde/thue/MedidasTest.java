package br.com.conde.thue;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.within;

import java.util.HashSet;
import java.util.Set;
import org.junit.jupiter.api.Test;

/**
 * Os numeros que estao escritos no README, presos aqui.
 *
 * <p>Se uma medida mudar, este arquivo quebra antes de o texto ficar mentindo.
 */
class MedidasTest {

    @Test
    void asTresColunasDaTabelaDeContagem() {
        long[] duasSemQuadrado = {0, 2, 2, 2, 0, 0, 0};
        for (int n = 1; n <= 6; n++) {
            assertThat(new Busca(2, 2).conta(n)).as("n=%d", n).isEqualTo(duasSemQuadrado[n]);
        }

        long[] tresSemQuadrado = {0, 3, 6, 12, 18, 30, 42, 60, 78, 108, 144, 204, 264,
            342, 456, 618, 798, 1_044, 1_392, 1_830, 2_388, 3_180, 4_146, 5_418, 7_032};
        for (int n = 1; n <= 24; n++) {
            assertThat(new Busca(3, 2).conta(n)).as("n=%d", n).isEqualTo(tresSemQuadrado[n]);
        }

        long[] duasSemCubo = {0, 2, 4, 6, 10, 16, 24, 36, 56, 80, 118, 174, 254,
            378, 554, 802, 1_168, 1_716, 2_502, 3_650, 5_324, 7_754, 11_320, 16_502, 24_054};
        for (int n = 1; n <= 24; n++) {
            assertThat(new Busca(2, 3).conta(n)).as("n=%d", n).isEqualTo(duasSemCubo[n]);
        }
    }

    @Test
    void asDuasPalavrasDeCadaTamanhoComDuasLetras() {
        Set<String> vistas = new HashSet<>();
        new Busca(2, 2).todas(3, palavra -> vistas.add(palavra.toString()));
        assertThat(vistas).containsExactlyInAnyOrder("aba", "bab");
        assertThat(new Busca(2, 2).maiorTamanho(10)).isEqualTo(3);
    }

    @Test
    void aTabelaDoCrescimento() {
        int[] tamanhos = {10, 20, 24, 30, 35, 40, 45, 50};
        long[] palavras = {144, 2_388, 7_032, 34_422, 129_516, 484_446, 1_812_876, 6_778_926};
        long[] nos = {1_074, 22_350, 67_746, 338_502, 1_277_886, 4_804_482, 17_998_626, 67_356_966};
        double[] raizes = {1.64375, 1.47537, 1.44642, 1.41654, 1.39980, 1.38717, 1.37745, 1.36969};
        Busca busca = new Busca(3, 2);
        for (int i = 0; i < tamanhos.length; i++) {
            long[] quantas = {0};
            long abertos = busca.todas(tamanhos[i], palavra -> quantas[0]++);
            assertThat(quantas[0]).as("n=%d", tamanhos[i]).isEqualTo(palavras[i]);
            assertThat(abertos).as("n=%d nos", tamanhos[i]).isEqualTo(nos[i]);
            assertThat(Math.pow(quantas[0], 1.0 / tamanhos[i]))
                    .as("n=%d raiz", tamanhos[i])
                    .isCloseTo(raizes[i], within(5e-6));
        }
    }

    @Test
    void aRaizAindaEstaCincoPorCentoAcimaDepoisDeCinquentaLetras() {
        double raiz = Math.pow(new Busca(3, 2).conta(50), 1.0 / 50);
        assertThat(raiz - Medidor.CRESCIMENTO_CONHECIDO).isCloseTo(0.06793, within(5e-6));
        assertThat(raiz / Medidor.CRESCIMENTO_CONHECIDO - 1).isBetween(0.05, 0.06);
    }

    @Test
    void aPodaEscreveDezLetrasPorPalavraEmQualquerTamanho() {
        Busca busca = new Busca(3, 2);
        for (int n : new int[] {30, 40, 50}) {
            long[] quantas = {0};
            long nos = busca.todas(n, palavra -> quantas[0]++);
            assertThat((double) nos / quantas[0]).as("n=%d", n).isBetween(9.8, 10.0);
        }
    }

    @Test
    void aFracaoDasPalavrasQueEscapaDespenca() {
        assertThat((double) new Busca(3, 2).conta(13) / Math.pow(3, 13))
                .isCloseTo(0.000215, within(5e-7));
        assertThat(new Busca(3, 2).conta(13)).isEqualTo(342);
        assertThat((long) Math.pow(3, 13)).isEqualTo(1_594_323);
    }

    @Test
    void oMorfismoDeThueAlcancaQuarentaDasDuzentasESessentaEQuatro() {
        Palavra longa = Morfismo.DE_THUE.iterado(9);
        assertThat(longa.tamanho()).isEqualTo(768);
        Set<Palavra> pedacos = new HashSet<>();
        for (int i = 0; i + 12 <= longa.tamanho(); i++) {
            pedacos.add(longa.pedaco(i, i + 12));
        }
        assertThat(pedacos).hasSize(40);
        assertThat(new Busca(3, 2).conta(12)).isEqualTo(264);
        assertThat(100.0 * pedacos.size() / 264).isCloseTo(15.2, within(0.05));
    }

    @Test
    void osTamanhosDaSubstituicaoDeThue() {
        long[] esperado = {1, 3, 6, 12, 24, 48, 96, 192, 384, 768};
        for (int vezes = 0; vezes <= 9; vezes++) {
            assertThat(Morfismo.DE_THUE.iterado(vezes).tamanho())
                    .as("%d vezes", vezes)
                    .isEqualTo((int) esperado[vezes]);
        }
    }

    @Test
    void aRazaoDasLivresDeCuboComDuasLetras() {
        Busca busca = new Busca(2, 3);
        assertThat((double) busca.conta(24) / busca.conta(23)).isCloseTo(1.4576, within(5e-5));
        assertThat(busca.conta(24)).isEqualTo(24_054);
    }

    @Test
    void asPrimeirasTrintaEDuasLetrasDeThueMorse() {
        assertThat(Morfismo.thueMorseAte(32).toString())
                .isEqualTo("abbabaabbaababbabaababbaabbabaab");
    }
}
