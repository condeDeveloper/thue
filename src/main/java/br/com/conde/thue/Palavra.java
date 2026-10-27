package br.com.conde.thue;

import java.util.Arrays;

/**
 * Uma palavra sobre um alfabeto de k letras, guardada como numeros de 0 a k-1.
 *
 * <p>As letras aparecem como {@code a}, {@code b}, {@code c} e assim por diante
 * quando a palavra e escrita, mas por dentro sao indices, que e o que a busca
 * e os testes usam.
 */
public record Palavra(int[] letras) {

    public Palavra {
        letras = letras.clone();
    }

    public static Palavra vazia() {
        return new Palavra(new int[0]);
    }

    /** De {@code "abcab"} para os indices 0,1,2,0,1. */
    public static Palavra de(String texto) {
        int[] letras = new int[texto.length()];
        for (int i = 0; i < texto.length(); i++) {
            letras[i] = texto.charAt(i) - 'a';
            if (letras[i] < 0) {
                throw new IllegalArgumentException("letra fora do alfabeto: " + texto.charAt(i));
            }
        }
        return new Palavra(letras);
    }

    public int tamanho() {
        return letras.length;
    }

    public int em(int posicao) {
        return letras[posicao];
    }

    public int[] comoVetor() {
        return letras.clone();
    }

    public Palavra mais(int letra) {
        int[] novas = Arrays.copyOf(letras, letras.length + 1);
        novas[letras.length] = letra;
        return new Palavra(novas);
    }

    public Palavra pedaco(int inicio, int fim) {
        return new Palavra(Arrays.copyOfRange(letras, inicio, fim));
    }

    /** Quantas letras diferentes aparecem. */
    public int letrasUsadas() {
        boolean[] vista = new boolean[128];
        int quantas = 0;
        for (int letra : letras) {
            if (letra < vista.length && !vista[letra]) {
                vista[letra] = true;
                quantas++;
            }
        }
        return quantas;
    }

    @Override
    public boolean equals(Object outro) {
        return outro instanceof Palavra p && Arrays.equals(letras, p.letras);
    }

    @Override
    public int hashCode() {
        return Arrays.hashCode(letras);
    }

    @Override
    public String toString() {
        StringBuilder texto = new StringBuilder();
        for (int letra : letras) {
            texto.append((char) ('a' + letra));
        }
        return texto.toString();
    }
}
