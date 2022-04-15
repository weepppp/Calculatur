package com.calculator;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.math.BigDecimal;
import java.util.Vector;

/*
先写到一个大类里，再试着分解成几个类
分解思路：暂定为
 */


// 主类
// 设置面板组件
public class Calculator {
//    public static void main(String[] args) {

    //在最前面定义一些成员变量，方便响应逻辑的实现
    String result = ""; // 输出的结果；不能用null，null不是一个对象，内存中没有空间，调用方法会显示空指针异常
    String str1 = "0"; // 四维运算中的操作符1
    String str2 = "0"; // 四维运算中的操作数2
    String signal = "+"; // 运算符
    // 定义五个状态开关
    int k1 = 1; // 逻辑1：定义输入次序，1代表要写入str1，2代表要写入str2。因为有多位数存在，第二个数字不一定是第二个运算位
    int k2 = 1; // 逻辑2：记录符号键的次数，>1代表进行的是多符号运算。因为写逻辑时一般就写一个符号位的一层运算
    int k3 = 1; // 逻辑3：用于标识str1能否被清零，1能，不等于1不能
    int k4 = 1; // 逻辑4：用于标识str2能否被清零，1能，不等于1不能。不懂为什么，难道不是任何时候都能清零吗？
    int k5 = 1; // 逻辑5：控制小数点能否被输入，1能，不等于1不能。逻辑上在没有数字和符号后是不可以输入小数点的
    JButton store; // 记录是否连续按下符号键。因为最后一个符号键会取代前一个符号键
    /*存储之前输入的运算符。
      Vector 主要用在事先不知道数组的大小，或者只是需要一个可以改变大小的数组的情况
      Vector(int size,int incr)构造方法创建指定大小的向量，并且增量用 incr 指定。增量表示向量每次增加的元素数目。
     */
    Vector vt = new Vector(20, 10);


    /*

    1.编写顶层框架

     */

    JFrame frame = new JFrame("Calculator");

    /*

    2.编写组件

     */

    // 用JTextField显示操作和计算结果
    JTextField result_TextField = new JTextField(result, 20);
    // 清除按钮
    JButton clear_Button = new JButton("Clear");
    // 数字按钮0-9
    JButton button0 = new JButton("0");
    JButton button1 = new JButton("1");
    JButton button2 = new JButton("2");
    JButton button3 = new JButton("3");
    JButton button4 = new JButton("4");
    JButton button5 = new JButton("5");
    JButton button6 = new JButton("6");
    JButton button7 = new JButton("7");
    JButton button8 = new JButton("8");
    JButton button9 = new JButton("9");
    // 操作符按钮
    JButton button_Dian = new JButton(".");
    JButton button_jia = new JButton("+");
    JButton button_jian = new JButton("-");
    JButton button_cheng = new JButton("*");
    JButton button_chu = new JButton("/");
    // 等于按钮
    JButton button_dy = new JButton("=");

    /*

    3.编写面板 内部类

     */

