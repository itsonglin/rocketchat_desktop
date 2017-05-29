package com.rc.forms;

import com.rc.components.Colors;
import com.rc.components.GBC;
import com.rc.components.RCTextField;
import com.rc.utils.FontUtil;

import javax.swing.*;
import javax.swing.border.LineBorder;
import java.awt.*;

/**
 * Created by song on 17-5-29.
 */
public class SearchPanel extends JPanel
{
    private RCTextField searchTextField;

    public SearchPanel()
    {
        initComponent();
        initView();
    }

    private void initComponent()
    {
        searchTextField = new RCTextField();
        searchTextField.setFont(FontUtil.getDefaultFont(14));
        searchTextField.setForeground(Colors.FONT_WHITE);
    }

    private void initView()
    {
        setBackground(Colors.DARK);
        this.setLayout(new GridBagLayout());
        this.add(searchTextField, new GBC(0, 0)
                .setFill(GBC.HORIZONTAL)
                .setWeight(1, 1)
                .setInsets(0, 15, 0, 15)
        );
    }
}
