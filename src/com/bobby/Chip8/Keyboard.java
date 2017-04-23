package com.bobby.Chip8;

/**
 * Created by Bobby on 4/22/2017.
 */
public class Keyboard {

    public int keys[];

    public int currentKeyPressed;

    public Keyboard() {
        this.keys = new int[16];
    }

    public boolean getKeyPressed(int key) {
        return keys[key] != 0;
    }

    public int currentKeyPressed() {
        return currentKeyPressed;
    }

    public void keyPressed(char key) {

        switch (key) {
            case '1':
                this.keys[0x1] = 1;
                currentKeyPressed = 0x1;
                break;
            case '2':
                this.keys[0x2] = 1;
                currentKeyPressed = 0x2;
                break;
            case '3':
                this.keys[0x3] = 1;
                currentKeyPressed = 0x3;
                break;
            case '4':
                this.keys[0xc] = 1;
                currentKeyPressed = 0xc;
                break;
            case 'q':
                this.keys[0x4] = 1;
                currentKeyPressed = 0x4;
                break;
            case 'w':
                this.keys[0x5] = 1;
                currentKeyPressed = 0x5;
                break;
            case 'e':
                this.keys[0x6] = 1;
                currentKeyPressed = 0x6;
                break;
            case 'r':
                this.keys[0xd] = 1;
                currentKeyPressed = 0xf;
                break;
            case 'a':
                this.keys[0x7] = 1;
                currentKeyPressed = 0x7;
                break;
            case 's':
                this.keys[0x8] = 1;
                currentKeyPressed = 0x8;
                break;
            case 'd':
                this.keys[0x9] = 1;
                currentKeyPressed = 0x9;
                break;
            case 'f':
                this.keys[0xe] = 1;
                currentKeyPressed = 0xe;
                break;
            case 'z':
                this.keys[0xa] = 1;
                currentKeyPressed = 0xa;
                break;
            case 'x':
                this.keys[0x0] = 1;
                currentKeyPressed = 0x0;
                break;
            case 'c':
                this.keys[0xb] = 1;
                currentKeyPressed = 0xb;
                break;
            case 'v':
                this.keys[0xf] = 1;
                currentKeyPressed = 0xf;
                break;

        }
    }

    public void keyReleased(char key) {
        currentKeyPressed = -1;
        switch (key) {
            case '1':
                this.keys[0x1] = 0;
                break;
            case '2':
                this.keys[0x2] = 0;
                break;
            case '3':
                this.keys[0x3] = 0;
                break;
            case '4':
                this.keys[0xc] = 0;
                break;
            case 'q':
                this.keys[0x4] = 0;
                break;
            case 'w':
                this.keys[0x5] = 0;
                break;
            case 'e':
                this.keys[0x6] = 0;
                break;
            case 'r':
                this.keys[0xd] = 0;
                break;
            case 'a':
                this.keys[0x7] = 0;
                break;
            case 's':
                this.keys[0x8] = 0;
                break;
            case 'd':
                this.keys[0x9] = 0;
                break;
            case 'f':
                this.keys[0xe] = 0;
                break;
            case 'z':
                this.keys[0xa] = 0;
                break;
            case 'x':
                this.keys[0x0] = 0;
                break;
            case 'c':
                this.keys[0xb] = 0;
                break;
            case 'v':
                this.keys[0xf] = 0;
                break;

        }
    }
}
