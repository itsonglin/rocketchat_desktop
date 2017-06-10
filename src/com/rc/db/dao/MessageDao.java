package com.rc.db.dao;

import com.rc.db.model.Message;
import org.apache.ibatis.session.SqlSession;

/**
 * Created by song on 09/06/2017.
 */
public class MessageDao extends BasicDao
{
    public MessageDao(SqlSession session)
    {
        super(session, MessageDao.class);
    }

    public Message findLastMessage(String roomId)
    {
        return (Message) session.selectOne("findLastMessage", roomId);
    }
}
