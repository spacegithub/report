package com.ruisi.ext.engine.dao;

import java.util.List;
import java.util.Map;
import org.springframework.jdbc.core.ConnectionCallback;
import org.springframework.jdbc.core.PreparedStatementCallback;
import org.springframework.orm.ibatis.SqlMapClientTemplate;

public abstract interface DaoHelper {
	public abstract List queryForList(String paramString);

	public abstract Map queryForMap(String paramString);

	public abstract Object queryForObject(String paramString, Class paramClass);

	public abstract Object execute(String paramString, PreparedStatementCallback paramPreparedStatementCallback);

	public abstract Long queryForLong(String paramString);

	public abstract void execute(String paramString);

	public abstract int queryForInt(String paramString);

	public abstract List queryForList(String paramString, Object[] paramArrayOfObject);

	public abstract Object execute(ConnectionCallback paramConnectionCallback);

	public abstract SqlMapClientTemplate getSqlMapClientTemplate();
}