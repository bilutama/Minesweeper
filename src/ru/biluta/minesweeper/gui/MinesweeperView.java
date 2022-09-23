package ru.biluta.minesweeper.gui;

import ru.biluta.minesweeper.logic.Game;
import ru.biluta.minesweeper.logic.Level;

import javax.swing.*;
import javax.swing.border.EtchedBorder;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.IOException;

public class MinesweeperView implements View {
    private final static int CELL_SIZE = 40;
    private final static int CELL_ICON_SIZE = 35;
    private final static int MAIN_ICON_SIZE = 50;
    private final static int TOP_PANEL_HEIGHT = 95;

    private final static String RESOURCES_PATH = "src/ru/biluta/minesweeper/resources/";
    private final static String IMAGES_FOLDER = "images/";
    private final static String FONTS_FOLDER = "fonts/";

    private final static String BOMB_IMAGE_PATH = RESOURCES_PATH + IMAGES_FOLDER + "bomb.png";
    private final static String EXPLOSION_IMAGE_PATH = RESOURCES_PATH + IMAGES_FOLDER + "explosion.png";
    private final static String FLAG_IMAGE_PATH = RESOURCES_PATH + IMAGES_FOLDER + "flag.png";
    private final static String STOPWATCH_IMAGE_PATH = RESOURCES_PATH + IMAGES_FOLDER + "stopwatch.png";

    private final static String SMILE_IMAGE_PATH = RESOURCES_PATH + IMAGES_FOLDER + "smile.png";
    private final static String SKULL_IMAGE_PATH = RESOURCES_PATH + IMAGES_FOLDER + "skull.png";
    private final static String WINNER_IMAGE_PATH = RESOURCES_PATH + IMAGES_FOLDER + "winner.png";

    private final static String CUSTOM_FONT_PATH = RESOURCES_PATH + FONTS_FOLDER + "digital-7/digital-7 (mono).ttf";

    private static ImageIcon explosionIcon;
    private static ImageIcon bombIcon;
    private static ImageIcon flagIcon;
    private static ImageIcon stopwatchIcon;

    private static ImageIcon smileIcon;
    private static ImageIcon skullIcon;
    private static ImageIcon winnerIcon;

    private JPanel[][] buttonPanels;
    private MatrixButton[][] fieldButtons;
    private JButton resetGameButton;
    private JPopupMenu gameLevelMenu;

    private JFrame frame;
    private JPanel minefield;
    private JLabel timeCounterLabel;
    private JLabel unflaggedMinesCounterLabel;
    private int minefieldWidth;
    private int minefieldHeight;

    private Game minesweeper;
    private MouseAdapter resetGameMouseAdapter;
    private ActionListener startNewGamePopupMenuListener;
    private ActionListener highScoresListener;
    private ActionListener aboutThisGameListener;

    private int[][] revealedCellsMatrix;
    private int[][] visitedCellsMatrix;
    private int[][] nearbyMinesCountMatrix; // -1 stands for mine

    public MinesweeperView(Game minesweeper) {
        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception ignored) {
            }

            initializeIcons();

            // set the main FRAME
            frame = new JFrame();
            frame.setIconImage(new ImageIcon(BOMB_IMAGE_PATH).getImage());
            frame.setResizable(false);
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

            // Set top panel for the Toolbar
            JPanel topPanel = new JPanel();
            topPanel.setSize(100, 20);
            topPanel.setBorder(BorderFactory.createEtchedBorder(EtchedBorder.LOWERED));
            frame.add(topPanel, BorderLayout.PAGE_START);

            unflaggedMinesCounterLabel = new JLabel(Integer.toString(minesweeper.getLevel().getMinesCount()), flagIcon, JLabel.CENTER);
            unflaggedMinesCounterLabel.setVerticalTextPosition(JLabel.CENTER);
            unflaggedMinesCounterLabel.setPreferredSize(new Dimension(100, 40));
            topPanel.add(unflaggedMinesCounterLabel, BorderLayout.WEST);

