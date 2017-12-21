package com.ruisi.ext.engine.view.builder.dsource;

import com.ruisi.ext.engine.ConstantsEngine;
import com.ruisi.ext.engine.util.PasswordEncrypt;
import com.ruisi.ext.engine.view.context.MVContext;
import com.ruisi.ext.engine.view.context.dsource.DataSourceContext;
import com.ruisi.ext.engine.view.context.grid.PageInfo;
import com.ruisi.ext.engine.view.exception.ExtConfigException;
import com.ruisi.ext.engine.view.exception.ExtRuntimeException;
import com.ruisi.ext.engine.wrapper.ExtRequest;
import java.sql.Blob;
import java.sql.Clob;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.sql.DataSource;
import org.apache.commons.collections.map.CaseInsensitiveMap;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class DataSourceBuilder {
	private static Log log = LogFactory.getLog(DataSourceBuilder.class);
	public static final String mysql = "com.mysql.jdbc.Driver";
	public static final String oracle = "oracle.jdbc.driver.OracleDriver";
	public static final String sqlserver = "net.sourceforge.jtds.jdbc.Driver";

	public DataSourceContext findDataSource(String id, MVContext mv) throws ExtConfigException {
		Map m = mv.getDsources();
		if ((m == null) || (m.get(id) == null)) {
			String err = ConstantsEngine.replace("配置的dataSource id = $0 不存在.", id);
			throw new ExtConfigException(err);
		}
		return ((DataSourceContext) m.get(id));
	}

	private int queryDataCount(String sql, DataSourceContext dsource, Connection conn) throws SQLException {
		int ret = 0;
		String nsql = "select count(*) cnt from (" + sql + ") ttt";
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			ps = conn.prepareStatement(nsql);
			rs = ps.executeQuery();
			rs.next();
			ret = rs.getInt(1);
		} finally {
			if (ps != null) {
				try {
					ps.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			if (rs != null) {
				try {
					rs.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}

		return ret;
	}

	public List queryForList(String sql, DataSourceContext dsource, PageInfo page, ExtRequest request) {
		List ls = new ArrayList();
		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			conn = crtConnection(dsource);

			int cnt = queryDataCount(sql, dsource, conn);
			page.setAllsize(cnt);
			request.setAttribute("ext.view.pageInfo", page);

			if (dsource.getLinktype().equals(DataSourceContext.linkTypes[1]))
				sql = "select * from (select rownum idd,t.* from (" + sql + ") t where rownum<="
						+ (page.getCurtpage() * page.getPagesize() + page.getPagesize())
						+ " order by rownum) tt where tt.idd>" + (page.getCurtpage() * page.getPagesize());
			else if (dsource.getLinktype().equals(DataSourceContext.linkTypes[0])) {
				sql = "select * from ( " + sql + " ) tt limit " + (page.getCurtpage() * page.getPagesize()) + ","
						+ page.getPagesize();
			} else if (dsource.getLinktype().equals(DataSourceContext.linkTypes[2]))
				sql = "select *from ( select row_number()over(order by tempColumn)tempRowNumber,* from (select top "
						+ (page.getCurtpage() * page.getPagesize() + page.getPagesize()) + " 0 tempColumn,"
						+ sql.replaceFirst("select", "") + " " + ")t " + " )tt where tempRowNumber>"
						+ (page.getCurtpage() * page.getPagesize());
			else {
				throw new ExtConfigException("您配置的连接类型系统暂不支持。");
			}
			log.info(sql);
			ps = conn.prepareStatement(sql);
			rs = ps.executeQuery();
			int c = rs.getMetaData().getColumnCount();
			String[] cols = new String[c];
			for (int i = 1; i <= c; ++i) {
				cols[(i - 1)] = rs.getMetaData().getColumnName(i);
			}
			while (rs.next()) {
				CaseInsensitiveMap m = new CaseInsensitiveMap();
				for (int i = 1; i <= cols.length; ++i) {
					String col = cols[(i - 1)];
					m.put(col, getResultSetValue(rs, i));
				}
				ls.add(m);
			}
		} catch (Exception ex) {
			ex.printStackTrace();
			throw new ExtRuntimeException(ex);
		} finally {
			if (ps != null) {
				try {
					ps.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			if (rs != null) {
				try {
					rs.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			closeConnection(conn);
		}
		return ls;
	}

	public List queryForList(String sql, DataSourceContext dsource) {
		List ls = new ArrayList();
		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			conn = crtConnection(dsource);
			ps = conn.prepareStatement(sql);
			log.info(sql);
			rs = ps.executeQuery();
			int c = rs.getMetaData().getColumnCount();
			String[] cols = new String[c];
			for (int i = 1; i <= c; ++i) {
				cols[(i - 1)] = rs.getMetaData().getColumnName(i);
			}
			while (rs.next()) {
				CaseInsensitiveMap m = new CaseInsensitiveMap();
				for (int i = 1; i <= cols.length; ++i) {
					String col = cols[(i - 1)];
					m.put(col, getResultSetValue(rs, i));
				}
				ls.add(m);
			}
		} catch (Exception ex) {
			ex.printStackTrace();
			throw new ExtRuntimeException(ex);
		} finally {
			if (ps != null) {
				try {
					ps.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			if (rs != null) {
				try {
					rs.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			closeConnection(conn);
		}
		return ls;
	}

	public Connection crtConnection(DataSourceContext dsource) {
		Connection conn = null;
		try {
			if ((dsource.getUse() == null) || ("jdbc".equalsIgnoreCase(dsource.getUse()))) {
				String clazz = null;
				if (dsource.getLinktype().equals(DataSourceContext.linkTypes[0]))
					clazz = "com.mysql.jdbc.Driver";
				else if (dsource.getLinktype().equals(DataSourceContext.linkTypes[1]))
					clazz = "oracle.jdbc.driver.OracleDriver";
				else if (dsource.getLinktype().equals(DataSourceContext.linkTypes[2]))
					clazz = "net.sourceforge.jtds.jdbc.Driver";
				else {
					throw new ExtRuntimeException("您配置的连接类型系统暂不支持。");
				}
				Class.forName(clazz).newInstance();
				String psd = PasswordEncrypt.decode(dsource.getLinkpwd());
				conn = DriverManager.getConnection(dsource.getLinkurl(), dsource.getLinkname(), psd);
			}else {
				Context ctx = new InitialContext();
				String strLookup = "java:comp/env/" + dsource.getJndiname();
				DataSource ds = (DataSource) ctx.lookup(strLookup);
				conn = ds.getConnection();
			}
		} catch (SQLException e) {
			e.printStackTrace();
			throw new ExtRuntimeException("数据库连接出错。");
		} catch (Exception e) {
			throw new ExtRuntimeException(e);
		}
		return conn;
	}

	public void closeConnection(Connection conn) {
		if (conn == null)
			return;
		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public static Object getResultSetValue(ResultSet rs, int index) throws SQLException {
		Object obj = rs.getObject(index);
		if (obj instanceof Blob) {
			obj = rs.getBytes(index);
		} else if (obj instanceof Clob) {
			obj = rs.getString(index);
		} else if ((obj != null) && (obj.getClass().getName().startsWith("oracle.sql.TIMESTAMP"))) {
			obj = rs.getTimestamp(index);
		} else if ((obj != null) && (obj.getClass().getName().startsWith("oracle.sql.DATE"))) {
			String metaDataClassName = rs.getMetaData().getColumnClassName(index);
			if (("java.sql.Timestamp".equals(metaDataClassName))
					|| ("oracle.sql.TIMESTAMP".equals(metaDataClassName))) {
				obj = rs.getTimestamp(index);
			} else {
				obj = rs.getDate(index);
			}
		} else if ((obj != null) && (obj instanceof Date)
				&& ("java.sql.Timestamp".equals(rs.getMetaData().getColumnClassName(index)))) {
			obj = rs.getTimestamp(index);
		}

		return obj;
	}
}