package com.bobby;

import com.bobby.Chip8.Chip8;
import processing.core.PApplet;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

// M A I N  C L A S S //////////////////////////////////////////////////////////////////////

public class Main extends PApplet{
    Debugger debugger;
    private Chip8 chip8;
    private boolean debugMode = false;

    public static void main(String[] args) {
        PApplet.main("com.bobby.Main");
    }
    public void settings(){
        size(10 * 64,10 * 32);
    }

    public void setup(){
        File game = openFilePicker();
        chip8 = new Chip8();
        chip8.initialize();
        chip8.loadProgram(game);
        frameRate(60);

        initiateDebugger();
    }


    public void initiateDebugger() {
        // D E B U G G I N G ////////////
        debugger = new Debugger();
        debugger.setSize(new Dimension(190, 280));
        debugger.setLocationRelativeTo(null);
        debugger.setVisible(true);

    }

    public File openFilePicker() {
        File game = null;
        final JFileChooser fc = new JFileChooser();
        fc.setCurrentDirectory(new File(System.getProperty("user.dir") + "/roms"));
        int returnValue = fc.showOpenDialog(null);
        if (returnValue == JFileChooser.APPROVE_OPTION) {
            game = fc.getSelectedFile();
        } else {
            System.exit(0);
        }
        return game;
    }
    public void draw(){
        if (!debugMode) {
            chip8.update();

        }
        debugger.update();
        chip8.draw(this);
    }
    public void keyPressed(){
        chip8.keyPressed(key);
    }
    public void keyReleased(){
        chip8.keyReleased(key);
    }

    class Debugger extends JFrame {
        JTable table;
        JButton debug, step;
        JButton increase, decrease;
        JLabel V, Stack;
        JTextField multiplier;


        public Debugger() {
            super("Debugger Window");
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception e) {

            }
            setResizable(false);
            setDefaultCloseOperation(3);
            setLayout(null);

            debug = new JButton("Debug: Off");
            debug.setBounds(5, 5, 80, 30);
            debug.setMargin(new Insets(0, 0, 0, 0));
            debug.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    debugMode = !debugMode;
                }
            });

            step = new JButton("Step");
            step.setBounds(5, 40, 80, 30);
            step.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    if (debugMode) {
                        chip8.update();
                    }
                }
            });

            increase = new JButton(">");
            increase.setBounds(142, 225, 40, 20);
            increase.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    chip8.INSTRUCTIONS_PER_FRAME += 1;
                }
            });

            decrease = new JButton("<");
            decrease.setBounds(3, 225, 40, 20);
            decrease.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    if (chip8.INSTRUCTIONS_PER_FRAME > 1)
                        chip8.INSTRUCTIONS_PER_FRAME -= 1;
                }
            });

            multiplier = new JTextField("600");
            multiplier.setBounds(45, 225, 95, 20);
            multiplier.setEditable(false);


            table = new JTable(16, 2);
            table.setRowHeight(12);
            table.setBounds(100, 20, 75, table.getRowHeight() * table.getRowCount());
            table.setGridColor(new Color(56, 56, 56));
            table.setBackground(new Color(134, 134, 134));


            table.setValueAt(1, 0, 0);

            V = new JLabel("V");
            V.setBounds(100, 5, 35, 15);
            V.setHorizontalAlignment(JLabel.CENTER);
            add(V);
            Stack = new JLabel("Stack");
            Stack.setBounds(137, 5, 35, 15);
            Stack.setHorizontalAlignment(JLabel.CENTER);
            add(Stack);

            add(debug);
            add(step);
            add(table);
            add(increase);
            add(decrease);
            add(multiplier);

        }

        public void update() {
            for (int i = 0; i < 16; i++) {
                table.setValueAt(chip8.cpu.hex(chip8.cpu.V[i]), i, 0);
                table.setValueAt(chip8.cpu.hex(chip8.cpu.stack[i]), i, 1);
            }

            if (!debugMode) {
                step.setEnabled(false);
                debug.setText("Debug: Off");
            } else {
                step.setEnabled(true);
                debug.setText("Debug: On");
            }

            multiplier.setText(String.valueOf(60 * chip8.INSTRUCTIONS_PER_FRAME));

        }
    }
}


