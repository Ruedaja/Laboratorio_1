package es.upm.dit.adsw.pacman;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class TestTerreno {
    public static final int N = 10;

    private Terreno terreno;
    private Jugador jugador;

    @Before
    public void setup() {
        terreno = new Terreno(N);
        // sin paredes
        jugador = new Jugador();
    }

    @Test
    public void testGetCasilla01() {
        Casilla casilla = terreno.getCasilla(2, 3);
        assertEquals(2, casilla.getX());
        assertEquals(3, casilla.getY());
    }

    @Test
    public void testGetCasilla02() {
        Casilla casilla = terreno.getCasilla(3, N - 1);
        assertEquals(3, casilla.getX());
        assertEquals(N - 1, casilla.getY());
    }

    @Test(expected = ArrayIndexOutOfBoundsException.class)
    public void testGetCasilla03() {
        Casilla casilla = terreno.getCasilla(0, N);
    }

}
