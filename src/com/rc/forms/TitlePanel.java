package com.rc.forms;

import com.rc.app.Launcher;
import com.rc.components.Colors;
import com.rc.components.GBC;
import com.rc.components.RCBorder;
import com.rc.components.VerticalFlowLayout;
import com.rc.db.model.Room;
import com.rc.db.service.RoomService;
import com.rc.listener.AbstractMouseListener;
import com.rc.utils.AvatarUtil;
import com.rc.utils.FontUtil;
import com.rc.utils.ImageCache;
import com.rc.utils.OSUtil;
import com.sun.awt.AWTUtilities;
import com.sun.imageio.plugins.common.ImageUtil;
import com.sun.javaws.Main;
import com.sun.javaws.jnl.LaunchDesc;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

/**
 * Created by song on 17-5-30.
 */
public class TitlePanel extends ParentAvailablePanel
{
    private static TitlePanel context;

    private JPanel titlePanel;
    private JLabel titleLabel;

    private JPanel controlPanel;
    private JLabel closeLabel;
    private JLabel maxLabel;
    private JLabel minLabel;
    private JLabel roomInfoButton;

    private ImageIcon maxIcon;
    private ImageIcon restoreIcon;
    private boolean windowMax; // 当前窗口是否已最大化
    private Rectangle desktopBounds; // 去除任务栏后窗口的大小
    private Rectangle normalBounds;

    private RoomService roomService = Launcher.roomService;
    private Room room;


    public TitlePanel(JPanel parent)
    {
        super(parent);
        context = this;

        initComponents();
        addListeners();
        initView();
        initBounds();
    }

    private void initBounds()
    {
        Toolkit tk = Toolkit.getDefaultToolkit();
        Dimension screenSize = tk.getScreenSize();
        //上面这种方式获取的是整个显示屏幕的大小，包含了任务栏的高度。
        Insets screenInsets = Toolkit.getDefaultToolkit()
                .getScreenInsets(MainFrame.getContext().getGraphicsConfiguration());
        desktopBounds = new Rectangle(
                screenInsets.left, screenInsets.top,
                screenSize.width - screenInsets.left - screenInsets.right,
                screenSize.height - screenInsets.top - screenInsets.bottom);

        normalBounds = new Rectangle(
                (screenSize.width - MainFrame.DEFAULT_WIDTH) / 2,
                (screenSize.height - MainFrame.DEFAULT_HEIGHT) / 2,
                MainFrame.DEFAULT_WIDTH,
                MainFrame.DEFAULT_HEIGHT);

    }

    private void addListeners()
    {
        roomInfoButton.addMouseListener(new AbstractMouseListener()
        {
            @Override
            public void mouseClicked(MouseEvent e)
            {
                JPanel roomMemberPanel = ((RightPanel) getParentPanel()).getRoomMembersPanel();
                if (roomMemberPanel.isVisible())
                {
                    roomInfoButton.setIcon(new ImageIcon(getClass().getResource("/image/options.png")));
                    roomMemberPanel.setVisible(false);
                }
                else
                {
                    roomInfoButton.setIcon(new ImageIcon(getClass().getResource("/image/options_restore.png")));
                    roomMemberPanel.setVisible(true);
                }
            }
        });
    }

    private void initComponents()
    {
        Cursor handCursor = new Cursor(Cursor.HAND_CURSOR);
        maxIcon = new ImageIcon(getClass().getResource("/image/window_max.png"));
        restoreIcon = new ImageIcon(getClass().getResource("/image/window_restore.png"));

        titlePanel = new JPanel();
        titlePanel.setLayout(new GridBagLayout());
        //titlePanel.setVisible(false);

        roomInfoButton = new JLabel();
        roomInfoButton.setIcon(new ImageIcon(getClass().getResource("/image/options.png")));
        roomInfoButton.setHorizontalAlignment(JLabel.CENTER);
        roomInfoButton.setCursor(handCursor);
        roomInfoButton.setVisible(false);


        titleLabel = new JLabel();
        titleLabel.setFont(FontUtil.getDefaultFont(16));
        titleLabel.setText("和理通");


        ControlLabelMouseListener listener = new ControlLabelMouseListener();
        Dimension controlLabelSize = new Dimension(30, 30);

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
        maxLabel.setIcon(maxIcon);
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
    }

    public static TitlePanel getContext()
    {
        return context;
    }


    public void updateRoomTitle(String roomId)
    {
        room = roomService.findById(roomId);
        this.titleLabel.setText(room.getName());

        //titlePanel.setVisible(true);
        roomInfoButton.setVisible(true);
        RightPanel parent = (RightPanel) getParent();
        parent.showPanel(RightPanel.MESSAGE);
    }

    private class ControlLabelMouseListener extends AbstractMouseListener
    {
        @Override
        public void mouseClicked(MouseEvent e)
        {
            if (e.getComponent() == closeLabel)
            {
                MainFrame.getContext().setVisible(false);
            }
            else if (e.getComponent() == maxLabel)
            {
                if (windowMax)
                {
                    MainFrame.getContext().setBounds(normalBounds);
                    maxLabel.setIcon(maxIcon);
                    windowMax = false;
                }
                else
                {
                    MainFrame.getContext().setBounds(desktopBounds);
                    maxLabel.setIcon(restoreIcon);
                    windowMax = true;
                }
            }
            else if (e.getComponent() == minLabel)
            {
                MainFrame.getContext().setExtendedState(JFrame.ICONIFIED);
            }
        }

        @Override
        public void mouseEntered(MouseEvent e)
        {
            ((JLabel) e.getSource()).setBackground(Colors.LIGHT_GRAY);
            super.mouseEntered(e);
        }

        @Override
        public void mouseExited(MouseEvent e)
        {
            ((JLabel) e.getSource()).setBackground(Colors.WINDOW_BACKGROUND);
            super.mouseExited(e);
        }
    }

}
