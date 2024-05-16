package control;

import boardifier.control.ActionFactory;
import boardifier.control.ActionPlayer;
import boardifier.control.Controller;
import boardifier.model.GameElement;
import boardifier.model.ContainerElement;
import boardifier.model.Model;
import boardifier.model.Player;
import boardifier.model.action.ActionList;
import boardifier.view.View;
import model.HoleStageModel;
import model.Pawn;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class HoleController extends Controller {

    BufferedReader consoleIn;
    boolean firstPlayer;

    private int pawnIndex;

    private int blackPawnIndex;
    private int redPawnIndex;

    public HoleController(Model model, View view) {
        super(model, view);
        firstPlayer = true;
        pawnIndex = 0;

        blackPawnIndex = 0;
        redPawnIndex = 0;
    }

    /**
     * Defines what to do within the single stage of the single party
     * It is pretty straight forward to write :
     */
    public void stageLoop() {
        consoleIn = new BufferedReader(new InputStreamReader(System.in));
        update();
        while(! model.getGameStage().checkWinCondition()) {
            playTurn();
            endOfTurn();
            update();
        }
        endGame();
    }

    private void playTurn() {
        // get the new player
        Player p = model.getCurrentPlayer();
        if (p.getType() == Player.COMPUTER) {
            System.out.println("COMPUTER PLAYS");
            HoleDecider decider = new HoleDecider(model,this);
            ActionPlayer play = new ActionPlayer(model, this, decider, null);
            play.start();
        }
        else {
            boolean ok = false;
            while (!ok) {
                System.out.print(p.getName()+ " > ");
                try {
                    String line = consoleIn.readLine();
                    if (line.length() == 2) {
                        ok = analyseAndPlay(line);
                    }
                    if (!ok) {
                        System.out.println("incorrect instruction. retry !");
                    }
                }
                catch(IOException e) {}
            }
        }

    }

    public void endOfTurn() {

        model.setNextPlayer();
        // get the new player to display its name
        Player p = model.getCurrentPlayer();
        HoleStageModel stageModel = (HoleStageModel) model.getGameStage();
        stageModel.getPlayerName().setText(p.getName());
    }


    private boolean analyseAndPlay(String line) {
        HoleStageModel gameStage = (HoleStageModel) model.getGameStage();
        if ((pawnIndex < 0) || (pawnIndex > 24)) return false;

        int col = (int) (line.charAt(0) - 'A');
        int row = (int) (line.charAt(1) - '1');
        if ((row < 0) || (row > 6)) return false;
        if ((col < 0) || (col > 6)) return false;

        ContainerElement pot = null;
        if (model.getIdPlayer() == 0) {
            pot = gameStage.getBlackPot();
        } else {
            pot = gameStage.getRedPot();
        }
        if (pot.isEmptyAt(pawnIndex, 0)) return false;
        GameElement pawn = pot.getElement(pawnIndex, 0);

        if (hasEmptyCells()) {
            gameStage.getBoard().setValidCells(pawnIndex + 1);
            if (!gameStage.getBoard().canReachCell(row, col)) return false;
        } else {
            if (!isAdjacentToAnotherPawn(row, col)) return false;
        }

        ActionList actions = ActionFactory.generatePutInContainer(model, pawn, "holeboard", row, col);
        actions.setDoEndOfTurn(true);
        ActionPlayer play = new ActionPlayer(model, this, actions);
        play.start();

        pawnIndex++;

        System.out.println(pawnIndex + " " + pawn);

        return true;
    }
    private boolean isAdjacentToAnotherPawn(int row, int col) {
        HoleStageModel gameStage = (HoleStageModel) model.getGameStage();
        int[][] directions = {
                {-1, 0}, {1, 0}, {0, -1}, {0, 1}, // up, down, left, right
                {-1, -1}, {-1, 1}, {1, -1}, {1, 1} // diagonals
        };

        for (int[] dir : directions) {
            int newRow = row + dir[0];
            int newCol = col + dir[1];
            if (newRow >= 0 && newRow <= 6 && newCol >= 0 && newCol <= 6) {
                if (!gameStage.getBoard().isEmptyAt(newRow, newCol)) {
                    return true;
                }
            }
        }
        return false;
    }
    private boolean hasEmptyCells() {
        HoleStageModel gameStage = (HoleStageModel) model.getGameStage();
        for (int row = 0; row <= 6; row++) {
            for (int col = 0; col <= 6; col++) {
                if (gameStage.getBoard().isEmptyAt(row, col)) {
                    return true;
                }
            }
        }
        return false;
    }


}