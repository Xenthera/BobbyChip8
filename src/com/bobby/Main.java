package com.bobby;

import com.bobby.Chip8.Chip8;
import com.bobby.Chip8.CpuDebug;
import processing.core.PApplet;
import processing.core.PFont;
import processing.core.PGraphics;

import javax.swing.*;
import java.io.File;


public class Main extends PApplet{

    Chip8 chip8;

    boolean debug = false;

    CpuDebug d;

    PGraphics c8;

    PFont font;

    public static void main(String[] args) {
        PApplet.main("com.bobby.Main");
    }

    public void settings(){
        size(10 * 64,10 * 32);
    }

    public void setup(){
        //Choose File
        final JFileChooser fc = new JFileChooser();
        fc.setCurrentDirectory(new File(System.getProperty("user.dir") + "/roms"));
        int returnValue = fc.showOpenDialog(null);
        File game = null;
        if (returnValue == JFileChooser.APPROVE_OPTION) {
            game = fc.getSelectedFile();
        }
        chip8 = new Chip8();
        chip8.initialize();
        chip8.loadProgram(game);
        frameRate(60);

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


