package com.bobby.Chip8;

import java.util.concurrent.ThreadLocalRandom;

/**
 * Created by Bobby on 4/22/2017.
 */
public class InstructionSet {

    Cpu cpu;
    Chip8 chip8;


    public InstructionSet(Cpu cpu, Chip8 chip8) {
        this.cpu = cpu;
        this.chip8 = chip8;
    }

    /*
    00E0 - CLS
    Clear the display.
     */
    void ClearScreen() {
        chip8.graphics.clearScreen();

    }

    /*
    00EE - RET
    Return from a subroutine.
    The interpreter sets the program counter to the address at
    the top of the stack, then subtracts 1 from the stack pointer.
     */
    void ReturnFromSubroutine() {
        this.cpu.sp -= 1;
        this.cpu.pc = chip8.ram.read(this.cpu.sp) << 8;
        this.cpu.sp -= 1;
        this.cpu.pc += chip8.ram.read(this.cpu.sp);
    }

    /*
    1nnn - JP addr
    Jump to location nnn.
    The interpreter sets the program counter to nnn.
     */
    void JumpToLocation() {
        this.cpu.pc = cpu.opcode & 0x0FFF;
    }

    /*
    2nnn - CALL addr
    Call subroutine at nnn.
    The interpreter increments the stack pointer, then puts
    the current PC on the top of the stack. The PC is then set to nnn.
     */
    void CallSubroutine() {
        chip8.ram.write(cpu.pc & 0x00FF, cpu.sp);
        cpu.sp += 1;
        chip8.ram.write((cpu.pc & 0xFF00) >> 8, cpu.sp);
        cpu.sp += 1;
        cpu.pc = cpu.opcode & 0x0FFF;
    }

    /*
    3xkk - SE Vx, byte
    Skip next instruction if Vx = kk.
    The interpreter compares register Vx to kk, and if they are equal, increments the program counter by 2.
     */
    void SkipNextIfVxEqualKk() {
        int sourceRegister = (this.cpu.opcode & 0x0F00) >> 8;
        if (this.cpu.V[sourceRegister] == (this.cpu.opcode & 0x00FF)) {
            this.cpu.pc += 2;
        }

    }

    /*
    4xkk - SNE Vx, byte
    Skip next instruction if Vx != kk.
    The interpreter compares register Vx to kk, and if they are not equal, increments the program counter by 2.
     */
    void SkipNextIfVxNotEqualKk() {
        int Vx = this.cpu.V[(cpu.opcode & 0x0F00) >> 8];
        int kk = cpu.opcode & 0x00FF;
        if (Vx != kk) {
            this.cpu.pc += 2;
        }

    }

    /*
    5xy0 - SE Vx, Vy
    Skip next instruction if Vx = Vy.
    The interpreter compares register Vx to register Vy, and if they are equal, increments the program counter by 2.
     */
    void SkipNextIfVxEqualVy() {
        int Vx = this.cpu.V[(cpu.opcode & 0x0F00) >> 8];
        int Vy = this.cpu.V[(cpu.opcode & 0x00F0) >> 4];
        if (Vx == Vy) {
            this.cpu.pc += 2;
        }

    }

    /*
    6xkk - LD Vx, byte
    Set Vx = kk.
    The interpreter puts the value kk into register Vx.
     */
    void SetVxEqualKk() {
        this.cpu.V[(cpu.opcode & 0x0F00) >> 8] = cpu.opcode & 0x00FF;

    }

    /*
    7xkk - ADD Vx, byte
    Set Vx = Vx + kk.
    Adds the value kk to the value of register Vx, then stores the result in Vx.
     */
    void SetVxAddKk() {
        int targetRegister = (cpu.opcode & 0x0F00) >> 8;
        int temp = cpu.V[targetRegister] + (cpu.opcode & 0x00FF);
        cpu.V[targetRegister] = (temp < 256) ? (short) temp : (short) (temp - 256);

    }

    /*
    8xy0 - LD Vx, Vy
    Set Vx = Vy.
    Stores the value of register Vy in register Vx.
     */
    void SetVxEqualVy() {
        int Vx = (cpu.opcode & 0x0F00) >> 8;
        int Vy = (cpu.opcode & 0x00F0) >> 4;
        this.cpu.V[Vx] = this.cpu.V[Vy];

    }

