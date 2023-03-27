package com.argo.r2eln.repo.dao;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.io.StringReader;
import java.io.StringWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DatabaseHandler {

	private Connection connection;
	
	protected static Pattern SQL_PATTERN = null;
	
	
	static {
		try {
			SQL_PATTERN = Pattern.compile("#([^', \\n\\r]+)#");
		} catch (Exception e) {}
	}
	
	private List<String> streamField = null;
	
	private List<String> textType = Arrays.asList(new String[]{
			"LONG","CLOB","VARBINARY"
	});
	
	private List<String> binaryType = Arrays.asList(new String[]{
			"BLOB"
	});
	
	// used for query time
	private PreparedStatement stmt = null;
	private ResultSet rs = null;
	private ResultSetMetaData meta = null;
	
	public DatabaseHandler(Connection connection) {
		this.connection = connection;
		this.streamField = new ArrayList<String>();
	}
	
	public void addStreamField(String fieldName) {
		streamField.add(fieldName);
	}
	
	public void addTextType(String type) {
		this.textType.add(type);
	}
	
	public void addBinaryType(String type) {
		this.binaryType.add(type);
	}
	
	/**
	 * #param# 
	 * @param sql
	 * @param source
	 * @throws UtilsException
	 */
	public void update(String sql, Map source, boolean logging)  {

		List<String> params = new ArrayList<String>();
		stmt = null;
		try {
			sql = parseSQL(sql, params);
			stmt = this.connection.prepareStatement(sql);
			for(int i=0;i<params.size();i++) {
				String fieldName = params.get(i);	
				setDataToField(source, stmt, i, fieldName);
			}
			stmt.executeUpdate();
		} catch (SQLException e) {
			throw new RuntimeException ("query-->"+sql+"\n" +source+" \n " +e.getMessage());
		} finally {
			closeStatement();
		}
	}

	private void setDataToField(Map source, PreparedStatement stmt, int i, String fieldName) throws SQLException {
		Reader readerCont;
		if(source.get(fieldName)==null) {
			stmt.setNull(i+1, java.sql.Types.VARCHAR);
		}else if(fieldName.toLowerCase().endsWith("_stream") || streamField.contains(fieldName)) {
			String message = (String)source.get(fieldName);
			readerCont = new StringReader(message);
			stmt.setCharacterStream(i+1,readerCont,message.length());						
		} else {
			stmt.setObject(i+1, source.get(fieldName));
		}
	}

	public List<Map<String, Object>> selectAll(String sql, Map<String, Object>param) 
			throws SQLException, IOException {
		
		List<Map<String, Object>> result = new ArrayList<Map<String,Object>>();
		
		executeQuery(sql, param);
		while(hasNext()) {
			result.add(next());
		}
		
		return result;
	}
	
	public void executeQuery(String sql) throws SQLException {
		executeQuery(sql, null);
	}
	
	public void executeQuery(String sql, Map source) throws SQLException {
		List<String> params = new ArrayList<String>();
		sql = parseSQL(sql, params);
		stmt = connection.prepareStatement(sql);
		for(int i=0;i<params.size();i++) {
			stmt.setObject(i+1, source.get(params.get(i)));
		}			
		rs = stmt.executeQuery();
		meta = rs.getMetaData();	
	}
	
	public boolean hasNext() throws SQLException {
		boolean hasNext = rs.next();
		if(hasNext) return true;
		
		closeStatement();
		return false;
	}
	
	public Map<String, Object> next() throws SQLException, IOException {
		
		String colName = null;
		String colType=null ;
		
		Map<String, Object> data = new HashMap<String, Object>();
		
		for(int i=1;i<=meta.getColumnCount();i++) {
			colName = meta.getColumnName(i).toUpperCase();
			colType = meta.getColumnTypeName(i).toUpperCase();

			if(textType.contains(colType)) {
				Reader l_contents = rs.getCharacterStream(colName);								
				data.put(meta.getColumnName(i), readerToString(l_contents));
			}else if(binaryType.contains(colType) || colName.toLowerCase().endsWith("_stream")) {
				InputStream is = rs.getBinaryStream(colName);
				data.put(meta.getColumnName(i), toByteArray(is));
			}else { 
				data.put(meta.getColumnName(i), rs.getObject(colName));
			}
		}
		
		return data;
	}
    
    private byte[] toByteArray(InputStream input) throws IOException	{
		ByteArrayOutputStream os = new ByteArrayOutputStream();						
		byte[] buffer = new byte[1024];
		int bytesRead;
		 while ((bytesRead = input.read(buffer)) != -1) {
			 os.write(buffer, 0, bytesRead);
		 }
		 
		 return os.toByteArray();
	}
    
    private String readerToString(Reader reader) throws IOException {
		StringWriter s_contents = new StringWriter();
		int ch;
		if(reader!=null) {
			while( (ch=reader.read() ) != -1) {
				  s_contents.write(ch);
			}
		}
		return s_contents.toString();
    }  
    
	private void closeStatement() {
		try{
			if(stmt!=null) {
				stmt.close();
			}
			if(rs!=null) {
				rs.close();
			}
		}catch(SQLException e){
			throw new RuntimeException(e.getMessage());
		}
	}
	
	protected String parseSQL(String sql, List params) {
		
		StringBuffer sb = new StringBuffer();
		
		Matcher m = SQL_PATTERN.matcher(sql);		
		while(m.find()) {
			String replace = m.group(0);
			params.add(m.group(1));
			sb.append(sql.substring(0, m.end()).replace(replace, "?"));
			
			sql = sql.substring(m.end());
			m = SQL_PATTERN.matcher(sql);
		}
		sb.append(sql);
		
		return sb.toString();
	} 
}
