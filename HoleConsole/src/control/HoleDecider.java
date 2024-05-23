package control;

import boardifier.control.ActionFactory;
import boardifier.control.Controller;
import boardifier.control.Decider;
import boardifier.model.GameElement;
import boardifier.model.Model;
import boardifier.model.action.ActionList;
import model.HoleBoard;
import model.HolePawnPot;
import model.HoleStageModel;
import model.Pawn;

import java.awt.*;
import java.util.Calendar;
import java.util.List;
import java.util.Random;

public class HoleDecider extends Decider {

    private static final Random loto = new Random(Calendar.getInstance().getTimeInMillis());

    public HoleDecider(Model model, Controller control) {
        super(model, control);
    }

    @Override
    public ActionList decide() {
        // do a cast get a variable of the real type to get access to the attributes of HoleStageModel
        HoleStageModel stage = (HoleStageModel)model.getGameStage();
        HoleBoard board = stage.getBoard(); // get the board
        HolePawnPot pot = null; // the pot where to take a pawn
        GameElement pawn = null; // the pawn that is moved
        int rowDest = 0; // the dest. row in board
        int colDest = 0; // the dest. col in board

        System.out.println("Deciding move for player: " + model.getIdPlayer());

        if (model.getIdPlayer() == Pawn.PAWN_BLACK) {
            pot = stage.getBlackPot();
        } else {
            pot = stage.getRedPot();
        }

        if (pot == null) {
            System.err.println("Error: Pot is null for player: " + model.getIdPlayer());
        } else {
            System.out.println("Using pot: " + (model.getIdPlayer() == Pawn.PAWN_BLACK ? "Black" : "Red"));
        }

        for (int row = 0; row < 7; row++) {
            for (int col = 0; col < 7; col++) {
                Pawn p = (Pawn) pot.getElement(row, col);
                if (p != null) {
                    System.out.println("Found pawn at position: (" + row + ", " + col + ")");
                    List<Point> valid = board.computeValidCells(p.getNumber());
                    System.out.println("Valid cells for pawn " + p.getNumber() + ": " + valid);

                    if (valid.size() != 0) {
                        int id = loto.nextInt(valid.size());
                        pawn = p;
                        rowDest = valid.get(id).y;
                        colDest = valid.get(id).x;
                        System.out.println("Chosen cell for pawn " + p.getNumber() + ": (" + rowDest + ", " + colDest + ")");
                        break;
                    }
                } else {
                    System.out.println("No pawn at position: (" + row + ", " + col + ")");
                }
            }
        }

        if (pawn == null) {
            System.err.println("Error: No pawn found to move for player: " + model.getIdPlayer());
        }

        ActionList actions = ActionFactory.generatePutInContainer(model, pawn, "holeboard", rowDest, colDest);
        actions.setDoEndOfTurn(true); // after playing this action list, it will be the end of turn for current player.

        System.out.println("Generated actions: " + actions);

        return actions;
    }
}
