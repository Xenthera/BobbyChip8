package com.bobby.Chip8;



import org.apache.commons.io.IOUtils;

import java.io.*;


import javax.sound.midi.*;


/**
 * Created by bobbylucero on 3/26/17.
 */
public class Chip8 {
    public boolean verbose = false;

    public Cpu cpu;
    public Memory ram;
    public Keyboard keyboard;


    public int gfx[];


    public boolean drawFlag;


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
        this.gfx = new int[64 * 32];


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
        this.cpu.sp = 0;
        this.cpu.V = new int[16];
        this.gfx = new int[64 * 32];
        this.cpu.stack = new int[16];
        this.ram.writeIntArray(fontSet, 0);
    }







    public void update(){
        if(this.delay_timer > 0)
            --this.delay_timer;

        if(this.sound_timer != 0){
            this.sound_timer--;
            if(!tone) {
                tone = true;

                midiChannel.noteOn(74,80);
            }
        }
        if(sound_timer == 0 && midiChannel != null){
            tone = false;
            midiChannel.noteOff(74);
        }
    }





    public String hex(int number){
        return "0x" + Integer.toHexString(number);
    }



    public void keyPressed(char key){
        this.keyboard.keyPressed(key);
    }

    public void keyReleased(char key){
        this.keyboard.keyReleased(key);
    }


}
