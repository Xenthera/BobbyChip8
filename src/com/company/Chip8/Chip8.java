package com.company.Chip8;



import org.apache.commons.io.IOUtils;

import javax.swing.*;
import java.io.*;
import java.security.InvalidParameterException;

import java.util.concurrent.ThreadLocalRandom;


import javax.sound.midi.*;


/**
 * Created by bobbylucero on 3/26/17.
 */
public class Chip8 {
    public boolean verbose = false;
    //35 opcodes
    public int opcode;
    //4k memory
    public int memory[];
    //15 general purpose registers, 16th used for carry flag
    public int V[];
    //index register
    public int I;
    //program counter
    public int pc;
    /* Memory Map
    0x000-0x1FF - Chip 8 interpreter (contains font set in emu)
    0x050-0x0A0 - Used for the built in 4x5 pixel font set (0-F)
    0x200-0xFFF - Program ROM and work RAM
     */

    public int gfx[];

    public int delay_timer;
    public int sound_timer;

    public int stack[];
    public int sp;



    public int keys[];

    public boolean drawFlag;

    public boolean idle;

    public short registerToWriteKey;

    public char[] fontSet = { 0xF0, 0x90, 0x90, 0x90, 0xF0,
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
        this.memory = new int[4096];
        this.V = new int[16];
        this.gfx = new int[64 * 32];
        this.stack = new int[16];
        this.keys = new int[16];

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
        try {
            InputStream is = new FileInputStream(new File(path));
            byte[] data = IOUtils.toByteArray(is);

            for (int i = 0; i < data.length; i++) {
                this.memory[0x200 + i] = data[i] & 0xFF;
            }
            is.close();

        }catch (Exception e){
            System.out.println("Unable to load program: " + e.getMessage());
        }

    }

    public void loadProgram(File file){
        try {
            InputStream is = new FileInputStream(file);
            byte[] data = IOUtils.toByteArray(is);

            for (int i = 0; i < data.length; i++) {
                this.memory[0x200 + i] = data[i] & 0xFF;
            }
            is.close();

        }catch (Exception e){
            System.out.println("Unable to load program: " + e.getMessage());
        }

    }

    public void initialize(){
        this.pc = 0x200;
        this.opcode = 0;
        this.I = 0;
        this.sp = 0;


        this.V = new int[16];
        this.gfx = new int[64 * 32];
        this.stack = new int[16];


        for (int i = 0; i < 80; i++) {
            this.memory[i] = fontSet[i];
        }
    }

    public void setIdle(boolean idle){
        this.idle = idle;
    }