    public Calculator() {


        //
        button0.setMnemonic(KeyEvent.VK_0);
        //

        //
        result_TextField.setHorizontalAlignment(JTextField.RIGHT);

        //把除输入框和clear的下面布局视为用来放按钮的第一个面板
        JPanel pan = new JPanel();
        // 确定要使用的布局为网格布局，四行四列，边距为五像素
        pan.setLayout(new GridLayout(4, 4, 5, 5));
        // 把按钮组件放进此面板，并按计算器的常规顺序摆放好
        pan.add(button7);
        pan.add(button8);
        pan.add(button9);
        pan.add(button_chu);
        pan.add(button4);
        pan.add(button5);
        pan.add(button6);
        pan.add(button_cheng);
        pan.add(button1);
        pan.add(button2);
        pan.add(button3);
        pan.add(button_jian);
        pan.add(button0);
        pan.add(button_Dian);
        pan.add(button_dy);
        pan.add(button_jia);
        // 设置复合边框边距
        pan.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        // 第2个面板
        JPanel pan2 = new JPanel();
        // 选择东西南北的边界布局
        pan2.setLayout(new BorderLayout());
        pan2.add(result_TextField, BorderLayout.WEST);
        pan2.add(clear_Button, BorderLayout.EAST);

    /*

    4.把面板添加到框架

     */

        // 设置位置
        frame.setLocation(300, 200);
        // 不可自己调节大小
        frame.setResizable(false);
        // 将当前面板布局改为BorderLayou，讲面板放上去
        frame.getContentPane().setLayout(new BorderLayout());
        frame.getContentPane().add(pan2, BorderLayout.NORTH);
        frame.getContentPane().add(pan, BorderLayout.CENTER);
        // 根据窗口里面的布局及组件的preferredSize来确定frame的最佳大小
        frame.pack();
        // 可视化
        frame.setVisible(true);


        // 下面所有的ActionListener都定义为局部内部类，写在构造函数中

    /*

     5、事件响应类

     */

        // 数字键的响应
        // 主要用于处理数字存入到对应的变量中去（第一个操作数存入str1，第二个操作数存入str2）
        // ActionListener动作时间监听器，当你在点击按钮时希望可以实现一个操作就使用该接口
        // 该接口只用重写实现一个方法叫做actionPerformed(ActionEvent e)这个方法。这个方法就是你希望触发事件时程序要做什么
        // 实现过程大体如下：编写一个ActionListener类的侦听器，组件注册该侦听器，侦听器内部要编写这个actionPerformed方法。
        class Listener implements ActionListener {
            @SuppressWarnings("unchecked")
            public void actionPerformed(ActionEvent e) {
                // 获取数据源，从数据源中获取输入的数据
                String ss = ((JButton) e.getSource()).getText();
                // 从获取的数据中得到符号键，添加到vt
                store = (JButton) e.getSource();
                vt.add(store);

                // 输入为操作数1的一部分
                if (k1 == 1) {

                    if (k3 == 1) {
                        str1 = "";
                        k5 = 1;
                    }
                    str1 = str1 + ss;

                    k3 = k3 + 1;

                    result_TextField.setText(str1);

                    // 输入为操作数2的一部分
                } else if (k1 == 2) {

                    if (k4 == 1) {
                        str2 = "";
                        k5 = 1;
                    }
                    str2 = str2 + ss;

                    k4 = k4 + 1;

                    result_TextField.setText(str2);
                }

            }

        }

        // 运算符号的响应
        class Listener_signal implements ActionListener {
            @SuppressWarnings("unchecked")
            public void actionPerformed(ActionEvent e) {
                String ss2 = ((JButton) e.getSource()).getText();
                store = (JButton) e.getSource();
                vt.add(store);
                // 如果是多符号运算
                if (k2 == 1) {
                    k1 = 2;
                    k5 = 1;
                    signal = ss2;
                    k2 = k2 + 1;
                } else {
                    int a = vt.size();
                    JButton c = (JButton) vt.get(a - 2);

                    // 如果继续按下运算符，上一次运算的结果储存进str1，进行运算后，把结果储存到str2
                    if (!(c.getText().equals("+")) && !(c.getText().equals("-")) && !(c.getText().equals("/"))) {
                        cal();
                        str1 = result;
                        k1 = 2;
                        k5 = 1;
                        k4 = 1;
                        signal = ss2;
                    }
                    k2 = k2 + 1;
                }

            }
        }

        // 清除的响应
        // 把变量值清空或者变回初始值
        class Listener_clear implements ActionListener {
            @SuppressWarnings("unchecked")
            public void actionPerformed(ActionEvent e) {
                store = (JButton) e.getSource();
                vt.add(store);
                k5 = 1;
                k2 = 1;
                k1 = 1;
                k3 = 1;
                k4 = 1;
                str1 = "0";
                str2 = "0";
                signal = "";
                result = "";
                result_TextField.setText(result);
                vt.clear();
            }
        }

        // 等于的响应
        class Listener_by implements ActionListener {
            @SuppressWarnings("unchecked")
            public void actionPerformed(ActionEvent e) {

                // 按下键，调用cal（）进行运算，最后还原开关的值
                store = (JButton) e.getSource();
                vt.add(store);
                cal();
                k1 = 1;
                k2 = 1;
                k3 = 1;
                k4 = 1;

                //为如果接着运算做准备
                str1 = result;
            }

        }


        // 小数点的响应
        class Listener_xiaos implements ActionListener {

            @SuppressWarnings("unchecked")
            public void actionPerformed(ActionEvent e) {
                store = (JButton) e.getSource();
                vt.add(store);
                if (k5 == 1) {
                    String ss2 = ((JButton) e.getSource()).getText();
                    if (k1 == 1) {
                        if (k3 == 1) {
                            str1 = "";
                            // 还原开关k5状态
                            k5 = 1;
                        }
                        str1 = str1 + ss2;

                        k3 = k3 + 1;

                        // 显示结果
                        result_TextField.setText(str1);

                    } else if (k1 == 2) {
                        if (k4 == 1) {
                            str2 = "";
                            // 还原开关k5的状态
                            k5 = 1;
                        }
                        str2 = str2 + ss2;

                        k4 = k4 + 1;

                        result_TextField.setText(str2);
                    }
                }

                k5 = k5 + 1;
            }
        }


        /*
        7.注册监听器
         */

        // 绑定UI的响应时间
        // 监听等于键
        Listener_by jt_by = new Listener_by();
        button_dy.addActionListener(jt_by);

        // 监听数字键
        Listener jt = new Listener();
        button0.addActionListener(jt);
        button1.addActionListener(jt);
        button2.addActionListener(jt);
        button3.addActionListener(jt);
        button4.addActionListener(jt);
        button5.addActionListener(jt);
        button6.addActionListener(jt);
        button7.addActionListener(jt);
        button8.addActionListener(jt);
        button9.addActionListener(jt);

        // 监听符号键
        Listener_signal jt_signal = new Listener_signal();
        button_jia.addActionListener(jt_signal);
        button_jian.addActionListener(jt_signal);
        button_cheng.addActionListener(jt_signal);
        button_chu.addActionListener(jt_signal);

        // 监听清除键
        Listener_clear jt_c = new Listener_clear();
        clear_Button.addActionListener(jt_c);

        // 监听小数点键
        Listener_xiaos jt_xs = new Listener_xiaos();
        button_Dian.addActionListener(jt_xs);

        // 窗口关闭时间的响应程序
        frame.addWindowFocusListener(new WindowAdapter() {
            public void windowOpened(WindowEvent e) {
                System.exit(0);
            }
        });

    }


