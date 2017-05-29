package com.rc.forms;

import com.rc.components.Colors;

import javax.swing.*;
import java.awt.*;

/**
 * Created by song on 17-5-29.
 */
public class RightPanel extends JPanel
{
    private TitlePanel titlePanel;
    public RightPanel()
    {
        initComponents();
        initView();

    }

    private void initComponents()
    {
        titlePanel = new TitlePanel();
    }

    private void initView()
    {
        this.setBackground(Colors.WINDOW_BACKGROUND);
        this.setLayout(new BorderLayout());
        add(titlePanel, BorderLayout.NORTH);
    }
}
