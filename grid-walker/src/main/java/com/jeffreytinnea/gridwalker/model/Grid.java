package com.jeffreytinnea.gridwalker.model;

import java.util.ArrayList;
import java.util.List;
import java.awt.Point;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.gson.Gson;

/**
 * <h1>Grid model class</h1>
 * Holds the matrix data and methods for traversing it.
 * 
 * @author Jeffrey Tinnea
 * @version 1.0
 */
public class Grid {
    @JsonProperty("matrix")
    private List<List<Integer>> matrix;
    private transient int maxRows;
    private transient int maxColumns;
    
    public Grid() {
        this.matrix = null;
        this.maxRows = 0;
        this.maxColumns = 0;
    }

    public Grid(List<List<Integer>> matrix) {
        this.setMatrix(matrix);
    }

    public List<List<Integer>> getMatrix() {
        return this.matrix;
    }

    public void setMatrix(List<List<Integer>> matrix) {
        this.matrix = matrix;
        this.maxRows = matrix.size();
        this.maxColumns = matrix.get(0).size();
    }

    /**
     * Traverses the matrix recursively in a clockwise, spiral touching each node once.
     * @return an in-order string representing the traversal.
     * @throws IllegalStateException
     * @throws IllegalArgumentException
     */
    public String traverseMatrix() throws IllegalStateException, IllegalArgumentException {
        // Validate the matrix is a valid (rectangular)
        List<List<Integer>> matrix = this.getMatrix();
        if (matrix == null)
            throw new IllegalStateException("Cannot traverse a null matrix");
        if (maxColumns <= 0)
            throw new IllegalStateException("Matrix must have at least one value");
        for (List<Integer> row : matrix) {
            if (row.size() != maxColumns)
                throw new IllegalStateException("Supplied matrix must be rectangular");
        }

        // Matrix is ok, so kick off the recersive method to traverse it
        String orderedTraversal = traverseRecursively(new Point(0,0), TraversalDirection.RIGHT, "", new ArrayList<Point>());
        // Remove the leading ", "
        return orderedTraversal.substring(2);
    }

    private String traverseRecursively(Point currentNode, TraversalDirection currentDirection, String workingString, ArrayList<Point> hitNodes) 
    throws IllegalArgumentException {
        if (!this.pointIsValid(currentNode, hitNodes)) {
            return workingString;
        }

        // We are valid, so mark this node as visited and add it's value to the return string
        workingString += ", " + this.getMatrix().get(currentNode.x).get(currentNode.y).toString();
        hitNodes.add(currentNode);

        // Try to continue traversing in the same direction
        String nextNodeString = this.traverseRecursively(this.getNextNode(currentNode, currentDirection), currentDirection, workingString, hitNodes);
        
        // If traversing the node didn't alter the working string then it was a dead-end and we need to go a different direction
        if (nextNodeString.equals(workingString)) {
            TraversalDirection newDirection = this.changeDirection(currentDirection);

            // For the below, it doesn't matter if the new direction was valid or not. If it was valid, we want to roll up it's result.
            // If it wasn't valid, we are all done and will roll up the completed string.
            workingString = this.traverseRecursively(this.getNextNode(currentNode, newDirection), newDirection, workingString, hitNodes);;
        }
        // If it was a good node, we just roll up that result
        else {
            workingString = nextNodeString;
        }

        return workingString;
    }

    /**
     * Gets the next node coordinates assuming we continue traveling in the same direction.
     * @param currentNode Coordinates of the current node in the matrix.
     * @param currentDirection Direction of the traversal.
     * @return The coordinates of the next node. May not be a valid node.
     * @throws IllegalArgumentException
     */
    private Point getNextNode(Point currentNode, TraversalDirection currentDirection) throws IllegalArgumentException {
        switch (currentDirection) {
            case RIGHT:
                return new Point(currentNode.x, currentNode.y + 1);
            case DOWN:
                return new Point(currentNode.x + 1, currentNode.y);
            case LEFT:
                return new Point(currentNode.x, currentNode.y - 1);
            case UP:
                return new Point(currentNode.x - 1, currentNode.y);
            default:
                throw new IllegalArgumentException(String.format("Don't know how to process direction %s", currentDirection));
        }
    }

    /**
     * Determines if the current node is valid inside the matrix for traversal.
     * @param node Coordinates of the current node.
     * @param hitNodes List of nodes already visited.
     * @return True if the point is inside the matrix and hasn't already been visited.
     */
    private boolean pointIsValid(Point node, List<Point> hitNodes) {
        if (hitNodes.contains(node) ||
            node.x >= this.maxRows ||
            node.y >= this.maxColumns ||
            node.x < 0 ||
            node.y < 0) {
                return false;
            }

            return true;
    }

    /**
     * Change the direction to the next one needed for a clockwise, spiral traversal.
     * @param currentDirection The current direction being traveled that is blocked.
     * @return The next direction we need to travel.
     * @throws IllegalArgumentException
     */
    private TraversalDirection changeDirection(TraversalDirection currentDirection) throws IllegalArgumentException {
        switch (currentDirection) {
            case RIGHT:
                return TraversalDirection.DOWN;
            case DOWN:
                return TraversalDirection.LEFT;
            case LEFT:
                return TraversalDirection.UP;
            case UP:
                return TraversalDirection.RIGHT;
            default:
                throw new IllegalArgumentException(String.format("Don't know how to process direction %s", currentDirection));
        }
    }

    @Override
    public String toString() {
        Gson gson = new Gson();
        return gson.toJson(this);
    }

    private enum TraversalDirection {
        RIGHT,
        DOWN,
        LEFT,
        UP
    }
}