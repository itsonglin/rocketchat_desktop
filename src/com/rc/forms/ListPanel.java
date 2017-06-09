package com.rc.forms;

import javax.swing.*;
import java.awt.*;

/**
 * Created by song on 17-5-30.
 */
public class ListPanel extends ParentAvailablePanel
{
    private RoomsPanel roomsPanel;
    private ContactsPanel contactsPanel;
    private CollectionsPanel collectionPanel;

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
        roomsPanel = new RoomsPanel(this);

        contactsPanel = new ContactsPanel(this);

        collectionPanel = new CollectionsPanel(this);

    }

    private void initView()
    {
        this.setLayout(cardLayout);
        add(roomsPanel, CHAT);
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
