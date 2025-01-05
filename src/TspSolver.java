import contracts.ITspChainOperation;
import contracts.ITspHeuristic;
import heuristics.ChainInversion;
import heuristics.TspSeqGrowthByNearest;
import metaheuristics.SaForTsp;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.StringTokenizer;

public class TspSolver {

    public static void main(String[] args) {
//        int[][] dij = loadDistanceMatrix(new File("files/mala_matica_dij.txt"));
        int[][] dij = loadDistanceMatrix(new File("files/matica_PO_(0664).txt"));

        ITspHeuristic initSolExecutor = new TspSeqGrowthByNearest(dij);
        initSolExecutor.solve();
        int[] initSolutionPath = initSolExecutor.getSolutionRoute();
        initSolExecutor.printSolution();
        ITspChainOperation primaryHeur = new ChainInversion(initSolutionPath, 5, dij);
        SaForTsp tspSimAnnealing = new SaForTsp(primaryHeur);
        tspSimAnnealing.solve();
        tspSimAnnealing.printSolution();
    }

    private static int[][] loadDistanceMatrix(File file){
        try {
            BufferedReader bfr = new BufferedReader(new FileReader(file));
            String line = "";
            int lineIndex = -1;
            int M;
            int[][] dij = null;
            while ((line = bfr.readLine()) != null) {
                if (lineIndex > -1) {
                    StringTokenizer st = new StringTokenizer(line, " ");
                    int offset = 0;
                    while (st.hasMoreTokens()) {
                        dij[lineIndex][offset] = Integer.parseInt(st.nextToken());
                        offset++;
                    }
                }
                else {
                    M = Integer.parseInt(line);
                    dij = new int [M][M];
                }
                lineIndex++;
            }
            bfr.close();
            return dij;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (Exception e) {
            System.out.println("error " + e);
        }
        return null;
    }
}
