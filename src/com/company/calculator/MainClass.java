package com.company.calculator;

import java.awt.*;

public class MainClass {

    public static void main(String[] args) {
        EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {

                Calculator calc = new Calculator();
                calc.setVisible(true);
            }
        });
    }

}
