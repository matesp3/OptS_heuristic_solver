package metaheuristics;

import contracts.ITspChainOperation;

/**
 * Metaheuristic Simulated Annealing for Traveling Salesman Problem.
 */
public class SaForTsp {
    private int[] path; // path of Traveling Salesman
    private ITspChainOperation heuristic; // heuristic algorithm for modifying sequence of nodes in path
    private int[][] dij; // matrix of distances between each pair of nodes

    public SaForTsp(ITspChainOperation chainOperation, int[] initSol, int[][] distanceMatrix) {
        if (chainOperation == null)
            throw new NullPointerException("Chain operation not provided");
        if (initSol == null)
            throw new NullPointerException("Initial valid solution not provided");
        if (initSol.length < 4)
            throw new IllegalArgumentException("Problem has to be composed at least of 4 nodes to be solved by this" +
                    "metaheuristic algorithm.");
        if (distanceMatrix == null)
            throw new NullPointerException("Distance matrix not provided");
        if (distanceMatrix.length != initSol.length -1)
            throw new IllegalArgumentException("Number of nodes in matrix and path are different.");

        this.path = initSol;
        this.heuristic = chainOperation;
    }

    public void solve() {
        // todo
    }

    public void printSolution() {
        // todo
    }
}
