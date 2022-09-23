package ru.biluta.minesweeper.main;

import ru.biluta.minesweeper.controller.Controller;
import ru.biluta.minesweeper.gui.MinesweeperView;
import ru.biluta.minesweeper.gui.View;
import ru.biluta.minesweeper.logic.Game;
import ru.biluta.minesweeper.logic.Level;
import ru.biluta.minesweeper.logic.MinesweeperGame;

public class Main {
    public static void main(String[] args) {
        Game minesweeper = new MinesweeperGame(Level.EASY);
        View view = new MinesweeperView(minesweeper);
        new Controller(minesweeper, view);
    }
}