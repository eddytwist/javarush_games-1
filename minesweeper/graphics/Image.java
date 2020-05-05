package com.javarush.games.minesweeper.graphics;

import com.javarush.engine.cell.*;

/**
 * Abstract class that allows drawing stuff using colored cells.
 * Objects of this class have graphics data and position. Graphics is made of color codes stored in int[][].
 * Objects of this class can be drawn straight, or mirrored by X or Y.
 * Objects of this class should have a game instance to be drawn in.
 * If images are too large to be drawn as as int[][], conversion from strings is possible.
 * If needed, colors of objects can be altered.
 * Has only one abstract method: assignGraphicsData(Graphics graphics), since it shouldn't have graphics data by itself.
 */

public abstract class Image {
    final protected Game GAME;                // game instance to be drawn into
    private int drawX;
    private int drawY;                        // real position in pixels
    protected int[][] bitmapData;             // matrix of color numbers
    protected Color[] colors;                 // an array to match colors and numbers

    protected Image(Bitmap bitmap, Game game, int drawX, int drawY) { // constructor with setting position at once
        this.colors = new Color[2];
        this.bitmapData = assignBitmap(bitmap);
        this.GAME = game;
        setPosition(drawX, drawY);
    }

    Image(Bitmap bitmap, Game game) { // constructor without setting position (for loading images in memory)
        this.colors = new Color[2];
        this.bitmapData = assignBitmap(bitmap);
        this.GAME = game;
    }

    public void draw() {
        for (int innerY = 0; innerY < bitmapData.length; innerY++) {
            for (int innerX = 0; innerX < bitmapData[0].length; innerX++) {
                if (bitmapData[innerY][innerX] == 0) {
                    continue;
                } // transparent color
                try {
                    GAME.setCellColor(
                            drawX + innerX,
                            drawY + innerY,
                            colors[bitmapData[innerY][innerX]]
                    );
                } catch (IndexOutOfBoundsException e) {
                    return;
                }
            }
        }
    }

    public final void draw(boolean mirror) { // true reflects X, false reflects Y
        for (int innerY = 0; innerY < bitmapData.length; innerY++) {
            for (int innerX = 0; innerX < bitmapData[0].length; innerX++) {
                if (bitmapData[innerY][innerX] == 0) {
                    continue;
                } // transparent color
                try {
                    if (mirror) {
                        GAME.setCellColor(
                                drawX + (bitmapData[0].length - 1 - innerX), // flip horizontally
                                drawY + innerY,
                                colors[bitmapData[innerY][innerX]]
                        );
                    } else {
                        GAME.setCellColor(
                                drawX + innerX,
                                drawY + (bitmapData.length - 1 - innerY),     // flip vertically
                                colors[bitmapData[innerY][innerX]]
                        );
                    }
                } catch (IndexOutOfBoundsException e) {
                    return;
                }
            }
        }
    }

    public final void replaceColor(Color color, int number) {
        try {
            this.colors[number] = color;
        } catch (IndexOutOfBoundsException ignored) {

        }
    }

    public final void setPosition(int drawX, int drawY) {
        if (drawX < 0) { // align X center
            this.drawX = 50 - bitmapData[0].length / 2;
        } else {         // put at position
            this.drawX = drawX;
        }
        if (drawY < 0) { // align Y center
            this.drawY = 50 - bitmapData.length / 2;
        } else {         // put at position
            this.drawY = drawY;
        }
    }

    protected abstract int[][] assignBitmap(Bitmap bitmap); // subclasses' individual pictures go here

    final int[][] createBitmapFromStrings(String... strings) {
        int sizeX = strings[0].length();
        int sizeY = strings.length;
        int[][] result = new int[sizeY][sizeX];
        for (int y = 0; y < sizeY; y++) {
            char[] row = strings[y].toCharArray();
            for (int x = 0; x < sizeX; x++) {
                result[y][x] = row[x] - 48; // - 48 to convert chars into ints
            }
        }
        return result;
    } // returns a map of numbers from strings

    final int[][] createWindowBitmap(int sizeX, int sizeY, boolean shadow, boolean frame) {
        if (shadow) {
            sizeX++;
            sizeY++;
        }
        int[][] window = new int[sizeY][sizeX];
        for (int y = 0; y < sizeY; y++) {
            for (int x = 0; x < sizeX; x++) {
                window[y][x] = 1;
            }
        }
        if (shadow) {
            for (int x = 0; x < sizeX; x++) {
                if (x == 0) {
                    window[sizeY - 1][x] = 0;
                } else {
                    window[sizeY - 1][x] = 2;
                }
            }
            for (int y = 0; y < sizeY; y++) {
                if (y == 0) {
                    window[y][sizeX - 1] = 0;
                } else {
                    window[y][sizeX - 1] = 2;
                }
            }
        }
        if (frame) {
            if (shadow) {
                sizeX--;
                sizeY--;
            }
            for (int x = 0; x < sizeX; x++) {
                window[0][x] = 3;
                window[sizeY - 1][x] = 3;
            }
            for (int y = 0; y < sizeY; y++) {
                window[y][0] = 3;
                window[y][sizeX - 1] = 3;
            }
        }
        return window;
    }
}
