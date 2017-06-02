package com.rc.forms;

import com.rc.components.Colors;
import com.rc.components.GBC;
import com.rc.components.RCBorder;
import com.rc.utils.FontUtil;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.LineBorder;
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

    public TitlePanel(JPanel parent)
    {
        super(parent);

        initComponents();
        initView();
    }

    private void initComponents()
    {
        titlePanel = new JPanel();
        titlePanel.setLayout(new FlowLayout(0));

        titleLabel = new JLabel();
        titleLabel.setText("即时通讯讨论群(5)");
        titleLabel.setFont(FontUtil.getDefaultFont(16));
        titlePanel.add(titleLabel);

        ControlLabelMouseListener listener = new ControlLabelMouseListener();
        Dimension controlLabelSize = new Dimension(30,30);
        Cursor handCursor = new Cursor(Cursor.HAND_CURSOR);

        controlPanel = new JPanel();
        controlPanel.setLayout(new GridBagLayout());

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

        controlPanel.add(minLabel, new GBC(0, 0).setFill(GBC.BOTH).setWeight(1, 1).setIpad(5,5));
        controlPanel.add(maxLabel, new GBC(1, 0).setFill(GBC.BOTH).setWeight(1, 1).setIpad(5,5));
        controlPanel.add(closeLabel, new GBC(2, 0).setFill(GBC.BOTH).setWeight(1, 1).setIpad(5,5));
    }

    private void initView()
    {
        //setLayout(new BorderLayout());
        setLayout(new GridBagLayout());

       // add(titlePanel, BorderLayout.WEST);
        //add(controlPanel, BorderLayout.EAST);
        setBorder(null);
        this.setBorder(new RCBorder(RCBorder.BOTTOM, Colors.LIGHT_GRAY));

        add(titlePanel, new GBC(0, 1).setFill(GBC.HORIZONTAL).setWeight(100, 1));
        add(controlPanel, new GBC(1, 0).setFill(GBC.HORIZONTAL).setWeight(1, 1));

        //add(minLabel, new GBC(1, 0).setFill(GBC.HORIZONTAL).setWeight(1, 1).setInsets(0, 5, 0, 0));
        //add(maxLabel, new GBC(2, 0).setFill(GBC.HORIZONTAL).setWeight(1, 1).setInsets(0, 5, 0, 0));
        //add(closeLabel, new GBC(3, 0).setFill(GBC.HORIZONTAL).setWeight(1, 1).setInsets(0, 5, 0, 0));
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
