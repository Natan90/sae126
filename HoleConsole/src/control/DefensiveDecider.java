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
import java.util.List;

import static control.HoleConsole.gameChoise;

public class DefensiveDecider extends Decider {

    public DefensiveDecider(Model model, Controller control) {
        super(model, control);
    }

    @Override
    public ActionList decide() {
        HoleStageModel stage = (HoleStageModel) model.getGameStage();
        HoleBoard board = stage.getBoard();
        GameElement pawn;
        int rowDest = 0;
        int colDest = 0;

        HoleController holeController = (HoleController) control;
        int pawnIndex = holeController.getPawnIndex();

        // Déterminer le joueur actuel
        boolean isFirstPlayer = (model.getIdPlayer() == 0);
        System.out.println("Joueur " + (isFirstPlayer ? "1 (Noir)" : "2 (Rouge)") + " joue.");

        // Obtenir le pion correspondant au joueur actuel
        if (isFirstPlayer) {
            pawn = stage.getBlackPawns()[pawnIndex];
        } else {
            pawn = stage.getRedPawns()[pawnIndex];
        }

        // Si c'est le premier tour, placer le pion au centre
        if (pawnIndex == 0) {
            rowDest = 3;
            colDest = 3;
            System.out.println("Premier mouvement, placement au centre (3,3).");
        } else {
            // Obtenir les cellules valides selon la stratégie actuelle
            board.setValidCells(pawnIndex + 1);
            List<Point> validCells = board.computeValidCells(pawnIndex + 1);
            System.out.println("Cellules valides : " + validCells);

            // Vérifier toutes les cellules pour trouver un mouvement bloquant
            Point blockingMove = findBlockingMove(board, validCells, isFirstPlayer ? 1 : 0);
            if (blockingMove != null) {
                rowDest = blockingMove.y;
                colDest = blockingMove.x;
                System.out.println("Mouvement bloquant trouvé à (" + rowDest + "," + colDest + ").");
            } else if (!validCells.isEmpty()) {
                // Sélectionner la première cellule valide de la liste si aucun mouvement bloquant n'est trouvé
                Point chosenCell = validCells.get(0);
                rowDest = chosenCell.y;
                colDest = chosenCell.x;
                System.out.println("Aucun mouvement bloquant trouvé. Placement à (" + rowDest + "," + colDest + ").");
            }
        }

        // Incrémenter l'indice de pion
        holeController.incrementPawnIndex();

        // Enregistrer le nombre de pions restants
        int pawnsRemaining = holeController.getPawnIndex();
        System.out.println("Pions restants : " + pawnsRemaining);

        // Vérifier si tous les pions ont été utilisés
        if (pawnsRemaining >= 48) {
            System.out.println("Tous les pions ont été placés. La partie est terminée.");
            ActionList actions = ActionFactory.generatePutInContainer(model, pawn, "holeboard", rowDest, colDest);
            actions.setDoEndOfTurn(true);
            gameChoise();
        }

        ActionList actions = ActionFactory.generatePutInContainer(model, pawn, "holeboard", rowDest, colDest);
        actions.setDoEndOfTurn(true);

        HoleBoard.lastCubePosition = new Point(colDest, rowDest);

        return actions;
    }

    private Point findBlockingMove(HoleBoard board, List<Point> validCells, int opponentColor) {
        System.out.println("Recherche de mouvements bloquants...");
        for (int i = 0; i < validCells.size(); i++) {
            Point cell = validCells.get(i);
            if (isBlockingMove(board, cell, opponentColor)) {
                System.out.println("Mouvement bloquant possible trouvé à (" + cell.y + "," + cell.x + ").");
                return cell;
            }
        }
        return null;
    }

    private boolean isBlockingMove(HoleBoard board, Point cell, int opponentColor) {
        int row = cell.y;
        int col = cell.x;

        // Vérifier les lignes horizontales, verticales et diagonales pour les mouvements bloquants potentiels
        return isBlockingDirection(board, row, col, opponentColor, 1, 0) || // Horizontal
                isBlockingDirection(board, row, col, opponentColor, 0, 1) || // Vertical
                isBlockingDirection(board, row, col, opponentColor, 1, 1) || // Diagonal bas-droite
                isBlockingDirection(board, row, col, opponentColor, 1, -1);  // Diagonal bas-gauche
    }

    private boolean isBlockingDirection(HoleBoard board, int row, int col, int opponentColor, int dRow, int dCol) {
        int count = 0;

        // Vérifier la direction avant
        for (int i = 1; i < 4; i++) {
            int newRow = row + i * dRow;
            int newCol = col + i * dCol;
            if (newRow < 0 || newRow >= 7 || newCol < 0 || newCol >= 7) break;
            Pawn pawn = (Pawn) board.getElement(newRow, newCol);
            if (pawn != null && pawn.getColor() == opponentColor) {
                count++;
            } else {
                break;
            }
        }

        // Vérifier la direction arrière
        for (int i = 1; i < 4; i++) {
            int newRow = row - i * dRow;
            int newCol = col - i * dCol;
            if (newRow < 0 || newRow >= 7 || newCol < 0 || newCol >= 7) break;
            Pawn pawn = (Pawn) board.getElement(newRow, newCol);
            if (pawn != null && pawn.getColor() == opponentColor) {
                count++;
            } else {
                break;
            }
        }

        return count >= 3; // Si trois ou plus de pions de l'adversaire sont alignés, c'est un mouvement bloquant
    }
}
