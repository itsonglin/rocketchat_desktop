package com.rc.app;

import com.rc.forms.FontFrame;
import com.rc.forms.MainForm;

import javax.swing.*;

/**
 * Created by song on 17-5-28.
 */
public class App
{
    public static void main(String[] args)
    {
       /* JFrame frame = new MainForm();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(300,400);
        frame.setVisible(true);*/

        MainForm frame = new MainForm();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);
    }
}
