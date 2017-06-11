package com.rc.db.dao;

import com.rc.db.model.Message;
import org.apache.ibatis.session.SqlSession;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

    public int deleteByRoomId(String roomId)
    {
        return session.delete("deleteByRoomId", roomId);
    }

    public int countByRoom(String roomId)
    {
        return (int) session.selectOne("countByRoom", roomId);
    }

    public List<Message> findByPage(String roomId, int page, int pageLength)
    {
        page = page < 1 ? 1 : page;
        Map map = new HashMap();
        map.put("roomId", "'" + roomId + "'");
        map.put("offset", (page - 1) * pageLength);
        map.put("pageLength", pageLength);
        return session.selectList("findByPage", map);
    }
}
