package ru.biluta.minesweeper.logic;

import java.io.Serializable;

public enum Level implements Serializable {
    EASY(9, 9, 10),
    NORMAL(16, 16, 40),
    HARD(25, 20, 80);

    private final static double MAXIMUM_MINES_COUNT_TO_CAPACITY_RATIO = 0.8;
    private final int minefieldWidth;
    private final int minefieldHeight;
    private final int minesCount;

    Level(int minefieldWidth, int minefieldHeight, int minesCount) {
        this.minefieldHeight = minefieldHeight;
        this.minefieldWidth = minefieldWidth;

        // Assure that mines count is not exceeding field capacity * ratio
        this.minesCount = Math.min(minesCount, (int) (minefieldHeight * minefieldWidth * MAXIMUM_MINES_COUNT_TO_CAPACITY_RATIO));
    }

    public int getMinefieldWidth() {
        return minefieldWidth;
    }

    public int getMinefieldHeight() {
        return minefieldHeight;
    }

    public int getMinesCount() {
        return minesCount;
    }
}