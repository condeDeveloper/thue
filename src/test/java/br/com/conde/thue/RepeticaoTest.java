package br.com.conde.thue;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.Test;

class RepeticaoTest {

    @Test
    void bananaTemQuadrado() {
        // "banana": o "anan" comeca na letra 1 e o pedaco repetido e "an".
        int[] onde = Repeticao.achaQuadrado(Palavra.de("banana"));
        assertThat(onde).containsExactly(1, 2);
    }

    @Test
    void alfalfaTemSobreposicao() {
        // "alfalfa": periodo 3, sete letras, que e 2x3+1.
        int[] onde = Repeticao.achaSobreposicao(Palavra.de("alfalfa"));
        assertThat(onde).containsExactly(0, 3);
        assertThat(Repeticao.achaCubo(Palavra.de("alfalfa"))).isNull();
    }

    @Test
    void aLetraDobradaJaEUmQuadrado() {
        assertThat(Repeticao.achaQuadrado(Palavra.de("aa"))).containsExactly(0, 1);
        assertThat(Repeticao.achaQuadrado(Palavra.de("abb"))).containsExactly(1, 1);
    }

    @Test
    void aPalavraVaziaEAUnicaLetraEstaoLimpas() {
        assertThat(Repeticao.livreDeQuadrados(Palavra.vazia())).isTrue();
        assertThat(Repeticao.livreDeQuadrados(Palavra.de("a"))).isTrue();
        assertThat(Repeticao.livreDeSobreposicoes(Palavra.de("ab"))).isTrue();
    }

    @Test
    void oCuboPrecisaDeTresCopias() {
        assertThat(Repeticao.achaCubo(Palavra.de("abab"))).isNull();
        assertThat(Repeticao.achaCubo(Palavra.de("ababab"))).containsExactly(0, 2);
        assertThat(Repeticao.achaCubo(Palavra.de("aaa"))).containsExactly(0, 1);
    }

    @Test
    void quemTemCuboTemQuadradoESobreposicao() {
        Palavra cubo = Palavra.de("abcabcabc");
        assertThat(Repeticao.achaCubo(cubo)).isNotNull();
        assertThat(Repeticao.achaQuadrado(cubo)).isNotNull();
        assertThat(Repeticao.achaSobreposicao(cubo)).isNotNull();
    }

    @Test
    void quemTemSobreposicaoTemQuadrado() {
        new Busca(3, 2).todas(10, palavra ->
                assertThat(Repeticao.achaSobreposicao(palavra))
                        .as("%s e livre de quadrados, nao pode ter sobreposicao", palavra)
                        .isNull());
    }

    @Test
    void oAtalhoDoFimConcordaComOJuizBurro() {
        // Em toda palavra de ate 9 letras sobre 3, com o comeco ja limpo, o
        // atalho da a mesma resposta que procurar quadrado em tudo.
        for (int expoente : new int[] {2, 3}) {
            new Busca(3, expoente).todas(9, palavra -> {
                int[] letras = palavra.comoVetor();
                for (int tamanho = 1; tamanho <= letras.length; tamanho++) {
                    boolean peloAtalho = Repeticao.oFimFechaPotencia(letras, tamanho, expoente);
                    boolean peloJuiz =
                            Repeticao.achaPotencia(palavra.pedaco(0, tamanho), expoente) != null;
                    assertThat(peloAtalho).as("%s ate %d", palavra, tamanho).isEqualTo(peloJuiz);
                }
            });
        }
    }

    @Test
    void oAtalhoPegaAPotenciaQueSoFechaNaUltimaLetra() {
        int[] letras = Palavra.de("abcab").comoVetor();
        assertThat(Repeticao.oFimFechaPotencia(letras, 4, 2)).isFalse();
        int[] comQuadrado = Palavra.de("abab").comoVetor();
        assertThat(Repeticao.oFimFechaPotencia(comQuadrado, 4, 2)).isTrue();
    }

    @Test
    void aPalavraRecusaLetraForaDoAlfabeto() {
        assertThatThrownBy(() -> Palavra.de("aA")).isInstanceOf(IllegalArgumentException.class);
        assertThat(Palavra.de("abcab").letrasUsadas()).isEqualTo(3);
        assertThat(Palavra.de("abcab").pedaco(1, 4).toString()).isEqualTo("bca");
        assertThat(Palavra.vazia().mais(0).toString()).isEqualTo("a");
    }
}
