package com.rc.db.service;

import com.rc.db.dao.TableDao;
import org.apache.ibatis.session.SqlSession;

/**
 * Created by song on 08/06/2017.
 */
public class TableService
{
    private TableDao dao;

    public TableService(SqlSession session)
    {
        dao = new TableDao(session);
    }

    public void createCurrentUserTable()
    {
        dao.createCurrentUserTable();
    }

    public boolean exist(String name)
    {
        return dao.exist(name);
    }
}
