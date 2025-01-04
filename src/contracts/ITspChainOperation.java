package contracts;

import java.util.Random;

/**
 * Primary heuristic that modifies node sequence in given path of Traveling Salesman.
 */
public interface ITspChainOperation {

    /**
     *
     * @param path existing proper path of Traveling Salesman problem.
     * @param r optional generator for retrieving nodes.
     */
    public void execute(int[] path, Random r);
}
