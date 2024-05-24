package model;

import boardifier.model.GameStageModel;
import boardifier.model.Model;
import boardifier.model.StageElementsFactory;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.awt.*;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class HoleBoardTest {

    @Test
    void computeValidCells_FirstTurn_AllCellsValid() {
        GameStageModel gameStageModel = createGameStageModel();
        HoleBoard holeBoard = new HoleBoard(0, 0, gameStageModel);

        HoleBoard.lastCubePosition = null;

        List<Point> validCells = holeBoard.computeValidCells(0);
        assertEquals(49, validCells.size());
    }

    @Test
    void computeValidCells_AdjacentToLastCube_CorrectCellsReturned() {
        GameStageModel gameStageModel = createGameStageModel();
        HoleBoard holeBoard = new HoleBoard(0, 0, gameStageModel);

        HoleBoard.lastCubePosition = new Point(3, 3);
        holeBoard.placeCube(new Point(3, 3), 1, 0);

        List<Point> validCells = holeBoard.computeValidCells(0);

        for (Point cell : validCells) {
            Assertions.assertTrue(isAdjacentToLastCube(cell, HoleBoard.lastCubePosition));
        }
    }

    // Méthode pour créer un GameStageModel
    private GameStageModel createGameStageModel() {
        Model model = new Model();
        return new GameStageModel("someString", model) {
            @Override
            public StageElementsFactory getDefaultElementFactory() {
                return null;
            }

            @Override
            public boolean checkWinCondition() {
                return false;
            }
        };
    }

    // Méthode utilitaire pour vérifier si une cellule est adjacente à une position spécifique
    private boolean isAdjacentToLastCube(Point cell, Point lastCubePosition) {
        return Math.abs(cell.x - lastCubePosition.x) <= 1 && Math.abs(cell.y - lastCubePosition.y) <= 1;
    }
}
