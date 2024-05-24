package model;

import boardifier.model.GameStageModel;
import boardifier.model.ContainerElement;

/**
 * Hole pot for pawns represent the element where pawns are stored at the beginning of the party.
 * Thus, a simple ContainerElement with 48 rows and 1 column is needed.
 */
public class HolePawnPot extends ContainerElement {
    public HolePawnPot(int x, int y, GameStageModel gameStageModel) {
        // call the super-constructor to create a 48x1 grid, named "pawnpot", and in x,y in space
        super("pawnpot", x, y, 48, 1, gameStageModel);
    }

    public GameStageModel getGameStageModel() {
        return this.gameStageModel;
    }
}
