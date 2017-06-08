package com.rc.db.dao;

import com.rc.db.model.BasicModel;
import org.apache.ibatis.session.SqlSession;

import java.util.List;

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

    public int delete(String id)
    {
        return session.delete("delete", id);
    }

    public int update(BasicModel model)
    {
        return session.update("update", model);
    }

}
