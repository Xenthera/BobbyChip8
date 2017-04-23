package com.bobby;

import com.bobby.Chip8.Chip8;
import processing.core.PApplet;

import javax.swing.*;
import java.io.File;

// M A I N  C L A S S //////////////////////////////////////////////////////////////////////

public class Main extends PApplet{
    private Chip8 chip8;
    private boolean debug = false;
    public static void main(String[] args) {
        PApplet.main("com.bobby.Main");
    }
    public void settings(){
        size(10 * 64,10 * 32);
    }
    public void setup(){
        File game = openFilePicker();
        chip8 = new Chip8();
        chip8.initialize();
        chip8.loadProgram(game);
        frameRate(60);

    }

    public File openFilePicker() {
        File game = null;
        final JFileChooser fc = new JFileChooser();
        fc.setCurrentDirectory(new File(System.getProperty("user.dir") + "/roms"));
        int returnValue = fc.showOpenDialog(null);
        if (returnValue == JFileChooser.APPROVE_OPTION) {
            game = fc.getSelectedFile();
        } else {
            System.exit(0);
        }
        return game;
    }
    public void draw(){
        chip8.update();
        chip8.draw(this);
    }
    public void keyPressed(){
        chip8.keyPressed(key);
    }
    public void keyReleased(){
        chip8.keyReleased(key);
    }
}


