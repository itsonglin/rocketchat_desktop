package com.rc.forms;


import com.rc.components.Colors;
import com.rc.components.GBC;
import com.rc.utils.FontUtil;

import javax.swing.*;
import java.awt.*;

/**
 * Created by song on 17-5-28.
 */
public class MainForm extends JFrame
{
    private int DEFAULT_WIDTH = 800;
    private int DEFAULT_HEIGHT = 600;

    private LeftPanel leftPanel;
    private RightPanel rightPanel;

    public MainForm()
    {
        initComponents();
        initView();
    }

    private void initComponents()
    {
        UIManager.put("Label.font", FontUtil.getDefaultFont());
        UIManager.put("Panel.font", FontUtil.getDefaultFont());

        leftPanel = new LeftPanel();
        rightPanel = new RightPanel();
    }

    private void initView()
    {
        setSize(DEFAULT_WIDTH, DEFAULT_HEIGHT);
        setLayout(new GridBagLayout());
        setMinimumSize(new Dimension(DEFAULT_WIDTH, DEFAULT_HEIGHT));

        add(leftPanel, new GBC(0, 0).setAnchor(GBC.CENTER).setWeight(1, 1).setFill(GBC.BOTH));
        add(rightPanel, new GBC(1, 0).setAnchor(GBC.CENTER).setWeight(7, 1).setFill(GBC.BOTH));
    }
}

