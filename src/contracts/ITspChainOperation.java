package contracts;

/**
 * It behaves as primary heuristic that modifies node sequence in given path of Traveling Salesman.
 */
public interface ITspChainOperation {

    /**
     * @return <code>true</code> if there's still some not used solution from set of solutions of current surrounding,
     * else <code>false</code>.
     */
    public boolean hasNextModification();

    /**
     * Picks another parameters for different solution which is in set of solutions of current surrounding.
     * These parameters are saved within this instance.
     *
     * @return received savings, if generated modification would be applied on current path
     */
    public int nextModification();

    /**
     * Before this method call it is required to generate modification parameters by calling <code>nextModification</code>
     * method of this class. This method applies generated changes on existing route of Traveling Salesman.
     */
    public void applyModification();

    public int[] getSolutionPath();

    public int getOverallDistance();

}
