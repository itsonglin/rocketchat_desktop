package com.rc.db.dao;

import com.rc.db.model.Room;
import org.apache.ibatis.session.SqlSession;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by song on 09/06/2017.
 */
public class RoomDao extends BasicDao
{
    public RoomDao(SqlSession session)
    {
        super(session, RoomDao.class);
    }

    public Room findRelativeRoomIdByUserId(String userId)
    {
        Map map = new HashMap();
        map.put("condition", "'%" + userId + "%'");
        return (Room) session.selectOne("findRelativeRoomIdByUserId", map);
    }
}
