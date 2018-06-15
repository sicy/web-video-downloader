package com.sicy.paipai;

import com.alibaba.druid.pool.DruidDataSource;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 数据库
 * @author Administrator
 *
 */
public class DbUtils {

	private static String url = "jdbc:mysql://localhost:3306/dk_db?useUnicode=true&amp;characterEncoding=utf-8&zeroDateTimeBehavior=convertToNull";
	private static String username = "root";
	private static String password = "smart";
	
	
	private static DbUtils dbUtil;
	
	private DbUtils(){
		
	}
	
	public static DbUtils getIntence(){
		if(dbUtil == null){
			return new DbUtils();
		}
		return dbUtil;
	}
	
	public boolean executeUpdateBatch(String[] sqls) throws SQLException {
		Connection conn = null;
		Statement stmt = null;
		try {
			conn = getConnection();
			int length = sqls.length;
			conn.setAutoCommit(false);
			stmt = conn.createStatement();
			for (int i = 0; i < length; i++) {
				stmt.addBatch(sqls[i]);
			}
			stmt.executeBatch();
			conn.commit();
			conn.setAutoCommit(true);
			return true;
		} finally {
			if (stmt != null) {
				try {
					stmt.close();
				} catch (Exception e) {
				}
			}
			try {
				conn.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}

	public int updateAndGetKey(String sql) throws Exception {
		Statement stmt = null;
		ResultSet rs = null;
		Connection conn = null;
		try {
			conn = getConnection();
			stmt = conn.createStatement(ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_UPDATABLE);
			stmt.executeUpdate(sql, new String[] { "ID" });
			int autoIncKeyFromApi = -1;
			rs = stmt.getGeneratedKeys();
			if (rs.next()) {
				autoIncKeyFromApi = rs.getInt(1);
			} else {
				autoIncKeyFromApi = -1;
			}
			rs.close();
			rs = null;
			return autoIncKeyFromApi;
		} finally {
			if (rs != null) {
				try {
					rs.close();
				} catch (Exception e) {
				}
			}
			if (stmt != null) {
				try {
					stmt.close();
				} catch (Exception e) {
				}
			}
			try {
				conn.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * 执行查询，将每行的结果保存到一个Map对象中，然后将所有Map对象保存到List中
	 * 
	 * @param sql
	 *            sql语句
	 * @param params
	 *            参数数组
	 * @return 查询结果
	 * @throws SQLException 
	 */
	public List<Map<String, Object>> find(String sql, Object...params) throws SQLException {
		Connection conn = null;
//		Statement stmt = null;
		PreparedStatement stat = null;
		ResultSet rs = null;
		List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
		try {
			conn = getConnection();
//			stmt = conn.createStatement();
//			rs = stmt.executeQuery(sql);
			stat = conn.prepareStatement(sql);
			for (int i = 0; i < params.length; i++) {
				stat.setObject(i+1, params[i]);
			}
			rs = stat.executeQuery();
			ResultSetMetaData md = rs.getMetaData();
			int columnCount = md.getColumnCount();
			while (rs.next()) {
				Map<String, Object> map = new HashMap<String, Object>();
				for (int i = 1; i <= columnCount; i++) {
					map.put(md.getColumnName(i), rs.getObject(i));
				}
				list.add(map);
			}
		} catch (SQLException e) {
			throw e;
		} finally {
			if (rs != null) {
				try {
					rs.close();
				} catch (Exception e) {
				}
			}
			if (stat != null) {
				try {
					stat.close();
				} catch (Exception e) {
				}
			}
			try {
				conn.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		return list;
	}
	
	public int update(String sql,Object... params) throws SQLException {
		Connection conn = null;
//		Statement stmt = null;
		PreparedStatement stat = null;
		int affectedRows = 0;
		try {
			conn = getConnection();
//			stmt = conn.createStatement();
//			affectedRows = stmt.executeUpdate(sql);
			//修改为预加载SQL方式
			stat = conn.prepareStatement(sql);
			for (int i = 0; i < params.length; i++) {
				stat.setObject(i+1, params[i]);
			}
			affectedRows = stat.executeUpdate();
//			conn.commit();
		} catch (SQLException e) {
			throw e;
		} finally {
			if (stat != null) {
				try {
					stat.close();
				} catch (Exception e) {
				}
			}
			try {
				conn.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		return affectedRows;
	}
	
	public static DruidDataSource dataSource;
	
	static {
	    try {
	        dataSource = new DruidDataSource();
			dataSource.setUsername(username);
	        dataSource.setPassword(password);
			dataSource.setUrl(url);
	        dataSource.setDriverClassName("com.mysql.jdbc.Driver");
	    } catch (Exception e) {
	        e.printStackTrace();
	    }
	}
	  
	/** 
	 * 从连接池中获取数据源链接 
	 * @return Connection 数据源链接 
	 * @throws SQLException 
	 */  
	private static Connection getConnection() throws SQLException {
	    Connection conn = null;
	    if (null != dataSource) {
	        try {
	            conn = dataSource.getConnection();
	        } catch (SQLException e) {
	        	throw e;
	        }
	    }
	    return conn;
	}
	
}
