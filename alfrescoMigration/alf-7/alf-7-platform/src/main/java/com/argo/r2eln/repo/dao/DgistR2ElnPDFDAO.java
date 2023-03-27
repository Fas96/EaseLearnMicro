package com.argo.r2eln.repo.dao;

import java.io.IOException;
import java.io.Serializable;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.sql.DataSource;

import org.apache.commons.lang.StringUtils;

public class DgistR2ElnPDFDAO {

	private static DataSource dataSource;
	private static boolean isRunning = false;
	private static SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

	private Connection connection = null;
	private DatabaseHandler databaseHandler;
	private static boolean isOracle = false;
	private static boolean isMySQL = false;

	private static String driverClassName;
	private static String dbUrl;
	private static String dbUsername;
	private static String dbPassword;

	public void setDataSource(DataSource dataSource) {
		DgistR2ElnPDFDAO.dataSource = dataSource;
	}

	public void setDriverClassName(String driver) {
		DgistR2ElnPDFDAO.driverClassName = driver;
	}

	public void setUrl(String dbUrl) {
		DgistR2ElnPDFDAO.dbUrl = dbUrl;
	}

	public void setUsername(String name) {
		DgistR2ElnPDFDAO.dbUsername = name;
	}

	public void setPassword(String password) {
		DgistR2ElnPDFDAO.dbPassword = password;
	}

	public synchronized void setRunning(boolean is) {
		this.isRunning = is;
	}

	public void initConnection() throws SQLException, ClassNotFoundException {
		while (isRunning) {
			try {
				Thread.currentThread().sleep(1000);
			} catch (InterruptedException e) {
			}
		}

		setRunning(true);

		Class.forName(DgistR2ElnPDFDAO.driverClassName);
		this.connection = DriverManager.getConnection(DgistR2ElnPDFDAO.dbUrl, DgistR2ElnPDFDAO.dbUsername,DgistR2ElnPDFDAO.dbPassword);
		System.out.println("dbUrl : " + DgistR2ElnPDFDAO.dbUrl);
        System.out.println("dbUsername : " + DgistR2ElnPDFDAO.dbUsername);

		DatabaseMetaData meta = this.connection.getMetaData();
		String dbms = meta.getDatabaseProductName();
		if (StringUtils.equalsIgnoreCase(dbms, "oracle")) {
			this.connection.setAutoCommit(true);
			isOracle = true;
		} else if (StringUtils.equalsIgnoreCase(dbms, "mysql")) {
			isMySQL = true;
		} else {
			// defalut type is oralce
			isOracle = true;
		}

		this.databaseHandler = new DatabaseHandler(this.connection);
	}

	public void closeConnection() throws SQLException {
		setRunning(false);

		if (this.connection != null && !this.connection.isClosed()) {
			this.connection.commit();
			this.connection.close();
		}
	}
	
	public void commit() throws SQLException {
		if (this.connection != null && !this.connection.isClosed()) {
			this.connection.commit();
		}
	}
	
	private static String sqlInsertJOB_CONV = " insert into PN_JOB_CONV (JOBID, SRCFILE, DESTFILE, FILENAME) values (#jobid#,#srcfile#,#destfile#,#filename#) ";
	
	public void insertJobConv(Map<String, Object> param) {
		
		this.databaseHandler.update(sqlInsertJOB_CONV, param, false);
	}
	
	
	private static String sqlSelectJOB_CONV = " select CODE from PN_JOB_CONV where JOBID=#jobid# ";
	
	public String selectJobConv(Map<String, Object> param) throws SQLException, IOException {
		System.out.println("sql : "+sqlSelectJOB_CONV);
		System.out.println(param);
		List<Map<String, Object>> list = this.databaseHandler.selectAll(sqlSelectJOB_CONV, param);
		System.out.println("size : "+list.size());
		if(list.size() > 0) {
			Map<String, Object> map = list.get(0);
			System.out.println(map);
			
			return map.get("CODE").toString();
		} else {
			return "";
		}
	}
	
    private static String sqlSelectDestFileJOB_CONV = " select DESTFILE from PN_JOB_CONV where JOBID=#jobid# ";
    
    public String selectDestFileJobConv(Map<String, Object> param) throws SQLException, IOException {
        System.out.println("sql : "+sqlSelectDestFileJOB_CONV);
        System.out.println(param);
        List<Map<String, Object>> list = this.databaseHandler.selectAll(sqlSelectDestFileJOB_CONV, param);
        System.out.println("size : "+list.size());
        if(list.size() > 0) {
            Map<String, Object> map = list.get(0);
            System.out.println(map);
            
            return map.get("DESTFILE").toString();
        } else {
            return "";
        }
    }
}