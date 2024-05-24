package model;

import boardifier.model.GameStageModel;
import boardifier.model.Model;
import boardifier.model.StageElementsFactory;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class HolePawnPotTest {

    @Test
    void testHolePawnPotCreation() {
        GameStageModel gameStageModel = createGameStageModel();
        HolePawnPot holePawnPot = new HolePawnPot(0, 0, gameStageModel);

        // Vérifiez le nom de l'élément
        Assertions.assertEquals("pawnpot", holePawnPot.getName());

        // Vérifiez les dimensions
        Assertions.assertEquals(48, holePawnPot.getNbRows());
        Assertions.assertEquals(1, holePawnPot.getNbCols());

        // Vérifiez la position
        Assertions.assertEquals(0, holePawnPot.getX());
        Assertions.assertEquals(0, holePawnPot.getY());

        // Vérifiez que le GameStageModel est correctement associé
        Assertions.assertEquals(gameStageModel, holePawnPot.getGameStageModel());
    }

    // Méthode pour créer un GameStageModel
    private GameStageModel createGameStageModel() {
        Model model = new Model();
        return new GameStageModel("testStage", model) {
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
}
