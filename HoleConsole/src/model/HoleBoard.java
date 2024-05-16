package model;

import boardifier.control.Logger;
import boardifier.model.GameStageModel;
import boardifier.model.ContainerElement;

import java.util.ArrayList;
import java.util.List;
import java.awt.*;
import java.util.Objects;

public class HoleBoard extends ContainerElement {

    public static Point lastCubePosition = null;
    public HoleBoard(int x, int y, GameStageModel gameStageModel) {

        super("holeboard", x, y, 7, 7, gameStageModel);

    }

    public void setValidCells(int number) {
        Logger.debug("called", this);
        resetReachableCells(false);
        List<Point> valid = computeValidCells(number);
        for (Point p : valid) {
            reachableCells[p.y][p.x] = true;
        }
    }

    public List<Point> computeValidCells(int number) {
        List<Point> validCells = new ArrayList<>();
        // Loop through each cell on the board
        for (int i = 0; i < 7; i++) {
            for (int j = 0; j < 7; j++) {
                // Check if the cell is empty
                if (isEmptyAt(i, j)) {
                    // Check if it's the first turn (placement is totally free)
                    if (isEmpty()) {
                        validCells.add(new Point(j, i));
                    } else {
                        // Check adjacency to a cube of the opposite color
                        if (adjacentToLastCube(i, j, number)) {
                            validCells.add(new Point(j, i));
                        }
                    }
                }
            }
        }
        return validCells;
    }

    // Method to check if a cell is adjacent to a cube of the opposite color
    private boolean adjacentToLastCube(int row, int col, int number) {

        // Check adjacent cells
        for (int i = -1; i <= 1; i++) {
            for (int j = -1; j <= 1; j++) {
                int newRow = row + i;
                int newCol = col + j;
                // Check if the new position is within the board boundaries
                if (newRow >= 0 && newRow < 7 && newCol >= 0 && newCol < 7) {
                    // Check if the adjacent cell contains a cube of the opposite color
                    Pawn adjacentCube = (Pawn) getElement(newRow, newCol);

                    //if (adjacentCube != null && adjacentCube.getNumber() % 2 != number % 2) {
                    if (adjacentCube != null && lastCubePosition != null && newRow == lastCubePosition.y && newCol == lastCubePosition.x) {
                        System.out.println(lastCubePosition);
                        return true;
                    }
                }
            }
        }
        return false;
    }
}

