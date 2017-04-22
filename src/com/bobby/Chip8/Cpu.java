package com.bobby.Chip8;

import javax.swing.*;
import java.security.InvalidParameterException;

/**
 * Created by Bobby on 4/22/2017.
 */
public class Cpu {
    public Chip8 chip8;
    public int opcode;
    public boolean idle = false;
    //15 general purpose registers, 16th used for carry flag
    public int V[];
    //index register
    public int I;
    //program counter
    public int pc;
    public int stack[];
    public int sp;
    public int delay_timer;
    public int sound_timer;
    public short registerToWriteKey;
    private InstructionSet instructionSet;


    public Cpu(Chip8 chip8) {
        this.V = new int[16];
        this.stack = new int[16];
        this.chip8 = chip8;
        instructionSet = new InstructionSet(this, chip8);
    }

    public void intialize() {
        this.sp = 0;
        this.V = new int[16];
        this.stack = new int[16];
        this.pc = 0x200;
    }

    public void emulateCycle() {
        this.pc = this.pc & 0xFFFF;
        if (this.idle) {
            if (chip8.keyboard.currentKeyPressed() != -1) {
                this.V[registerToWriteKey] = chip8.keyboard.currentKeyPressed();
                this.idle = false;
            }
            return;
        }
        try {
            this.decodeOpcode(fetchOpcode());
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(0);
        }


        for (int i = 0; i < V.length; i++) {
            this.V[i] = this.V[i] & 0xFF;
        }
    }

    public int fetchOpcode() {
        return this.chip8.ram.read(this.pc) << 8 | this.chip8.ram.read(this.pc + 1);
    }

    public void setIdle(boolean idle) {
        this.idle = idle;
    }

    public String hex(int number) {
        return "0x" + Integer.toHexString(number);
    }

    public void decodeOpcode(int opcode) throws InvalidParameterException {
        //System.out.println("0x"+Integer.toHexString(opcode));

        this.opcode = opcode;
        switch (opcode & 0xF000) {
            case 0x0000: {
                switch (opcode & 0x000F) {
                    case 0x0:
                        instructionSet.ClearScreen();
                        break;
                    case 0xE:
                        instructionSet.ReturnFromSubroutine();
                }
                break;
            }
            case 0x1000: {
                instructionSet.JumpToLocation();
                break;
            }
            case 0x2000: {
                instructionSet.CallSubroutine();
                break;
            }
            case 0x3000: {
                instructionSet.SkipNextIfVxEqualKk();
                break;
            }
            case 0x4000: {
                instructionSet.SkipNextIfVxNotEqualKk();
                break;
            }
            case 0x5000: {
                instructionSet.SkipNextIfVxEqualVy();
                break;
            }
            case 0x6000: {
                instructionSet.SetVxEqualKk();
                break;
            }
            case 0x7000: {
                instructionSet.SetVxAddKk();
                break;
            }
            case 0x8000: {
                switch (opcode & 0x000F) {
                    case 0x0: {
                        instructionSet.SetVxEqualVy();
                        break;
                    }
                    case 0x1: {
                        instructionSet.SetVxOrVy();
                        break;
                    }
                    case 0x2: {
                        instructionSet.SetVxAndVy();
                        break;
                    }
                    case 0x3: {
                        instructionSet.SetVxXorVy();
                        break;
                    }
                    case 0x4: {
                        instructionSet.SetVxAddVy();
                        break;
                    }
                    case 0x5: {
                        instructionSet.setVxSubVy();
                        break;
                    }
                    case 0x6: {
                        instructionSet.SetVxShiftRight();
                        break;
                    }
                    case 0x7: {
                        instructionSet.SetVxVySubVx();
                        break;
                    }
                    case 0xE: {
                        instructionSet.SetVxShiftLeft();
                        break;
                    }

                }
                break;
            }
            case 0x9000: {
                instructionSet.SkipNextIfVxNotEqualVy();
                break;
            }
            case 0xA000: {
                instructionSet.SetIEqualNNN();
                break;
            }
            case 0xB000: {
                instructionSet.JumpToNnnPlusV0();
                break;
            }
            case 0xC000: {
                instructionSet.SetVxRandAndKk();
                break;


            }
            case 0xD000: {
                instructionSet.DrawSprite();
                break;
            }
            case 0xE000: {
                switch (opcode & 0x00FF) {
                    case 0x009E:
                        instructionSet.SkipNextIfVxPressed();
                        break;

                    case 0x00A1:
                        instructionSet.SkipNextIfVxNotPressed();
                        break;

                    default:
                        break;
                }

                break;
            }
            case 0xF000: {

                switch (opcode & 0x00FF) {
                    case 0x0007: {
                        instructionSet.SetVxEqualDelayTimer();
                        break;
                    }
                    case 0x000A: {
                        instructionSet.WaitForKeyPress();
                        break;
                    }
                    case 0x0015: {
                        instructionSet.SetDelayTimerEqualVx();
                        break;
                    }
                    case 0x0018: {
                        instructionSet.SetSoundTimerEqualVx();
                        break;
                    }
                    case 0x001E: {
                        instructionSet.SetIAddVx();
                        break;
                    }
                    case 0x0029: {
                        instructionSet.SetIToVxSpriteLocation();
                        break;
                    }

                    case 0x0033: {
                        instructionSet.StoreBcdAtI();
                        break;
                    }

                    case 0x0055: {
                        instructionSet.StoreRegistersAtI();
                        break;
                    }
                    case 0x0065: {
                        instructionSet.ReadRegistersAtI();
                        break;
                    }

                    default: {
                        System.out.println("Unknown Opcode: " + hex(opcode));
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
}
