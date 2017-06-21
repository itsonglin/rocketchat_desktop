package com.rc.forms;

import javax.swing.*;
import java.awt.*;

/**
 * Created by song on 17-5-30.
 */
public class ListPanel extends ParentAvailablePanel
{
    private static ListPanel context;
    private RoomsPanel roomsPanel;
    private ContactsPanel contactsPanel;
    private CollectionsPanel collectionPanel;
    private SearchResultPanel searchResultPanel;

    public static final String CHAT = "CHAT";
    public static final String CONTACTS = "CONTACTS";
    public static final String COLLECTIONS = "COLLECTIONS";
    public static final String SEARCH = "SEARCH";

    private CardLayout cardLayout = new CardLayout();


    public ListPanel(JPanel parent)
    {
        super(parent);
        context = this;

        initComponents();
        initView();
    }


    private void initComponents()
    {
        roomsPanel = new RoomsPanel(this);

        contactsPanel = new ContactsPanel(this);

        collectionPanel = new CollectionsPanel(this);

        searchResultPanel = new SearchResultPanel(this);

    }

    private void initView()
    {
        this.setLayout(cardLayout);
        add(roomsPanel, CHAT);
        add(contactsPanel, CONTACTS);
        add(collectionPanel, COLLECTIONS);
        add(searchResultPanel, SEARCH);
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

    public static ListPanel getContext()
    {
        return context;
    }

}
