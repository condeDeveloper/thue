# thue

Escreva uma palavra com as letras **a** e **b** sem que nenhum pedaço apareça duas vezes
seguidas. `ab` serve. `aba` serve. Agora tente quatro letras.

Não dá. Não existe nenhuma. Com duas letras, a maior palavra sem repetição tem **três**
letras, e são só duas: `aba` e `bab`.

Com **três** letras, existem palavras de qualquer tamanho. É o teorema de Thue, de 1906, e
foi a primeira coisa que alguém provou sobre palavras infinitas. Este repositório não usa a
prova: varre todas as palavras possíveis, julga cada uma com um verificador burro, e mede
onde está a diferença entre duas letras e três.

```bash
mvn -B -ntp verify                               # 39 testes
mvn -B -ntp exec:java -Dexec.args="contagem"     # quantas escapam, até 24 letras
mvn -B -ntp exec:java -Dexec.args="crescimento"  # até 50 letras, e a raiz n-ésima
mvn -B -ntp exec:java -Dexec.args="poda"         # o que a poda joga fora
mvn -B -ntp exec:java -Dexec.args="morfismo"     # a substituição que Thue inventou
mvn -B -ntp exec:java -Dexec.args="morse"        # a sequência de Thue-Morse
```

Java 21, sem dependência fora de JUnit e AssertJ.

## O que conta como repetição

- **quadrado**: um pedaço `xx`. O `anan` de *banana*.
- **cubo**: um pedaço `xxx`.
- **sobreposição**: um pedaço que se repete e ainda emenda mais uma letra, `axaxa`. O
  *alfalfa* tem uma, com `x = lf`, e não tem cubo nenhum.

O `Repeticao` procura as três do jeito mais burro possível: todo pedaço contra todo pedaço.
A busca usa um atalho que só olha o fim da palavra, e um teste confronta os dois em cada
prefixo de cada palavra que a busca produz.

## Quantas escapam

```
n   2 letras sem quadrado  3 letras sem quadrado  2 letras sem cubo
1   2                      3                      2
2   2                      6                      4
3   2                      12                     6
4   0                      18                     10
5   0                      30                     16
6   0                      42                     24
8   0                      78                     56
12  0                      264                    254
16  0                      798                    1.168
20  0                      2.388                  5.324
24  0                      7.032                  24.054
```

A coluna do meio nunca zera. A da esquerda zera na quarta letra e não volta mais.

E a da direita é o detalhe que me pegou: **duas letras bastam, se a proibição for cubo**.
O problema não é o alfabeto ser pequeno, é a exigência ser forte. Com duas letras
proibindo quadrado o assunto acaba em três letras; com duas letras proibindo cubo, cada
letra a mais multiplica a contagem por 1,4576 na passagem de 23 para 24, e a coluna não dá
sinal de parar.

## O crescimento

```
n    palavras      nós escritos   letras por palavra  raiz n-ésima  falta
10   144           1.074          7.46                1.64375       +0.34199
20   2.388         22.350         9.36                1.47537       +0.17361
24   7.032         67.746         9.63                1.44642       +0.14466
30   34.422        338.502        9.83                1.41654       +0.11478
35   129.516       1.277.886      9.87                1.39980       +0.09804
40   484.446       4.804.482      9.92                1.38717       +0.08541
45   1.812.876     17.998.626     9.93                1.37745       +0.07569
50   6.778.926     67.356.966     9.94                1.36969       +0.06793
```

O número de palavras de n letras cresce como `C^n`, e o C é conhecido: **1,30176**. Olhe a
penúltima coluna. Depois de varrer quase sete milhões de palavras de cinquenta letras, a
raiz n-ésima ainda está em 1,3697, **5% acima** do valor certo, e descendo uns 0,008 por
cada cinco letras a mais. Varrer não chega lá.

A coluna do meio tem a outra surpresa: **o custo por palavra para de crescer**. A busca
escreve 9,94 letras (contando as que depois apaga) por palavra entregue em n = 50, e 9,92
em n = 40. Dobrar o tamanho da palavra não torna cada palavra mais cara de achar.

## O que a poda joga fora

