package br.com.conde.thue;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.HashSet;
import java.util.Set;
import org.junit.jupiter.api.Test;

class BuscaTest {

    @Test
    void aPodaEAForcaBrutaNuncaDiscordam() {
        for (int letras = 2; letras <= 3; letras++) {
            for (int expoente = 2; expoente <= 3; expoente++) {
                Busca busca = new Busca(letras, expoente);
                for (int n = 1; n <= 11; n++) {
                    assertThat(busca.conta(n))
                            .as("%d letras, expoente %d, n=%d", letras, expoente, n)
                            .isEqualTo(busca.contaPorForcaBruta(n));
                }
            }
        }
    }

    @Test
    void tudoQueABuscaDevolvePassaPeloJuiz() {
        new Busca(3, 2).todas(12, palavra -> {
            assertThat(palavra.tamanho()).isEqualTo(12);
            assertThat(Repeticao.livreDeQuadrados(palavra)).as("%s", palavra).isTrue();
        });
        new Busca(2, 3).todas(14, palavra ->
                assertThat(Repeticao.livreDeCubos(palavra)).as("%s", palavra).isTrue());
    }

    @Test
    void nenhumaPalavraSaiRepetida() {
        Set<Palavra> vistas = new HashSet<>();
        long[] quantas = {0};
        new Busca(3, 2).todas(10, palavra -> {
            assertThat(vistas.add(palavra)).as("%s", palavra).isTrue();
            quantas[0]++;
        });
        assertThat((long) vistas.size()).isEqualTo(quantas[0]);
    }

    @Test
    void duasLetrasSemQuadradoMorremNoComprimentoTres() {
        Busca busca = new Busca(2, 2);
        assertThat(busca.conta(3)).isEqualTo(2);
        assertThat(busca.conta(4)).isZero();
        assertThat(busca.maiorTamanho(10)).isEqualTo(3);
    }

    @Test
    void tresLetrasSemQuadradoNaoMorrem() {
        Busca busca = new Busca(3, 2);
        for (int n = 1; n <= 30; n++) {
            assertThat(busca.conta(n)).as("n=%d", n).isPositive();
        }
        assertThat(busca.maiorTamanho(30)).isEqualTo(-1);
    }

    @Test
    void duasLetrasSemCuboTambemNaoMorrem() {
        Busca busca = new Busca(2, 3);
        for (int n = 1; n <= 24; n++) {
            assertThat(busca.conta(n)).as("n=%d", n).isPositive();
        }
    }

    @Test
    void aContagemNuncaEncolheMaisDoQueODobro() {
        // Toda palavra limpa de n letras vem de uma de n-1 letras cortando o fim.
        Busca busca = new Busca(3, 2);
        long antes = busca.conta(4);
        for (int n = 5; n <= 20; n++) {
            long agora = busca.conta(n);
            assertThat(agora).as("n=%d", n).isLessThanOrEqualTo(antes * 2);
            antes = agora;
        }
    }

    @Test
    void cortarOFimDeUmaPalavraLimpaDeixaOutraLimpa() {
        new Busca(3, 2).todas(11, palavra -> {
            for (int corte = 1; corte <= 11; corte++) {
                assertThat(Repeticao.livreDeQuadrados(palavra.pedaco(0, corte)))
                        .as("%s cortada em %d", palavra, corte)
                        .isTrue();
            }
        });
    }

    @Test
    void aBuscaRecusaAlfabetoOuExpoenteImpossivel() {
        assertThatThrownBy(() -> new Busca(0, 2)).isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(() -> new Busca(3, 1)).isInstanceOf(IllegalArgumentException.class);
        assertThat(new Busca(3, 2).letras()).isEqualTo(3);
        assertThat(new Busca(3, 2).expoente()).isEqualTo(2);
    }

    @Test
    void comUmaLetraSoNadaEscapa() {
        Busca busca = new Busca(1, 2);
        assertThat(busca.conta(1)).isEqualTo(1);
        assertThat(busca.conta(2)).isZero();
    }
}
