package com.rc.forms;

import com.rc.components.Colors;
import com.rc.components.GBC;
import com.rc.components.RCBorder;
import com.rc.components.VerticalFlowLayout;
import com.rc.utils.FontUtil;
import com.rc.utils.OSUtil;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

/**
 * Created by song on 17-5-30.
 */
public class TitlePanel extends ParentAvailablePanel
{
    private JPanel titlePanel;
    private JLabel titleLabel;

    private JPanel controlPanel;
    private JLabel closeLabel;
    private JLabel maxLabel;
    private JLabel minLabel;
    private JLabel roomInfoButton;

    public TitlePanel(JPanel parent)
    {
        super(parent);

        initComponents();
        initView();
    }

    private void initComponents()
    {
        titlePanel = new JPanel();
        titlePanel.setLayout(new GridBagLayout());

        roomInfoButton = new JLabel();
        roomInfoButton.setIcon(new ImageIcon(getClass().getResource("/image/options.png")));
        roomInfoButton.setHorizontalAlignment(JLabel.CENTER);

        titleLabel = new JLabel();
        titleLabel.setText("小学生(5)");
        titleLabel.setFont(FontUtil.getDefaultFont(16));



        ControlLabelMouseListener listener = new ControlLabelMouseListener();
        Dimension controlLabelSize = new Dimension(30, 30);
        Cursor handCursor = new Cursor(Cursor.HAND_CURSOR);

        controlPanel = new JPanel();
        controlPanel.setLayout(new FlowLayout(FlowLayout.RIGHT, 0, 0));

        closeLabel = new JLabel();
        closeLabel.setIcon(new ImageIcon(getClass().getResource("/image/close.png")));
        closeLabel.setHorizontalAlignment(JLabel.CENTER);
        closeLabel.setOpaque(true);
        closeLabel.addMouseListener(listener);
        closeLabel.setPreferredSize(controlLabelSize);
        closeLabel.setCursor(handCursor);

        maxLabel = new JLabel();
        maxLabel.setIcon(new ImageIcon(getClass().getResource("/image/window_max.png")));
        maxLabel.setHorizontalAlignment(JLabel.CENTER);
        maxLabel.setOpaque(true);
        maxLabel.addMouseListener(listener);
        maxLabel.setPreferredSize(controlLabelSize);
        maxLabel.setCursor(handCursor);

        minLabel = new JLabel();
        minLabel.setIcon(new ImageIcon(getClass().getResource("/image/window_min.png")));
        minLabel.setHorizontalAlignment(JLabel.CENTER);
        minLabel.setOpaque(true);
        minLabel.addMouseListener(listener);
        minLabel.setPreferredSize(controlLabelSize);
        minLabel.setCursor(handCursor);

    }

    private void initView()
    {
        setLayout(new VerticalFlowLayout(VerticalFlowLayout.TOP, 0, 0, true, true));

        setBorder(null);
        this.setBorder(new RCBorder(RCBorder.BOTTOM, Colors.LIGHT_GRAY));


        controlPanel.add(minLabel);
        controlPanel.add(maxLabel);
        controlPanel.add(closeLabel);

        int margin;
        if (OSUtil.getOsType() != OSUtil.Mac_OS)
        {
            add(controlPanel);
            add(titlePanel);
            margin = 5;
        }
        else
        {
            add(titlePanel);
            margin = 15;
        }

        titlePanel.add(titleLabel, new GBC(0, 0).setFill(GBC.BOTH).setWeight(100, 1).setInsets(margin, margin, 0, 0));
        titlePanel.add(roomInfoButton, new GBC(1, 0).setFill(GBC.BOTH).setWeight(1, 1).setInsets(margin, 0, 0, margin));

        //add(controlPanel);
        //add(titlePanel);


    }

    private class ControlLabelMouseListener implements MouseListener
    {
        @Override
        public void mouseClicked(MouseEvent e)
        {
            if (e.getComponent() == closeLabel)
            {
                System.exit(1);
            }
            else if (e.getComponent() == maxLabel)
            {
                MainFrame.getContext().setExtendedState(JFrame.MAXIMIZED_BOTH);
            }
            else if (e.getComponent() == minLabel)
            {
                MainFrame.getContext().setExtendedState(JFrame.ICONIFIED);
            }
        }

        @Override
        public void mousePressed(MouseEvent e)
        {

        }

        @Override
        public void mouseReleased(MouseEvent e)
        {

        }

        @Override
        public void mouseEntered(MouseEvent e)
        {
            e.getComponent().setBackground(Colors.LIGHT_GRAY);
        }

        @Override
        public void mouseExited(MouseEvent e)
        {
            e.getComponent().setBackground(null);
        }
    }
}
