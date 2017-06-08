package com.rc.utils;

import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;

import java.io.IOException;
import java.io.Reader;

public class DbUtils
{
	private static SqlSession sqlSession = null;

	static {
		try {
			Reader reader = Resources.getResourceAsReader("mybatis.xml");
			SqlSessionFactory sqlMapper = new SqlSessionFactoryBuilder().build(reader);
			sqlSession = sqlMapper.openSession(true);
			reader.close();

		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	private DbUtils() {

	}

	public static SqlSession getSqlSession() {
		return sqlSession;
	}
}
