Oi Flávia e Ronaldo,

vou fazer um exemplo para vcs :D

Em primeiro lugar vcs devem rodar o teste estatístico de maneira independente do RankGraph.jar. Lembrem-se que esse jar apenas "converte" o resultado do teste estatístico para um gráfico. Para isso, ele precisa dos resultados do teste estatístico. Eu quero muito colocar esse passo de conversão dentro do script, mas ainda não tive tempo :(

Então, supondo que seus resultados sejam:

	BestGlobal	BestLocal-k1	Siblings	LessInclusive	Inclusive
Hglass	0.63479	0.62094	0.67802	0.74855	0.74855
Fabien	0.37276	0.44168	0.50750	0.51593	0.51593
Marsyas	0.48033	0.53043	0.65093	0.64995	0.64995
Thomas-rh	0.34108	0.35400	0.51026	0.50550	0.50550
Thomas-ssd	0.42338	0.44694	0.60073	0.57272	0.57272
G-Pfam	0.46170	0.52677	0.57257	0.57170	0.57173
G-Prints	0.62541	0.66304	0.65730	0.65790	0.65862
G-prosite	0.40937	0.50423	0.54903	0.54711	0.54719
G-Interpro	0.66835	0.67611	0.66011	0.65848	0.65915
E-Pfam	0.54831	0.67550	0.79708	0.80463	0.80550
E-Prints	0.58317	0.70248	0.83390	0.83798	0.83849
E-Prosite	0.65184	0.74570	0.84879	0.85292	0.85422
E-Interpro	0.77078	0.76701	0.79753	0.80313	0.80362



ou seja, 5 métodos (BestGlobal, BestLocal-k1, Siblings, LessInclusive, Inclusive) e 13 datasets (cada linha é um dataset), 5x13 "combinações". Conforme essa tabela de resultados, para cada combinação, vcs têm o valor médio da medida de avaliação que te interessa, por exemplo F-Measure, e sem o desvio padrão.

Então, vão criar um arquivo com exatamente o formato dessa tabela acima.
Após rodar o teste estatístico indicado para o seu caso. Vamos supor que queiram rodar o teste de Nemenyi. Se o objetivo for maximizar a medida (F-measure por exemplo), tem que rodar o teste nemenyi.pl. Se o objetivo for minimizar a medida (error por exemplo) então devem rodar o nemenyiLoss.pl. Para isso usem o seguinte comando:

perl nemenyi.pl datafile

onde datafile é o arquivo com os dados (ou seja, com a tabela acima).

Após a execução desse comando, o script do teste estatístico vai gerar um arquivo de saída com o valor do ranking de cada método, ok?!?

Esse arquivo de saída do teste estatístico é do seguinte formato:

===========================================================================================
                    	BestGlobal		BestLocal-k1		Siblings		LessInclusive		Inclusive		

              Hglass	0.635(4.0)	0.621(5.0)	0.678(3.0)	0.749(1.5)	0.749(1.5)	
              Fabien	0.373(5.0)	0.442(4.0)	0.507(3.0)	0.516(1.5)	0.516(1.5)	
             Marsyas	0.480(5.0)	0.530(4.0)	0.651(1.0)	0.650(2.5)	0.650(2.5)	
           Thomas-rh	0.341(5.0)	0.354(4.0)	0.510(1.0)	0.505(2.5)	0.505(2.5)	
          Thomas-ssd	0.423(5.0)	0.447(4.0)	0.601(1.0)	0.573(2.5)	0.573(2.5)	
              G-Pfam	0.462(5.0)	0.527(4.0)	0.573(1.0)	0.572(3.0)	0.572(2.0)	
            G-Prints	0.625(5.0)	0.663(1.0)	0.657(4.0)	0.658(3.0)	0.659(2.0)	
           G-prosite	0.409(5.0)	0.504(4.0)	0.549(1.0)	0.547(3.0)	0.547(2.0)	
          G-Interpro	0.668(2.0)	0.676(1.0)	0.660(3.0)	0.658(5.0)	0.659(4.0)	
              E-Pfam	0.548(5.0)	0.675(4.0)	0.797(3.0)	0.805(2.0)	0.805(1.0)	
            E-Prints	0.583(5.0)	0.702(4.0)	0.834(3.0)	0.838(2.0)	0.838(1.0)	
           E-Prosite	0.652(5.0)	0.746(4.0)	0.849(3.0)	0.853(2.0)	0.854(1.0)	
          E-Interpro	0.771(4.0)	0.767(5.0)	0.798(3.0)	0.803(2.0)	0.804(1.0)	

        average rank	4.615		3.692		2.308		2.500		1.885		

The Chi-Square statistics is 26.32.
The critical value of the Chi-square statistics with 4 degrees of freedom and at 95 percentile is 9.49
According to the Freidman test using the Chi-Square statistics, the null-hypothesis that all algorithms behave similar should be rejected

The critical value of the Chi-square statistics with 4 degrees of freedom and at 90 percentile is 7.78
According to the Freidman test using the Chi-Square statistics, the null-hypothesis that all algorithms behave similar should be rejected

The F-statistics is 12.30.
The critical value of the F-statistics with 4 and 48 degrees of freedom and at 95 percentile is 2.57
According to the Freidman test using the F-statistics, the null-hypothesis that all algorithms behave similar should be rejected

The critical value of the F-statistics with 4 and 48 degrees of freedom and at 90 percentile is 2.07
According to the Freidman test using the F-statistics, the null-hypothesis that all algorithms behave similar should be rejected

Running the Nemenyi post-hoc test to verify wheter it is possible to detect differences among algorithms.

According to the the Nemenyi statistis, the critical value for comparing the mean-ranking of two different algorithms at 95 percentile is 1.69. Mean-rankings differences above this value are significative.

Algorithms which performed better are marked as +++, worst as ---, and where it is not possible to detect difference is marked as ooo.

                     Inclusive	Siblings	LessInclusive	BestLocal-k1	BestGlobal
           Inclusive	[ooo	ooo	ooo	+++	+++	]
            Siblings	[ooo	ooo	ooo	ooo	+++	]
       LessInclusive	[ooo	ooo	ooo	ooo	+++	]
        BestLocal-k1	[---	ooo	ooo	ooo	ooo	]
          BestGlobal	[---	---	---	ooo	ooo	]

According to the the Nemenyi statistis, the critical value for comparing the mean-ranking of two different algorithms at 90 percentile is 1.53. Mean-rankings differences above this value are significative.

Algorithms which performed better are marked as +++, worst as ---, and where it is not possible to detect difference is marked as ooo

                     Inclusive	Siblings	LessInclusive	BestLocal-k1	BestGlobal
           Inclusive	[ooo	ooo	ooo	+++	+++	]
            Siblings	[ooo	ooo	ooo	ooo	+++	]
       LessInclusive	[ooo	ooo	ooo	ooo	+++	]
        BestLocal-k1	[---	ooo	ooo	ooo	ooo	]
          BestGlobal	[---	---	---	ooo	ooo	]
===========================================================================================


A partir do resultado do teste estatístico você tem que criar um arquivo de entrada para o RankGraph.jar, o qual deve ter a extensão .property
Esse arquivo de entrada deve ter o seguinte formato:


names=BestGlobal,BestLocal-k1,Siblings,LessInclusive,Inclusive
ranks=4.615,3.692,2.308,2.500,1.885
cd=1.69
width=6.0

onde, 

- names é uma lista separada por vírgula com os nomes dos métodos que foram avaliados.

- ranks é o average rank de cada método. Essa informação está na última linha da tabela de ranking gerada pelo teste estatístico. Para esse exemplo corresponde à 
	"        average rank	4.615		3.692		2.308		2.500		1.885		"

-cd é o valor crítico. Para esse parâmetro o teste estatístico dá dois valores, relacionados ao percentual de significância estatística. O primeiro valor é para 95% e o segundo para 90%. Para encontrar esse valor no arquivo, veja as frases que começam com "According to the the Nemenyi statistis, the critical value for comparing the mean-ranking of two different algorithms at 95 percentile is 1.69 ..."

- width é uma medida em centímetros que você deve passar para o rankGraph. Essa medida é usada para definir a largura do gráfico e apenas isso. O valor default é 6.0, se me recordo bem.

Criado esse arquivo esse arquivo .property, digamos que o nome dele seja RankGraph-input.property, basta executar o seguinte comando:

java -jar RankGraph.jar -f RankGraph-input.property

Por default o RankGraph.jar vai criar um novo arquivo contento o código TikZ (latex) referente ao gráfico, o qual será colocado em um novo arquivo com o mesmo nome do arquivo de entrada, mas com o sufixo .tex adicionado. Portanto, para esse exemplo, o arquivo com o gráfico será RankGraph-input.property.tex

O conteúdo será:


\begin{figure} \centering \begin{tikzpicture}[xscale=2]
\node (Label) at  (01.8450,0.7) {\tiny{CD}}; % the label
\draw[very thick, color = red!60!black!70](01.0000, 0.5) -- (02.6900, 0.5);
\foreach \x in {01.0000,02.6900} \draw[thick,color = red!60!black!70] (\x, 0.4) -- (\x, 0.6);

\draw[gray, thick](01.0000, 0) -- (05.0000, 0);
\foreach \x in {01.0000,02.0000,03.0000,04.0000,05.0000}\draw (\x cm,1.5pt) -- (\x cm, -1.5pt);
\node (Label) at (01.0000,0.2) {\tiny{1}};
\node (Label) at (02.0000,0.2) {\tiny{2}};
\node (Label) at (03.0000,0.2) {\tiny{3}};
\node (Label) at (04.0000,0.2) {\tiny{4}};
\node (Label) at (05.0000,0.2) {\tiny{5}};
\draw[very thick, color = red!60!black!70](01.8350,-00.2500) -- ( 02.5500,-00.2500);
\draw[very thick, color = red!60!black!70](02.2580,-00.4000) -- ( 03.7420,-00.4000);
\draw[very thick, color = red!60!black!70](03.6420,-00.2500) -- ( 04.6650,-00.2500);
\node (Point) at (01.8850, 0){};  \node (Label) at (0.5,-00.8500){\small{Inclusive}}; \draw (Point) |- (Label);
\node (Point) at (02.3080, 0){};  \node (Label) at (0.5,-01.1500){\small{Siblings}}; \draw (Point) |- (Label);
\node (Point) at (04.6150, 0){};  \node (Label) at (5.5,-00.8500){\small{BestGlobal}}; \draw (Point) |- (Label);
\node (Point) at (03.6920, 0){};  \node (Label) at (5.5,-01.1500){\small{BestLocal-k1}}; \draw (Point) |- (Label);
\node (Point) at (02.5000, 0){};  \node (Label) at (5.5,-01.4500){\small{LessInclusive}}; \draw (Point) |- (Label);
\end{tikzpicture}
\caption{./f1micro-nb-mln.property}
\label{fig:}
\end{figure}

Observem que por ser um código latex, vcs podem usar suas macros para nomear os métodos e datasets, caso lhe interesse. Notem que apenas o código referente ao gráfico é gerado e não o cabeçalho de um "root" latex. Vocês devem incluir esse arquivo no seu projeto latex para ter a figura compilada.

OBS: Para que vcs consigam realmente gerar o pdf com esse gráfico devem incluir o pacote tikz (\usepackage{tikz}).


Estou enviando também o exemplo completo com todos os arquivos necessários e também o tex e pdf com a figura inclusa.
Os arquivos são:

	- exp-result-table.data
	- statistics.txt
	- RankGraph-input.property
	- RankGraph-input.property.tex
	- root.tex
	- root.pdf

Espero que ajude.

Cheers,
