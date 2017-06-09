package com.rc.db.dao;

import com.rc.db.model.BasicModel;
import com.rc.db.model.CurrentUser;
import org.apache.ibatis.session.SqlSession;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by song on 08/06/2017.
 */
public abstract  class BasicDao
{
    protected SqlSession session;

    public BasicDao(SqlSession session)
    {
        this.session = session;
    }

    public int insert(BasicModel model)
    {
        return session.insert("insert", model);
    }

    public List findAll()
    {
        return session.selectList("findAll");
    }

    public BasicModel findById(String id)
    {
        return (BasicModel) session.selectOne("findById", id);
    }

    public List find(String field, Object val)
    {
        Map map = new HashMap();
        map.put("field", field);

        if (val instanceof String)
        {
            map.put("val", "'" + val + "'");
        }
        else
        {
            map.put("val", val);
        }

        return session.selectList("find", map);
    }

    public int delete(String id)
    {
        return session.delete("delete", id);
    }

    public int deleteAll()
    {
        return session.delete("deleteAll");
    }

    public int update(BasicModel model)
    {
        return session.update("update", model);
    }

    public int updateIgnoreNull(BasicModel model)
    {
        return session.update("updateIgnoreNull", model);
    }

    public int count()
    {
        return (int) session.selectOne("count");
    }

    public boolean exist(String id)
    {
        return ((int)(session.selectOne("exist", id))) > 0;
    }

}
