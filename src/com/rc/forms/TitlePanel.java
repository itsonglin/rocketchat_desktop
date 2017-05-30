package com.rc.forms;

import com.rc.components.Colors;
import com.rc.components.GBC;

import javax.swing.*;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

/**
 * Created by song on 17-5-30.
 */
public class TitlePanel extends JPanel
{
    private JLabel title;
    private JLabel closeLabel;
    private JLabel maxLabel;
    private JLabel minLabel;

    public TitlePanel()
    {
        initComponents();
        initView();
    }

    private void initComponents()
    {
        title = new JLabel();
        title.setText("即时通讯讨论群");
        ControlLabelMouseListener listener = new ControlLabelMouseListener();
        Dimension controlLabelSize = new Dimension(25, 25);
        Cursor handCursor = new Cursor(Cursor.HAND_CURSOR);

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
        setLayout(new GridBagLayout());

        add(title, new GBC(0, 0).setFill(GBC.HORIZONTAL).setWeight(30, 1));
        add(minLabel, new GBC(1, 0).setFill(GBC.HORIZONTAL).setWeight(1, 1).setIpad(-10, -10).setInsets(2, 5, 5, 0));
        add(maxLabel, new GBC(2, 0).setFill(GBC.HORIZONTAL).setWeight(1, 1).setIpad(-10, -10).setInsets(2, 5, 5, 0));
        add(closeLabel, new GBC(3, 0).setFill(GBC.HORIZONTAL).setWeight(1, 1).setIpad(-10, -10).setInsets(2, 5, 5, 0));
    }

    private class ControlLabelMouseListener implements MouseListener
    {
        @Override
        public void mouseClicked(MouseEvent e)
        {
            if (e.getComponent() == closeLabel)
            {
                System.exit(1);
            } else if (e.getComponent() == maxLabel)
            {
                MainFrame.getContext().setExtendedState(JFrame.MAXIMIZED_BOTH);
            } else if (e.getComponent() == minLabel)
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
