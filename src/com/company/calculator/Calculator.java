package com.company.calculator;

import com.company.calculator.exceptions.ZeroDividingRunTimeException;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.math.BigDecimal;
import java.math.MathContext;

public final class Calculator extends JFrame implements ActionListener {

    private double operand1;
    private boolean isDotPressed = false;
    private final StringBuilder displayValue;
    private int operandCount = 0;
    private Operator previousOperator = null;
    private Display display;
    private Action lastAction;
    private static final double MAX_VALUE = 999_999_999_999_999d;

    private enum Operator {

        EQUAL, PLUS, MINUS, MULTIPLY, DIVIDE, INVERSE_SIGN, DOUBLE_GRADE;
    }

    private enum Action {
        NUM, OPERATION
    }

    public Calculator() {
        super("Calculator");
        displayValue = new StringBuilder(32);
        initComponents();
        setSize(460, 400);
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
        if (Double.parseDouble(display.getValue()) < MAX_VALUE) {

            lastAction = Action.NUM;
            if (this.previousOperator == Operator.EQUAL) {
                previousOperator = null;
                clearDisplay();
                isDotPressed = false;
            }

            displayValue.append(number);
            display.setValue(displayValue.toString());
        }
    }

    private void pressedDeleteLastNumber(){
        String prevValue = display.getValue();

        clearDisplay();
        String cutValue;
        if(prevValue.length() == 1){
            cutValue = "0";
        }else {
            cutValue = prevValue.substring(0, prevValue.length() - 1);
        }
        display.setValue(cutValue);
        displayValue.append(prevValue);
        if (previousOperator == Operator.EQUAL) operand1 = Double.parseDouble(display.getValue());
    }
    private void pressedClear() {
        previousOperator = null;
        operand1 = 0;
        isDotPressed = false;
        clearDisplay();
        operandCount = 0;
    }

    private void calculate(Operator operator, double b) {
        isDotPressed = false;
        BigDecimal operandBigDecimal1 = new BigDecimal(operand1, MathContext.DECIMAL32);
        BigDecimal operandBigDecimal2 = new BigDecimal(b, MathContext.DECIMAL32);
        switch (operator) {
            case PLUS:
                operandBigDecimal1 = operandBigDecimal1.add(operandBigDecimal2);
                break;
            case MINUS:
                operandBigDecimal1 = operandBigDecimal1.subtract(operandBigDecimal2);
                break;
            case MULTIPLY:
                operandBigDecimal1 = operandBigDecimal1.multiply(operandBigDecimal2);
                break;
            case DIVIDE:
                if (b == 0) throw new ZeroDividingRunTimeException();
                operandBigDecimal1 = operandBigDecimal1.divide(operandBigDecimal2);
                break;
        }
        operand1 = operandBigDecimal1.doubleValue();
    }

    private void unaryOperator(Operator operator, double operand2){
        isDotPressed = false;
        switch (operator) {
            case DOUBLE_GRADE:
                operand2 = Math.pow(operand2, 2);
                break;
            case INVERSE_SIGN:
                operand2 = -operand2;
                break;
        }
        displayValue.delete(0, displayValue.length());
        displayValue.append(operand2);
        display.setValue("" + (long) operand2);
        operand1 = operand2;
    }

    private void pressedOperator(Operator operator) {
        double operand2 = displayValue.length() > 0
                ? Double.parseDouble(display.getValue()) : operand1;

        try {
            if (operator == Operator.EQUAL) {

                calculate(previousOperator, operand2);
                operandCount = 0;

            } else if (operator == Operator.INVERSE_SIGN || operator == Operator.DOUBLE_GRADE) {
                unaryOperator(operator, operand2);
                return;
            } else {
                if (lastAction != Action.OPERATION || previousOperator == Operator.EQUAL) {
                    operandCount++;
                }
                if (lastAction != Action.OPERATION && operandCount > 1) {
                    calculate(previousOperator, operand2);
                } else {
                    if (previousOperator != Operator.EQUAL) {
                        operand1 = operand2;
                    }
                }
                clearDisplay();
            }

            isDotPressed = false;

            if (isDecimalValue(operand1)) {
                display.setValue("" + operand1);
            } else {
                display.setValue("" + (long) operand1);
            }
            if (previousOperator != Operator.INVERSE_SIGN) {
                lastAction = Action.OPERATION;
            }
            this.previousOperator = operator;
        } catch (ZeroDividingRunTimeException zeroException){
            pressedClear();
            display.setValue(zeroException.getMessage());
        }
    }

    private boolean isDecimalValue(double value){
        String[] splitter = Double.toString(value).split("\\.");
        long decimalPart = Long.parseLong(splitter[1]);
        if (decimalPart > 0) return true;
        else return false;
    }

    private void addDotToNumber(){
        if (!isDotPressed){
            isDotPressed = true;
            String addingPart = ".";

            if (lastAction != Action.OPERATION) {
                if (Double.parseDouble(display.getValue()) == 0) {
                    addingPart = "0.";
                }
                displayValue.append(addingPart);
                display.setValue(display.getValue() + ".");
                lastAction = Action.NUM;
            }else {
                clearDisplay();
                displayValue.append("0.");
                display.setValue("0.");
                lastAction = Action.NUM;
            }
        }
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
                addDotToNumber();
                break;
            case "C":
                pressedClear();
                break;
            case "X^2":
                pressedOperator(Operator.DOUBLE_GRADE);
                break;
            case "<--":
                pressedDeleteLastNumber();
                break;
        }
    }
}
