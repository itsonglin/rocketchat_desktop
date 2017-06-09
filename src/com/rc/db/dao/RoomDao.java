package com.rc.db.dao;

import org.apache.ibatis.session.SqlSession;

/**
 * Created by song on 09/06/2017.
 */
public class RoomDao extends BasicDao
{
    public RoomDao(SqlSession session)
    {
        super(session);
    }
}
