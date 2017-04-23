package com.bobby.Chip8;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * Created by Bobby on 4/22/2017.
 */
public class Chip8Test {

    Chip8 chip8;
    Cpu cpu;

    public Chip8Test() {
        chip8 = new Chip8();
        cpu = new Cpu(chip8);
    }

    @Test
    public void testReturnFromSubroutine() {
        for (int address = 0x200; address < 0xFFFF; address += 0x10) {
            chip8.ram.write(address & 0x00FF, cpu.sp);
            chip8.ram.write((address & 0xFF00) >> 8, cpu.sp + 1);
            cpu.sp += 2;
            cpu.pc = 0;
            cpu.instructionSet.ReturnFromSubroutine();
            assertEquals(address, cpu.pc);
        }
    }

    @Test
    public void testJumpToAddress() {
        for (int address = 0x0; address < 0xFFFF; address += 0x10) {
            cpu.opcode = address;
            cpu.pc = 0;
            assertEquals(0, cpu.pc);
            cpu.instructionSet.JumpToLocation();
            assertEquals(address & 0x0FFF, cpu.pc);
        }
    }

    @Test
    public void testJumpToSubroutine() {
        for (int address = 0x200; address < 0xFFFF; address += 0x10) {
            cpu.opcode = address;
            cpu.sp = 0;
            cpu.pc = 0x100;
            cpu.instructionSet.CallSubroutine();
            assertEquals(address & 0x0FFF, cpu.pc);
            assertEquals(2, cpu.sp);
            assertEquals(0, chip8.ram.read(0));
            assertEquals(1, chip8.ram.read(1));
        }
    }

    @Test
    public void testSkipIfRegisterEqualValue() {
        for (int register = 0; register < 0x10; register++) {
            for (int value = 0; value < 0xFF; value += 0x10) {
                for (int regValue = 0; regValue < 0xFF; regValue++) {
                    cpu.opcode = register << 8;
                    cpu.opcode += value;
                    cpu.V[register] = (short) regValue;
                    cpu.pc = 0;
                    cpu.instructionSet.SkipNextIfVxEqualKk();
                    if (value == regValue) {
                        assertEquals(2, cpu.pc);
                    } else {
                        assertEquals(0, cpu.pc);
                    }
                }
            }
        }
    }

    @Test
    public void testSkipIfRegisterNotEqualValue() {
        for (int register = 0; register < 0x10; register++) {
            for (int value = 0; value < 0xFF; value += 0x10) {
                for (int regValue = 0; regValue < 0xFF; regValue++) {
                    cpu.opcode = register << 8;
                    cpu.opcode += value;
                    cpu.V[register] = (short) regValue;
                    cpu.pc = 0;
                    cpu.instructionSet.SkipNextIfVxNotEqualKk();
                    if (value != regValue) {
                        assertEquals(2, cpu.pc);
                    } else {
                        assertEquals(0, cpu.pc);
                    }
                }
            }
        }
    }

    @Test
    public void testSkipIfRegisterEqualRegister() {
        for (int register = 0; register < 0x10; register++) {
            cpu.V[register] = (short) register;
        }

        for (int register1 = 0; register1 < 0x10; register1++) {
            for (int register2 = 0; register2 < 0x10; register2++) {
                cpu.opcode = register1;
                cpu.opcode <<= 4;
                cpu.opcode += register2;
                cpu.opcode <<= 4;
                cpu.pc = 0;
                cpu.instructionSet.SkipNextIfVxEqualVy();
                if (register1 == register2) {
                    assertEquals(2, cpu.pc);
                } else {
                    assertEquals(0, cpu.pc);
                }
            }
        }
    }

    @Test
    public void testSkipIfRegisterNotEqualRegister() {
        for (int register = 0; register < 0x10; register++) {
            cpu.V[register] = (short) register;
        }

        for (int register1 = 0; register1 < 0x10; register1++) {
            for (int register2 = 0; register2 < 0x10; register2++) {
                cpu.opcode = register1;
                cpu.opcode <<= 4;
                cpu.opcode += register2;
                cpu.opcode <<= 4;
                cpu.pc = 0;
                cpu.instructionSet.SkipNextIfVxNotEqualVy();
                if (register1 != register2) {
                    assertEquals(2, cpu.pc);
                } else {
                    assertEquals(0, cpu.pc);
                }
            }
        }
    }

