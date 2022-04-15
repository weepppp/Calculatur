package com.calculator;

import javax.swing.*;

public class TestCalculator {


    @SuppressWarnings("unused")
    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel("com.sun.java.swing.plaf.windows.WindowsLookAndFeel");
        } catch (Exception e) {
            e.printStackTrace();

        }
        Calculator cal = new Calculator();
    }
}
