package com.rc.app;

import com.rc.forms.CardLayoutDemo;
import com.rc.forms.LoginFrame;
import com.rc.forms.MainFrame;
import com.rc.utils.CharacterParser;
import com.rc.utils.HttpUtil;

import javax.swing.*;

/**
 * Created by song on 17-5-28.
 */
public class App
{
    public static void main(String[] args)
    {

        /*MainFrame frame = new MainFrame();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);*/

        LoginFrame frame2 = new LoginFrame();
        frame2.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame2.setVisible(true);
    }
}
