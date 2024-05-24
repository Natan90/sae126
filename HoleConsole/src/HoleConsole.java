package control;

import java.util.Scanner;
import boardifier.control.Logger;
import boardifier.model.GameException;
import boardifier.view.View;
import boardifier.control.StageFactory;
import boardifier.model.Model;

public class HoleConsole {

    public static void main(String[] args) {
        gameChoise();
    }

    public static void gameChoise() {
        Logger.setLevel(Logger.LOGGER_TRACE);
        Logger.setVerbosity(Logger.VERBOSE_HIGH);

        Scanner scanner = new Scanner(System.in);
        int mode = -1;

        while (mode < 0 || mode > 4) {
            System.out.println("Veuillez choisir un mode de jeu:");
            System.out.println("0: Deux joueurs humains");
            System.out.println("1: Un joueur humain contre un ordinateur");
            System.out.println("2: Deux ordinateurs");
            System.out.println("3: HoleDecider contre RandomDecider");
            System.out.println("4: Quitter");
            System.out.print("Entrez le numéro du mode: ");
            try {
                mode = Integer.parseInt(scanner.nextLine());
                if (mode < 0 || mode > 4) {
                    System.out.println("Mode invalide. Veuillez réessayer.");
                }
            } catch (NumberFormatException e) {
                System.out.println("Entrée invalide. Veuillez entrer un numéro.");
            }
        }

        Model model = new Model();
        if (mode == 0) {
            model.addHumanPlayer("player1");
            model.addHumanPlayer("player2");
        } else if (mode == 1) {
            model.addHumanPlayer("player");
            model.addComputerPlayer("computer");
        } else if (mode == 2) {
            model.addComputerPlayer("computer1");
            model.addComputerPlayer("computer2");
        } else if (mode == 3) {
            model.addComputerPlayer("HoleDecider");
            model.addComputerPlayer("RandomDecider");
        } else if (mode == 4) {
            System.out.println("Vous quittez le jeu.");
            System.exit(0);
        }

        StageFactory.registerModelAndView("hole", "model.HoleStageModel", "view.HoleStageView");
        View holeView = new View(model);
        HoleController control = new HoleController(model, holeView);
        control.setFirstStageName("hole");
        try {
            control.startGame();
            control.stageLoop();
        } catch (GameException e) {
            System.out.println("Cannot start the game. Abort");
        }

        scanner.close();
    }
}
