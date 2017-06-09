package com.rc.db.dao;

import com.rc.db.model.BasicModel;
import com.rc.db.model.CurrentUser;
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

    public boolean exist(String userId)
    {
        return ((int)(session.selectOne("exist", userId))) > 0;
    }

    public int insertOrUpdate(CurrentUser currentUser)
    {
        if (exist(currentUser.getUserId()))
        {
            CurrentUser user = (CurrentUser) findById(currentUser.getUserId());
            System.out.println(user);
            return update(currentUser);
        }else
        {
            return insert(currentUser);
        }
    }
}
