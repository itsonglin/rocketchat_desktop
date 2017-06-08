package com.rc.db.dao;

import org.apache.ibatis.session.SqlSession;

/**
 * Created by song on 08/06/2017.
 */
public class TableDao extends BasicDao
{
    public TableDao(SqlSession session)
    {
        super(session);
    }

    public void createCurrentUserTable()
    {
        session.update("createCurrentUserTable");
    }

    public int exist(String name)
    {
        return (int) session.selectOne("tableExist", name);
    }
}
