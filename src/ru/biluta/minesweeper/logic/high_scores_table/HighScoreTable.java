package ru.biluta.minesweeper.logic.high_scores_table;

import ru.biluta.minesweeper.logic.Level;

import java.io.Serializable;
import java.util.LinkedList;

public class HighScoreTable implements Serializable {
    @java.io.Serial
    private final static long serialVersionUID = 1L;
    private final static int HIGH_SCORES_TABLE_CAPACITY = 10;

    private int recordsCount;
    private long maximumTime;

    private final LinkedList<HighScoreRecord> highScoreRecords;

    public HighScoreTable() {
        highScoreRecords = new LinkedList<>();
    }

    public void addHighScoreRecord(Level level, String nickname, long gameTime) {
        highScoreRecords.addLast(new HighScoreRecord(level, nickname, gameTime));
        highScoreRecords.sort(new HighScoreRecordsComparator());

        while (highScoreRecords.size() > HIGH_SCORES_TABLE_CAPACITY) {
            highScoreRecords.removeLast();
        }

        recordsCount = highScoreRecords.size();
        maximumTime = highScoreRecords.getLast().getGameTime();
    }

    public LinkedList<HighScoreRecord> getHighScoreRecords() {
        return highScoreRecords;
    }

    public boolean isValidToAdd(long newGameTime) {
        return recordsCount < HIGH_SCORES_TABLE_CAPACITY || newGameTime < maximumTime;
    }
}