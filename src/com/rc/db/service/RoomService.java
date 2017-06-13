package com.rc.db.service;

import com.rc.db.dao.CurrentUserDao;
import com.rc.db.dao.RoomDao;
import com.rc.db.model.BasicModel;
import com.rc.db.model.CurrentUser;
import com.rc.db.model.Room;
import org.apache.ibatis.session.SqlSession;

import java.util.List;

/**
 * Created by song on 08/06/2017.
 */
public class RoomService extends BasicService<RoomDao, Room>
{
    public RoomService(SqlSession session)
    {
        dao = new RoomDao(session);
        super.setDao(dao);
    }

    public int insertOrUpdate(Room room)
    {
        if (exist(room.getRoomId()))
        {
            return update(room);
        }else
        {
            return insert(room);
        }
    }

}