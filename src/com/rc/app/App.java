package com.rc.app;

import com.rc.db.service.TableService;
import com.rc.utils.DbUtils;
import org.apache.ibatis.session.SqlSession;

/**
 * Created by song on 17-5-28.
 */
public class App
{
    public static void main(String[] args)
    {
        checkTable();
        Launcher launcher = new Launcher();
        launcher.launch();
    }


    private static void checkTable()
    {
        SqlSession session = DbUtils.getSqlSession();

        TableService tableService = new TableService(session);
        if (!tableService.exist("current_user"))
        {
            System.out.println("创建表 current_user");
            tableService.createCurrentUserTable();
        }
        if (!tableService.exist("room"))
        {
            System.out.println("创建表 room");
            tableService.createRoomTable();
        }
        if (!tableService.exist("message"))
        {
            System.out.println("创建表 message");
            tableService.createMessageTable();
        }
        if (!tableService.exist("file_attachment"))
        {
            System.out.println("创建表 file_attachment");
            tableService.createFileAttachmentTable();
        }
        if (!tableService.exist("image_attachment"))
        {
            System.out.println("创建表 image_attachment");
            tableService.createImageAttachmentTable();
        }
        if (!tableService.exist("contacts_user"))
        {
            System.out.println("创建表 contacts_user");
            tableService.createContactsUserTable();
        }
    }

}
