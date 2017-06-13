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
    private String className;


    public BasicDao(SqlSession session, Class clazz)
    {
        this.session = session;
        this.className = clazz.getName();
    }

    public int insert(BasicModel model)
    {
        return session.insert(className + ".insert", model);
    }

    public List findAll()
    {
        return session.selectList(className + ".findAll");
    }

    public BasicModel findById(String id)
    {
        return (BasicModel) session.selectOne(className + ".findById", id);
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

        return session.selectList(className + ".find", map);
    }

    public int delete(String id)
    {
        return session.delete(className + ".delete", id);
    }

    public int deleteAll()
    {
        return session.delete(className + ".deleteAll");
    }

    public int update(BasicModel model)
    {
        return session.update(className + ".update", model);
    }

    public int updateIgnoreNull(BasicModel model)
    {
        return session.update(className + ".updateIgnoreNull", model);
    }

    public List updateField(String field, Object val)
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

        return session.selectList(className + ".updateField", map);
    }

    public int count()
    {
        return (int) session.selectOne(className + ".count");
    }

    public boolean exist(String id)
    {
        return ((int)(session.selectOne(className + ".exist", id))) > 0;
    }

}