    @Test
    public void testMoveValueToRegister() {
        int value = 0x23;

        for (int register = 0; register < 0x10; register++) {
            cpu.opcode = 0x60 + register;
            cpu.opcode <<= 8;
            cpu.opcode += value;
            cpu.instructionSet.SetVxEqualKk();
            for (int registerToCheck = 0; registerToCheck < 0x10; registerToCheck++) {
                if (registerToCheck != register) {
                    assertEquals(0, cpu.V[registerToCheck]);
                } else {
                    assertEquals(value, cpu.V[registerToCheck]);
                }
            }
            cpu.V[register] = 0;
        }
    }

    @Test
    public void testAddValueToRegister() {
        for (int register = 0; register < 0x10; register++) {
            for (int registerValue = 0; registerValue < 0xFF; registerValue += 0x10) {
                for (int value = 0; value < 0xFF; value++) {
                    cpu.V[register] = (short) registerValue;
                    cpu.opcode = register << 8;
                    cpu.opcode += value;
                    cpu.instructionSet.SetVxAddKk();
                    if (value + registerValue < 256) {
                        assertEquals(value + registerValue, cpu.V[register]);
                    } else {
                        assertEquals(value + registerValue - 256,
                                cpu.V[register]);
                    }
                }
            }
        }
    }

    @Test
    public void testMoveRegisterIntoRegister() {
        for (int source = 0; source < 0x10; source++) {
            for (int target = 0; target < 0x10; target++) {
                if (source != target) {
                    cpu.V[target] = 0x32;
                    cpu.V[source] = 0;
                    cpu.opcode = source << 8;
                    cpu.opcode += (target << 4);
                    cpu.instructionSet.SetVxEqualVy();
                    assertEquals(0x32, cpu.V[source]);
                }
            }
        }
    }

    @Test
    public void testLogicalOr() {
        for (int source = 0; source < 0x10; source++) {
            for (int target = 0; target < 0x10; target++) {
                if (source != target) {
                    for (int sourceVal = 0; sourceVal < 0xFF; sourceVal += 0x10) {
                        for (int targetVal = 0; targetVal < 0xFF; targetVal += 0x10) {
                            cpu.V[source] = (short) sourceVal;
                            cpu.V[target] = (short) targetVal;
                            cpu.opcode = source << 8;
                            cpu.opcode += (target << 4);
                            cpu.instructionSet.SetVxOrVy();
                            assertEquals(sourceVal | targetVal, cpu.V[source]);
                        }
                    }
                }
            }
        }
    }

    @Test
    public void testLogicalAnd() {
        for (int source = 0; source < 0x10; source++) {
            for (int target = 0; target < 0x10; target++) {
                if (source != target) {
                    for (int sourceVal = 0; sourceVal < 0xFF; sourceVal += 0x10) {
                        for (int targetVal = 0; targetVal < 0xFF; targetVal += 0x10) {
                            cpu.V[source] = (short) sourceVal;
                            cpu.V[target] = (short) targetVal;
                            cpu.opcode = source << 8;
                            cpu.opcode += (target << 4);
                            cpu.instructionSet.SetVxAndVy();
                            assertEquals(sourceVal & targetVal, cpu.V[source]);
                        }
                    }
                }
            }
        }
    }

    @Test
    public void testExclusiveOr() {
        for (int source = 0; source < 0x10; source++) {
            for (int target = 0; target < 0x10; target++) {
                if (source != target) {
                    for (int sourceVal = 0; sourceVal < 0xFF; sourceVal += 0x10) {
                        for (int targetVal = 0; targetVal < 0xFF; targetVal += 0x10) {
                            cpu.V[source] = (short) sourceVal;
                            cpu.V[target] = (short) targetVal;
                            cpu.opcode = source << 8;
                            cpu.opcode += (target << 4);
                            cpu.instructionSet.SetVxXorVy();
                            assertEquals(sourceVal ^ targetVal, cpu.V[source]);
                        }
                    }
                }
            }
        }
    }

