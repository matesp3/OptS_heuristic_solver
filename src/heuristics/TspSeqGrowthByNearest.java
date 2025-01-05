package heuristics;

import contracts.ITspHeuristic;

import java.util.*;

/**
 * Heuristic solver for Traveling Salesman Problem builds route by finding nearest node to all nodes already
 * visited (these are part of current route). It's dual heuristic which starts in inadmissible solution - not complete
 * route of Travelling Salesman.
 * @author Matej Poljak
 */
public class TspSeqGrowthByNearest implements ITspHeuristic {
    private static final int INVALID = -1; // invalid reference - not defined
    private class Node { // helper wrapper class just for internal usage
        final int id;
        int next = -1;   // successor node in route of Traveling Salesman

        public Node(int id) {
            this.id = id;
        }
    }

    private final int M;          // nodes count
    private final int[][] dij;    // distance matrix
    private final int[] x;        // solution - route of salesman
    // v--<-- additional information attributes for faster access - simple implementation of linked list
    private final int sourceNode; // id of starting node
    private final Node[] nodes;   // sorted by node's ID ascending for direct access
    private int routeLength;      // overall length of created route

    public TspSeqGrowthByNearest(int[][] distanceMatrix) {
        if (distanceMatrix == null)
            throw new NullPointerException("Distance matrix not provided");
        if (distanceMatrix.length < 4)
            throw new IllegalArgumentException("Heuristic solves TSP with at least 4 nodes");

        this.M = distanceMatrix.length;
        this.dij = distanceMatrix.clone();
        this.x = new int[this.M + 1];
        Arrays.fill(this.x, INVALID);
        this.nodes = new Node[this.M];
        for (int i = 0; i < this.nodes.length; i++) {
            this.nodes[i] = new Node(i);
        }
        this.sourceNode = 0;
        this.routeLength = 0;
    }

    public void solve() {
        // >>->-> task is to create linked list using all nodes once except starting node which is the last node at the same time
        // prepare inadmissible route
        final int i1, i2, i3;
        i1 = this.sourceNode;
        i2 = this.getGreatestDistanceFrom(i1, new int[]{i1});
        i3 = this.getGreatestDistanceFrom(i2, new int[]{i1, i2});
        this.nodes[i1].next = i2;
        this.nodes[i2].next = i3;
        this.nodes[i3].next = i1; // enclosing route by returning to the starting node (source)
        this.updateRouteLength();
        // initialize list of not processed nodes
        List<Integer> notProcessed = new ArrayList<>(this.M - 3);
        for (int i = 0; i < this.nodes.length; i++) {
            if (this.nodes[i].id != i1 && this.nodes[i].id != i2 && this.nodes[i].id != i3) {
                notProcessed.add(this.nodes[i].id);
            }
        }
        // execute heuristic algorithm
        while (!notProcessed.isEmpty()) {
            int minDist = Integer.MAX_VALUE;
            int bestNode = -1; // best node for insertion to the current route
            for (int candidateId : notProcessed) {
                int dist = this.getDistanceFromIncluded(candidateId);
                if (dist < minDist) {
                    minDist = dist;
                    bestNode = candidateId;
                }
            }
            int[] result = this.findBestPlaceToInsert(bestNode);
            int prev = result[0];
            int next = this.nodes[prev].next;
            // link new node inside pair of nodes whose beginning in 'prev' node
            this.nodes[prev].next = bestNode;
            this.nodes[bestNode].next = next;
            this.routeLength += result[1];
//            notProcessed.remove(new Integer(bestNode)); // update list - boxing needed in order to not take it as idx
            removeIntByValue(notProcessed, bestNode); // update list
        }
        this.retrieveSolution(); // writes route to solution array 'x'
    }

    @Override
    public int[] getSolutionRoute() {
        return this.x;
    }

    public void printSolution() {
        System.out.print("\n TSP sequential growth heuristic result:\n  x = (");
        for (int i = 0; i < (this.x.length - 1); i++) {
            if (i != 0 && i % 22 == 0)
                System.out.print("\n      ");
            System.out.printf("%s, ", (this.x[i] < 0 ? "?" : String.valueOf(this.x[i])));
        }
        System.out.printf("%s)\n", (this.x[0] < 0 ? "?" : String.valueOf(this.x[0])));
        System.out.println("  * TSP route length: " + this.routeLength);
        System.out.println("  * Test of nodes presence: " + (this.verifyNodesPresence() ? "PASSED" : "FAILED!"));
    }

