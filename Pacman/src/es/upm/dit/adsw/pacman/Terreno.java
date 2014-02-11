package es.upm.dit.adsw.pacman;

import java.util.Random;

/**
 * El terreno de juego.
 *
 * @author jose a. manas
 * @version 10.2.2014
 */
public class Terreno {
    private final Casilla[][] casillas;

    /**
     * Constructor.
     *
     * @param n numero de casillas en horizontal (X) y vertical (Y).
     */
    public Terreno(int n) {
        casillas = new Casilla[n][n];

        for (int x = 0; x < n; x++) {
            for (int y = 0; y < n; y++) {
                casillas[x][y] = new Casilla(x, y);
            }
        }
    }

    /**
     * Organiza el laberinto.
     */
    public void ponParedes() {
        int n = getN();

        // borramos las paredes
        for (int x = 0; x < n; x++) {
            for (int y = 0; y < n; y++) {
                Casilla casilla = getCasilla(x, y);
                casilla.quitaParedes();
            }
        }

        Direccion[] direcciones = Direccion.values();
        Random random = new Random();
        for (int i = 0; i < 200; i++) {
            int x = random.nextInt(n);
            int y = random.nextInt(n);
            int d = random.nextInt(direcciones.length);
            Direccion direccion = direcciones[d];
            ponPared(getCasilla(x, y), direccion);
        }
    }

    /**
     * Getter.
     *
     * @return dimension horizontal o vertical del terreno.
     */
    public int getN() {
        return casillas.length;
    }

    /**
     * Casilla en una cierta posicion.
     *
     * @param x posicion horizontal (0..N-1).
     * @param y posicion vertical (0..N-1).
     * @return casilla en esas coordenadas.
     * @throws ArrayIndexOutOfBoundsException si las coordenadas estan fuera del tablero.
     */
    public Casilla getCasilla(int x, int y) {
        return casillas[x][y];
    }

    /**
     * Intenta trasladar el movil en la direccion indicada,
     * devolviendo la casilla en la que acaba el movil.
     * Si no puede moverse, devuelve la misma casilla origen.
     * Si en el destino hay otro movil, se lo 'come' = muere().
     *
     * @param movil    el que queremos mover.
     * @param direccion direccion en la que nos queremos desplazar.
     * @return la casilla a donde se mueve el movil, si es posible moverse.
     */
    public Casilla move(Movil movil, Direccion direccion) {
        Casilla origen = movil.getCasilla();
        try {
            if (movil.puedoMoverme(direccion) != 0)
                return origen;
            Casilla destino = getCasilla(origen.getX(), origen.getY(), direccion);
            if (destino == null)
                return origen;

            Movil m2 = destino.getMovil();
            if (m2 != null)
                m2.muere();
            origen.setMovil(null);
            destino.setMovil(movil);
            movil.setCasilla(destino);
            return destino;
        } catch (Exception e) {
            return origen;
        }
    }

    /**
     * Casilla anexa en una cierta direccion.
     *
     * @param x         posicion horizontal (0..N-1).
     * @param y         posicion vertical (0..N-1).
     * @param direccion direccion en la que miramos.
     * @return casilla anexa en la direccion indicada o null si no existe, por ejemplo al borde del terreno.
     */
    private Casilla getCasilla(int x, int y, Direccion direccion) {
        try {
            switch (direccion) {
                case NORTE:
                    return getCasilla(x, y + 1);
                case SUR:
                    return getCasilla(x, y - 1);
                case ESTE:
                    return getCasilla(x + 1, y);
                case OESTE:
                    return getCasilla(x - 1, y);
            }
        } catch (Exception ignored) {
        }
        return null;
    }

    /**
     * Plantamos una pared en el terreno.
     * Las paredes separan casillas, de forma que hay 2 casillas afectadas.
     *
     * @param casilla   una de las casillas en la que colocar la pared.
     * @param direccion en que lado de la casilla se coloca.
     */
    public void ponPared(Casilla casilla, Direccion direccion) {
        casilla.ponPared(direccion);
        Casilla anexa = getCasilla(casilla.getX(), casilla.getY(), direccion);
        if (anexa != null)
            anexa.ponPared(direccion.opuesta());
    }

    /**
     * Quitamos una pared del terreno.
     * Las paredes separan casillas, de forma que hay 2 casillas afectadas.
     *
     * @param casilla   una de las casillas de la que retirar la pared.
     * @param direccion de que lado de la casilla se retira.
     */
    public void quitaPared(Casilla casilla, Direccion direccion) {
        casilla.quitaPared(direccion);
        Casilla anexa = getCasilla(casilla.getX(), casilla.getY(), direccion);
        if (anexa != null)
            anexa.quitaPared(direccion.opuesta());
    }

    /**
     * Pregunta si mirando desde una casilla en una cierta direccion nos topamos con una pared.
     *
     * @param casilla   casilla desde la que miramos.
     * @param direccion en que direccion miramos.
     * @return cierto si hay pared.
     */
    public boolean hayPared(Casilla casilla, Direccion direccion) {
        return casilla.hayPared(direccion);
    }

    /**
     * Coloca un movil sobre el terreno.
     * Indirectamente, hay una casilla afectada.
     *
     * @param x     posicion horizontal (0..N-1).
     * @param y     posicion vertical (0..N-1).
     * @param movil lo que queremos colocar.
     * @return true si se puede y queda colocado; false si no es posible.
     */
    public boolean put(int x, int y, Movil movil) {
        try {
            Casilla casilla = getCasilla(x, y);
            if (casilla.getMovil() != null)
                return false;
            casilla.setMovil(movil);
            movil.setCasilla(casilla);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Marca una posicion como objetivo para que el jugador gane.
     *
     * @param x posicion horizontal (0..N-1).
     * @param y posicion vertical (0..N-1).
     * @return true si es posible marcar la casilla como objetivo; false si no es posible.
     */
    public boolean setObjetivo(int x, int y) {
        try {
            Casilla casilla= getCasilla(x, y);
            casilla.setObjetivo(true);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}
