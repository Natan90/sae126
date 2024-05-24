package model;

import boardifier.model.Model;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class PawnTest {
    private Pawn blackPawn;
    private Pawn redPawn;

    @Before
    public void setUp() {
        // Créer des pions noirs et rouges avec des numéros et des couleurs spécifiques
        blackPawn = new Pawn(1, Pawn.PAWN_BLACK, new HoleStageModel("Player1", new Model()));
        redPawn = new Pawn(2, Pawn.PAWN_RED, new HoleStageModel("Player1", new Model()));
    }

    @Test
    public void testPawnNumber() {
        // Vérifier si les numéros des pions sont correctement initialisés
        assertEquals(1, blackPawn.getNumber());
        assertEquals(2, redPawn.getNumber());
    }

    @Test
    public void testPawnColor() {
        // Vérifier si les couleurs des pions sont correctement initialisées
        assertEquals(Pawn.PAWN_BLACK, blackPawn.getColor());
        assertEquals(Pawn.PAWN_RED, redPawn.getColor());
    }
}
