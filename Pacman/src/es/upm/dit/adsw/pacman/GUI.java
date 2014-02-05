package es.upm.dit.adsw.pacman;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.AffineTransform;

/**
 * Interfaz grafica.
 * Presenta el estado del juego y captura la interaccon del usuario.
 *
 * @author Jose A. Manas
 * @version 3.2.2014
 */
public class GUI
        extends JPanel {
    /**
     * Nombre del juego.
     */
    public static final String TITULO = "Pacman (3.2.2014)";

    /**
     * Espacio entre la zona de juego y el borde de la ventana.
     */
    private static final int MARGEN = 10;
    /**
     * Ancho de la zona de juego.
     */
    private static final int ANCHO = 500;
    /**
     * Tamano de una casilla: pixels.
     */
    private int lado1;

    private final Terreno terreno;
    private final Jugador jugador;

    private Casilla celdaSeleccionada;

    public GUI(Terreno terreno, Jugador jugador) {
        this.terreno = terreno;
        this.jugador = jugador;

        lado1 = (ANCHO - 2 * MARGEN) / terreno.getN();

        JFrame frame = new JFrame(TITULO);
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setPreferredSize(new Dimension(ANCHO, ANCHO));
        frame.getContentPane().add(this, BorderLayout.CENTER);
        setFocusable(true);

        addKeyListener(new MyKeyListener());
        addMouseListener(new MyMouseListener());

        frame.pack();
        frame.setVisible(true);
        requestFocusInWindow();
    }

    /**
     * Le dice al thread de swing que deberia refrescar la pantalla.
     * Swing lo hara cuando le parezca bien.
     */
    public void pintame() {
        repaint();
    }

    /**
     * Llamada por java para pintarse en la pantalla.
     *
     * @param g sistema grafico 2D para dibujarse.
     */
    public void paint(Graphics g) {
        g.setColor(Color.WHITE);
        g.fillRect(0, 0, getWidth(), getHeight());

        g.setColor(Color.LIGHT_GRAY);
        int nwx = MARGEN;
        int nwy = MARGEN;
        int lado = terreno.getN();

        // pinta las celdas con su contenido (movil)
        for (int x = 0; x < lado; x++) {
            for (int y = 0; y < lado; y++) {
                pintaCasilla(g, x, y);
            }
        }

        // pinta el marco
        g.setColor(Color.BLACK);
        g.drawLine(nwx - 1, nwy - 1, nwx - 1, nwy + lado * lado1 + 1);
        g.drawLine(nwx + lado * lado1 + 1, nwy - 1, nwx + lado * lado1 + 1, nwy + lado * lado1 + 1);
        g.drawLine(nwx - 1, nwy - 1, nwx + lado * lado1 + 1, nwy - 1);
        g.drawLine(nwx - 1, nwy + lado * lado1 + 1, nwx + lado * lado1 + 1, nwy + lado * lado1 + 1);
    }

    /**
     * Pinta una celda.
     *
     * @param g sistema grafico 2D para dibujarse.
     * @param x columna.
     * @param y fila.
     */
    private void pintaCasilla(Graphics g, int x, int y) {
        Casilla casilla = terreno.getCasilla(x, y);

        pintaTipo(g, casilla);
        if (casilla.equals(celdaSeleccionada))
            rellena(g, x, y, Color.LIGHT_GRAY);

        // pinta las paredes de la casilla
        g.setColor(Color.RED);
        if (terreno.hayPared(casilla, Direccion.NORTE))
            g.drawLine(sw_x(x), sw_y(y + 1), sw_x(x + 1), sw_y(y + 1));
        if (terreno.hayPared(casilla, Direccion.SUR))
            g.drawLine(sw_x(x), sw_y(y), sw_x(x + 1), sw_y(y));
        if (terreno.hayPared(casilla, Direccion.ESTE))
            g.drawLine(sw_x(x + 1), sw_y(y), sw_x(x + 1), sw_y(y + 1));
        if (terreno.hayPared(casilla, Direccion.OESTE))
            g.drawLine(sw_x(x), sw_y(y), sw_x(x), sw_y(y + 1));

        Movil movil = casilla.getMovil();
        if (movil != null) {
            pintaImagen((Graphics2D) g, movil.getImagen(), x, y);
        }
    }

    /**
     * Pinta el tipo de casilla.
     *
     * @param g       sistema grafico 2D para dibujarse.
     * @param casilla casilla a pintar.
     */
    private void pintaTipo(Graphics g, Casilla casilla) {
        Color color;
        if (casilla.isObjetivo())
            color = Color.BLUE;
        else
            return;
        int x = casilla.getX();
        int y = casilla.getY();
        int nwx = sw_x(x) + 3;
        int nwy = sw_y(y + 1) + 3;
        int dx = this.lado1 - 6;
        int dy = this.lado1 - 6;
        g.setColor(color);
        g.fillOval(nwx, nwy, dx, dy);
    }

    /**
     * Rellena el cuadrado de un cierto color.
     *
     * @param g     sistema grafico 2D para dibujarse.
     * @param x     columna.
     * @param y     fila.
     * @param color para rellenar.
     */
    private void rellena(Graphics g, int x, int y, Color color) {
        int nwx = sw_x(x) + 1;
        int nwy = sw_y(y + 1) + 1;
        int dx = this.lado1 - 2;
        int dy = this.lado1 - 2;
        g.setColor(color);
        g.fillRect(nwx, nwy, dx, dy);
    }

    /**
     * Pinta la imagen propia del movil.
     *
     * @param g2d    sistema grafico 2D para dibujar.
     * @param imagen imagen a dibujar.
     * @param x      columna.
     * @param y      fila.
     */
    private void pintaImagen(Graphics2D g2d, Image imagen, int x, int y) {
        if (imagen == null)
            return;
        int iWidth = imagen.getWidth(null);
        int iHeight = imagen.getHeight(null);
        double escalaX = 0.9 * lado1 / iWidth;
        double escalaY = 0.9 * lado1 / iHeight;
        double escala = Math.min(escalaX, escalaY);
        double nwX = sw_x(x) + (lado1 - escala * iWidth) / 2;
        double nwY = sw_y(y + 1) + (lado1 - escala * iHeight) / 2;
        AffineTransform transform = new AffineTransform(escala, 0, 0, escala, nwX, nwY);
        g2d.drawImage(imagen, transform, null);
    }

    /**
     * Dada una columna, calcula el vertice inferior izquierdo.
     *
     * @param columna columna.
     * @return abscisa del vertice inferior izquierdo.
     */
    private int sw_x(int columna) {
        return MARGEN + columna * lado1;
    }

    /**
     * Dada una fila, calcula el vertice inferior izquierdo.
     *
     * @param fila fila.
     * @return vertice inferior izquierdo.
     */
    private int sw_y(int fila) {
        int lado = terreno.getN();
        return MARGEN + (lado - fila) * lado1;
    }

    /**
     * Captura el teclado.
     */
    private class MyKeyListener
            extends KeyAdapter {
        /**
         * Gestiona el teclado.
         *
         * @param event tecla pulsada.
         */
        @Override
        public void keyPressed(KeyEvent event) {
            Direccion direccion = getDireccion(event);
            if (direccion != null) {
                Casilla origen = jugador.getCasilla();
                terreno.move(origen, direccion);
                pintame();
                if (jugador.getMessage() != null) {
                    JOptionPane.showMessageDialog(GUI.this,
                            jugador.getMessage(), "es/upm/dit/adsw/pacman",
                            JOptionPane.INFORMATION_MESSAGE);
                    System.exit(0);
                }

            }
        }

        private Direccion getDireccion(KeyEvent ke) {
            if (ke.getKeyCode() == KeyEvent.VK_UP)
                return Direccion.NORTE;
            if (ke.getKeyCode() == KeyEvent.VK_DOWN)
                return Direccion.SUR;
            if (ke.getKeyCode() == KeyEvent.VK_RIGHT)
                return Direccion.ESTE;
            if (ke.getKeyCode() == KeyEvent.VK_LEFT)
                return Direccion.OESTE;
            return null;
        }
    }

    private class MyMouseListener
            extends MouseAdapter {
        @Override
        public void mouseClicked(MouseEvent event) {
            try {
                int pixelX = event.getX();
                int pixelY = event.getY();
                int x = (pixelX - MARGEN) / lado1;
                int y = terreno.getN() - 1 - (pixelY - MARGEN) / lado1;
                Casilla casilla = terreno.getCasilla(x, y);
                if (casilla == celdaSeleccionada)
                    celdaSeleccionada = null;
                else
                    celdaSeleccionada = casilla;
                pintame();
            } catch (Exception ignored) {
            }
        }
    }
}
