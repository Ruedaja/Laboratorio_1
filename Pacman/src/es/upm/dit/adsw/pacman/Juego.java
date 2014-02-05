package es.upm.dit.adsw.pacman;

/**
 * Arranca el juego.
 *
 * @author jose a. manas
 * @version 2.2.2014
 */
public class Juego {
    public static final int N = 15;

    /**
     * Crea el terreno y el jugador.
     * Crea la interfaz de usuario y le cede el control.
     *
     * @param args no se usa.
     */
    public static void main(String[] args) {
        Terreno terreno = new Terreno(N);
        terreno.creaParedes();
        terreno.setObjetivo(N - 1, N - 1);

        Jugador jugador = new Jugador();
        terreno.put(0, 0, jugador);

        GUI gui = new GUI(terreno, jugador);
    }
}
