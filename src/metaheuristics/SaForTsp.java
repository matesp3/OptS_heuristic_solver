package metaheuristics;

import contracts.ITspChainOperation;

import java.util.Calendar;
import java.util.Random;

/**
 * Meta heuristic Simulated Annealing for Traveling Salesman Problem.
 */
public class SaForTsp {
    private static final int T_MAX = 10_000; // max temperature
    private static final int U = 40; // max examined transits count from transit to current solution
    private static final int Q = 50; // max examined transits from last temperature change
    private Random rand;
    private ITspChainOperation heuristic; // primary heuristic algorithm for modifying sequence of nodes in path
    private int bestRouteLength; // function value of x variable (best found path)
    private int routeLength;     // function value of current (x_i) variable
    private int[] x;      // best found path of Traveling Salesman


    public SaForTsp(ITspChainOperation chainOperation) {
        if (chainOperation == null)
            throw new NullPointerException("Chain operation not provided");
        this.heuristic = chainOperation;
        this.rand = new Random(25);
    }

    public void solve() {
        final double beta = 0.5;
        final double expiration = 60 * 5; // =5min seconds
        this.x = this.heuristic.getSolutionRoute().clone();
        this.bestRouteLength = this.heuristic.getRouteLength();
        this.routeLength = this.bestRouteLength;
        long startSecs = Calendar.getInstance().getTimeInMillis() / 1000;
        long endSecs = startSecs;
        int r;        // examined transits count from last transit to current solution
        int t;        // current temperature
        int v = 0;    // annealing
        while (true) {
            t = T_MAX;
            // examine transits from last temperature change
            int w = 0;
            r = 0;
            v = 0; // annealing
            while ((endSecs - startSecs) < expiration && r != U) {
                if (w == Q) {
                    t = (int) (t / (1.0 + beta * t)); // lower down temperature
                    w = 0;
                }
                int newRouteLen = this.heuristic.nextModification();
                w++;
                r++;
                if ((newRouteLen <= this.routeLength) || this.shouldPass(newRouteLen, t)) { // is solution from surrounding accepted?
                    this.heuristic.applyModification(); // route modification
                    this.routeLength = newRouteLen;
                    if (this.routeLength < this.bestRouteLength) {
                        this.bestRouteLength = this.routeLength;
                        this.x = this.heuristic.getSolutionRoute().clone();
                        v++;
                    }
                    r = 0;
                }
                endSecs = Calendar.getInstance().getTimeInMillis() / 1000;
            }
            if ((endSecs - startSecs) >= expiration || v == 0)
                break;
        }
    }

    public void printSolution() {
        System.out.print(" - - -\nTSP simulated annealing using "+this.heuristic.getHeuristicName()+":\n  x = (");
        for (int i = 0; i < (this.x.length - 1); i++) {
            if (i != 0 && i % 22 == 0)
                System.out.print("\n      ");
            System.out.printf("%s, ", (this.x[i] < 0 ? "?" : String.valueOf(this.x[i])));
        }
        System.out.printf("%s)\n", (this.x[0] < 0 ? "?" : String.valueOf(this.x[0])));
        System.out.println("  * length of best found route: " + this.bestRouteLength);
        System.out.println("  * Test of nodes presence: " + (this.verifyNodesPresence() ? "PASSED" : "FAILED!"));
    }

    /**
     * Executes experiment to decide whether algorithm should take worse function result or not.
     * This method should be called only when currently computed value of function is not better (is greater) than
     * routeLength of current solution (i).
     * @param newLength currently computed function value of solution from surrounding
     * @param t current temperature
     * @return <code>true</code> if experiment passed, else <code>false</code>
     */
    private boolean shouldPass(double newLength, int t) {
        if (newLength <= this.routeLength)
            return true;
        double prob = Math.pow(Math.E, (-1*(newLength - this.routeLength) / t));
        double h = rand.nextDouble();
        return (h <= prob);
    }

    /**
     * @return <code>true</code> or <code>false</code> whether all nodes of network are included in route of TSP.
     */
    private boolean verifyNodesPresence() {
        for (int id = 0; id < this.x.length-1; id++) {
            boolean found = false;
            for (int j = 0; j < this.x.length - 1; j++) { // -1, bcs it is duplicate id of first node
                if (this.x[j] == id) {
                    found = true;
                    break;
                }
            }
            if (!found)
                return false;
        }
        return true;
    }
}
