package com.example.computermoblie;

import android.content.Intent;
import android.util.Log;
import android.view.View;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.List;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.Stack;

public class ReversePolish {
    public List<String> SumNumber;


    Stack<BigDecimal> SymbolStack = new Stack<>();

    //表示没有计算错误的情况
    private boolean Error = false;

    public static final int LeftBrack = 0;   //左括号优先级
    public static final int AandS = 1;   //加减法
    public static final int MandD = 2;   //乘除
    public static final int RightBrack = 3;   //右括号优先级

    // 中缀转后缀
    public Queue<String> InfixToSuffix() {
        Queue<String> queue = new ArrayDeque<>();
        Stack<String> swapStack = new Stack<>();

        for (int i = 0; i < SumNumber.size(); i++) {
            String isStr = SumNumber.get(i);

            if (!IsSymbol(isStr)) {
                queue.add(isStr); // 将数字直接添加到输出队列中
            } else {
                if (isStr.equals("(")) {
                    swapStack.push(isStr);
                } else if (isStr.equals(")")) {
                    while (!swapStack.isEmpty() && !swapStack.peek().equals("(")) {
                        queue.add(swapStack.pop());
                    }

                    if (!swapStack.isEmpty()) {
                        swapStack.pop(); // 弹出 "("
                    } else {
                        Error = true; // 处理不匹配的右括号
                        break;
                    }
                } else {
                    while (!swapStack.isEmpty() && JudgmentPriority(swapStack.peek(), isStr)) {
                        queue.add(swapStack.pop());
                    }
                    swapStack.push(isStr);
                }
            }
        }

        while (!swapStack.isEmpty()) {
            queue.add(swapStack.pop());
        }

        if (Error) {
            return null;
        } else {
            return queue;
        }
    }
    public String EvaluatePostfixExpressions() {
        Error = false;  // 表示运算未发现错误
        BigDecimal ret = null; // 表示最后的运算结果
        Queue<String> queue = InfixToSuffix();  // 获取后缀表达式
        if (queue != null){
            Log.d("WQWQ",queue.toString());

            while (!queue.isEmpty()) {
                String str = queue.remove();

                if (!IsSymbol(str)) {
                    //保存数据
                    try {
                        BigDecimal number = new BigDecimal(str);
                        SymbolStack.push(number);
                    } catch (NumberFormatException e) {
                        Log.d("Number","出现空值");
                    }
                } else {
                    if (SymbolStack.size() < 2) {
                        Error = true; // 栈中元素不足2个，运算错误
                        break;
                    }

                    BigDecimal op2 = SymbolStack.pop();
                    BigDecimal op1 = SymbolStack.pop();
                    BigDecimal result = null;

                    switch (str) {
                        case "+":
                            result = op1.add(op2);
                            break;
                        case "-":
                            result = op1.subtract(op2);
                            break;
                        case "×":
                            result = op1.multiply(op2);
                            break;
                        case "÷":
                            if (op2.compareTo(BigDecimal.ZERO) == 0) {
                                Error = true; // 除数为零，运算错误
                                break;
                            }
                            result = op1.divide(op2, 3, RoundingMode.HALF_UP);
                            // Check if the divisor is an integer
                            if (op1.remainder(op2).compareTo(BigDecimal.ZERO) == 0) {
                                result = result.setScale(0, RoundingMode.HALF_UP); // Convert to an integer
                            }
                            break;
                        default:
                            Error = true; // 非法的运算符，运算错误
                            break;
                    }

                    if (Error) {
                        break;
                    } else {
                        SymbolStack.push(result);
                    }
                }
            }

            if (Error) {
                return "Error"; // 出现错误后直接返回错误信息
            } else {
                if (!SymbolStack.isEmpty()) {
                    ret = SymbolStack.pop();
                }

                // Use stripTrailingZeros() to remove trailing zeros after the decimal point
                ret = ret.stripTrailingZeros();

                // Convert the result to an integer if there are no decimal places
                if (ret.scale() <= 0) {
                    return ret.toBigInteger().toString(); // Return the result as a BigInteger (integer)
                } else {
                    // 使用toEngineeringString()方法将结果输出为工程计数法
                    return ret.toEngineeringString();
                }
            }
        }else {
            return "Error";
        }
    }

    public ReversePolish(List<String> sumNumber) {
        SumNumber = sumNumber;
    }

    //判断是否为符号
    public boolean IsSymbol(String str) {
        boolean isSymbole = false;
        if (str.equals("+")) {
            isSymbole = true;
        } else if (str.equals("-")) {
            isSymbole = true;
        } else if (str.equals("×")) {
            isSymbole = true;
        } else if (str.equals("÷")) {
            isSymbole = true;
        } else if (str.equals("(")) {
            isSymbole = true;
        } else if (str.equals(")")) {
            isSymbole = true;
        }

        return isSymbole;
    }

    //判断优先级
    public boolean JudgmentPriority(String PreantStr, String SunStr) {
        //默认PreantStr优先级小于SunStr
        boolean value = false;

        if (SymbolValue(PreantStr) >= SymbolValue(SunStr)) {
            value = true;
        }

        return value;
    }

    //判断属于什么符号
    public int SymbolValue(String str) {
        int Value = LeftBrack;

        if (str.equals("+")) {
            Value = AandS;
        } else if (str.equals("-")) {
            Value = AandS;
        } else if (str.equals("×")) {
            Value = MandD;
        } else if (str.equals("÷")) {
            Value = MandD;
        } else if (str.equals("(")) {
            Value = LeftBrack;
        } else if (str.equals(")")) {
            Value = RightBrack;
        }
        return Value;
    }

    public List<String> getSumNumber() {
        return SumNumber;
    }
}