package com.rc.components.message;

import com.rc.components.Colors;
import com.rc.components.GBC;
import com.rc.components.VerticalFlowLayout;
import com.rc.utils.IconUtil;

import javax.swing.*;
import java.awt.*;

/**
 * 文件在输入框中的缩略图，当文件直接被粘贴到输入框时，该文件将会以缩略图的形式显示在输入框中
 * Created by song on 29/06/2017.
 */
public class FileEditorThumbnail extends JPanel
{
    private JLabel icon;
    private JLabel text;


    public FileEditorThumbnail()
    {
        initComponents();
        initView();
    }

    private void initComponents()
    {
        setPreferredSize(new Dimension(100, 100));
        setMaximumSize(new Dimension(100, 100));
        setBackground(Colors.FONT_WHITE);

        icon = new JLabel();
        icon.setIcon(IconUtil.getIcon(this, "/image/pdf.png"));
        icon.setHorizontalAlignment(SwingConstants.CENTER);

        text = new JLabel();
        text.setText("和理通使用说明.pdf");
    }

    private void initView()
    {
        /*setLayout(new GridBagLayout());
        add(icon, new GBC(0,0).setFill(GBC.BOTH).setInsets(5).setWeight(1,1));
        add(text, new GBC(0,1).setFill(GBC.BOTH).setInsets(5).setWeight(1,1));*/

        setLayout(new VerticalFlowLayout(VerticalFlowLayout.MIDDLE, 0, 5, true, true));
        add(icon);
        add(text);
    }

}
