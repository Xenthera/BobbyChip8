package com.bobby.Chip8;


import org.apache.commons.io.IOUtils;
import processing.core.PApplet;

import javax.sound.midi.MidiChannel;
import javax.sound.midi.MidiSystem;
import javax.sound.midi.Synthesizer;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;


public class Chip8 {
    private final int INSTRUCTIONS_PER_FRAME = 10;
    public boolean verbose = false;
    public Cpu cpu;
    public Memory ram;
    public Keyboard keyboard;
    public Graphics graphics;
    public int[] fontSet = {0xF0, 0x90, 0x90, 0x90, 0xF0,
            0x20, 0x60, 0x20, 0x20, 0x70,
            0xF0, 0x10, 0xF0, 0x80, 0xF0,
            0xF0, 0x10, 0xF0, 0x10, 0xF0,
            0x90, 0x90, 0xF0, 0x10, 0x10,
            0xF0, 0x80, 0xF0, 0x10, 0xF0,
            0xF0, 0x80, 0xF0, 0x90, 0xF0,
            0xF0, 0x10, 0x20, 0x40, 0x40,
            0xF0, 0x90, 0xF0, 0x90, 0xF0,
            0xF0, 0x90, 0xF0, 0x10, 0xF0,
            0xF0, 0x90, 0xF0, 0x90, 0x90,
            0xE0, 0x90, 0xE0, 0x90, 0xE0,
            0xF0, 0x80, 0x80, 0x80, 0xF0,
            0xE0, 0x90, 0x90, 0x90, 0xE0,
            0xF0, 0x80, 0xF0, 0x80, 0xF0,
            0xF0, 0x80, 0xF0, 0x80, 0x80 };

    Synthesizer midiSynth;
    MidiChannel midiChannel;
    boolean tone = false;

    public Chip8(){
        this.ram = new Memory(4096);
        this.cpu = new Cpu(this);
        this.keyboard = new Keyboard();
        this.graphics = new Graphics();


        try {
            midiSynth = MidiSystem.getSynthesizer();
            midiSynth.open();
            midiChannel = midiSynth.getChannels()[0];
            midiChannel.programChange(86);


        } catch (Exception e){
            System.out.println("Sound unavailable");
        }

    }

    public void loadProgram(String path){
        this.loadProgram(new File(path));
    }

    public void loadProgram(File file){
        try {
            InputStream is = new FileInputStream(file);
            byte[] data = IOUtils.toByteArray(is);
            this.ram.writeByteArray(data, 0x200);
            is.close();
        }catch (Exception e){
            System.out.println("Unable to load program: " + e.getMessage());
            System.exit(0);
        }

    }

    public void initialize(){
        this.cpu.intialize();
        this.graphics.initialize();
        this.ram.initialize();
        this.ram.writeIntArray(fontSet, 0);
    }

    public void tickTimers() {
        if (this.cpu.delay_timer > 0)
            --this.cpu.delay_timer;
    }

    public void tickCpu() {
        for (int i = 0; i < INSTRUCTIONS_PER_FRAME; i++) {
            this.cpu.emulateCycle();
        }
    }

    public void update(){
        System.out.println(keyboard.currentKeyPressed);
        this.tickTimers();
        this.tickCpu();

        if (this.cpu.sound_timer != 0) {
            this.cpu.sound_timer--;
            if(!tone) {
                tone = true;

                midiChannel.noteOn(74,80);
            }
        }
        if (this.cpu.sound_timer == 0 && midiChannel != null) {
            tone = false;
            midiChannel.noteOff(74);
        }
    }

    public void draw(PApplet g) {
        this.graphics.draw(g);
    }


    public void keyPressed(char key){
        this.keyboard.keyPressed(key);
    }

    public void keyReleased(char key){
        this.keyboard.keyReleased(key);
    }


}