            // Add toolbar to the panel
            JToolBar toolBar = new JToolBar();
            toolBar.setFloatable(false);
            toolBar.setOrientation(SwingConstants.HORIZONTAL);
            topPanel.add(toolBar, BorderLayout.CENTER);

            // Add New_game_button to the toolBar
            resetGameButton = new JButton();
            resetGameButton.setFocusable(false);
            resetGameButton.setIcon(smileIcon);
            resetGameButton.addMouseListener(resetGameMouseAdapter);
            toolBar.add(resetGameButton, BorderLayout.CENTER);

            // Add Time Counter to the toolBar
            timeCounterLabel = new JLabel("0", stopwatchIcon, JLabel.CENTER);
            timeCounterLabel.setHorizontalAlignment(SwingConstants.RIGHT);
            timeCounterLabel.setVerticalTextPosition(SwingConstants.CENTER);
            timeCounterLabel.setHorizontalTextPosition(SwingConstants.RIGHT);

            timeCounterLabel.setPreferredSize(new Dimension(100, 40));
            topPanel.add(timeCounterLabel, BorderLayout.EAST);

            // Set custom font
            try {
                timeCounterLabel.setFont(Font.createFont(Font.PLAIN, new File(MinesweeperView.CUSTOM_FONT_PATH)).deriveFont(35f));
                unflaggedMinesCounterLabel.setFont(Font.createFont(Font.PLAIN, new File(MinesweeperView.CUSTOM_FONT_PATH)).deriveFont(35f));
            } catch (FontFormatException | IOException e) {
                e.printStackTrace();
            }

            // Bind popup menu to the reset button
            gameLevelMenu = new JPopupMenu();

            for (Level levelEnumElement : Level.values()) {
                JMenuItem menuItem = new JMenuItem(String.valueOf(levelEnumElement));
                menuItem.addActionListener(startNewGamePopupMenuListener);
                gameLevelMenu.add(menuItem);
            }

            gameLevelMenu.addSeparator();

            // Add RecordTable item
            JMenuItem highScoresItem = new JMenuItem("HIGH SCORES");
            highScoresItem.addActionListener(highScoresListener);
            gameLevelMenu.add(highScoresItem);

            // Add About This Game item
            JMenuItem aboutThisGameItem = new JMenuItem("ABOUT");
            aboutThisGameItem.addActionListener(aboutThisGameListener);
            gameLevelMenu.add(aboutThisGameItem);

            // Set a minefield
            minefield = new JPanel();
            minefield.setBorder(BorderFactory.createEtchedBorder(EtchedBorder.LOWERED));
            frame.add(minefield, BorderLayout.CENTER);

