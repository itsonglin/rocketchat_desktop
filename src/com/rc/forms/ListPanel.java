package com.rc.forms;

import javafx.scene.Parent;

import javax.swing.*;
import java.awt.*;

/**
 * Created by song on 17-5-30.
 */
public class ListPanel extends ParentAvailablePanel
{
    private ChatPanel chatPanel;
    private JPanel contactsPanel;
    private JPanel collectionPanel;

    public static final String CHAT = "CHAT";
    public static final String CONTACTS = "CONTACTS";
    public static final String COLLECTIONS = "COLLECTIONS";

    private CardLayout cardLayout = new CardLayout();


    public ListPanel(JPanel parent)
    {
        super(parent);

        initComponents();
        initView();
    }


    private void initComponents()
    {
        chatPanel = new ChatPanel(this);
        chatPanel.setBackground(Color.RED);

        contactsPanel = new JPanel();
        contactsPanel.setBackground(Color.GREEN);

        collectionPanel = new JPanel();
        collectionPanel.setBackground(Color.BLUE);

    }

    private void initView()
    {
        this.setLayout(cardLayout);
        add(chatPanel, CHAT);
        add(contactsPanel, CONTACTS);
        add(collectionPanel, COLLECTIONS);
    }

    /**
     * 显示指定的card
     *
     * @param who
     */
    public void showPanel(String who)
    {
        cardLayout.show(this, who);
    }

}
