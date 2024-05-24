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
import java.util.Random;

public class HoleDecider extends Decider {

    private static final Random random = new Random();
    public HoleDecider(Model model, Controller control) {
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
            rowDest = random.nextInt(7);
            colDest = random.nextInt(7);
            System.out.println("Premier mouvement, placement aléatoire à (" + rowDest + "," + colDest + ").");
        } else {
            // Obtenir les cellules valides selon la stratégie actuelle
            board.setValidCells(pawnIndex + 1);
            List<Point> validCells = board.computeValidCells(pawnIndex + 1);
            System.out.println("Cellules valides : " + validCells);

            // Vérifier toutes les cellules pour trouver un mouvement offensif
            Point offensiveMove = findOffensiveMove(board, validCells, isFirstPlayer ? 0 : 1);
            if (offensiveMove != null) {
                rowDest = offensiveMove.y;
                colDest = offensiveMove.x;
                System.out.println("Mouvement offensif trouvé à (" + rowDest + "," + colDest + ").");
            } else {
                // Vérifier toutes les cellules pour trouver un mouvement bloquant si aucun mouvement offensif n'est trouvé
                Point blockingMove = findBlockingMove(board, validCells, isFirstPlayer ? 1 : 0);
                if (blockingMove != null) {
                    rowDest = blockingMove.y;
                    colDest = blockingMove.x;
                    System.out.println("Mouvement bloquant trouvé à (" + rowDest + "," + colDest + ").");
                } else if (!validCells.isEmpty()) {
                    // Sélectionner la première cellule valide de la liste si aucun mouvement offensif ou bloquant n'est trouvé
                    Point chosenCell = validCells.get(0);
                    rowDest = chosenCell.y;
                    colDest = chosenCell.x;
                    System.out.println("Aucun mouvement offensif ou bloquant trouvé. Placement à (" + rowDest + "," + colDest + ").");
                }
            }
        }

        holeController.incrementPawnIndex();

        // Enregistrer le nombre de pions restants
        int pawnsRemaining = holeController.getPawnIndex();
        System.out.println("Pions restants : " + pawnsRemaining);

        ActionList actions = ActionFactory.generatePutInContainer(model, pawn, "holeboard", rowDest, colDest);
        actions.setDoEndOfTurn(true);

        HoleBoard.lastCubePosition = new Point(colDest, rowDest);

        return actions;
    }

    private Point findOffensiveMove(HoleBoard board, List<Point> validCells, int playerColor) {
        System.out.println("Recherche de mouvements offensifs...");
        // Vérifier les opportunités pour compléter une ligne de trois pions
        for (int i = 0; i < validCells.size(); i++) {
            Point cell = validCells.get(i);
            if (isOffensiveMove(board, cell, playerColor, 3)) {
                System.out.println("Mouvement offensif pour compléter une ligne de 3 trouvé à (" + cell.y + "," + cell.x + ").");
                return cell;
            }
        }
        // Si aucune ligne de trois n'est possible, vérifier les opportunités pour former une ligne de deux pions
        for (int i = 0; i < validCells.size(); i++) {
            Point cell = validCells.get(i);
            if (isOffensiveMove(board, cell, playerColor, 2)) {
                System.out.println("Mouvement offensif pour compléter une ligne de 2 trouvé à (" + cell.y + "," + cell.x + ").");
                return cell;
            }
        }
        return null;
    }

    private boolean isOffensiveMove(HoleBoard board, Point cell, int playerColor, int targetCount) {
        int row = cell.y;
        int col = cell.x;

        // Vérifier les lignes horizontales, verticales et diagonales pour les mouvements offensifs potentiels
        return isOffensiveDirection(board, row, col, playerColor, 1, 0, targetCount) || // Horizontal
                isOffensiveDirection(board, row, col, playerColor, 0, 1, targetCount) || // Vertical
                isOffensiveDirection(board, row, col, playerColor, 1, 1, targetCount) || // Diagonal bas-droite
                isOffensiveDirection(board, row, col, playerColor, 1, -1, targetCount);  // Diagonal bas-gauche
    }

    private boolean isOffensiveDirection(HoleBoard board, int row, int col, int playerColor, int dRow, int dCol, int targetCount) {
        int count = 0;

        // Vérifier la direction avant
        for (int i = 1; i <= targetCount; i++) {
            int newRow = row + i * dRow;
            int newCol = col + i * dCol;
            if (newRow < 0 || newRow >= 7 || newCol < 0 || newCol >= 7) break;
            Pawn pawn = (Pawn) board.getElement(newRow, newCol);
            if (pawn != null && pawn.getColor() == playerColor) {
                count++;
            } else {
                break;
            }
        }

        // Vérifier la direction arrière
        for (int i = 1; i <= targetCount; i++) {
            int newRow = row - i * dRow;
            int newCol = col - i * dCol;
            if (newRow < 0 || newRow >= 7 || newCol < 0 || newCol >= 7) break;
            Pawn pawn = (Pawn) board.getElement(newRow, newCol);
            if (pawn != null && pawn.getColor() == playerColor) {
                count++;
            } else {
                break;
            }
        }

        return count >= targetCount - 1; // Si suffisamment de pions du joueur sont alignés, c'est un mouvement offensif
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
