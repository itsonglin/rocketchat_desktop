package com.rc.panels;

import com.rc.components.Colors;
import com.rc.components.GBC;
import com.rc.components.RCBorder;
import com.rc.listener.ExpressionListener;
import com.rc.utils.IconUtil;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

/**
 * Created by song on 04/07/2017.
 *
 * 表情弹窗
 */
public class ExpressionPopup extends JPopupMenu
{
    private static final int WIDTH = 400;
    private static final int HEIGHT = 300;

    private JPanel listPanel;
    private JPanel tabIconPanel;

    private JPanel emojiTabPanel;
    private JPanel meng2TabPanel;

    private JLabel emojiTabLabel;
    private JLabel meng2TabLabel;


    private EmojiPanel emojiPanel;
    private Meng2Panel meng2Panel;


    private CardLayout cardLayout;
    public static final String EMOJI = "EMOJI";
    public static final String MENG2 = "MENG2";


    public ExpressionPopup()
    {
        initComponents();
        initView();

        setListeners();

        selectTab(emojiTabPanel);
    }

    private void initComponents()
    {
        listPanel = new JPanel();
        listPanel.setBorder(new RCBorder(RCBorder.BOTTOM, Colors.LIGHT_GRAY));
        cardLayout = new CardLayout();
        listPanel.setLayout(cardLayout);

        tabIconPanel = new JPanel();
        tabIconPanel.setLayout(new FlowLayout(FlowLayout.LEFT, 5,0));

        // emoji
        emojiTabPanel = new JPanel();
        emojiTabLabel = new JLabel();
        emojiTabLabel.setIcon(IconUtil.getIcon(this, "/image/smile.png", 23, 23));
        emojiPanel = new EmojiPanel();

        // 萌二
        meng2TabPanel = new JPanel();
        meng2TabLabel = new JLabel();
        meng2TabLabel.setIcon(IconUtil.getIcon(this, "/expression/meng2/meng2.png", 23, 23));
        meng2Panel = new Meng2Panel();

        setBackground(Colors.WINDOW_BACKGROUND);
        this.setPopupSize(WIDTH, HEIGHT);
    }

    private void initView()
    {
        emojiTabPanel.add(emojiTabLabel);
        meng2TabPanel.add(meng2TabLabel);

        tabIconPanel.add(emojiTabPanel);
        tabIconPanel.add(meng2TabPanel);

        listPanel.add(emojiPanel, EMOJI);
        listPanel.add(meng2Panel, MENG2);


        setLayout(new GridBagLayout());
        add(listPanel, new GBC(0, 0).setWeight(1,1000).setFill(GBC.BOTH));
        add(tabIconPanel, new GBC(0, 1).setWeight(1,1).setFill(GBC.BOTH).setInsets(3,0,0,0));
    }

    public void setExpressionListener(ExpressionListener listener)
    {
        emojiPanel.setExpressionListener(listener, this);
        meng2Panel.setExpressionListener(listener, this);
    }


    private void selectTab(JPanel tab)
    {
        for (Component component : tabIconPanel.getComponents())
        {
            if (component == tab)
            {
                component.setBackground(Colors.SCROLL_BAR_TRACK_LIGHT);
            }
            else
            {
                component.setBackground(Colors.WINDOW_BACKGROUND);
            }
        }
    }


    public void showPanel(String who)
    {
        cardLayout.show(listPanel, who);
    }

    private void setListeners()
    {
        MouseAdapter adapter = new MouseAdapter()
        {
            @Override
            public void mouseClicked(MouseEvent e)
            {
                if (e.getSource() == emojiTabPanel)
                {
                    showPanel(EMOJI);
                }
                else if (e.getSource() == meng2TabPanel)
                {
                    showPanel(MENG2);
                }

                selectTab((JPanel) e.getSource());

                super.mouseClicked(e);
            }
        };

        emojiTabPanel.addMouseListener(adapter);
        meng2TabPanel.addMouseListener(adapter);
    }

}
