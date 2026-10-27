package br.com.conde.thue;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

class MorfismoTest {

    @Test
    void aSubstituicaoDeThueDaAsPalavrasEsperadas() {
        assertThat(Morfismo.DE_THUE.iterado(1).toString()).isEqualTo("abc");
        assertThat(Morfismo.DE_THUE.iterado(2).toString()).isEqualTo("abcacb");
        assertThat(Morfismo.DE_THUE.iterado(3).toString()).isEqualTo("abcacbabcbac");
        assertThat(Morfismo.DE_THUE.iterado(4).toString())
                .isEqualTo("abcacbabcbacabcacbacabcb");
    }

    @Test
    void aSubstituicaoDeThueNuncaFechaQuadrado() {
        for (int vezes = 1; vezes <= 10; vezes++) {
            Palavra palavra = Morfismo.DE_THUE.iterado(vezes);
            assertThat(Repeticao.livreDeQuadrados(palavra))
                    .as("%d vezes, %d letras", vezes, palavra.tamanho())
                    .isTrue();
        }
    }

    @Test
    void oTamanhoPrevistoBateComOGerado() {
        for (int vezes = 0; vezes <= 12; vezes++) {
            assertThat(Morfismo.DE_THUE.tamanhoDepoisDe(vezes))
                    .as("%d vezes", vezes)
                    .isEqualTo(Morfismo.DE_THUE.iterado(vezes).tamanho());
        }
        assertThat(Morfismo.DE_THUE_MORSE.tamanhoDepoisDe(10)).isEqualTo(1_024);
    }

    @Test
    void cadaPalavraDaSubstituicaoComecaComAAnterior() {
        for (int vezes = 1; vezes <= 9; vezes++) {
            Palavra antes = Morfismo.DE_THUE.iterado(vezes - 1);
            Palavra agora = Morfismo.DE_THUE.iterado(vezes);
            assertThat(agora.pedaco(0, antes.tamanho())).isEqualTo(antes);
        }
    }

    @Test
    void thueMorsePelaSubstituicaoEPelaContagemDeBitsSaoAMesmaCoisa() {
        for (int vezes = 1; vezes <= 12; vezes++) {
            Palavra gerada = Morfismo.DE_THUE_MORSE.iterado(vezes);
            assertThat(gerada).isEqualTo(Morfismo.thueMorseAte(gerada.tamanho()));
        }
    }

    @Test
    void thueMorseTemQuadradoMasNaoTemCuboNemSobreposicao() {
        for (int tamanho : new int[] {4, 16, 64, 256, 1_024}) {
            Palavra palavra = Morfismo.thueMorseAte(tamanho);
            assertThat(Repeticao.achaQuadrado(palavra)).as("%d letras", tamanho).isNotNull();
            assertThat(Repeticao.achaCubo(palavra)).as("%d letras", tamanho).isNull();
            assertThat(Repeticao.achaSobreposicao(palavra)).as("%d letras", tamanho).isNull();
        }
    }

    @Test
    void oPrimeiroQuadradoDeThueMorseEALetraDobrada() {
        assertThat(Repeticao.achaQuadrado(Morfismo.thueMorseAte(32))).containsExactly(1, 1);
        assertThat(Morfismo.thueMorseAte(8).toString()).isEqualTo("abbabaab");
    }

    @Test
    void aContagemDeBitsDaOsPrimeirosTermos() {
        int[] esperado = {0, 1, 1, 0, 1, 0, 0, 1};
        for (int i = 0; i < esperado.length; i++) {
            assertThat(Morfismo.thueMorse(i)).as("termo %d", i).isEqualTo(esperado[i]);
        }
    }

    @Test
    void todoPedacoDaPalavraDeThueEUmaPalavraLivreDeQuadrados() {
        Palavra longa = Morfismo.DE_THUE.iterado(7);
        for (int i = 0; i + 10 <= longa.tamanho(); i += 7) {
            assertThat(Repeticao.livreDeQuadrados(longa.pedaco(i, i + 10)))
                    .as("pedaco em %d", i)
                    .isTrue();
        }
    }
}
