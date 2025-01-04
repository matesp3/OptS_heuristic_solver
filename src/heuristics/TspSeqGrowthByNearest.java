package heuristics;

import contracts.ITspHeuristic;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.StringTokenizer;

/**
 * Heuristic solver for Traveling Salesman Problem building path by finding nearest node to all nodes already
 * visited on current path.
 * @author Matej Poljak
 */
public class TspSeqGrowthByNearest implements ITspHeuristic {
    private int M;          // nodes count
    private int[][] dij;    // distance matrix
    private int[] x;        // solution - path of salesman

    public TspSeqGrowthByNearest(int[][] distanceMatrix) {
        if (distanceMatrix == null)
            throw new NullPointerException("Distance matrix not provided");
        if (distanceMatrix.length < 4)
            throw new IllegalArgumentException("Heuristic solves TSP with at least 4 nodes");
        this.M = distanceMatrix.length;
        this.dij = distanceMatrix;
        this.x = new int[this.M];
    }

    public void solve() {

    }

    @Override
    public int[] getSolutionPath() {
        return new int[0];
    }

    public void printSolution() {
        if (this.x == null || this.x.length == 0) {
            System.out.println("There's not result to display. Input data - Distance matrix - hasn't been loaded yet.");
            return;
        }
        System.out.println("\n ----- * * *  S O L U T I O N   O U T P U T * * * -----");
        System.out.println("Legend: ... -> [Number of node in path]: node ID -> ...");
        for (int i = 0; i < this.x.length; i++) {
            if (i % 22 == 0)
                System.out.println();
            System.out.printf(" [%d]:%s->"
                    , (i+1)
                    , (this.x[i] == 0 ? "?" : String.valueOf(this.x[i]))
            );
        }
        System.out.printf(" [1]:%s", (this.x[0] == 0 ? "?" : String.valueOf(this.x[0])));
    }
}
