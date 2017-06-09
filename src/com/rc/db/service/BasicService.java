package com.rc.db.service;

import com.rc.db.dao.BasicDao;
import com.rc.db.model.BasicModel;
import com.rc.db.model.CurrentUser;
import org.apache.ibatis.session.SqlSession;

import java.util.List;

/**
 * Created by song on 09/06/2017.
 */
public class BasicService<T extends BasicDao>
{
    T dao;

    public void setDao(T dao)
    {
        this.dao = dao;
    }

    public int insert(BasicModel model)
    {
        return dao.insert(model);
    }

    public List findAll()
    {
        return dao.findAll();
    }

    public BasicModel findById(String id)
    {
        return dao.findById(id);
    }

    public int delete(String id)
    {
        return dao.delete(id);
    }

    public int deleteAll()
    {
        return dao.deleteAll();
    }

    public int update(BasicModel model)
    {
        return dao.update(model);
    }

    public int updateIgnoreNull(BasicModel model)
    {
        return dao.updateIgnoreNull(model);
    }

    public int count()
    {
        return dao.count();
    }

    public boolean exist(String id)
    {
        return dao.exist(id);
    }

    public int insertOrUpdate(CurrentUser model)
    {
        return dao.insertOrUpdate(model);
    }

}
