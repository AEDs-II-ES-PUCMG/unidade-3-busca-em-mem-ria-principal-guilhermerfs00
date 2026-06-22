import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Random;
import java.util.Set;

public class TestePreAtividade {

    private static final int N = 10_000;
    private static final int M = 1_000;

    private record Resultado(String cenario, long comparacoes, double tempoNanos) {
    }

    public static void main(String[] args) {

        Random geradorInsercao = new Random(42);
        List<Integer> valoresInsercao = gerarValoresUnicos(N, geradorInsercao, 1_000_000);

        Random geradorBuscas = new Random(99);
        List<Integer> valoresBusca = gerarBuscas(M, geradorBuscas, valoresInsercao, 1_000_000);

        List<Integer> valoresOrdenados = new ArrayList<>(valoresInsercao);
        Collections.sort(valoresOrdenados);

        Resultado r1 = executarCenario("ABB / inserção aleatória", false, valoresInsercao, valoresBusca);
        Resultado r2 = executarCenario("AVL / inserção aleatória", true, valoresInsercao, valoresBusca);
        Resultado r3 = executarCenario("ABB / inserção ordenada", false, valoresOrdenados, valoresBusca);
        Resultado r4 = executarCenario("AVL / inserção ordenada", true, valoresOrdenados, valoresBusca);

        imprimirTabela(r1, r2, r3, r4);
    }

    private static List<Integer> gerarValoresUnicos(int quantidade, Random sorteio, int limiteSuperiorExclusivo) {
        Set<Integer> conjunto = new HashSet<>();
        while (conjunto.size() < quantidade) {
            conjunto.add(sorteio.nextInt(limiteSuperiorExclusivo));
        }
        return new ArrayList<>(conjunto);
    }

    private static List<Integer> gerarBuscas(int quantidade, Random sorteio, List<Integer> valoresBase,
            int limiteSuperiorExclusivo) {
        List<Integer> buscas = new ArrayList<>(quantidade);
        for (int i = 0; i < quantidade; i++) {
            if (i % 2 == 0) {
                int indice = sorteio.nextInt(valoresBase.size());
                buscas.add(valoresBase.get(indice));
            } else {
                buscas.add(sorteio.nextInt(limiteSuperiorExclusivo));
            }
        }
        return buscas;
    }

    private static Resultado executarCenario(String nomeCenario, boolean usarAvl, List<Integer> insercao,
            List<Integer> buscas) {

        ABB<Integer, Integer> arvore = usarAvl ? new AVL<>() : new ABB<>();

        for (Integer valor : insercao) {
            arvore.inserir(valor, valor);
        }

        long totalComparacoes = 0;
        double totalTempoNanos = 0;

        for (Integer procurado : buscas) {
            long inicio = System.nanoTime();
            try {
                arvore.pesquisar(procurado);
            } catch (NoSuchElementException e) {
                // busca sem sucesso também conta no experimento
            }
            long fim = System.nanoTime();

            totalComparacoes += arvore.getComparacoes();
            totalTempoNanos += (fim - inicio);
        }

        return new Resultado(nomeCenario, totalComparacoes, totalTempoNanos);
    }

    private static void imprimirTabela(Resultado... resultados) {
        System.out.printf("%-30s | %-20s | %-20s%n", "Cenário", "Total comparações", "Tempo total (ns)");
        System.out.println("-------------------------------------------------------------------------------------");
        for (Resultado r : resultados) {
            System.out.printf("%-30s | %-20d | %-20.0f%n", r.cenario(), r.comparacoes(), r.tempoNanos());
        }
    }
}