    public void emulateCycle(){

        this.pc = this.pc & 0xFFFF;
        int opcode = this.memory[this.pc] << 8 | this.memory[this.pc + 1];
        if(this.idle){
            for (int i = 0; i < keys.length; i++) {
                if(keys[i] == 1){
                    this.V[registerToWriteKey] = i;
                    this.idle = false;
                }
            }

            return;
        }
        try {
            this.decodeOpcode(opcode);
        }catch (Exception e){
            e.printStackTrace();
            System.exit(0);
        }



        for (int i = 0; i < V.length; i++) {
            this.V[i] = this.V[i] & 0xFF;
        }
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


    public void decodeOpcode(int opcode) throws InvalidParameterException{
        //System.out.println("0x"+Integer.toHexString(opcode));

        switch (opcode & 0xF000) {
            case 0x0000: {
                switch (opcode & 0x000F) {
                    case 0x0:
                        for (int i = 0; i < this.gfx.length; i++) {
                            this.gfx[i] = 0;
                        }
                        this.drawFlag = true;
                        this.pc += 2;
                        break;
                    case 0xE:

                            --this.sp;
                            this.pc = this.stack[this.sp];
                            this.stack[this.sp] = 0x0;

                }
                break;
            }
            case 0x1000: {
                this.pc = opcode & 0x0FFF;
                break;
            }
            case 0x2000: {

                this.stack[this.sp] = this.pc += 2;
                this.sp++;
                this.pc = opcode & 0x0FFF;
                break;
            }
            case 0x3000: {
                int Vx = (opcode & 0x0F00) >> 8;
                int kk = opcode & 0x00FF;

                if (this.V[Vx] == kk) {
                    this.pc += 2;
                }
                this.pc += 2;
                break;
            }
            case 0x4000: {
                int Vx = this.V[(opcode & 0x0F00) >> 8];
                int kk = opcode & 0x00FF;
                if (Vx != kk) {
                    this.pc += 2;
                }
                this.pc += 2;
                break;
            }
            case 0x5000: {
                int Vx = this.V[(opcode & 0x0F00) >> 8];
                int Vy = this.V[(opcode & 0x00F0) >> 4];
                if (Vx == Vy) {
                    this.pc += 2;
                }
                this.pc += 2;
                break;
            }
            case 0x6000: {
                this.V[(opcode & 0x0F00) >> 8] = opcode & 0x00FF;
                this.pc += 2;
                break;
            }
            case 0x7000: {
                int V = (opcode & 0x0F00) >> 8;
                int kk = opcode & 0x00FF;
                this.V[V] = (this.V[V] + kk);
                this.pc += 2;
                break;
            }
            case 0x8000: {
                switch (opcode & 0x000F) {
                    case 0x0: {
                        int Vx = (opcode & 0x0F00) >> 8;
                        int Vy = (opcode & 0x00F0) >> 4;
                        this.V[Vx] = this.V[Vy];
                        this.pc += 2;
                        break;
                    }
                    case 0x1: {
                        int Vx = (opcode & 0x0F00) >> 8;
                        int Vy = (opcode & 0x00F0) >> 4;
                        this.V[Vx] |= this.V[Vy];
                        this.pc += 2;
                        break;
                    }
                    case 0x2: {
                        int Vx = (opcode & 0x0F00) >> 8;
                        int Vy = (opcode & 0x00F0) >> 4;
                        this.V[Vx] &= this.V[Vy];
                        this.pc += 2;
                        break;
                    }
                    case 0x3: {
                        int Vx = (opcode & 0x0F00) >> 8;
                        int Vy = (opcode & 0x00F0) >> 4;
                        this.V[Vx] ^= this.V[Vy];
                        this.pc += 2;
                        break;
                    }
                    case 0x4: {
                        int Vx = (opcode & 0x0F00) >> 8;
                        int Vy = (opcode & 0x00F0) >> 4;
                        int result = this.V[Vx] + this.V[Vy];
                        if (result > 0xFF) {
                            this.V[0xF] = 1;
                        } else {
                            this.V[0xF] = 0;
                        }
                        this.V[Vx] = result & 0xFF;
                        this.pc += 2;
                        break;
                    }
                    case 0x5: {
                        int Vx = (opcode & 0x0F00) >> 8;
                        int Vy = (opcode & 0x00F0) >> 4;
                        int result = this.V[Vx] - this.V[Vy];
                        if (this.V[Vx] > this.V[Vy]) {
                            this.V[0xF] = 1;
                        } else {
                            this.V[0xF] = 0;
                        }
                        this.V[Vx] = result;
                        this.pc += 2;
                        break;
                    }
                    case 0x6: {
                        int Vx = (opcode & 0x0F00) >> 8;
                        if ((this.V[Vx] & 0x1) == 1) {
                            this.V[0xF] = 1;
                        } else {
                            this.V[0xF] = 0;
                        }
                        this.V[Vx] >>= 1;
                        this.pc += 2;
                        break;
                    }
                    case 0x7: {
                        int Vx = (opcode & 0x0F00) >> 8;
                        int Vy = (opcode & 0x00F0) >> 4;
                        int result = this.V[Vy] - this.V[Vx];
                        if (this.V[Vy] > this.V[Vx]) {
                            this.V[0xF] = 1;
                        } else {
                            this.V[0xF] = 0;
                        }
                        this.V[Vx] = result;
                        this.pc += 2;
                        break;
                    }
                    case 0xE: {
                        int Vx = (opcode & 0x0F00) >> 8;
                        if ((this.V[Vx] & 0x80) >> 8 == 1) {
                            this.V[0xF] = 1;
                        } else {
                            this.V[0xF] = 0;
                        }
                        this.V[Vx] <<= 1;
                        this.pc += 2;
                        break;
                    }

                }
                break;
            }
            case 0x9000:{
                int Vx = (opcode & 0x0F00) >> 8;
                int Vy = (opcode & 0x00F0) >> 4;
                if(this.V[Vx] != this.V[Vy]){
                    this.pc += 2;
                }
                this.pc += 2;
                break;
            }
            case 0xA000: {
                this.I = opcode & 0x0FFF;
                this.pc += 2;
                break;
            }
            case 0xB000: {
                this.pc = (opcode & 0x0FFF) + this.V[0x0];
                break;
            }
            case 0xC000: {
                byte randByte = (byte)ThreadLocalRandom.current().nextInt(0, 255);
                int Vx = (opcode & 0x0F00) >> 8;
                int kk = opcode & 0x00FF;
                this.V[Vx] = randByte & kk;
                this.pc += 2;
                break;


            }
            case 0xD000: {
                int x = this.V[(opcode & 0x0F00) >> 8];
                int y = this.V[(opcode & 0x00F0) >> 4];
                int height = opcode & 0x000F;
                int pixel;

                V[0xF] = 0;
                for (int yline = 0; yline < height; yline++)
                {
                    pixel = this.memory[this.I + yline];
                    for (int xline = 0; xline < 8; xline++)
                    {
                        if ((pixel & (0x80 >> xline)) != 0)
                        {
                            if((x + xline + ((y + yline) * 64)) < 2048) {
                                if (this.gfx[(x + xline + ((y + yline) * 64))] == 1) {
                                    this.V[0xF] = 1;
                                }
                            this.gfx[x + xline + ((y + yline) * 64)] ^= 1;

                            }
                        }
                    }
                }
                this.drawFlag = true;
                this.pc += 2;
                break;
            }
            case 0xE000: {
                switch(opcode & 0x00FF)
                {
                    case 0x009E: // EX9E: Skips the next instruction if the key stored in VX is pressed
                        if(keys[V[(opcode & 0x0F00) >> 8]] != 0)
                            pc += 4;
                        else
                            pc += 2;
                        break;

                    case 0x00A1: // EXA1: Skips the next instruction if the key stored in VX isn't pressed
                        if(keys[V[(opcode & 0x0F00) >> 8]] == 0)
                            pc += 4;
                        else
                            pc += 2;
                        break;

                    default:
                        break;
                }

                break;
            }
            case 0xF000: {

                switch (opcode & 0x00FF){
                    case 0x0007: {
                        int Vx = (opcode & 0x0F00) >> 8;

                        this.V[Vx] = this.delay_timer;
                        this.pc += 2;
                        break;
                    }
                    case 0x0015: {
                        int Vx = (opcode & 0x0F00) >> 8;
                        this.delay_timer = this.V[Vx];
                        this.pc += 2;
                        break;
                    }
                    case 0x0018: {
                        int Vx = (opcode & 0x0F00) >> 8;
                        this.sound_timer = this.V[Vx];
                        this.pc += 2;
                        break;
                    }
                    case 0x001E: {
                        int Vx = (opcode & 0x0F00) >> 8;
                        this.I = this.I + this.V[Vx];
                        this.pc += 2;
                        break;
                    }
                    case 0x000A: {
                        this.registerToWriteKey = (short)((opcode & 0x0F00) >> 8);
                        this.setIdle(true);
                        this.pc += 2;
                        break;
                    }


                    case 0x0033: {

                        this.memory[this.I]     = this.V[(opcode & 0x0F00) >> 8] / 100;
                        this.memory[this.I + 1] = (this.V[(opcode & 0x0F00) >> 8] / 10) % 10;
                        this.memory[this.I + 2] = (this.V[(opcode & 0x0F00) >> 8] % 100) % 10;
                        this.pc += 2;
                        break;
                    }
                    case 0x0029: {
                        int Vx = (opcode & 0x0F00) >> 8;

                        this.I = this.V[Vx] * 5;

                        pc += 2;
                        break;
                    }
                    case 0x0055: {
                        int numRegisters = (opcode & 0x0F00) >> 8;
                        for (int i = 0; i < numRegisters; i++) {
                            this.memory[this.I + i] = this.V[i];
                        }
                        //I += ((opcode & 0x0F00) >> 8) + 1;
                        this.pc += 2;
                        break;
                    }
                    case 0x0065: {
                        int numRegister = (opcode & 0x0F00) >> 8;
                        for(int i = 0; i <= numRegister; i++){
                            this.V[i] = this.memory[this.I + i];
                        }
                        I += ((opcode & 0x0F00) >> 8) + 1;
                        this.pc += 2;
                        break;
                    }

                    default: {
                        System.out.println("OPCODE DISC.");
                        break;
                    }
                }
                break;
            }
            default: {
                JOptionPane.showMessageDialog(null, "Unknown opcode: 0x" + Integer.toHexString(opcode), "Unknown Opcode", JOptionPane.ERROR_MESSAGE);
                throw new InvalidParameterException();
                //this.pc += 2;


            }
        }
    }

    public String hex(int number){
        return "0x" + Integer.toHexString(number);
    }



    public void keyPressed(char key){
        switch (key){
            case '1':
                this.keys[0x1] = 1;
                break;
            case '2':
                this.keys[0x2] = 1;
                break;
            case '3':
                this.keys[0x3] = 1;
                break;
            case '4':
                this.keys[0xc] = 1;
                break;
            case 'q':
                this.keys[0x4] = 1;
                break;
            case 'w':
                this.keys[0x5] = 1;
                break;
            case 'e':
                this.keys[0x6] = 1;
                break;
            case 'r':
                this.keys[0xd] = 1;
                break;
            case 'a':
                this.keys[0x7] = 1;
                break;
            case 's':
                this.keys[0x8] = 1;
                break;
            case 'd':
                this.keys[0x9] = 1;
                break;
            case 'f':
                this.keys[0xe] = 1;
                break;
            case 'z':
                this.keys[0xa] = 1;
                break;
            case 'x':
                this.keys[0x0] = 1;
                break;
            case 'c':
                this.keys[0xb] = 1;
                break;
            case 'v':
                this.keys[0xf] = 1;
                break;

        }
    }

    public void keyReleased(char key){
        switch (key){
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
