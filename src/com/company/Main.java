package com.company;

import com.company.Chip8.Chip8;
import com.company.Chip8.CpuDebug;
import processing.core.*;

import javax.swing.*;
import java.io.File;
import java.util.Arrays;


public class Main extends PApplet{

    Chip8 chip8;

    boolean debug = false;

    CpuDebug d;

    PGraphics c8;

    PFont font;

    public static void main(String[] args) {
        PApplet.main("com.company.Main");
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
        if(returnValue == JFileChooser.APPROVE_OPTION){
            game = fc.getSelectedFile();
        }
        //
        chip8 = new Chip8();
        chip8.initialize();
        chip8.loadProgram(game);
        //noLoop();
        d = new CpuDebug();
        c8 = createGraphics(10*64,10*32);
        frameRate(60);
        Debugger debugger = new Debugger();
        font = createFont("CaviarDreams_Bold.ttf", 10, false);
    }
    public void draw(){

            chip8.update();

            if(!this.debug)
                for (int i = 0; i < 10; i++) {
                    chip8.emulateCycle();
                }

            if(chip8.drawFlag || this.debug) {
                c8.beginDraw();
                c8.noStroke();
                c8.background(0, 40, 40);
                for (int i = 0; i < chip8.gfx.length; i++) {
                    if (chip8.gfx[i] == 1) {

                        c8.fill(0, 255, 255);
                        c8.rect((i % 64) * 10, (floor(i / 64)) * 10, 10, 10);

                    }
                }
                chip8.drawFlag = false;
                c8.endDraw();
                image(c8,0,0);
            }

            //redraw();

        //Drawing the hardware?
//        if(this.debug) {
//            //frameRate(2);
//            fill(0,0,0,150);
//            rect(0,0,width,height);
//            fill(255);
//            textAlign(LEFT);
//            textSize(10);
//            for (int i = 0; i < chip8.V.length; i++) {
//                text("Register " + String.valueOf(i) + ": " + chip8.hex(chip8.V[i]), 5, 15 + i * 10);
//            }
//            text("PC: " + chip8.hex(chip8.pc), 5, 200);
//            text("Cur inst: " + chip8.hex(chip8.memory[chip8.pc] << 8 | (chip8.memory[chip8.pc + 1])), 5, 210);
//            for (int i = 0; i < chip8.stack.length; i++) {
//                text("Stack " + String.valueOf(i) + ": " + chip8.hex(chip8.stack[i]), 5, 230 + i * 10);
//            }
//            text("I: " + chip8.hex(chip8.I), 5, 400);
//            text("Keys: " + Arrays.toString(chip8.keys), 5, 410);
//            text(d.getOpcodeDescription(chip8.memory[chip8.pc] << 8 | (chip8.memory[chip8.pc + 1])), 5, 420);
//            text("Delay Timer: " + String.valueOf(chip8.delay_timer), 5, 430);
//
//            textAlign(CENTER);
//            text("DEBUG MODE: Space: step cycle P: reset O: toggle debug", width/2, 10);
//            textSize(7);
//            int row = 0;


//            for (int i = 0; i < chip8.memory.length - 2000; i++) {
//                if (i % 40 == 0)
//                    row++;
//
//                if (chip8.pc == i || chip8.pc == i - 1) {
//                    fill(0, 255, 255);
//                    text(chip8.hex(chip8.memory[i]), 100 + (i % 40) * 20, 10 + (row * 8));
//                } else {
//                    fill(255, 0, 0);
//                    text(chip8.hex(chip8.memory[i]), 100 + (i % 40) * 20, 10 + (row * 8));
//                }
//
//            }

//        }

    }

    public void keyPressed(){

        if(this.debug) {

            if (key == ' ') {
                chip8.emulateCycle();
            } else if (key == 'p') {
                chip8.initialize();
            }
        }
        if(key == 'o'){
            this.debug = !this.debug;
        }
        chip8.keyPressed(key);

    }
    public void keyReleased(){
        chip8.keyReleased(key);
    }

    class Debugger extends PApplet{

        public Debugger() {
            super();
            this.setSize(300,280);
            PApplet.runSketch(new String[]{this.getClass().getName()}, this);

        }

        public void setup(){

        }

        public void draw(){

                if(font != null) {
                    textFont(font);
                }
                frameRate(200);
                background(0);
                fill(255);
                textAlign(LEFT);
                textSize(10);
                for (int i = 0; i < chip8.V.length; i++) {
                    text("Register " + String.valueOf(i) + ": " + chip8.hex(chip8.V[i]), 5, 15 + i * 10);
                }
                text("PC: " + chip8.hex(chip8.pc), 5, 200);
            text("Cur inst: " + chip8.hex(chip8.ram.read(chip8.pc) << 8 | (chip8.ram.read(chip8.pc + 1))), 5, 250);
                for (int i = 0; i < chip8.stack.length; i++) {
                    text("Stack " + String.valueOf(i) + ": " + chip8.hex(chip8.stack[i]), 100, 15 + i * 10);
                }
                text("I: " + chip8.hex(chip8.I), 5, 210);
                text("Keys: " + Arrays.toString(chip8.keys), 5, 220);
            text(d.getOpcodeDescription(chip8.ram.read(chip8.pc) << 8 | (chip8.ram.read(chip8.pc + 1))), 5, 230);
                text("Delay Timer: " + String.valueOf(chip8.delay_timer), 5, 240);

                textAlign(CENTER);
                //text("DEBUG MODE: Space: step cycle P: reset O: toggle debug", width/2, 10);
                textSize(7);


        }
    }


}