            initializeGui(minesweeper);
            frame.setLocationRelativeTo(null);
        });
    }

    public void setHomeButton(MouseAdapter resetGameMouseAdapter, ActionListener startNewGamePopupMenuListener, ActionListener highScoresListener, ActionListener aboutThisGameListener) {
        this.resetGameMouseAdapter = resetGameMouseAdapter;
        this.startNewGamePopupMenuListener = startNewGamePopupMenuListener;
        this.highScoresListener = highScoresListener;
        this.aboutThisGameListener = aboutThisGameListener;
    }

    public void showPopupMenu(MouseEvent e) {
        gameLevelMenu.show(resetGameButton, e.getX(), e.getY());
    }

    public void initializeGui(Game minesweeper) {
        minefield.removeAll();
        resetGameButton.setIcon(smileIcon);

        this.minesweeper = minesweeper;
        Level level = minesweeper.getLevel();

        frame.setTitle("Minesweeper - " + level.toString().toUpperCase());
        unflaggedMinesCounterLabel.setText(Integer.toString(level.getMinesCount()));

        minefieldWidth = level.getMinefieldWidth();
        minefieldHeight = level.getMinefieldHeight();

        revealedCellsMatrix = minesweeper.getRevealedCellsMatrix();
        nearbyMinesCountMatrix = minesweeper.getNearbyMinesCountMatrix();
        visitedCellsMatrix = new int[minefieldHeight][minefieldWidth];

        minefield.setLayout(new GridLayout(minefieldHeight, minefieldWidth));

        // Set frame size depending on minefield dimensions
        minefield.getTopLevelAncestor().setSize(minefieldWidth * CELL_SIZE, minefieldHeight * CELL_SIZE + TOP_PANEL_HEIGHT);

        buttonPanels = new JPanel[minefieldHeight][minefieldWidth];
        fieldButtons = new MatrixButton[minefieldHeight][minefieldWidth];

        for (int j = 0; j < minefieldHeight; j++) {
            for (int i = 0; i < minefieldWidth; i++) {
                buttonPanels[j][i] = new JPanel();
                minefield.add(buttonPanels[j][i]);

                buttonPanels[j][i].setLayout(new BorderLayout());

                fieldButtons[j][i] = new MatrixButton(j, i);
                fieldButtons[j][i].getButton().setFocusable(false);
                buttonPanels[j][i].add(fieldButtons[j][i].getButton());

                MatrixButton matrixButton = fieldButtons[j][i];

                matrixButton.addMouseListener(new MouseAdapter() {
                    public void mousePressed(MouseEvent e) {
                        if (minesweeper.isGameOver() || minesweeper.isWinner()) {
                            return;
                        }

                        if (e.getButton() == MouseEvent.BUTTON1 && !matrixButton.isFlagged()) {
                            updateView(matrixButton.getButtonY(), matrixButton.getButtonX());
                            minefield.updateUI();
                            return;
                        }

                        if (e.getButton() == MouseEvent.BUTTON3) {
                            if (matrixButton.getButton().getIcon() == null) {
                                matrixButton.setFlagged();
                                matrixButton.getButton().setIcon(flagIcon);
                                unflaggedMinesCounterLabel.setText(Integer.toString(Integer.parseInt(unflaggedMinesCounterLabel.getText()) - 1));
                                return;
                            }

                            matrixButton.setUnflagged();
                            matrixButton.getButton().setIcon(null);
                            unflaggedMinesCounterLabel.setText(Integer.toString(Integer.parseInt(unflaggedMinesCounterLabel.getText()) + 1));
                        }
                    }
                });
            }
        }

        minefield.updateUI();
        frame.setVisible(true);
    }

    public void setGameTime(long gameTime) {
        timeCounterLabel.setText(String.format("%03d", gameTime / 1000));
    }

    private void initializeIcons() {
        smileIcon = new ImageIcon(
                new ImageIcon(SMILE_IMAGE_PATH)
                        .getImage()
                        .getScaledInstance(MAIN_ICON_SIZE, MAIN_ICON_SIZE, Image.SCALE_AREA_AVERAGING)
        );

        skullIcon = new ImageIcon(
                new ImageIcon(SKULL_IMAGE_PATH)
                        .getImage()
                        .getScaledInstance(MAIN_ICON_SIZE, MAIN_ICON_SIZE, Image.SCALE_AREA_AVERAGING)
        );

        explosionIcon = new ImageIcon(
                new ImageIcon(EXPLOSION_IMAGE_PATH)
                        .getImage()
                        .getScaledInstance(CELL_ICON_SIZE, CELL_ICON_SIZE, Image.SCALE_AREA_AVERAGING)
        );

        winnerIcon = new ImageIcon(
                new ImageIcon(WINNER_IMAGE_PATH)
                        .getImage()
                        .getScaledInstance(MAIN_ICON_SIZE, MAIN_ICON_SIZE, Image.SCALE_AREA_AVERAGING)
        );

        flagIcon = new ImageIcon(
                new ImageIcon(FLAG_IMAGE_PATH)
                        .getImage()
                        .getScaledInstance(CELL_ICON_SIZE, CELL_ICON_SIZE, Image.SCALE_AREA_AVERAGING)
        );

        bombIcon = new ImageIcon(
                new ImageIcon(BOMB_IMAGE_PATH)
                        .getImage()
                        .getScaledInstance(CELL_ICON_SIZE, CELL_ICON_SIZE, Image.SCALE_AREA_AVERAGING)
        );

        stopwatchIcon = new ImageIcon(
                new ImageIcon(STOPWATCH_IMAGE_PATH)
                        .getImage()
                        .getScaledInstance(CELL_ICON_SIZE, CELL_ICON_SIZE, Image.SCALE_AREA_AVERAGING)
        );
    }

    private void updateView(int revealedCellX, int revealedCellY) {
        minesweeper.revealCellRange(revealedCellX, revealedCellY);

        // Update view
        for (int j = 0; j < minefieldHeight; ++j) {
            for (int i = 0; i < minefieldWidth; ++i) {
                if (revealedCellsMatrix[j][i] == 1 && visitedCellsMatrix[j][i] == 0) {
                    visitedCellsMatrix[j][i] = 1;

                    // Open flagged cell -> update counter
                    if (fieldButtons[j][i].isFlagged()) {
                        unflaggedMinesCounterLabel.setText(Integer.toString(Integer.parseInt(unflaggedMinesCounterLabel.getText()) + 1));
                    }

                    buttonPanels[j][i].remove(fieldButtons[j][i].getButton());
                    int minesCount = nearbyMinesCountMatrix[j][i];

                    // Mine explosion -> game over, revealing all the mines
                    if (minesCount == -1) {
                        JLabel labelExplosion = new JLabel(explosionIcon);
                        buttonPanels[j][i].add(labelExplosion);
                    }

                    // Show neighbouring mines count
                    if (minesCount > 0) {
                        JLabel label = new JLabel(Integer.toString(minesCount), JLabel.CENTER);
                        buttonPanels[j][i].add(label, BorderLayout.CENTER);
                    }
                }
            }
        }

        checkGameStatus();
    }

    private void checkGameStatus() {
        if (minesweeper.isWinner()) {
            resetGameButton.setIcon(winnerIcon);

            if (minesweeper.isNewHighScore()) {
                offerToSaveNewHighScore();
            }
        }

        // Mine explosion -> game over, revealing all the mines
        if (minesweeper.isGameOver()) {
            resetGameButton.setIcon(skullIcon);
            revealAllMines();
        }
    }

    private void offerToSaveNewHighScore() {
        try {
            String winnerName = JOptionPane.showInputDialog(null, "Enter your nickname:", "New high score!", JOptionPane.PLAIN_MESSAGE);

            if (winnerName != null) {
                winnerName = winnerName.trim();

                if (winnerName.equals("")) {
                    winnerName = "[anonymous]";
                }

                minesweeper.addNewHighScore(minesweeper.getLevel(), winnerName, minesweeper.getGameTime());
            }
        } catch (Exception ignored) {
        }
    }

    private void revealAllMines() {
        for (int j = 0; j < minefieldHeight; ++j) {
            for (int i = 0; i < minefieldWidth; ++i) {
                if (revealedCellsMatrix[j][i] == 0 && nearbyMinesCountMatrix[j][i] == -1) {
                    MatrixButton buttonForMinedCell = fieldButtons[j][i];
                    JPanel buttonPanelForMinedCell = buttonPanels[j][i];

                    JLabel bombLabel = new JLabel(bombIcon);

                    if (buttonForMinedCell.isFlagged()) {
                        buttonForMinedCell.getButton().setBorder(new LineBorder(Color.GREEN, 2));
                        continue;
                    }

                    buttonPanelForMinedCell.remove(buttonForMinedCell.getButton());
                    buttonPanelForMinedCell.add(bombLabel, BorderLayout.CENTER);
                }
            }
        }
    }
}