package com.rc.db.service;

import com.rc.db.dao.MessageDao;
import com.rc.db.dao.RoomDao;
import com.rc.db.model.Message;
import com.rc.db.model.Room;
import org.apache.ibatis.session.SqlSession;

import java.util.List;

/**
 * Created by song on 08/06/2017.
 */
public class MessageService extends BasicService<MessageDao, Message>
{
    public MessageService(SqlSession session)
    {
        dao = new MessageDao(session);
        super.setDao(dao);
    }

    public int insertOrUpdate(Message message)
    {
        if (exist(message.getId()))
        {
            return update(message);
        }else
        {
            return insert(message);
        }
    }

    public Message findLastMessage(String roomId)
    {
        return dao.findLastMessage(roomId);
    }

    public int deleteByRoomId(String roomId)
    {
        return dao.deleteByRoomId(roomId);
    }

    public int countByRoom(String roomId)
    {
        return dao.countByRoom(roomId);
    }

    public List<Message> findByPage(String roomId, int page, int pageLength)
    {
        return dao.findByPage(roomId, page, pageLength);
    }
}
