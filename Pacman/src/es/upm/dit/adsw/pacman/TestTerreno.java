package es.upm.dit.adsw.pacman;

import org.junit.Before;
import org.junit.Test;

import static junit.framework.Assert.*;

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
    public void testGetCasilla012() {
        Casilla casilla = terreno.getCasilla(3, N - 1);
        assertEquals(3, casilla.getX());
        assertEquals(N - 1, casilla.getY());
    }

    @Test(expected = ArrayIndexOutOfBoundsException.class)
    public void testGetCasilla03() {
        Casilla casilla = terreno.getCasilla(0, N);
    }

    @Test
    public void testPut01() {
        assertTrue(terreno.put(2, 3, jugador));
        assertEquals(jugador, terreno.getCasilla(2, 3).getMovil());
    }

    @Test
    public void testPut02() {
        terreno.put(0, N - 1, jugador);
        assertEquals(jugador, terreno.getCasilla(0, N - 1).getMovil());
    }

    @Test
    public void testPut03() {
        assertFalse(terreno.put(0, N, jugador));
    }

}