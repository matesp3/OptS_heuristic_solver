package contracts;

public interface ITspHeuristic {
    /**
     * Executes specific algorithm to solve TSP.
     */
    public void solve();

    /**
     * Returns solution of TSP.
     * @return solution path where element on i-th position is i-th visited node on path of traveling salesman.
     */
    public int[] getSolutionPath();
}
