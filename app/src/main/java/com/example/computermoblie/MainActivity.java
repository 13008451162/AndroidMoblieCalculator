package com.example.computermoblie;

import android.animation.Animator;
import android.animation.AnimatorInflater;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.app.Service;
import android.os.Build;
import android.os.Bundle;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.text.InputType;
import android.util.Log;
import android.view.ContextMenu;
import android.view.MenuItem;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.computermoblie.databinding.ActivityMainBinding;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;


public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    public List<Data> mData = new ArrayList<>();   //储存历史记录

    // 计算器储存总数据的上限
    public List<String> SumNumber = new ArrayList<>();

    //计算器存储的数字上限
    private StringBuilder Number = new StringBuilder();


    //当前输入框字体大小,初始状态为40dp
    private int SIzeOfText = 40;

    public ActivityMainBinding activityMainBinding;

    private boolean GetEqual = false;    //表示没有按下等号

    private boolean NegativeSymbol = false; //表示不是负数的符号

    public String ret; //运算结果

    private static final String Del = "Delete"; //表示删除键

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        activityMainBinding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(activityMainBinding.getRoot());

        //禁用输入法修改EditText文本内容
        activityMainBinding.ScanfEditText.setInputType(InputType.TYPE_NULL);

        //传入各个按键
        //功能键
        activityMainBinding.C.setOnClickListener(this);
        activityMainBinding.DeleteOne.setOnClickListener(this);

        //加减乘除,括号,等于
        activityMainBinding.add.setOnClickListener(this);
        activityMainBinding.Sub.setOnClickListener(this);
        activityMainBinding.Multiply.setOnClickListener(this);
        activityMainBinding.Division.setOnClickListener(this);
        activityMainBinding.leftC.setOnClickListener(this);
        activityMainBinding.RightC.setOnClickListener(this);
        activityMainBinding.equal.setOnClickListener(this);

        //数字，小数点
        activityMainBinding.O.setOnClickListener(this);
        activityMainBinding.OneNum.setOnClickListener(this);
        activityMainBinding.TwoNum.setOnClickListener(this);
        activityMainBinding.ThreeNum.setOnClickListener(this);
        activityMainBinding.FourNum.setOnClickListener(this);
        activityMainBinding.FiveNum.setOnClickListener(this);
        activityMainBinding.SixNum.setOnClickListener(this);
        activityMainBinding.SevenNum.setOnClickListener(this);
        activityMainBinding.EightNum.setOnClickListener(this);
        activityMainBinding.NineNum.setOnClickListener(this);
        activityMainBinding.Point.setOnClickListener(this);


        activityMainBinding.TwoArrowsShrink.setOnClickListener(this);

        //长按触发菜单
        registerForContextMenu(activityMainBinding.ThreePoints);


    }

    @Override
    public void onClick(View view) {
        final int Id = view.getId();
        // 调用按键动画
        animation(view);

        //退出
        if (Id == R.id.TwoArrows_Shrink) {
            finish();
        }


        //功能按钮
        if (Id == R.id.C) {
            //清空显示
            activityMainBinding.ScanfEditText.setText("");
            ret = "";
            activityMainBinding.PrintfText.setText(ret);

            //清空储存的内容
            SumNumber.clear();
            Number.setLength(0);

            //还原字体大小
            SIzeOfText = 40;

        } else {
            if (Id == R.id.leftC) {

                if (!GetEqual) {
                    if (Number.length() > 0) {
                        char cat = Number.charAt(Number.length() - 1);
                        if (cat != '.' && !Character.isDigit(cat)) {
                            SumNumber.add("(");

                        } else {
                            //左括号前面没有符号只有数字默认为乘号
                            //将number的数据放入Sumnumber
                            if (!Number.equals("") && Number != null) {
                                //保存数据
                                try {
                                    SumNumber.add(String.valueOf(Number));
                                } catch (NumberFormatException e) {
                                    Log.d("Number", "出现空值");
                                }

                            }
                            Number.setLength(0);
                            SumNumber.add("×");
                            SumNumber.add("(");
                        }
                    } else if (!SumNumber.isEmpty()) {
                        //判断是不是存在两个相邻的操作
                        String str = SumNumber.get(SumNumber.size() - 1);
                        if (!str.equals("+") && !str.equals("-") && !str.equals("×") && !str.equals("÷") && !str.equals("(")) {
                            SumNumber.add("×");
                            SumNumber.add("(");
                        } else {
                            SumNumber.add("(");
                        }
                    }

                    //开头就使用括号的情况
                    if (SumNumber.isEmpty()) {
                        SumNumber.add("(");
                    }
                }
            }

            if (Id == R.id.RightC) {
                if (!GetEqual) {
                    if (Number.length() > 0) {
                        char cat = Number.charAt(Number.length() - 1);
                        if (cat != '.') {
                            //将number的数据放入Sumnumber
                            if (!Number.equals("") && Number != null) {
                                //保存数据
                                try {
                                    SumNumber.add(String.valueOf(Number));
                                } catch (NumberFormatException e) {
                                    Log.d("Number", "出现空值");
                                }

                            }
                            Number.setLength(0);
                            SumNumber.add(")");
                            Log.d("USUSU",SumNumber.toString());
                            //使得按下后动态计算结果
                            DynamicCalculation();
                        }
                    } else if (!SumNumber.isEmpty()) {
                        //判断是不是存在两个相邻的操作
                        String str = SumNumber.get(SumNumber.size() - 1);
                        Log.d("DEL1", SumNumber.toString());
                        if (!str.equals("+") && !str.equals("-") && !str.equals("×") && !str.equals("÷") && !str.equals("(")) {
                            SumNumber.add(")");
                            //使得按下后动态计算结果
                            DynamicCalculation();
                        }
                    }
                }
            }

            if (Id == R.id.DeleteOne) {
                if (!GetEqual) {
                    if (Number.length() > 0) {
                        Number.deleteCharAt(Number.length() - 1);
                    } else if (SumNumber.size() > 0) {
                        try {
                            Number.setLength(0);
                            Number.append(SumNumber.remove(SumNumber.size() - 1));
                            Number.deleteCharAt(Number.length() - 1);
                        } catch (NumberFormatException e) {
                            Log.d("Number", "出现空值");
                        }
                    } else {
                        ret = "";
                        activityMainBinding.PrintfText.setText(ret);
                    }

                    //删除所有数据后清除结果
                    if (SumNumber.isEmpty()) {
                        ListComputing(Del);
                    }
                } else {
                    ListComputing(Del);
                }

                //使得按下后动态计算结果
                DynamicCalculation();
            }

            //加减乘除
            if (Id == R.id.add) {
                if (!GetEqual) {
                    if (Number.length() > 0) {
                        if (Number.charAt(Number.length() - 1) != '.' && Number.charAt(Number.length()-1) != '-') {
                            //将number的数据放入Sumnumber
                            if (!Number.equals("") && Number != null) {
                                //保存数据
                                try {
                                    SumNumber.add(String.valueOf(Number));
                                } catch (NumberFormatException e) {
                                    Log.d("Number", "出现空值");
                                }

                            }

                            Number.setLength(0);
                            SumNumber.add("+");
                        }
                    } else if (!SumNumber.isEmpty()) {
                        //判断是不是存在两个相邻的操作
                        String str = SumNumber.get(SumNumber.size() - 1);
                        if (!str.equals("+") && !str.equals("-") && !str.equals("×") && !str.equals("÷") && !str.equals("(")) {
                                //将number的数据放入Sumnumber
                                if (!Number.equals("") && Number != null) {
                                    //保存数据
                                    try {
                                        SumNumber.add(String.valueOf(Number));
                                    } catch (NumberFormatException e) {
                                        Log.d("Number", "出现空值");
                                    }

                                }
                                Log.d("String1", String.valueOf(Number));
                                Number.setLength(0);
                                SumNumber.add("+");
                        } else if (!str.equals("(")) {
                            SumNumber.remove(SumNumber.size() - 1);
                            SumNumber.add("+");
                        }
                    }
                } else {
                    ListComputing("+");
                }
            }

            if (Id == R.id.Sub) {
                if (!GetEqual) {
                    if (Number.length() > 0) {
                        if (Number.charAt(Number.length() - 1) != '.'&& Number.charAt(Number.length()-1) != '-') {
                            //将number的数据放入Sumnumber
                            if (!Number.equals("") && Number != null) {
                                //保存数据
                                try {
                                    SumNumber.add(String.valueOf(Number));
                                } catch (NumberFormatException e) {
                                    Log.d("Number", "出现空值");
                                }

                            }

                            Log.d("String1", String.valueOf(Number));
                            Number.setLength(0);
                            SumNumber.add("-");
                        }
                    } else if (!SumNumber.isEmpty()) {
                        //判断是不是存在两个相邻的操作
                        String str = SumNumber.get(SumNumber.size() - 1);
                        if (!str.equals("+") && !str.equals("-") && !str.equals("×") && !str.equals("÷") && !str.equals("(")) {
                            //将number的数据放入Sumnumber
                            if (!Number.equals("") && Number != null) {
                                //保存数据
                                try {
                                    SumNumber.add(String.valueOf(Number));
                                } catch (NumberFormatException e) {
                                    Log.d("Number", "出现空值");
                                }

                            }

                            Log.d("String1", String.valueOf(Number));
                            Number.setLength(0);
                            SumNumber.add("-");
                        } else if (str.equals("×") || str.equals("÷") || str.equals("(")) {
                            Number.append("-");
                        } else if (!str.equals("(")) {
                            SumNumber.remove(SumNumber.size() - 1);
                            SumNumber.add("-");
                        }
                    } else {
                        Number.append("-");
                    }
                } else {
                    ListComputing("-");
                }
            }

            if (Id == R.id.Multiply) {
                if (!GetEqual) {
                    if (Number.length() > 0) {
                        if (Number.charAt(Number.length() - 1) != '.'&& Number.charAt(Number.length()-1) != '-') {
                            //将number的数据放入Sumnumber
                            if (!Number.equals("") && Number != null) {
                                //保存数据
                                try {
                                    SumNumber.add(String.valueOf(Number));
                                } catch (NumberFormatException e) {
                                    Log.d("Number", "出现空值");
                                }

                            }

                            Log.d("String1", String.valueOf(Number));
                            Number.setLength(0);
                            SumNumber.add("×");
                        }
                    } else if (!SumNumber.isEmpty()) {
                        //判断是不是存在两个相邻的操作
                        String str = SumNumber.get(SumNumber.size() - 1);
                        if (!str.equals("+") && !str.equals("-") && !str.equals("×") && !str.equals("÷") && !str.equals("(")) {
                            //将number的数据放入Sumnumber
                            if (!Number.equals("") && Number != null) {
                                //保存数据
                                try {
                                    SumNumber.add(String.valueOf(Number));
                                } catch (NumberFormatException e) {
                                    Log.d("Number", "出现空值");
                                }

                            }

                            Log.d("String1", String.valueOf(Number));
                            Number.setLength(0);
                            SumNumber.add("×");
                        } else if (!str.equals("(")) {
                            SumNumber.remove(SumNumber.size() - 1);
                            SumNumber.add("×");
                        }
                    }
                } else {
                    ListComputing("×");
                }
            }

            if (Id == R.id.Division) {
                if (!GetEqual) {

                    if (Number.length() > 0) {
                        if (Number.charAt(Number.length() - 1) != '.'&& Number.charAt(Number.length()-1) != '-') {
                            //将number的数据放入Sumnumber
                            if (!Number.equals("") && Number != null) {
                                //保存数据
                                try {
                                    SumNumber.add(String.valueOf(Number));
                                } catch (NumberFormatException e) {
                                    Log.d("Number", "出现空值");
                                }

                            }

                            Log.d("String1", String.valueOf(Number));
                            Number.setLength(0);
                            SumNumber.add("÷");
                        }
                    } else if (!SumNumber.isEmpty()) {
                        //判断是不是存在两个相邻的操作
                        String str = SumNumber.get(SumNumber.size() - 1);
                        if (!str.equals("+") && !str.equals("-") && !str.equals("×") && !str.equals("÷") && !str.equals("(")) {
                            //将number的数据放入Sumnumber
                            if (!Number.equals("") && Number != null) {
                                //保存数据
                                try {
                                    SumNumber.add(String.valueOf(Number));
                                } catch (NumberFormatException e) {
                                    Log.d("Number", "出现空值");
                                }

                            }

                            Log.d("String1", String.valueOf(Number));
                            Number.setLength(0);
                            SumNumber.add("÷");
                        } else if (!str.equals("(")) {
                            SumNumber.remove(SumNumber.size() - 1);
                            SumNumber.add("÷");
                        }
                    }
                } else {
                    ListComputing("÷");
                }
            }

            //数字
            if (Id == R.id.OneNum) {
                ClearDisplay();//清空显示
                if (!SumNumber.isEmpty()) {
                    String str = SumNumber.get(SumNumber.size() - 1);
                    if (str.equals(")")) {
                        SumNumber.add("×");
                    }
                }
                Number.append("1");

//                //使得按下数字后动态计算结果
                DynamicCalculation();
            }

            if (Id == R.id.TwoNum) {
                ClearDisplay();//清空显示
                if (!SumNumber.isEmpty()) {
                    String str = SumNumber.get(SumNumber.size() - 1);
                    if (str.equals(")")) {
                        SumNumber.add("×");
                    }
                }
                Number.append("2");

//                //使得按下数字后动态计算结果
                DynamicCalculation();
            }

            if (Id == R.id.ThreeNum) {
                ClearDisplay();//清空显示
                if (!SumNumber.isEmpty()) {
                    String str = SumNumber.get(SumNumber.size() - 1);
                    if (str.equals(")")) {
                        SumNumber.add("×");
                    }
                }
                Number.append("3");

//                //使得按下数字后动态计算结果
                DynamicCalculation();
            }

            if (Id == R.id.FourNum) {
                ClearDisplay();//清空显示
                if (!SumNumber.isEmpty()) {
                    String str = SumNumber.get(SumNumber.size() - 1);
                    if (str.equals(")")) {
                        SumNumber.add("×");
                    }
                }
                Number.append("4");

//                //使得按下数字后动态计算结果
                DynamicCalculation();
            }
            if (Id == R.id.FiveNum) {
                ClearDisplay();//清空显示
                if (!SumNumber.isEmpty()) {
                    String str = SumNumber.get(SumNumber.size() - 1);
                    if (str.equals(")")) {
                        SumNumber.add("×");
                    }
                }
                Number.append("5");

//                //使得按下数字后动态计算结果
                DynamicCalculation();
            }

            if (Id == R.id.SixNum) {
                ClearDisplay();//清空显示
                if (!SumNumber.isEmpty()) {
                    String str = SumNumber.get(SumNumber.size() - 1);
                    if (str.equals(")")) {
                        SumNumber.add("×");
                    }
                }
                Number.append("6");

//                //使得按下数字后动态计算结果
                DynamicCalculation();
            }

            if (Id == R.id.SevenNum) {
                ClearDisplay();//清空显示
                if (!SumNumber.isEmpty()) {
                    String str = SumNumber.get(SumNumber.size() - 1);
                    if (str.equals(")")) {
                        SumNumber.add("×");
                    }
                }
                Number.append("7");

//                //使得按下数字后动态计算结果
                DynamicCalculation();
            }

            if (Id == R.id.EightNum) {
                ClearDisplay();//清空显示
                if (!SumNumber.isEmpty()) {
                    String str = SumNumber.get(SumNumber.size() - 1);
                    if (str.equals(")")) {
                        SumNumber.add("×");
                    }
                }
                Number.append("8");

//                //使得按下数字后动态计算结果
                DynamicCalculation();
            }
            if (Id == R.id.NineNum) {
                ClearDisplay();//清空显示
                if (!SumNumber.isEmpty()) {
                    String str = SumNumber.get(SumNumber.size() - 1);
                    if (str.equals(")")) {
                        SumNumber.add("×");
                    }
                }
                Number.append("9");

                //使得按下数字后动态计算结果
                DynamicCalculation();
            }
            if (Id == R.id.O) {
                ClearDisplay();//清空显示
                if (!SumNumber.isEmpty()) {
                    String str = SumNumber.get(SumNumber.size() - 1);
                    if (str.equals(")")) {
                        SumNumber.add("×");
                    }
                }
                Number.append("0");

                //使得按下数字后动态计算结果
                DynamicCalculation();
            }


            if (Id == R.id.Point) {
                if (Number.length() > 0) {
                    int t = 0;
                    for (int i = 0; i < Number.length(); i++) {
                        if (Number.charAt(i) == '.') {
                            t++;
                        }
                    }
                    if (t < 1) {
                        Number.append(".");
                    }
                }
            }

            if (Id == R.id.equal) {
                if (Number.length() > 0) {
                    if (Number.charAt(Number.length() - 1) != '.') {
                        //将number的数据放入Sumnumber
                        if (!Number.equals("") && Number != null) {
                            //保存数据
                            try {
                                SumNumber.add(String.valueOf(Number));
                            } catch (NumberFormatException e) {
                                Log.d("Number", "出现空值");
                            }

                        }

                        Number.setLength(0);

                        //表示等号已经按下，用于清除文本框
                        GetEqual = true;

                        //计算结果
                        ReversePolish reversePolish = new ReversePolish(SumNumber);
                        ret = reversePolish.EvaluatePostfixExpressions();
                        //加载大小变化的动画
                        DisplayFrameAnimation(activityMainBinding.PrintfText, activityMainBinding.ScanfEditText);
                    }
                } else if (!SumNumber.isEmpty()) {
                    //判断是不是存在两个相邻的操作
                    String str = SumNumber.get(SumNumber.size() - 1);
                    if (str.equals(")")) {
                        //将number的数据放入Sumnumber
                        if (!Number.equals("") && Number != null) {
                            //保存数据
                            try {
                                SumNumber.add(String.valueOf(Number));
                            } catch (NumberFormatException e) {
                                Log.d("Number", "出现空值");
                            }

                        }

                        Number.setLength(0);

                        //表示等号已经按下，用于清除文本框
                        GetEqual = true;

                        //计算结果
                        ReversePolish reversePolish = new ReversePolish(SumNumber);
                        ret = reversePolish.EvaluatePostfixExpressions();

                        //加载大小变化的动画
                        DisplayFrameAnimation(activityMainBinding.PrintfText, activityMainBinding.ScanfEditText);
                    }
                }
            }

            //显示文本框内容
            if (Number != null) {
                StringBuilder builder = new StringBuilder();
                for (String str : SumNumber) {
                    builder.append(str);
                }
                builder.append(Number);
                activityMainBinding.ScanfEditText.setText(builder);
                activityMainBinding.PrintfText.setText(ret);
                MaxDisplayFrameAnimation(activityMainBinding.ScanfEditText);
            }

        }


    }

    //按键缩放动画和实现震动效果
    private void animation(View view) {
        // 加载缩放动画资源
        final AnimatorSet scaleAnimation = (AnimatorSet) AnimatorInflater.loadAnimator(this, R.animator.scale_animation);

        //实现震动效果
        /**
         *         Vibrator vibrator=(Vibrator)getSystemService(Service.VIBRATOR_SERVICE);
         *         vibrator.vibrate(new long[]{0,100}, -1);
         * 从Android API level 26开始，Vibrator类的vibrate()方法被弃用，推荐使用VibrationEffect来实现震动效果。VibrationEffect类提供了更灵活和可控的震动方式。
         */
        // 获取Vibrator实例
        Vibrator vibrator = (Vibrator) getSystemService(Service.VIBRATOR_SERVICE);

        // 判断Android版本是否大于等于API 26
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            // 创建VibrationEffect对象，指定震动模式和震动时长
            VibrationEffect vibrationEffect = VibrationEffect.createOneShot(100, VibrationEffect.DEFAULT_AMPLITUDE);
            // 开始震动
            vibrator.vibrate(vibrationEffect);
        } else {
            // Android版本低于API 26，使用老版本的vibrate方法
            vibrator.vibrate(100);
        }

        // 开始执行缩放动画
        scaleAnimation.setTarget(view);
        scaleAnimation.start();

        scaleAnimation.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {

                // 在缩放动画结束时，创建还原动画并执行还原动画
                ObjectAnimator restoreAnimationX = ObjectAnimator.ofFloat(view, "scaleX", 1.0f);
                ObjectAnimator restoreAnimationY = ObjectAnimator.ofFloat(view, "scaleY", 1.0f);
                AnimatorSet restoreAnimation = new AnimatorSet();
                restoreAnimation.playTogether(restoreAnimationX, restoreAnimationY);
                restoreAnimation.start();
            }
        });
    }


    // 显示框的动画效果
    private void DisplayFrameAnimation(View Little_big, View Big_little) {

        /**
         * SIzeOfText == 40 由上方40-25 下方25-40
         * SIzeOfText == 30 由上方30-25 下方25-30
         * SIzeOfText == 25 由上方不变 下方25-30
         */


        if (SIzeOfText == 40) {
            // 加载动画资源,从25变到40
            AnimatorSet Little_Big = (AnimatorSet) AnimatorInflater.loadAnimator(this, R.animator.text_size_25_40);
            // 设置目标视图
            Little_Big.setTarget(Little_big);
            // 启动动画
            Little_Big.start();

            // 加载动画资源,从40变到25
            AnimatorSet Big_Little = (AnimatorSet) AnimatorInflater.loadAnimator(this, R.animator.text_size_40_25);
            // 设置目标视图
            Big_Little.setTarget(Big_little);
            // 启动动画
            Big_Little.start();
        } else if (SIzeOfText == 30) {
            Log.d("SIzeOfText", String.valueOf(SIzeOfText));

            // 加载动画资源,从25变到30
            AnimatorSet Little_Big = (AnimatorSet) AnimatorInflater.loadAnimator(this, R.animator.text_size_25_30);
            // 设置目标视图
            Little_Big.setTarget(Little_big);
            // 启动动画
            Little_Big.start();

            // 加载动画资源,从30变到25
            AnimatorSet Big_Little = (AnimatorSet) AnimatorInflater.loadAnimator(this, R.animator.text_size_30_25);
            // 设置目标视图
            Big_Little.setTarget(Big_little);
            // 启动动画
            Big_Little.start();
        } else if (SIzeOfText == 25) {
            // 加载动画资源,从25变到30
            AnimatorSet Little_Big = (AnimatorSet) AnimatorInflater.loadAnimator(this, R.animator.text_size_25_30);
            // 设置目标视图
            Little_Big.setTarget(Little_big);
            // 启动动画
            Little_Big.start();
        }

    }

    //显示框的字符达到指定长度使得一行无法放下
    private void MaxDisplayFrameAnimation(View Big_littleView) {
        int MaxDigit = 0;
        for (String str : SumNumber) {
            MaxDigit += str.length();
        }

        MaxDigit += Number.length();

        Log.d("StrSize", "num:" + MaxDigit);
        if (MaxDigit == 10) {

            SIzeOfText = 30;    //字体变化为30

            // 加载动画资源,从40变到30
            AnimatorSet Big_Little = (AnimatorSet) AnimatorInflater.loadAnimator(this, R.animator.text_size_40_30);
            // 设置目标视图
            Big_Little.setTarget(Big_littleView);
            // 启动动画
            Big_Little.start();

        }
        if (MaxDigit > 10 && MaxDigit < 25) activityMainBinding.ScanfEditText.setTextSize(30);

        if (MaxDigit == 15) {

            SIzeOfText = 25;    //字体变化为25

            // 加载动画资源,从30变到25
            AnimatorSet Big_Little = (AnimatorSet) AnimatorInflater.loadAnimator(this, R.animator.text_size_30_25);
            // 设置目标视图
            Big_Little.setTarget(Big_littleView);

            // 启动动画
            Big_Little.start();


            activityMainBinding.ScanfEditText.setTextSize(25);
        }
        if (MaxDigit > 15) activityMainBinding.ScanfEditText.setTextSize(25);

    }


    //加载菜单
    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        Log.d("Tag", "加载菜单");
        getMenuInflater().inflate(R.menu.function, menu);
        super.onCreateContextMenu(menu, v, menuInfo);
    }

    //注册菜单点击事件
    @Override
    public boolean onContextItemSelected(@NonNull MenuItem item) {
        Log.d("Tag", "点击事件");
        final int Id = item.getItemId();


        if (Id == R.id.Log) {
            //历史记录

        } else if (Id == R.id.SetUp) {
            //设置

        }

        return super.onContextItemSelected(item);
    }

    //清空显示
    private void ClearDisplay() {

        //还原显示框大小
        activityMainBinding.ScanfEditText.setTextSize(40);
        activityMainBinding.PrintfText.setTextSize(30);

        //表示已经按下=后，重新输入数字的情况
        if (GetEqual) {
            GetEqual = false;
            activityMainBinding.ScanfEditText.setText("");
            ret = "";
            //清空储存的内容
            SumNumber.clear();
            Number.setLength(0);

            //还原字体大小
            SIzeOfText = 40;
        }
    }

    //连续计算的情况
    private void ListComputing(String str) {

        if (str.equals(Del)) {
            GetEqual = false;

            //还原显示框大小
            activityMainBinding.ScanfEditText.setTextSize(40);
            activityMainBinding.PrintfText.setTextSize(30);

            SIzeOfText = 40;

            ret = "";

            if (Number.length() > 0) {
                Number.deleteCharAt(Number.length() - 1);
            } else if (SumNumber.size() > 0) {
                try {
                    Number.setLength(0);
                    Number.append(SumNumber.remove(SumNumber.size() - 1));
                    Number.deleteCharAt(Number.length() - 1);
                } catch (NumberFormatException e) {
                    //清空显示
                    activityMainBinding.ScanfEditText.setText("");
                    ret = "";
                    activityMainBinding.PrintfText.setText(ret);

                    //清空储存的内容
                    SumNumber.clear();
                    Number.setLength(0);

                    //还原字体大小
                    SIzeOfText = 40;
                }
            }

        } else if (!ret.equals("Error")) {
            GetEqual = false;

            //还原显示框大小
            activityMainBinding.ScanfEditText.setTextSize(40);
            activityMainBinding.PrintfText.setTextSize(30);

            //清空储存的内容
            SumNumber.clear();
            Number.setLength(0);
            //还原字体大小
            SIzeOfText = 40;

            SumNumber.add(ret);
            SumNumber.add(str);

            ret = "";
        }
    }

    //使得按下数字后动态计算结果
    private void DynamicCalculation() {
        if (!SumNumber.isEmpty() && SumNumber.size() > 0) {
            //将number的数据放入Sumnumber
            if (!Number.equals("") && Number != null) {
                //保存数据
                try {
                    SumNumber.add(String.valueOf(Number));
                } catch (NumberFormatException e) {
                    Log.d("Number", "出现空值");
                }

            }

            //计算当前结果
            ReversePolish reversePolish = new ReversePolish(SumNumber);
            ret = reversePolish.EvaluatePostfixExpressions();

            //删去暂时存放的数据
            SumNumber.remove(SumNumber.size() - 1);
        }
    }
}

