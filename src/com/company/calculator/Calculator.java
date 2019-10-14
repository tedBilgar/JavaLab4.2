package com.company.calculator;

import com.company.calculator.exceptions.ZeroDividingRunTimeException;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;

public final class Calculator extends JFrame implements ActionListener {

    private BigDecimal operand1;
    private boolean isDotPressed = false;
    private final StringBuilder displayValue;
    private int operandCount = 0;
    private Operator previousOperator = null;
    private Display display;
    private Action lastAction;
    private static final double MAX_VALUE = 999_999d;

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

    private void cleanDisplayValue(){
        displayValue.delete(0, displayValue.length());
    }

    private void pressedNumber(String number) {
        if (Double.parseDouble(display.getValue()) < MAX_VALUE || lastAction == Action.OPERATION) {

            lastAction = Action.NUM;
            if (this.previousOperator == Operator.EQUAL) {
                previousOperator = null;
                clearDisplay();
                isDotPressed = false;
            }

            if ((displayValue.toString().isEmpty() || displayValue.toString().equals("0"))){
                if (displayValue.toString().length() > 0) displayValue.deleteCharAt(0);
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
        if (previousOperator == Operator.EQUAL) operand1 = new BigDecimal(display.getValue());
    }
    private void pressedClear() {
        previousOperator = null;
        operand1 = BigDecimal.ZERO;
        isDotPressed = false;
        clearDisplay();
        operandCount = 0;
    }

    private void calculate(Operator operator, BigDecimal b) {
        isDotPressed = false;
        BigDecimal operandBigDecimal1 = operand1;
        BigDecimal operandBigDecimal2 = b;
        switch (operator) {
            case PLUS:
                operandBigDecimal1 = operandBigDecimal1.add(operandBigDecimal2).stripTrailingZeros();
                break;
            case MINUS:
                operandBigDecimal1 = operandBigDecimal1.subtract(operandBigDecimal2).stripTrailingZeros();
                break;
            case MULTIPLY:
                operandBigDecimal1 = operandBigDecimal1.multiply(operandBigDecimal2).stripTrailingZeros();
                break;
            case DIVIDE:
                if (b.compareTo(BigDecimal.ZERO) == 0 ) throw new ZeroDividingRunTimeException();
                operandBigDecimal1 = operandBigDecimal1.divide(operandBigDecimal2, 6, RoundingMode.HALF_UP).stripTrailingZeros();
                break;
        }
        operand1 = new BigDecimal(operandBigDecimal1.toPlainString());
    }

    private void unaryOperator(Operator operator, BigDecimal operand2){
        switch (operator) {
            case DOUBLE_GRADE:
                operand2 = operand2.pow(2);
                break;
            case INVERSE_SIGN:
                isDotPressed = false;
                operand2 = operand2.negate();
                break;
        }
        displayValue.delete(0, displayValue.length());
        displayValue.append(operand2);
        display.setValue("" + operand2.toString());
        operand1 = new BigDecimal(operand2.toString());
    }

    private void pressedOperator(Operator operator) {
        BigDecimal operand2 = displayValue.length() > 0
                ? new BigDecimal(display.getValue()) : operand1;

        try {
            // Проверка если оператор EQUAL
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
                        operand1 = new BigDecimal(operand2.toString());
                    }
                }
                clearDisplay();
            }

            isDotPressed = false;
            display.setValue(operand1.toString());

            if (previousOperator != Operator.INVERSE_SIGN) {
                lastAction = Action.OPERATION;
            }
            this.previousOperator = operator;
        } catch (ZeroDividingRunTimeException zeroException){
            pressedClear();
            display.setValue(zeroException.getMessage());
        }
    }

    private void addDotToNumber(){
        if (!isDotPressed){
            isDotPressed = true;
            String addingPart = display.getValue().isEmpty() ? "0." : display.getValue() + ".";

            if (lastAction != Action.OPERATION) {
                cleanDisplayValue();
                displayValue.append(addingPart);
                display.setValue(displayValue.toString());
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
