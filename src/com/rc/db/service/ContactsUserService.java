package com.rc.db.service;

import com.rc.db.dao.ContactsUserDao;
import com.rc.db.model.ContactsUser;
import org.apache.ibatis.session.SqlSession;

/**
 * Created by song on 08/06/2017.
 */
public class ContactsUserService extends BasicService<ContactsUserDao, ContactsUser>
{
    public ContactsUserService(SqlSession session)
    {
        dao = new ContactsUserDao(session);
        super.setDao(dao);
    }

    public int insertOrUpdate(ContactsUser contactsUser)
    {
        if (exist(contactsUser.getUserId()))
        {
            return update(contactsUser);
        }else
        {
            return insert(contactsUser);
        }
    }

    public int deleteByUsername(String name)
    {
        return dao.deleteByUsername(name);
    }
}
