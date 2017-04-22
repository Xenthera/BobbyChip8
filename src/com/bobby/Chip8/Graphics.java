package com.bobby.Chip8;

import processing.core.PApplet;

/**
 * Created by Bobby on 4/22/2017.
 */
public class Graphics {
    public int gfx[];
    boolean drawFlag = false;

    public Graphics() {
        this.gfx = new int[64 * 32];
    }

    public void clearScreen() {
        for (int i = 0; i < this.gfx.length; i++) {
            this.gfx[i] = 0;
        }
        this.drawFlag = true;
    }

    public void initialize() {
        this.gfx = new int[64 * 32];
    }

    public void draw(PApplet g) {
        if (this.drawFlag) {
            g.noStroke();
            g.background(0, 40, 40);
            for (int i = 0; i < this.gfx.length; i++) {
                if (this.gfx[i] == 1) {

                    g.fill(0, 255, 255);
                    g.rect((i % 64) * 10, (PApplet.floor(i / 64)) * 10, 10, 10);

                }
            }
            this.drawFlag = false;


        }
    }
}
