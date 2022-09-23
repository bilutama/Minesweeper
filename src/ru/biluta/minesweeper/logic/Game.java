package ru.biluta.minesweeper.logic;

import ru.biluta.minesweeper.logic.high_scores_table.HighScoreTable;

public interface Game {
    void revealCellRange(int cellX, int cellY);

    Level getLevel();

    int getClosedCellsCount();

    HighScoreTable getHighScoresTable();

    boolean isGameOver();

    boolean isWinner();

    boolean isNewHighScore();

    void addNewHighScore(Level level, String nickname, long gameTime);

    int[][] getRevealedCellsMatrix();

    int[][] getNearbyMinesCountMatrix();

    long getGameTime();
}