    /*
    8xy1 - OR Vx, Vy
    Set Vx = Vx OR Vy.
    Performs a bitwise OR on the values of Vx and Vy, then stores the result in Vx. A bitwise OR compares the
    corresponding bits from two values, and if either bit is 1, then the same bit in the result is also 1.
    Otherwise, it is 0.
     */
    //TODO: Finish the rest of the documentation of op-codes.
    void SetVxOrVy() {
        int Vx = (cpu.opcode & 0x0F00) >> 8;
        int Vy = (cpu.opcode & 0x00F0) >> 4;
        this.cpu.V[Vx] |= this.cpu.V[Vy];

    }

    void SetVxAndVy() {
        int Vx = (cpu.opcode & 0x0F00) >> 8;
        int Vy = (cpu.opcode & 0x00F0) >> 4;
        this.cpu.V[Vx] &= this.cpu.V[Vy];

    }

    void SetVxXorVy() {
        int Vx = (cpu.opcode & 0x0F00) >> 8;
        int Vy = (cpu.opcode & 0x00F0) >> 4;
        this.cpu.V[Vx] ^= this.cpu.V[Vy];

    }

    void SetVxAddVy() {
        int Vx = (cpu.opcode & 0x0F00) >> 8;
        int Vy = (cpu.opcode & 0x00F0) >> 4;
        int result = this.cpu.V[Vx] + this.cpu.V[Vy];
        if (result > 0xFF) {
            this.cpu.V[0xF] = 1;
        } else {
            this.cpu.V[0xF] = 0;
        }
        this.cpu.V[Vx] = result & 0xFF;

    }

    void SetVxSubVy() {
        int targetRegister = (cpu.opcode & 0x0F00) >> 8;
        int sourceRegister = (cpu.opcode & 0x00F0) >> 4;
        int resultValue;
        if (cpu.V[targetRegister] > cpu.V[sourceRegister]) {
            resultValue = cpu.V[targetRegister] - cpu.V[sourceRegister];
            cpu.V[0xF] = 1;
        } else {
            resultValue = 256 + cpu.V[targetRegister] - cpu.V[sourceRegister];
            cpu.V[0xF] = 0;
        }
        cpu.V[targetRegister] = (short) resultValue;

    }

    void SetVxShiftRight() {
        int Vx = (cpu.opcode & 0x0F00) >> 8;
        if ((this.cpu.V[Vx] & 0x1) == 1) {
            this.cpu.V[0xF] = 1;
        } else {
            this.cpu.V[0xF] = 0;
        }
        this.cpu.V[Vx] >>= 1;

    }

    void SetVxVySubVx() {
        int targetRegister = (cpu.opcode & 0x0F00) >> 8;
        int sourceRegister = (cpu.opcode & 0x00F0) >> 4;
        int resultValue;
        if (cpu.V[sourceRegister] > cpu.V[targetRegister]) {
            resultValue = cpu.V[sourceRegister] - cpu.V[targetRegister];
            cpu.V[0xF] = 1;
        } else {
            resultValue = 256 + cpu.V[sourceRegister] - cpu.V[targetRegister];
            cpu.V[0xF] = 0;
        }
        cpu.V[targetRegister] = (short) resultValue;

    }

    void SetVxShiftLeft() {
        int Vx = (cpu.opcode & 0x0F00) >> 8;
        if ((this.cpu.V[Vx] & 0x80) >> 8 == 1) {
            this.cpu.V[0xF] = 1;
        } else {
            this.cpu.V[0xF] = 0;
        }
        this.cpu.V[Vx] <<= 1;

    }

    void SkipNextIfVxNotEqualVy() {
        int Vx = (cpu.opcode & 0x0F00) >> 8;
        int Vy = (cpu.opcode & 0x00F0) >> 4;
        if (this.cpu.V[Vx] != this.cpu.V[Vy]) {
            this.cpu.pc += 2;
        }

    }

    void SetIEqualNNN() {
        this.cpu.I = cpu.opcode & 0x0FFF;

    }

    void JumpToNnnPlusV0() {
        this.cpu.pc = (cpu.opcode & 0x0FFF) + this.cpu.V[0x0];
    }