    @Test
    public void testAddToRegister() {
        for (int source = 0; source < 0xF; source++) {
            for (int target = 0; target < 0xF; target++) {
                if (source != target) {
                    for (int sourceVal = 0; sourceVal < 0xFF; sourceVal += 0x10) {
                        for (int targetVal = 0; targetVal < 0xFF; targetVal += 0x10) {
                            cpu.V[source] = (short) sourceVal;
                            cpu.V[target] = (short) targetVal;
                            cpu.opcode = source << 8;
                            cpu.opcode += (target << 4);
                            cpu.instructionSet.SetVxAddVy();
                            if ((sourceVal + targetVal) > 255) {
                                assertEquals(sourceVal + targetVal - 256,
                                        cpu.V[source]);
                                assertEquals(1, cpu.V[0xF]);
                            } else {
                                assertEquals(sourceVal + targetVal,
                                        cpu.V[source]);
                                assertEquals(0, cpu.V[0xF]);
                            }
                        }
                    }
                }
            }
        }
    }

    @Test
    public void testSubtractRegisterFromRegister() {
        for (int source = 0; source < 0xF; source++) {
            for (int target = 0; target < 0xF; target++) {
                if (source != target) {
                    for (int sourceVal = 0; sourceVal < 0xFF; sourceVal += 0x10) {
                        for (int targetVal = 0; targetVal < 0xFF; targetVal += 0x10) {
                            cpu.V[source] = (short) sourceVal;
                            cpu.V[target] = (short) targetVal;
                            cpu.opcode = source << 8;
                            cpu.opcode += (target << 4);
                            cpu.instructionSet.SetVxSubVy();
                            if (sourceVal > targetVal) {
                                assertEquals(sourceVal - targetVal,
                                        cpu.V[source]);
                                assertEquals(1, cpu.V[0xF]);
                            } else {
                                assertEquals(sourceVal - targetVal + 256,
                                        cpu.V[source]);
                                assertEquals(0, cpu.V[0xF]);
                            }
                        }
                    }
                }
            }
        }
    }

    @Test
    public void testRightShift() {
        for (int register = 0; register < 0xF; register++) {
            for (int value = 0; value < 0xFF; value++) {
                cpu.V[register] = (short) value;
                cpu.opcode = register << 8;
                for (int index = 1; index < 8; index++) {
                    int shiftedValue = value >> index;
                    cpu.V[0xF] = 0;
                    int bitZero = cpu.V[register] & 1;
                    cpu.instructionSet.SetVxShiftRight();
                    assertEquals(shiftedValue, cpu.V[register]);
                    assertEquals(bitZero, cpu.V[0xF]);
                }
            }
        }
    }

    @Test
    public void testSubtractRegisterFromRegister1() {
        for (int source = 0; source < 0xF; source++) {
            for (int target = 0; target < 0xF; target++) {
                if (source != target) {
                    for (int sourceValue = 0; sourceValue < 0xFF; sourceValue += 10) {
                        for (int targetValue = 0; targetValue < 0xF; targetValue++) {
                            cpu.V[source] = (short) sourceValue;
                            cpu.V[target] = (short) targetValue;
                            cpu.opcode = source << 8;
                            cpu.opcode += (target << 4);
                            cpu.instructionSet.SetVxVySubVx();
                            if (targetValue > sourceValue) {
                                assertEquals(targetValue - sourceValue,
                                        cpu.V[source]);
                                assertEquals(1, cpu.V[0xF]);
                            } else {
                                assertEquals(256 + targetValue - sourceValue,
                                        cpu.V[source]);
                                assertEquals(0, cpu.V[0xF]);
                            }
                        }
                    }
                }
            }
        }
    }

    @Test
    public void testLeftShift() {
        for (int register = 0; register < 0xF; register++) {
            for (int value = 0; value < 256; value++) {
                cpu.V[register] = (short) value;
                cpu.opcode = register << 8;
                int shiftedValue = value;
                for (int index = 1; index < 8; index++) {
                    shiftedValue = value << index;
                    int bitSeven = (shiftedValue & 0x100) >> 9;
                    shiftedValue = shiftedValue & 0xFFFF;
                    cpu.V[0xF] = 0;
                    cpu.instructionSet.SetVxShiftLeft();
                    assertEquals(shiftedValue, cpu.V[register]);
                    assertEquals(bitSeven, cpu.V[0xF]);
                }
            }
        }
    }

