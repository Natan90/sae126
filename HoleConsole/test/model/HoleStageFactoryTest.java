package model;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.junit.Before;
import org.junit.Test;

import boardifier.model.GameStageModel;
import boardifier.model.Model;
import boardifier.model.Player;
import boardifier.model.TextElement;

public class HoleStageFactoryTest {
    private HoleStageModel gameStageModel;

    @Before
    public void setUp() {
        Model model = new Model();
        model.addHumanPlayer("Player 1");  // Ajouter un joueur humain pour le test
        gameStageModel = new HoleStageModel("testStage", model);
    }

    @Test
    public void testSetup() {
        HoleStageFactory stageFactory = new HoleStageFactory(gameStageModel);
        stageFactory.setup();

        // Verify if the player name text element is created and assigned correctly
        TextElement playerName = gameStageModel.getPlayerName();
        assertNotNull(playerName);
        assertEquals("Player 1", playerName.getText());

        // Verify if the board is created and assigned correctly
        HoleBoard board = gameStageModel.getBoard();
        assertNotNull(board);

        // Verify if black pawns are created and assigned correctly
        Pawn[] blackPawns = gameStageModel.getBlackPawns();
        assertNotNull(blackPawns);
        assertEquals(48, blackPawns.length);

        // Verify if red pawns are created and assigned correctly
        Pawn[] redPawns = gameStageModel.getRedPawns();
        assertNotNull(redPawns);
        assertEquals(48, redPawns.length);
    }
}
