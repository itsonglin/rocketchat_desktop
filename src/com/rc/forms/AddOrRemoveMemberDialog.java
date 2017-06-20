package com.rc.forms;

import com.rc.app.Launcher;
import com.rc.app.ShadowBorder;
import com.rc.components.*;
import com.rc.db.model.ContactsUser;
import com.rc.db.service.ContactsUserService;
import com.rc.utils.FontUtil;
import com.rc.utils.OSUtil;
import com.rc.websocket.WebSocketClient;
import com.sun.awt.AWTUtilities;

import javax.swing.*;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;

import static com.rc.app.Launcher.roomService;

/**
 * Created by song on 06/20/2017.
 */

public class AddOrRemoveMemberDialog extends JDialog
{
    private JPanel editorPanel;
    private RCTextField searchTextField;

    private SelectUserPanel selectUserPanel;
    private JPanel buttonPanel;
    private JButton cancelButton;
    private JButton okButton;
    private List<String> userList = new ArrayList<>();

    public static final int DIALOG_WIDTH = 600;
    public static final int DIALOG_HEIGHT = 500;


    public AddOrRemoveMemberDialog(Frame owner, boolean modal, List<String> userList)
    {
        super(owner, modal);
        this.userList = userList;

        initComponents();

        initView();
        setListeners();
    }

    private void initComponents()
    {
        int posX = MainFrame.getContext().getX();
        int posY = MainFrame.getContext().getY();

        posX = posX + (MainFrame.getContext().currentWindowWidth - DIALOG_WIDTH) / 2;
        posY = posY + (MainFrame.getContext().currentWindowHeight - DIALOG_HEIGHT) / 2;
        setBounds(posX, posY, DIALOG_WIDTH, DIALOG_HEIGHT);
        setUndecorated(true);

        getRootPane().setBorder(new LineBorder(Colors.LIGHT_GRAY));

        if (OSUtil.getOsType() != OSUtil.Mac_OS)
        {
            // 边框阴影，但是会导致字体失真
            AWTUtilities.setWindowOpaque(this, false);
            //getRootPane().setOpaque(false);
            getRootPane().setBorder(ShadowBorder.newInstance());
        }

        selectUserPanel = new SelectUserPanel(userList);

        // 输入面板
        editorPanel = new JPanel();
        searchTextField = new RCTextField();
        searchTextField.setPlaceholder("群聊名称");
        searchTextField.setPreferredSize(new Dimension(DIALOG_WIDTH / 2, 35));
        searchTextField.setFont(FontUtil.getDefaultFont(14));
        searchTextField.setForeground(Colors.FONT_BLACK);
        searchTextField.setMargin(new Insets(0, 15, 0, 0));


        // 按钮组
        buttonPanel = new JPanel();
        buttonPanel.setLayout(new FlowLayout(FlowLayout.RIGHT, 10, 10));

        cancelButton = new RCButton("取消");
        cancelButton.setForeground(Colors.FONT_BLACK);

        okButton = new RCButton("创建", Colors.MAIN_COLOR, Colors.MAIN_COLOR_DARKER, Colors.MAIN_COLOR_DARKER);
        okButton.setBackground(Colors.PROGRESS_BAR_START);
    }


    private void initView()
    {
        editorPanel.setLayout(new FlowLayout(FlowLayout.LEFT, 10, 10));
        editorPanel.add(searchTextField);

        buttonPanel.add(cancelButton, new GBC(0, 0).setWeight(1, 1).setInsets(15, 0, 0, 0));
        buttonPanel.add(okButton, new GBC(1, 0).setWeight(1, 1));


        /*setLayout(new VerticalFlowLayout(VerticalFlowLayout.LEFT, 0, 0, false, false));
        editorPanel.setPreferredSize(new Dimension(DIALOG_WIDTH, 40));
        selectUserPanel.setPreferredSize(new Dimension(400, 200));
        buttonPanel.setPreferredSize(new Dimension(DIALOG_WIDTH, 40));
        add(editorPanel);
        add(selectUserPanel);
        add(buttonPanel);
*/

        add(editorPanel, BorderLayout.NORTH);
        add(selectUserPanel, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);
    }

    private void setListeners()
    {
        cancelButton.addMouseListener(new MouseAdapter()
        {
            @Override
            public void mouseClicked(MouseEvent e)
            {
                setVisible(false);

                super.mouseClicked(e);
            }
        });

        okButton.addMouseListener(new MouseAdapter()
        {
            @Override
            public void mouseClicked(MouseEvent e)
            {
                super.mouseClicked(e);
            }
        });
    }

    public List<String> getSelectedUser()
    {
        return selectUserPanel.getSelectedUser();
    }

}
