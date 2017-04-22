package com.company.Chip8;

/**
 * Created by bobbylucero on 4/2/17.
 */
public class Memory {

    private int[] data;

    public Memory(int size) {
        this.data = new int[size];
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
