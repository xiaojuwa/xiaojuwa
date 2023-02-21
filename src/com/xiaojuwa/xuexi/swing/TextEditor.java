package com.xiaojuwa.xuexi.swing;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

public class TextEditor extends JFrame implements ActionListener {

    private JTextArea textArea;
    private JMenuBar menuBar;
    private JMenu fileMenu;
    private JMenuItem newFile, openFile, saveFile;

    public TextEditor() {
        super("Text Editor");
        setSize(500, 400);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        textArea = new JTextArea();
        JScrollPane scrollPane = new JScrollPane(textArea);
        add(scrollPane, BorderLayout.CENTER);
        menuBar = new JMenuBar();
        fileMenu = new JMenu("File");
        newFile = new JMenuItem("New");
        newFile.addActionListener(this);
        openFile = new JMenuItem("Open");
        openFile.addActionListener(this);
        saveFile = new JMenuItem("Save");
        saveFile.addActionListener(this);
        fileMenu.add(newFile);
        fileMenu.add(openFile);
        fileMenu.add(saveFile);
        menuBar.add(fileMenu);
        setJMenuBar(menuBar);
        setVisible(true);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == newFile) {
            textArea.setText("");
        } else if (e.getSource() == openFile) {
            String fileName = JOptionPane.showInputDialog("Enter file name:");
            if (fileName != null && fileName.trim().length() > 0) {
                try {
                    FileReader fr = new FileReader(fileName);
                    BufferedReader br = new BufferedReader(fr);
                    String line = "";
                    textArea.setText("");
                    while ((line = br.readLine()) != null) {
                        textArea.append(line + "\n");
                    }
                    br.close();
                    fr.close();
                } catch (IOException ex) {
                    JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        } else if (e.getSource() == saveFile) {
            String fileName = JOptionPane.showInputDialog("Enter file name:");
            if (fileName != null && fileName.trim().length() > 0) {
                try {
                    FileWriter fw = new FileWriter(fileName);
                    fw.write(textArea.getText());
                    fw.close();
                } catch (IOException ex) {
                    JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        }
    }

    public static void main(String[] args) {
        new TextEditor();
    }
}
