/**
 * @author AMCC
 * <br /> 邮箱:zhouyusgs@ahmobile.com
 * <br /> 描述:H2DBHelper.java
 * <br /> 版本：1.0.0
 * <br /> 日期：2013-10-16
 */
package com.cmcc.zysoft.schedule.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;
import java.util.List;
import java.util.Map;

/**
 * @author 周瑜 com.cmcc.zysoft.groupaddressbook.util 创建时间：2013-10-16
 */
public class H2DBHelper {

	private static final String SOURCE_URL = "jdbc:h2:tcp://localhost/mem:testmemdb";// H2DB
																						// mem
																						// mode
	private static final String USER = "sa";
	private static final String KEY = "";

	public void createTable() {
		try {
			Class.forName("org.h2.Driver");
			Connection conn = DriverManager
					.getConnection(SOURCE_URL, USER, KEY);// 把驱动放入连接
			Statement stmt = conn.createStatement();
			stmt.execute("CREATE TABLE userinfo(h varchar(32),content text)");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void insert(String h, String content) {
		try {
			Class.forName("org.h2.Driver");
			Connection conn = DriverManager
					.getConnection(SOURCE_URL, USER, KEY);// 把驱动放入连接
			Statement stmt = conn.createStatement();
			stmt.execute("INSERT INTO userinfo (h,content) VALUES ('" + h
					+ "','" + content + "')");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public List<Map<String, Object>> query() {
		try {
			Class.forName("org.h2.Driver");
			Connection conn = DriverManager
					.getConnection(SOURCE_URL, USER, KEY);// 把驱动放入连接
			Statement stmt = conn.createStatement();
			stmt.executeQuery("SELECT * FROM userinfo");
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
}
