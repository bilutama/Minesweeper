package ru.biluta.minesweeper.logic.high_scores_table;

import ru.biluta.minesweeper.logic.Level;

import java.io.Serial;
import java.io.Serializable;

public class HighScoreRecord implements Serializable {
    @Serial
    private final static long serialVersionUID = 1L;

    private final String level;
    private final String nickName;
    private final long gameTime;

    public HighScoreRecord(Level level, String nickName, long gameTime) {
        this.level = level.toString();
        this.nickName = nickName;
        this.gameTime = gameTime;
    }

    public String getLevel() {
        return level;
    }

    public long getGameTime() {
        return gameTime;
    }

    public String getNickName() {
        return nickName;
    }

    public String toString() {
        return String.format("%s: %s - %.1f sec", level, nickName, (double) gameTime / 1000);
    }
}