        /*
        6.编写计算逻辑
         */

    public void cal() {
        // 将操作数转为double类型，定义a2和b2来存储str1、2
        double a2;
        double b2;
        // 储存运算符
        String c = signal;
        double result2 = 0;

        // 下面把有无输入符号作为能否计算的依据
        if (c.equals("")) {
            result_TextField.setText("Please input operator");
        } else {

            // 解决只输入小数点的问题
            if (str1.equals("."))
                str1 = "0.0";
            if (str2.equals("."))
                str2 = "0.0";

            // 将一个字符串转化成一个Double对象（Double是一个类），然后再调用这个对象的doubleValue()方法返回其对应的double数值
            a2 = Double.valueOf(str1).doubleValue();
            b2 = Double.valueOf(str2).doubleValue();

            if (c.equals("+")) {
                result2 = a2 + b2;
            }
            if (c.equals("-")) {
                result2 = a2 - b2;
            }
            if (c.equals("*")) {
                // 为了确保精度，把double存入浮点类BigDecimal
                BigDecimal m1 = new BigDecimal(Double.toString(a2));
                BigDecimal m2 = new BigDecimal(Double.toString(b2));
                result2 = m1.multiply(m2).doubleValue();
            }
            if (c.equals("/")) {
                if (b2 == 0) {
                    result2 = 0;
                } else {
                    result2 = a2 / b2;
                }
            }

            result = ((new Double(result2)).toString());

            result_TextField.setText(result);
        }
    }
}










