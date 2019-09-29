package com.company.calculator;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public final class Calculator extends JFrame implements ActionListener {

    private double operand1;
    private boolean isDotPressed = false;
    private final StringBuilder displayValue;
    private int operandCount = 0;
    private Operator previousOperator = null;
    private Display display;
    private Action lastAction;

    private enum Operator {

        EQUAL, PLUS, MINUS, MULTIPLY, DIVIDE, INVERSE_SIGN;
    }

    private enum Action {
        NUM, OPERATION
    }

    public Calculator() {
        super("Calculator");
        displayValue = new StringBuilder(32);
        initComponents();
        setSize(230, 200);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }

    private void initComponents() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

        display = new Display();
        panel.add(display);

        panel.add(new Keypad(this));
        this.add(panel);
    }

    private void clearDisplay() {
        displayValue.delete(0, displayValue.length());
        display.setValue("0");
    }

    private void pressedNumber(String number) {
        lastAction = Action.NUM;
        if (this.previousOperator == Operator.EQUAL) {
            previousOperator = null;
            clearDisplay();
            isDotPressed = false;
        }

        displayValue.append(number);
        display.setValue(displayValue.toString());
    }

    private void pressedClear() {
        previousOperator = null;
        operand1 = 0;
        clearDisplay();
        operandCount = 0;
    }

    private void calculate(Operator operator, double b) {
        switch (operator) {
            case PLUS:
                operand1 += b;
                break;
            case MINUS:
                operand1 -= b;
                break;
            case MULTIPLY:
                operand1 *= b;
                break;
            case DIVIDE:
                operand1 /= b;
                break;
            case INVERSE_SIGN:
                b = -b;
                break;
        }
    }

    private void pressedOperator(Operator operator) {
        double operand2 = displayValue.length() > 0
                ? Double.parseDouble(displayValue.toString()) : operand1;

        if (operator == Operator.EQUAL) {

            calculate(previousOperator, operand2);
            operandCount = 0;

        } else if (operator == Operator.INVERSE_SIGN){
            operand2 = -operand2;
            displayValue.delete(0, displayValue.length());
            displayValue.append(operand2);
            display.setValue("" + (long) operand2);
            return;
        }else {
            if (lastAction != Action.OPERATION) {
                operandCount++;
            }
            if (lastAction != Action.OPERATION && operandCount > 1) {
                calculate(previousOperator, operand2);
            }else{
                if (previousOperator != Operator.EQUAL) {
                    operand1 = operand2;
                }
            }
            clearDisplay();
        }

        if (isDotPressed || isDecimalValue(operand1)) {
            display.setValue("" + operand1);
        } else {
            display.setValue("" + (long) operand1);
        }
        lastAction = Action.OPERATION;
        this.previousOperator = operator;
    }

    private boolean isDecimalValue(double value){
        String[] splitter = Double.toString(value).split("\\.");
        long decimalPart = Long.parseLong(splitter[1]);
        if (decimalPart > 0) return true;
        else return false;
    }

    @Override
    public void actionPerformed(ActionEvent e) {

        JButton src = (JButton) e.getSource();
        String text = src.getText();
        switch (text) {
            case "0":
            case "1":
            case "2":
            case "3":
            case "4":
            case "5":
            case "6":
            case "7":
            case "8":
            case "9":
                pressedNumber(src.getText());
                break;

            case "=":
                pressedOperator(Operator.EQUAL);
                break;
            case "+":
                pressedOperator(Operator.PLUS);
                break;
            case "-":
                pressedOperator(Operator.MINUS);
                break;
            case "x":
                pressedOperator(Operator.MULTIPLY);
                break;
            case "/":
                pressedOperator(Operator.DIVIDE);
                break;
            case "+/-":
                pressedOperator(Operator.INVERSE_SIGN);
                break;
            case ".":
                if (!isDotPressed) {
                    isDotPressed = true;
                    displayValue.append('.');
                }
                break;
            case "C":
                pressedClear();
                break;
        }
    }
}
