package com.mycompany.lab3progra2_samuel_said;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.border.EmptyBorder;
import javax.swing.SwingWorker;
import java.awt.Point;

/**
 * Clase principal que monta la GUI de Sudoku con panel de selección 1-9 y
 * destaca fila, columna y subcuadrante de la celda seleccionada.
 */
public class SudokuApp extends JFrame {
    private Sudoku modelo;
    private Tablero tablero;
    private Boton selectedCell;

    // Colores de resaltado
    private static final Color HIGHLIGHT = new Color(220, 235, 252);
    private static final Color SELECTED = new Color(153, 204, 255);

    public SudokuApp(int[][] inicial) {
        super("Sudoku");
        modelo = new Sudoku(inicial);
        tablero = new Tablero(modelo);

        // Panel de números 1-9
        JPanel numPanel = new JPanel(new GridLayout(3, 3, 5, 5));
        numPanel.setBorder(new EmptyBorder(10, 10, 10, 10));
        for (int i = 1; i <= 9; i++) {
            JButton btn = new JButton(String.valueOf(i));
            btn.setFont(new Font("SansSerif", Font.BOLD, 18));
            btn.addActionListener(e -> fillNumber(btn));
            numPanel.add(btn);
        }

        // New Game
        JButton btnNew = new JButton("New Game");
        btnNew.setFont(new Font("SansSerif", Font.BOLD, 16));
        btnNew.addActionListener(e -> crearPuzzle());

        // Eventos de selección de celda
        attachCellListeners();

        // Layout general
        setLayout(new BorderLayout(5, 5));
        add(tablero, BorderLayout.CENTER);
        JPanel east = new JPanel(new BorderLayout());
        east.add(numPanel, BorderLayout.CENTER);
        east.add(btnNew, BorderLayout.SOUTH);
        add(east, BorderLayout.EAST);

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        pack();
        // Aumentar tamaño para mejor visualización
        setSize(800, 600);
        setLocationRelativeTo(null);
        setVisible(true);

        crearPuzzle(); // Inicia con un puzzle
    }

    private void fillNumber(JButton btn) {
        if (selectedCell != null && selectedCell.isEnabled()) {
            int num = Integer.parseInt(btn.getText());
            selectedCell.setNum(num);
            selectedCell.setText(btn.getText());
            modelo.set(selectedCell.getFila(), selectedCell.getColumna(), num, true);
            tablero.validar();
            colorSelection();
        }
    }

    private void attachCellListeners() {
        for (int r = 0; r < 9; r++) {
            for (int c = 0; c < 9; c++) {
                Boton b = tablero.getBoton(r, c);
                b.addActionListener(e -> seleccionarCelda((Boton) e.getSource()));
            }
        }
    }

    /**
     * Selecciona una celda, resaltando fila, columna y bloque.
     */
    private void seleccionarCelda(Boton b) {
        selectedCell = b;
        colorSelection();
    }

    /**
     * Aplica colores a la fila, columna y subcuadrante de la celda seleccionada.
     */
    private void colorSelection() {
        // Limpia todos
        for (int r = 0; r < 9; r++) {
            for (int c = 0; c < 9; c++) {
                Boton cell = tablero.getBoton(r, c);
                cell.setBackground(Color.WHITE);
            }
        }
        if (selectedCell == null) return;
        int sr = selectedCell.getFila();
        int sc = selectedCell.getColumna();
        // Resaltar fila y columna
        for (int i = 0; i < 9; i++) {
            tablero.getBoton(sr, i).setBackground(HIGHLIGHT);
            tablero.getBoton(i, sc).setBackground(HIGHLIGHT);
        }
        // Resaltar subcuadrante 3x3
        int br = (sr / 3) * 3;
        int bc = (sc / 3) * 3;
        for (int r = br; r < br + 3; r++) {
            for (int c = bc; c < bc + 3; c++) {
                tablero.getBoton(r, c).setBackground(HIGHLIGHT);
            }
        }
        // Color celda activa
        selectedCell.setBackground(SELECTED);
    }

    /**
     * Genera un puzzle simple: resuelve y elimina celdas.
     */
    private void crearPuzzle() {
        getContentPane().removeAll();
        modelo = new Sudoku(new int[9][9]);
        modelo.solve();
        int[][] complete = modelo.getGrid();
        int[][] puzzle = new int[9][9];
        java.util.List<Point> cells = new java.util.ArrayList<>();
        for (int r = 0; r < 9; r++) {
            for (int c = 0; c < 9; c++) {
                puzzle[r][c] = complete[r][c];
                cells.add(new Point(r, c));
            }
        }
        java.util.Collections.shuffle(cells);
        for (int i = 0; i < 45; i++) {
            Point p = cells.get(i);
            puzzle[p.x][p.y] = 0;
        }
        tablero = new Tablero(modelo = new Sudoku(puzzle));
        attachCellListeners();

        // Reconstruir panel de números y botón
        JPanel numPanel = new JPanel(new GridLayout(3, 3, 5, 5));
        numPanel.setBorder(new EmptyBorder(10, 10, 10, 10));
        for (int i = 1; i <= 9; i++) {
            JButton btn = new JButton(String.valueOf(i));
            btn.setFont(new Font("SansSerif", Font.BOLD, 18));
            btn.addActionListener(e -> fillNumber(btn));
            numPanel.add(btn);
        }
        JButton btnNew = new JButton("New Game");
        btnNew.setFont(new Font("SansSerif", Font.BOLD, 16));
        btnNew.addActionListener(e -> crearPuzzle());

        setLayout(new BorderLayout(5, 5));
        add(tablero, BorderLayout.CENTER);
        JPanel east = new JPanel(new BorderLayout());
        east.add(numPanel, BorderLayout.CENTER);
        east.add(btnNew, BorderLayout.SOUTH);
        add(east, BorderLayout.EAST);

        revalidate();
        repaint();
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new SudokuApp(new int[9][9]));
    }
}