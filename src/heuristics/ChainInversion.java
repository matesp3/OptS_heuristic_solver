package heuristics;

import contracts.ITspChainOperation;

import java.util.Random;

public class ChainInversion implements ITspChainOperation {
    private final int chainLen;
    private final int[] route; // valid route of TSP
    private final int[][] dij; // matrix of distances between each pair of nodes
    private int seqStart; // index of sequence beginning node in route variable
    private int savings = Integer.MIN_VALUE;
    private int distance = 0;

    /**
     * @param path existing valid path of Traveling Salesman problem.
     * @param chainLength length of chain to be inverted
     */
    public ChainInversion(int[] path, int chainLength, int[][] distanceMatrix) {
        if (chainLength < 0)
            throw new IllegalArgumentException("Chain length cannot be negative");
        if (path == null)
            throw new NullPointerException("Path of TSP not provided");
        if (path.length < chainLength+2)
            throw new IllegalArgumentException("Inconsistent combination of path nodes count and chain length");
        if (path.length - 1 != distanceMatrix.length)
            throw new IllegalArgumentException("Mismatch between path nodes count and matrix nodes count");

        this.chainLen = chainLength;
        this.route = path;
        this.dij = distanceMatrix;
        this.seqStart = 0;
        this.calcRouteDistance();
    }

    @Override
    public boolean hasNextModification() {
        return ((this.seqStart + this.chainLen) + 1 < this.route.length); // whether chain end is lower than last index of arr
    }

    @Override
    public int nextModification() {
        // startIdx cannot be first and last index, because it's source node
        this.seqStart++;
        int seqEnd = this.seqStart + this.chainLen - 1;
        int p = this.route[this.seqStart - 1];  // predecessor node of sequence start node
        int z = this.route[this.seqStart];      // sequence start node
        int k = this.route[seqEnd];             // sequence end node
        int n = this.route[seqEnd + 1];         // successor node of sequence end node
        this.savings = (this.dij[p][z] + this.dij[k][n]) - (this.dij[p][k] + this.dij[z][n]);
        return this.savings;
    }

    @Override
    public void applyModification() {
        int l = this.seqStart;
        int r = this.seqStart + this.chainLen - 1;
        int hlp;
        while (l != r && l+1 != r) { // first cond for odd, second for even chainLen
            hlp = this.route[l];
            this.route[l] = this.route[r];
            this.route[r] = hlp;
            l++; r--;
        }
        this.distance -= this.savings;
    }

    @Override
    public int[] getSolutionPath() {
        return this.route;
    }

    @Override
    public int getOverallDistance() {
        return this.distance;
    }

    private void calcRouteDistance() {
        this.distance = 0;
        for (int i = 0; i < (this.route.length - 1); i++) {
            this.distance += this.dij[this.route[i]][this.route[i+1]];
        }
    }
}
