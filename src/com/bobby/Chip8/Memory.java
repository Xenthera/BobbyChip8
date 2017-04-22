package com.bobby.Chip8;

/**
 * Created by bobbylucero on 4/2/17.
 */
public class Memory {

    private int[] data;
    private int size;

    public Memory(int size) {
        this.data = new int[size];
        this.size = size;
    }

    public String hex(int number) {
        return "0x" + Integer.toHexString(number);
    }

    public void initialize() {
        this.data = new int[this.size];
    }

    public void writeIntArray(int[] data, int startAddress) {
        for (int i = 0; i < data.length; i++) {
            this.data[startAddress + i] = data[i] & 0xFF;
        }
    }

    public void writeByteArray(byte[] data, int startAddress) {
        for (int i = 0; i < data.length; i++) {
            this.data[startAddress + i] = data[i] & 0xFF;
        }


    }

    public int read(int address) {
        return this.data[address];
    }

    public void write(int value, int address) {
        this.data[address] = value & 0xFF;
    }


}