    @Test
    public void testLoadIndexWithValue() {
        for (int value = 0; value < 0x10000; value++) {
            cpu.opcode = value;
            cpu.instructionSet.SetIEqualNNN();
            assertEquals(value & 0x0FFF, cpu.I);
        }
    }

    @Test
    public void testGenerateRandomNumber() {
        for (int register = 0; register < 0xF; register++) {
            for (int value = 0; value < 0xFF; value += 10) {
                cpu.V[register] = -1;
                cpu.opcode = register << 8;
                cpu.opcode += value;
                cpu.instructionSet.SetVxRandAndKk();
                assertTrue(cpu.V[register] >= 0);
                assertTrue(cpu.V[register] <= 255);
            }
        }
    }

    @Test
    public void testMoveDelayTimerIntoRegister() {
        for (int register = 0; register < 0xF; register++) {
            for (int value = 0; value < 0xFF; value += 10) {
                cpu.delay_timer = (short) value;
                cpu.opcode = register << 8;
                cpu.V[register] = 0;
                cpu.instructionSet.SetVxEqualDelayTimer();
                assertEquals(value, cpu.V[register]);
            }
        }
    }

    @Test
    public void testMoveRegisterIntoDelayRegister() {
        for (int register = 0; register < 0xF; register++) {
            for (int value = 0; value < 0xFF; value += 10) {
                cpu.V[register] = (short) value;
                cpu.opcode = register << 8;
                cpu.delay_timer = 0;
                cpu.instructionSet.SetDelayTimerEqualVx();
                assertEquals(value, cpu.delay_timer);
            }
        }
    }

    @Test
    public void testLoadIndexWithSprite() {
        for (int number = 0; number < 0x10; number++) {
            cpu.I = 0xFFF;
            cpu.V[0] = (short) number;
            cpu.opcode = 0xF029;
            cpu.instructionSet.SetIToVxSpriteLocation();
            assertEquals(number * 5, cpu.I);
        }
    }

    @Test
    public void testStoreBCDInMemory() {
        for (int number = 0; number < 0x100; number++) {
            String bcdValue = String.valueOf(number);
            if (number < 100) {
                bcdValue = "0" + bcdValue;
            }
            if (number < 10) {
                bcdValue = "0" + bcdValue;
            }
            cpu.I = 0;
            cpu.V[0] = (short) number;
            cpu.opcode = 0xF033;
            cpu.instructionSet.StoreBcdAtI();
            assertEquals(bcdValue.charAt(0), String.valueOf(chip8.ram.read(0))
                    .charAt(0));
            assertEquals(bcdValue.charAt(1), String.valueOf(chip8.ram.read(1))
                    .charAt(0));
            assertEquals(bcdValue.charAt(2), String.valueOf(chip8.ram.read(2))
                    .charAt(0));
        }
    }

    @Test
    public void testReadRegistersFromMemory() {
        int index = 0x500;
        cpu.I = index;

        for (int register = 0; register < 0xF; register++) {
            chip8.ram.write(register + 0x89, index + register);
        }

        for (int register = 0; register < 0xF; register++) {
            for (int registerToSet = 0; registerToSet < 0xF; registerToSet++) {
                cpu.V[registerToSet] = 0;
            }

            cpu.opcode = 0xF000;
            cpu.opcode += (register << 8);
            cpu.opcode += 0x65;
            cpu.instructionSet.ReadRegistersAtI();
            for (int registerToCheck = 0; registerToCheck <= 0xF; registerToCheck++) {
                if (registerToCheck > register) {
                    assertEquals(0, cpu.V[registerToCheck]);
                } else {
                    assertEquals(registerToCheck + 0x89,
                            cpu.V[registerToCheck]);
                }
            }
        }
    }

    @Test
    public void testSkipIfKeyPressedSkipsCorrectly() {
        for (int register = 0; register < 0xF; register++) {
            cpu.V[register] = 9;
            cpu.opcode = register << 8;
            cpu.pc = 0;
            cpu.instructionSet.SkipNextIfVxPressed();
            assertEquals(2, cpu.pc);
        }
    }

}