    void SetVxRandAndKk() {
        byte randByte = (byte) ThreadLocalRandom.current().nextInt(0, 255);
        int Vx = (cpu.opcode & 0x0F00) >> 8;
        int kk = cpu.opcode & 0x00FF;
        this.cpu.V[Vx] = randByte & kk;

    }

    void DrawSprite() {
        int x = this.cpu.V[(cpu.opcode & 0x0F00) >> 8];
        int y = this.cpu.V[(cpu.opcode & 0x00F0) >> 4];
        int height = cpu.opcode & 0x000F;
        int pixel;

        this.cpu.V[0xF] = 0;
        for (int yline = 0; yline < height; yline++) {

            pixel = this.chip8.ram.read(this.cpu.I + yline);
            for (int xline = 0; xline < 8; xline++) {
                if ((pixel & (0x80 >> xline)) != 0) {
                    if ((x + xline + ((y + yline) * 64)) < 2048) {
                        if (this.chip8.graphics.gfx[(x + xline + ((y + yline) * 64))] == 1) {
                            this.cpu.V[0xF] = 1;
                        }
                        this.chip8.graphics.gfx[x + xline + ((y + yline) * 64)] ^= 1;

                    }
                }
            }
        }
        this.chip8.graphics.drawFlag = true;

    }

    void SkipNextIfVxPressed() {// EX9E: Skips the next instruction if the key stored in VX is pressed
        int sourceRegister = (cpu.opcode & 0x0F00) >> 8;
        int keyToCheck = cpu.V[sourceRegister];
        if (chip8.keyboard.currentKeyPressed == keyToCheck) {
            this.cpu.pc += 2;
        }
    }

    void SkipNextIfVxNotPressed() { // EXA1: Skips the next instruction if the key stored in VX isn't pressed
        int sourceRegister = (cpu.opcode & 0x0F00) >> 8;
        int keyToCheck = cpu.V[sourceRegister];
        if (chip8.keyboard.currentKeyPressed != keyToCheck) {
            this.cpu.pc += 2;
        }

    }

    void SetVxEqualDelayTimer() {
        int Vx = (this.cpu.opcode & 0x0F00) >> 8;

        this.cpu.V[Vx] = this.cpu.delay_timer;


    }

    void SetDelayTimerEqualVx() {
        int Vx = (this.cpu.opcode & 0x0F00) >> 8;
        this.cpu.delay_timer = this.cpu.V[Vx];


    }

    void SetSoundTimerEqualVx() {
        int Vx = (this.cpu.opcode & 0x0F00) >> 8;
        this.cpu.sound_timer = this.cpu.V[Vx];


    }

    void SetIAddVx() {
        int Vx = (cpu.opcode & 0x0F00) >> 8;
        this.cpu.I = this.cpu.I + this.cpu.V[Vx];


    }

    void WaitForKeyPress() {
        this.cpu.registerToWriteKey = (short) ((cpu.opcode & 0x0F00) >> 8);
        this.cpu.setIdle(true);


    }


    void StoreBcdAtI() {
        this.chip8.ram.write(this.cpu.V[(cpu.opcode & 0x0F00) >> 8] / 100, this.cpu.I);
        this.chip8.ram.write((this.cpu.V[(cpu.opcode & 0x0F00) >> 8] / 10) % 10, this.cpu.I + 1);
        this.chip8.ram.write((this.cpu.V[(cpu.opcode & 0x0F00) >> 8] % 100) % 10, this.cpu.I + 2);


    }

    void SetIToVxSpriteLocation() {
        int Vx = (cpu.opcode & 0x0F00) >> 8;

        this.cpu.I = this.cpu.V[Vx] * 5;


    }

    void StoreRegistersAtI() {
        int numRegisters = (cpu.opcode & 0x0F00) >> 8;
        for (int i = 0; i < numRegisters; i++) {
            this.chip8.ram.write(this.cpu.V[i], this.cpu.I + i);
        }
        //I += ((cpu.opcode & 0x0F00) >> 8) + 1;


    }

    void ReadRegistersAtI() {
        int numRegisters = (cpu.opcode & 0x0F00) >> 8;
        for (int counter = 0; counter <= numRegisters; counter++) {
            cpu.V[counter] = chip8.ram.read(cpu.I + counter);
        }


    }


}