    /**
     * Computes sum of distances between specified <code>node</code> (which is assumed to not be part of route) and all
     * nodes included in current route of Traveling Salesman.
     * @param node id of observed node
     * @return overall distance of <code>node</code> from included nodes in route.
     */
    private int getDistanceFromIncluded(int node) {
        int current = this.sourceNode;
        int sum = 0;
        do {
            sum += this.dij[node][current];
            current = this.nodes[current].next;
        } while (current != this.sourceNode); // when approaches sourceNode, it has iterated everything
        return sum;
    }

    /**
     * Gets ID of the node that has the greatest distance from specified <code>node</code> and does not appear in set
     * of <code>forbiddenNodes</code>.
     * @param node
     * @param forbiddenNodes set of nodes, whose distances from <code>node</code> are not compared
     * @return ID of node that has the greatest distance from <code>node</code>.
     */
    private int getGreatestDistanceFrom(int node, int[] forbiddenNodes) {
        int maxDistFromNode = -1;
        int wantedId = -1;
        for (int i = 0; i < this.M; i++) { // i2 = get node with the greatest distance from i1
            boolean canBeCompared = true;
            if (forbiddenNodes != null && forbiddenNodes.length != 0) { // check whether 'i' is not forbidden to compare
                for (int j = 0; j < forbiddenNodes.length; j++) {
                    if (i == forbiddenNodes[j]) {
                        canBeCompared = false;
                        break;
                    }
                }
            }
            if (canBeCompared && this.dij[node][i] > maxDistFromNode) {
                maxDistFromNode = this.dij[node][i];
                wantedId = i; // update node ID (node ID corresponds index position)
            }
        }
        return wantedId;
    }

    /**
     * Recalculates length of current route.
     */
    private void updateRouteLength() {
        if (this.sourceNode == -1)
            return;
        int node = this.sourceNode;
        this.routeLength = 0;
        int next;
        do {
            next = this.nodes[node].next;
            this.routeLength += this.dij[node][next];
            node = next;
        } while (node != this.sourceNode);
    }

    /**
     * Finds the best pair of nodes between which should be specified <code>node</code> inserted. Determining condition
     * is to extend current route as least as possible.
     * @param node ID of node to insert to route
     * @return resultArr[idx=0] is ID of predecessor's node - node, after which should be specified <code>node</code> inserted.
     * resultArr[idx=1] is the value by which will route be longer after inserting <code>node</code> on found place.
     */
    private int[] findBestPlaceToInsert(int node) {
        int extension; // value by which would be route longer, if node was inserted between node i and node j, which are a part of route
        int leastExt = Integer.MAX_VALUE;
        int bestNode = -1; // ID of node that is predecessor of best place for node to insert
        int i = this.sourceNode;
        int j; // successor of node i
        do {
            j = this.nodes[i].next;
            extension = this.dij[i][node] + this.dij[node][j] - this.dij[i][j];
            if (extension < leastExt) {
                leastExt = extension;
                bestNode = i;
            }
            i = j;
        } while (leastExt > 0 && i != this.sourceNode); // if extension is 0, no need to find better candidate
        return new int[] { bestNode, leastExt };
    }

    /**
     * Transforms route from linked nodes to array representing solution of TSP.
     */
    private void retrieveSolution() {
        int nodeId = this.nodes[this.sourceNode].id;
        int i = 0;
        do {
            this.x[i] = nodeId;
            nodeId = this.nodes[nodeId].next;
            i++;
        } while (nodeId != this.sourceNode);
        this.x[i] = nodeId;
    }

    /**
     * @return <code>true</code> or <code>false</code> whether all nodes of network are included in route of TSP.
     */
    private boolean verifyNodesPresence() {
        for (int id = 0; id < this.M; id++) {
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

    /**
     * Instead of standard remove by object, because int was taken as index parameter.
     * @param list
     * @param value
     */
    private static void removeIntByValue(List<Integer> list, int value) {
        for (int i = 0; i < list.size(); i++) {
            if (list.get(i) == value) {
                list.remove(i); // remove by index, when value is the same
                return;
            }
        }
    }
}
