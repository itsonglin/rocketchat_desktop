package com.rc.adapter;

import com.rc.components.Colors;
import com.rc.components.GBC;
import com.rc.components.RCBorder;
import com.rc.entity.ContactsItem;
import com.rc.listener.AbstractMouseListener;
import com.rc.utils.CharacterParser;
import com.rc.utils.FontUtil;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.*;
import java.util.List;

/**
 * Created by song on 17-5-30.
 */
public class ContactsItemsAdapter extends BaseAdapter<ContactsItemViewHolder>
{
    private List<ContactsItem> contactsItems;
    private List<ContactsItemViewHolder> viewHolders = new ArrayList<>();
    Map<Integer, String> positionMap = new HashMap<>();
    private ContactsItemViewHolder selectedViewHolder;

    public ContactsItemsAdapter(List<ContactsItem> contactsItems)
    {
        this.contactsItems = contactsItems;

        if (contactsItems != null)
        {
            processData();
        }
    }

    @Override
    public int getCount()
    {
        return contactsItems.size();
    }

    @Override
    public ContactsItemViewHolder onCreateViewHolder(int viewType)
    {
        return new ContactsItemViewHolder();
    }

    @Override
    public HeaderViewHolder onCreateHeaderViewHolder(int viewType, int position)
    {
        for (int pos : positionMap.keySet())
        {
            if (pos == position)
            {
                String ch = positionMap.get(pos);

                return new ContactsHeaderViewHolder(ch.toUpperCase());
            }
        }

        return null;
    }

    @Override
    public void onBindHeaderViewHolder(HeaderViewHolder viewHolder, int position)
    {
        ContactsHeaderViewHolder holder = (ContactsHeaderViewHolder) viewHolder;
        holder.setPreferredSize(new Dimension(100, 25));
        holder.setBackground(Colors.DARKER);
        holder.setBorder(new RCBorder(RCBorder.BOTTOM));
        holder.setOpaque(true);

        holder.letterLabel = new JLabel();
        holder.letterLabel.setText(holder.getLetter());
        holder.letterLabel.setForeground(Colors.FONT_GRAY);

        holder.setLayout(new BorderLayout());
        holder.add(holder.letterLabel, BorderLayout.WEST);
    }

    @Override
    public void onBindViewHolder(ContactsItemViewHolder viewHolder, int position)
    {
        viewHolders.add(position, viewHolder);
        ContactsItem item = contactsItems.get(position);

        //viewHolder.panelItem = new JPanel();
        viewHolder.setPreferredSize(new Dimension(100, 50));
        viewHolder.setBackground(Colors.DARK);
        viewHolder.setBorder(new RCBorder(RCBorder.BOTTOM));
        viewHolder.setOpaque(true);
        viewHolder.setForeground(Colors.FONT_WHITE);

        // 头像
        viewHolder.avatar = new JLabel();
        ImageIcon imageIcon = new ImageIcon(getClass().getResource("/image/avatar.jpg"));
        imageIcon.setImage(imageIcon.getImage().getScaledInstance(30, 30, Image.SCALE_SMOOTH));
        viewHolder.avatar.setIcon(imageIcon);

        // 名字
        viewHolder.roomName = new JLabel();
        viewHolder.roomName.setText(item.getName());
        viewHolder.roomName.setFont(FontUtil.getDefaultFont(13));
        viewHolder.roomName.setForeground(Colors.FONT_WHITE);

        viewHolder.setLayout(new GridBagLayout());
        viewHolder.add(viewHolder.avatar, new GBC(0, 0).setWeight(1, 1).setFill(GBC.BOTH).setInsets(0,5,0,0));
        viewHolder.add(viewHolder.roomName, new GBC(1, 0).setWeight(10, 1).setFill(GBC.BOTH));


        viewHolder.addMouseListener(new AbstractMouseListener()
        {
            @Override
            public void mouseClicked(MouseEvent e)
            {
                setBackground(viewHolder, Colors.ITEM_SELECTED);
                selectedViewHolder = viewHolder;

                for (ContactsItemViewHolder holder : viewHolders)
                {
                    if (holder != viewHolder)
                    {
                        setBackground(holder, Colors.DARK);
                    }
                }
            }

            @Override
            public void mouseEntered(MouseEvent e)
            {
                if (selectedViewHolder != viewHolder)
                {
                    setBackground(viewHolder, Colors.ITEM_SELECTED_DARK);
                }
            }

            @Override
            public void mouseExited(MouseEvent e)
            {
                if (selectedViewHolder != viewHolder)
                {
                    setBackground(viewHolder, Colors.DARK);
                }
            }
        });
    }

    private void setBackground(ContactsItemViewHolder holder, Color color)
    {
        holder.setBackground(color);
    }

    private void processData()
    {
        Collections.sort(contactsItems);

        int index = 0;
        String lastChara = "";
        for (ContactsItem item : contactsItems)
        {
            String ch = CharacterParser.getSelling(item.getName()).substring(0, 1);
            if (!ch.equals(lastChara))
            {
                lastChara = ch;
                positionMap.put(index, ch);
            }

            index++;
        }
    }
}
