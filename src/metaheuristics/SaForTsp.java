package metaheuristics;

import contracts.ITspChainOperation;

import java.util.Random;

/**
 * Meta heuristic Simulated Annealing for Traveling Salesman Problem.
 */
public class SaForTsp {
    private Random rand;
    private ITspChainOperation heuristic; // primary heuristic algorithm for modifying sequence of nodes in path
    private int[][] dij; // matrix of distances between each pair of nodes
    private int[] x; // current best path of Traveling Salesman

    public SaForTsp(ITspChainOperation chainOperation, int[] initSol, int[][] distanceMatrix) {
        if (chainOperation == null)
            throw new NullPointerException("Chain operation not provided");
        if (initSol == null)
            throw new NullPointerException("Initial valid solution not provided");
        if (initSol.length < 4)
            throw new IllegalArgumentException("Problem has to be composed at least of 4 nodes to be solved by this" +
                    "meta heuristic algorithm.");
        if (distanceMatrix == null)
            throw new NullPointerException("Distance matrix not provided");
        if (distanceMatrix.length != initSol.length -1)
            throw new IllegalArgumentException("Number of nodes in matrix and path are different.");

        this.x = initSol;
        this.heuristic = chainOperation;
        this.dij = distanceMatrix;
        this.rand = new Random(25);
    }

    public void solve() {
        // todo
    }

    public void printSolution() {
        System.out.print("\n TSP sequential growth heuristic result:\n  x = (");
        for (int i = 0; i < (this.x.length - 1); i++) {
            if (i != 0 && i % 22 == 0)
                System.out.print("\n      ");
            System.out.printf("%s, ", (this.x[i] < 0 ? "?" : String.valueOf(this.x[i])));
        }
        System.out.printf("%s)\n", (this.x[0] < 0 ? "?" : String.valueOf(this.x[0])));
//        System.out.println("  * TSP route length: " + this.routeLength);
//        System.out.println("  * Test of nodes presence: " + (this.verifyNodesPresence() ? "PASSED" : "FAILED!"));
    }

    /**
     * Executes experiment to decide whether algorithm should take worse function result or not.
     * This method should be called only when currently computed value of function is not better (is not lower) than
     * specified <code>minFuncVal</code>, what is current function value.
     * @param funcVal currently computed function value
     * @param minFuncVal function value of current solution
     * @param t temperature
     * @return <code>true</code> if experiment passed, else <code>false</code>
     */
    private boolean shouldPass(double funcVal, double minFuncVal, int t) {
        if (funcVal < minFuncVal)
            return true;
        double prob = Math.pow(Math.E, (-1*(funcVal - minFuncVal)/t));
        double experiment = rand.nextDouble();
        return (experiment <= prob);
    }
}
