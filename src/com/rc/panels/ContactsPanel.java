package com.rc.panels;

import com.rc.adapter.ContactsItemsAdapter;
import com.rc.app.Launcher;
import com.rc.components.Colors;
import com.rc.components.GBC;
import com.rc.components.RCListView;
import com.rc.db.model.ContactsUser;
import com.rc.db.service.ContactsUserService;
import com.rc.db.service.CurrentUserService;
import com.rc.entity.ContactsItem;
import com.rc.utils.AvatarUtil;
import org.apache.log4j.Logger;
import com.rc.tasks.HttpBytesGetTask;
import com.rc.tasks.HttpResponseListener;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by song on 17-5-30.
 */
public class ContactsPanel extends ParentAvailablePanel
{
    private static ContactsPanel context;

    private RCListView contactsListView;
    private List<ContactsItem> contactsItemList = new ArrayList<>();
    private ContactsUserService contactsUserService = Launcher.contactsUserService;
    private Logger logger = Logger.getLogger(this.getClass());
    private CurrentUserService currentUserService = Launcher.currentUserService;
    private String currentUsername;

    public ContactsPanel(JPanel parent)
    {
        super(parent);
        context = this;

        initComponents();
        initView();
        initData();
        contactsListView.setAdapter(new ContactsItemsAdapter(contactsItemList));
    }


    private void initComponents()
    {
        contactsListView = new RCListView();
    }

    private void initView()
    {
        setLayout(new GridBagLayout());
        contactsListView.setContentPanelBackground(Colors.DARK);
        add(contactsListView, new GBC(0, 0).setFill(GBC.BOTH).setWeight(1, 1));
    }

    private void initData()
    {
        contactsItemList.clear();

        List<ContactsUser> contactsUsers = contactsUserService.findAll();
        for (ContactsUser contactsUser : contactsUsers)
        {
            if (contactsUser.getUsername().equals("admin") || contactsUser.getUsername().equals("appStoreTest"))
            {
                continue;
            }

            ContactsItem item = new ContactsItem(contactsUser.getUserId(),
                    contactsUser.getUsername(), "d");

            contactsItemList.add(item);
        }

    }

    public void notifyDataSetChanged()
    {
        initData();
        ((ContactsItemsAdapter) contactsListView.getAdapter()).processData();
        contactsListView.notifyDataSetChanged(false);

        // 通讯录更新后，获取头像
        getContactsUserAvatar();
    }

    public static ContactsPanel getContext()
    {
        return context;
    }

    /**
     * 获取通讯录中用户的头像
     */
    private void getContactsUserAvatar()
    {
        new Thread(new Runnable()
        {
            @Override
            public void run()
            {
                for (ContactsItem user : contactsItemList)
                {
                    if (!AvatarUtil.customAvatarExist(user.getName()))
                    {
                        final String username = user.getName();
                        //logger.debug("获取头像:" + username);
                        getUserAvatar(username, true);
                    }
                }

                // 获取头像
                currentUsername = currentUserService.findAll().get(0).getUsername();
                if (!AvatarUtil.customAvatarExist(currentUsername))
                {
                    getUserAvatar(currentUsername, true);
                }
            }
        }).start();

    }

    /**
     * 更新指定用户头像
     * @param username 用户名
     * @param hotRefresh 是否热更新，hotRefresh = true， 将刷新该用户的头像缓存
     */
    public void getUserAvatar(String username, boolean hotRefresh)
    {
        HttpBytesGetTask task = new HttpBytesGetTask();
        task.setListener(new HttpResponseListener<byte[]>()
        {
            @Override
            public void onResult(byte[] data)
            {
                processAvatarData(data, username);
                if (hotRefresh)
                {
                    //MyInfoPanel.getContext().reloadAvatar();
                    AvatarUtil.refreshUserAvatarCache(username);

                    if (username.equals(currentUsername))
                    {
                        MyInfoPanel.getContext().reloadAvatar();
                    }
                }

            }
        });
        task.execute(Launcher.HOSTNAME + "/avatar/" + username);
    }

    /**
     * 处理头像数据
     * @param data
     * @param username
     */
    private void processAvatarData(byte[] data, String username)
    {
        if (data != null && data.length > 1024)
        {
            AvatarUtil.saveAvatar(data, username);
        }
        else
        {
            AvatarUtil.deleteCustomAvatar(username);
        }
    }

}
