package com.rc.forms;

import com.rc.components.GBC;
import javafx.geometry.Bounds;

import javax.swing.*;

/**
 * Created by song on 06/06/2017.
 */
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLayeredPane;

/**
 * 层JLayeredPane使用的小例子
 *
 * @author 五斗米 <如转载请保留作者和出处>
 * @blog http://blog.csdn.net/mq612
 */
public class TestForm extends JFrame implements ActionListener
{

    private static final long serialVersionUID = 4785452373598819719L;
    private final JPanel panelRight;


    private JLayeredPane lp = null; // 我们要用到的层
    private JButton button;

    public TestForm()
    {
        super("JLayeredPane");

        JPanel panelLeft = new JPanel();
        panelLeft.setPreferredSize(new Dimension(900, 500));
        panelLeft.setBackground(Color.gray);

        panelRight = new JPanel();
        panelRight.setPreferredSize(new Dimension(100, 500));
        panelRight.setBackground(Color.RED);

        button = new JButton("打开");
        button.addActionListener(this);

        setLayout(new GridBagLayout());
        add(panelLeft, new GBC(0, 0).setWeight(10, 10).setFill(GBC.BOTH));

        add(button, new GBC(0, 1).setWeight(1, 1).setFill(GBC.BOTH).setAnchor(GBC.EAST));


        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setSize(900, 500);
        this.setVisible(true);
    }


    public void actionPerformed(ActionEvent e)
    {
        add(panelRight, new GBC(1, 0).setWeight(2, 10).setFill(GBC.BOTH).setAnchor(GBC.EAST));
        revalidate();

       /* System.out.println(e);
        //Rectangle bb = ((JButton) e.getSource()).getBounds();
        Rectangle bb = getBounds();

        SmallForm form = new SmallForm();
        form.setBounds(bb.x + 100, bb.y + 100, 200 ,100);*/

        //JDialog dialog = new JDialog();

        //dialog.setVisible(true);
    }

    public static void main(String args[])
    {
        new TestForm();
    }


    class SmallForm extends  JFrame{

        public SmallForm()
        {
            this.setAlwaysOnTop(true);
            this.setSize(200, 100);
            this.setVisible(true);
        }
    }
}