package com.rc.forms;

import java.awt.BorderLayout;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JWindow;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

@SuppressWarnings("serial")
public class WindowClosingAnimation extends JPanel {
    public WindowClosingAnimation() {
        setLayout(new BorderLayout());
        add(new JLabel("测试窗口关闭效果", SwingConstants.CENTER), BorderLayout.CENTER);
    }

    private static void createAndShowGUI() {
        JFrame.setDefaultLookAndFeelDecorated(true);
        try {
            UIManager.setLookAndFeel("com.han.gen.GenLookAndFeel");
        } catch (Exception e) {
            e.printStackTrace();
        }

        JFrame jFrame = new JFrame();
        jFrame.setContentPane(new WindowClosingAnimation());
        jFrame.setSize(300, 200);
        jFrame.setLocationRelativeTo(null);
        jFrame.setVisible(true);

        // Choose "true" or "false" to see the difference
        setWindowClosingAnimated(true, jFrame);
    }

    private static void setWindowClosingAnimated(boolean animated, JFrame jFrame) {
        if (animated) {
            jFrame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
            addWindowClosingAnimationToJFrame(jFrame);
        } else {
            jFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        }
    }

    private static void addWindowClosingAnimationToJFrame(JFrame jFrame) {
        jFrame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                windowClosingAnimation(jFrame);
            }
        });
    }

    private static void windowClosingAnimation(JFrame jFrame) {
        BufferedImage image = new BufferedImage(jFrame.getWidth(),
                jFrame.getHeight(), BufferedImage.TYPE_INT_ARGB);
        Graphics g = image.getGraphics();
        jFrame.paint(g);
        g.dispose();

        JWindow jWindow = new JWindow() {
            @Override
            public void paint(Graphics g) {
                super.paint(g);
                g.drawImage(image, 0, 0, getWidth(), getHeight(), 0, 0,
                        image.getWidth(), image.getHeight(), this);
            }
        };
        jWindow.setBounds(jFrame.getBounds());
        jFrame.setVisible(false);
        jWindow.setVisible(true);

        new Thread(new Runnable() {
            @Override
            public void run() {
                final int indent = 20;
                for (;;) {
                    Rectangle bounds = jWindow.getBounds();
                    bounds.x += indent / 2;
                    bounds.y += indent / 2;
                    bounds.width -= indent;
                    bounds.height -= indent;
                    if (bounds.width < 0 || bounds.height < 0) {
                        System.exit(0);
                    } else {
                        jWindow.setBounds(bounds.x, bounds.y, bounds.width,
                                bounds.height);// will call repaint()
                    }
                    try {
                        Thread.sleep(15);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                createAndShowGUI();
            }
        });
    }
}