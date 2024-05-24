package control;

import boardifier.control.ActionFactory;
import boardifier.control.Controller;
import boardifier.control.Decider;
import boardifier.model.GameElement;
import boardifier.model.Model;
import boardifier.model.action.ActionList;
import model.HoleBoard;
import model.HoleStageModel;
import model.Pawn;

import java.awt.Point;
import java.util.Calendar;
import java.util.Random;

public class RandomDecider extends Decider {

    private static final Random random = new Random(Calendar.getInstance().getTimeInMillis());

    public RandomDecider(Model model, Controller control) {
        super(model, control);
    }

    @Override
    public ActionList decide() {
        HoleStageModel stage = (HoleStageModel) model.getGameStage();
        HoleBoard board = stage.getBoard();
        GameElement pawn = null;
        int rowDest = 0;
        int colDest = 0;

        HoleController holeController = (HoleController) control;
        int pawnIndex = holeController.getPawnIndex();

        while (true) {
            rowDest = random.nextInt(7);
            colDest = random.nextInt(7);

            if (!board.isEmptyAt(rowDest, colDest)) continue;

            if (pawnIndex >= 48) {
                System.out.println("Tous les pions ont été placés.");
                return new ActionList();
            }

            if (model.getIdPlayer() == 0) {
                pawn = stage.getBlackPawns()[pawnIndex];
            } else {
                pawn = stage.getRedPawns()[pawnIndex];
            }

            board.setValidCells(pawnIndex + 1);
            if (board.canReachCell(rowDest, colDest)) break;
        }

        holeController.incrementPawnIndex();

        // Log the number of pawns remaining
        int pawnsRemaining = holeController.getPawnIndex();
        System.out.println("Pions restants : " + pawnsRemaining);

        ActionList actions = ActionFactory.generatePutInContainer(model, pawn, "holeboard", rowDest, colDest);
        actions.setDoEndOfTurn(true);

        HoleBoard.lastCubePosition = new Point(colDest, rowDest);

        return actions;
    }
}