```
n    com poda  por força bruta  3^n         nós escritos  fração de 3^n
4    18        18               81          66            0.222222
6    42        42               729         210           0.057613
8    78        78               6.561       516           0.011888
10   144       144              59.049      1.074         0.002439
13   342       342              1.594.323   2.910         0.000215
```

As duas primeiras colunas são o mesmo número por dois caminhos: um backtracking que corta
assim que o fim fecha um quadrado, e um laço bobo que escreve as 3ⁿ palavras e julga cada
uma. A tabela quebra o programa se elas discordarem.

Com treze letras, **duas em cada dez mil** palavras escapam da repetição.

## A substituição que Thue inventou

Troque cada `a` por `abc`, cada `b` por `ac` e cada `c` por `b`. Comece em `a` e repita.

```
vezes  tamanho  livre de quadrados  palavra
1      3        sim                 abc
2      6        sim                 abcacb
3      12       sim                 abcacbabcbac
4      24       sim                 abcacbabcbacabcacbacabcb
9      768      sim
```

Cada uma dessas passou pelo verificador burro, que procurou todo pedaço repetido contra
todo pedaço. Nenhuma prova foi usada para dizer "sim".

Mas a substituição é uma receita, não um catálogo. Dos 768 caracteres gerados, saem **40**
pedaços diferentes de doze letras, de **264** palavras limpas de doze letras que existem:
**15,2%**. A construção garante que não acaba; ela não chega perto de dar todas.

## A sequência mais famosa não resolve o problema

A sequência de Thue-Morse é `abbabaabbaababbabaababbaabbabaab...`, a paridade do número de
bits 1 de cada índice. Ela aparece em todo lugar, e é fácil confundi-la com a resposta.

```
tamanho  tem quadrado  tem cubo  tem sobreposição
4        sim           não       não
64       sim           não       não
1.024    sim           não       não
4.096    sim           não       não
```

Ela **tem** quadrado, e logo na segunda letra: `bb`. O que ela escapa é de cubo e de
sobreposição, que é bem mais do que parece e bem menos do que o problema pedia. Para fugir
do quadrado é preciso a terceira letra, e não tem jeito.

## O que as medidas me corrigiram

**Eu ia dizer que duas letras são poucas, e o problema não é esse.** Com duas letras e a
proibição de cubo são 24.054 palavras de vinte e quatro letras, e a contagem multiplica por
1,4576 a cada letra nova. O alfabeto de dois não é pequeno demais para nada; é a exigência "nenhum
pedaço duas vezes" que é dura demais para ele.

**Eu achava que varrer até cinquenta letras daria o crescimento com alguma casa decimal.**
Dá 1,3697 contra 1,30176. Não é ruído, é viés: a raiz n-ésima desce por cima, devagar, e
mesmo com sete milhões de palavras ela erra na primeira casa. Para esse número a varredura
não serve, e foi preciso medir até cinquenta para eu aceitar isso.

**Eu esperava que o custo por palavra crescesse com o tamanho.** Em n = 10 a busca escreve
7,46 letras por palavra entregue; em n = 50, 9,94. Entre 40 e 50 o número mal se mexe. A
poda corta tão cedo que quase toda letra escrita faz parte de alguma palavra boa, e isso
não piora quando a palavra fica mais longa.

**E quase apresentei a substituição de Thue como "a resposta".** Ela resolve a pergunta
original: existe palavra limpa de qualquer tamanho. Mas ela só alcança 15,2% das palavras
limpas de doze letras. Uma construção que prova a existência e uma varredura que dá o
conjunto são coisas diferentes, e eu estava prestes a escrever uma pela outra.

## Dentro

| arquivo | o que faz |
|---|---|
| `Palavra` | letras como índices, com pedaço, concatenação e escrita |
| `Repeticao` | o juiz: quadrado, cubo e sobreposição por força bruta, mais o atalho do fim |
| `Busca` | backtracking com poda, e a contagem por força bruta para confrontar |
| `Morfismo` | substituições; a de Thue, a de Thue-Morse e a paridade de bits |
| `Medidor` | as cinco tabelas acima |

39 testes. Os números deste README estão presos em `MedidasTest`.

## Licença

MIT.
