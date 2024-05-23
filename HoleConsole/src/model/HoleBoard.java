package model;

import boardifier.control.Logger;
import boardifier.model.GameStageModel;
import boardifier.model.ContainerElement;

import java.util.ArrayList;
import java.util.List;
import java.awt.*;

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
        boolean surrounded = lastCubePosition != null && isSurrounded(lastCubePosition.y, lastCubePosition.x);

        // Parcourez chaque cellule du tableau
        for (int i = 0; i < 7; i++) {
            for (int j = 0; j < 7; j++) {
                // Vérifiez si la cellule est vide
                if (isEmptyAt(i, j)) {
                    // Vérifiez si c'est le premier tour
                    if (isEmpty()) {
                        validCells.add(new Point(j, i));
                    } else {
                        // Vérifier la contiguïté à un cube de couleur opposée
                        if (adjacentToLastCube(i, j, number) || (surrounded && adjacentToAnyOppositeCube(i, j, number))) {
                            validCells.add(new Point(j, i));
                        }
                    }
                }
            }
        }
        System.out.println("Valid cells computed: " + validCells);
        return validCells;
    }

    // Méthode pour vérifier si une cellule est adjacente à un cube
    private boolean adjacentToLastCube(int row, int col, int number) {
        for (int i = -1; i <= 1; i++) {
            for (int j = -1; j <= 1; j++) {
                int newRow = row + i;
                int newCol = col + j;
                if (newRow >= 0 && newRow < 7 && newCol >= 0 && newCol < 7) {
                    Pawn adjacentCube = (Pawn) getElement(newRow, newCol);
                    if (adjacentCube != null && lastCubePosition != null && newRow == lastCubePosition.y && newCol == lastCubePosition.x) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    // Méthode pour vérifier si une cellule est adjacente à un cube de la couleur opposée
    private boolean adjacentToAnyOppositeCube(int row, int col, int number) {
        for (int i = -1; i <= 1; i++) {
            for (int j = -1; j <= 1; j++) {
                int newRow = row + i;
                int newCol = col + j;
                if (newRow >= 0 && newRow < 7 && newCol >= 0 && newCol < 7) {
                    Pawn adjacentCube = (Pawn) getElement(newRow, newCol);
                    if (adjacentCube != null && adjacentCube.getNumber() % 2 != number % 2) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    // Méthode pour vérifier si le dernier cube placé est entouré d'autres cubes
    private boolean isSurrounded(int row, int col) {
        for (int i = -1; i <= 1; i++) {
            for (int j = -1; j <= 1; j++) {
                if (i != 0 || j != 0) {
                    int newRow = row + i;
                    int newCol = col + j;
                    if (newRow >= 0 && newRow < 7 && newCol >= 0 && newCol < 7) {
                        if (isEmptyAt(newRow, newCol)) {
                            return false;
                        }
                    }
                }
            }
        }
        return true;
    }
}
