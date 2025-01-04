import contracts.ITspHeuristic;
import heuristics.TspSeqGrowthByNearest;
import metaheuristics.SaForTsp;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.StringTokenizer;

public class TspSolver {

    public static void main(String[] args) {
        int[][] dij = loadDistanceMatrix(new File("files/mala_matica_dij.txt"));
        ITspHeuristic initSolExecutor = new TspSeqGrowthByNearest(dij);
        initSolExecutor.solve();
        int[] initSolutionPath = initSolExecutor.getSolutionRoute();
        initSolExecutor.printSolution();
//        SaForTsp tspSimAnnealing = new SaForTsp(null, initSolutionPath, dij);
//        tspSimAnnealing.solve();
//        tspSimAnnealing.printSolution();
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
