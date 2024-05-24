package control;

import boardifier.control.ActionFactory;
import boardifier.control.ActionPlayer;
import boardifier.control.Controller;
import boardifier.model.GameElement;
import boardifier.model.Model;
import boardifier.model.Player;
import boardifier.model.action.ActionList;
import boardifier.view.ElementLook;
import boardifier.view.View;
import model.HoleBoard;
import model.HoleStageModel;
import view.PawnLook;

import java.awt.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class HoleController extends Controller {

    BufferedReader consoleIn;
    boolean firstPlayer;
    private int pawnIndex;
    private String vainqueur;

    public HoleController(Model model, View view) {
        super(model, view);
        firstPlayer = true;
        pawnIndex = 0;
    }

    public int getPawnIndex() {
        return pawnIndex;
    }

    public void incrementPawnIndex() {
        pawnIndex++;
    }

    /**
     * Defines what to do within the single stage of the single party
     * It is pretty straight forward to write :
     */
    public void stageLoop() {
        consoleIn = new BufferedReader(new InputStreamReader(System.in));
        update();
        while(!model.isEndStage()) {
            playTurn();
            endOfTurn();
            update();
            if (model.getGameStage().checkWinCondition()) {
                if(model.getIdPlayer() == 0){
                    vainqueur = "VERT";
                }else{
                    vainqueur = "VIOLET";
                }
                System.out.println("\nLe joueur "+vainqueur+" a gagné la partie !!\n");
                control.HoleConsole.gameChoise();
            }
            HoleStageModel stageModel = (HoleStageModel) model.getGameStage();
            if (!stageModel.getBoard().canFormFourInARow(model.getIdPlayer())) {
                System.out.println("Il n'est plus possible de faire un alignement de 4 cubes, fin de la partie. Aucun gagnant; partie nulle.");
                control.HoleConsole.gameChoise();
            }
        }
        endGame();
    }

    private void playTurn() {
        // get the new player
        Player p = model.getCurrentPlayer();
        if (p.getType() == Player.COMPUTER) {
            System.out.println("COMPUTER PLAYS");
            HoleDecider decider = new HoleDecider(model, this);
            ActionPlayer play = new ActionPlayer(model, this, decider, null);
            play.start();
        } else {
            boolean ok = false;
            while (!ok) {
                System.out.print(p.getName() + " > ");
                try {
                    String line = consoleIn.readLine();
                    if (line.length() == 2) {
                        ok = analyseAndPlay(line);
                    }
                    if (!ok) {
                        System.out.println("incorrect input. Try again.");
                    }
                } catch (IOException e) {}
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

        // get the coords in the board
        int col = (int) (line.charAt(0) - 'A');
        int row = (int) (line.charAt(1) - '1');
        // check coords validity
        if ((row < 0) || (row > 6)) return false;
        if ((col < 0) || (col > 6)) return false;

        // Check if the selected position on the board is empty
        if (!gameStage.getBoard().isEmptyAt(row, col)) return false;

        // Ensure the pawnIndex does not exceed the array bounds
        if (pawnIndex >=48) {
            System.out.println("Tous les pions ont été placés.");
            return false;
        }

        // Get the current player's pawn
        GameElement pawn;
        if (model.getIdPlayer() == 0) {
            pawn = gameStage.getBlackPawns()[pawnIndex];
        } else {
            pawn = gameStage.getRedPawns()[pawnIndex];
        }

        // Compute valid cells for the chosen pawn
        gameStage.getBoard().setValidCells(pawnIndex + 1);
        if (!gameStage.getBoard().canReachCell(row, col)) return false;

        // Create the look for the pawn if it doesn't exist
        ElementLook look = view.getElementLook(pawn);
        if (look == null) {
            look = new PawnLook(pawn);
            //view.addElementLook(look);
        }

        ActionList actions = ActionFactory.generatePutInContainer(model, pawn, "holeboard", row, col);
        actions.setDoEndOfTurn(true); // after playing this action list, it will be the end of turn for current player.
        ActionPlayer play = new ActionPlayer(model, this, actions);
        play.start();

        incrementPawnIndex();

        HoleBoard.lastCubePosition = new Point(col, row);

        System.out.println(pawnIndex + " " + pawn);
        return true;
    }
}
