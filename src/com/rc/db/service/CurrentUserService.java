package com.rc.db.service;

import com.rc.db.dao.CurrentUserDao;
import com.rc.db.model.CurrentUser;
import com.rc.utils.DbUtils;
import org.apache.ibatis.session.SqlSession;
import org.sqlite.core.DB;

import java.util.List;

/**
 * Created by song on 08/06/2017.
 */
public class CurrentUserService
{
    private CurrentUserDao dao;

    public CurrentUserService(SqlSession session)
    {
        dao = new CurrentUserDao(session);
    }

    public int insert(CurrentUser user)
    {
        return dao.insert(user);
    }

    public List<CurrentUser> findAll()
    {
        return dao.findAll();
    }


}
