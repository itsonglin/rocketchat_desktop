package com.rc.db.dao;

import org.apache.ibatis.session.SqlSession;

/**
 * Created by song on 09/06/2017.
 */
public class ContactsUserDao extends BasicDao
{
    public ContactsUserDao(SqlSession session)
    {
        super(session, ContactsUserDao.class);
    }

    public int deleteByUsername(String username)
    {
        return session.delete("deleteByUsername", username);
    }
}
