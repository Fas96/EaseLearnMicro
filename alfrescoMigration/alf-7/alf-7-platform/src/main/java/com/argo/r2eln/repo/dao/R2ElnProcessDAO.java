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
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class R2ElnProcessDAO {

	Log logger = LogFactory.getLog(R2ElnProcessDAO.class);

	
	private static String KEY_WORKFLOW_TYPE = "workflowType";
	private static String KEY_CONTENT_ID = "contentId";

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
		R2ElnProcessDAO.dataSource = dataSource;
	}

	public void setDriverClassName(String driver) {
		R2ElnProcessDAO.driverClassName = driver;
	}

	public void setUrl(String dbUrl) {
		R2ElnProcessDAO.dbUrl = dbUrl;
	}

	public void setUsername(String name) {
		R2ElnProcessDAO.dbUsername = name;
	}

	public void setPassword(String password) {
		R2ElnProcessDAO.dbPassword = password;
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

		Class.forName(R2ElnProcessDAO.driverClassName);
		this.connection = DriverManager.getConnection(R2ElnProcessDAO.dbUrl, R2ElnProcessDAO.dbUsername,
				R2ElnProcessDAO.dbPassword);
		this.connection.setAutoCommit(false);

		DatabaseMetaData meta = this.connection.getMetaData();
		String dbms = meta.getDatabaseProductName();
		if (StringUtils.equalsIgnoreCase(dbms, "oracle")) {
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

	private static String sqlWorkflowComplete = "UPDATE PN_WORKFLOW SET STATUS=5, ENDDT=SYSDATE WHERE WORKFLOWTYPE=#workflowType# AND APPLY_ID=#contentId#";

	public void completeWorkflow(String workflowType, String contentId) {
		Map<String, String> param = new HashMap();
		param.put(KEY_WORKFLOW_TYPE, workflowType);
		param.put(KEY_CONTENT_ID, contentId);

		if (isMySQL) {
			sqlWorkflowComplete = "UPDATE PN_WORKFLOW SET STATUS=5, ENDDT=NOW() WHERE WORKFLOWTYPE=#workflowType# AND APPLY_ID=#contentId#";
		}
		this.databaseHandler.update(sqlWorkflowComplete, param, false);
	}

	private static String sqlSelectNoteByApplyId = "SELECT USER_ID FROM PN_NOTE WHERE (NT_PROJECT_ID,USER_ID)=( SELECT A.NT_PROJECT_ID, NVL(A.USER_ID,B.USER_ID) USER_ID FROM PN_APP_NEW_NOTE A, PN_WORKFLOW B WHERE A.APPLY_ID=B.APPLY_ID AND B.WORKFLOWTYPE='CREATENOTE' AND A.APPLY_ID=#contentId#)";

	public Map<String, Object> getNoteByContentId(String contentId) throws SQLException, IOException {
		Map<String, Object> param = new HashMap();
		param.put(KEY_CONTENT_ID, contentId);

		List<Map<String, Object>> list = this.databaseHandler.selectAll(sqlSelectNoteByApplyId, param);
		if (list.size() == 0) {
			return null;
		}
		return (Map) list.get(0);
	}

	private static String sqlGetAppNewNoteInfo = "SELECT A.NT_PROJECT_ID, A.USER_ID, A.NOTE_TYPE, B.USER_NM FROM PN_APP_NEW_NOTE A, PN_USER B WHERE A.USER_ID=B.USER_ID AND A.APPLY_ID=#"
			+ KEY_CONTENT_ID + "#";

	public Map getAppNewNoteInfo(String contentId) throws SQLException, IOException {
		Map<String, Object> param = new HashMap();
		param.put(KEY_CONTENT_ID, contentId);

		List<Map<String, Object>> list = this.databaseHandler.selectAll(sqlGetAppNewNoteInfo, param);
		if (list.size() == 0) {
			return null;
		}
		return (Map) list.get(0);
	}

	private static String sqlCreateNewNoteByApplyId = "INSERT INTO PN_NOTE (NT_PROJECT_ID, USER_ID, NOTE_TYPE, NOTE_NM, VALID_YN, IS_CREATED, INSDT)  "
			+ " (SELECT A.NT_PROJECT_ID, A.USER_ID, A.NOTE_TYPE, B.USER_NM, 'Y','Y',SYSDATE   FROM PN_APP_NEW_NOTE A, PN_USER B   WHERE A.USER_ID=B.USER_ID AND A.APPLY_ID=#"
			+ KEY_CONTENT_ID + "#) ";

	public void createNewNote(String contentId) {
		Map<String, String> param = new HashMap();
		param.put(KEY_CONTENT_ID, contentId);

		if (isMySQL) {
			sqlCreateNewNoteByApplyId = "INSERT INTO PN_NOTE (NT_PROJECT_ID, USER_ID, NOTE_TYPE, NOTE_NM, VALID_YN, IS_CREATED, INSDT)  (SELECT A.NT_PROJECT_ID, A.USER_ID, A.NOTE_TYPE, B.USER_NM, 'Y','Y', NOW()  FROM PN_APP_NEW_NOTE A, PN_USER B   WHERE A.USER_ID=B.USER_ID AND A.APPLY_ID=#"
					+ KEY_CONTENT_ID + "#) ";
		}

		this.databaseHandler.update(sqlCreateNewNoteByApplyId, param, false);
	}

	private static String sqlUpdateNewNoteByApplyId = "UPDATE PN_NOTE SET VALID_YN='Y', IS_CREATED='Y' WHERE (NT_PROJECT_ID,USER_ID)=(SELECT NT_PROJECT_ID,USER_ID FROM PN_APP_NEW_NOTE WHERE APPLY_ID=#"
			+ KEY_CONTENT_ID + "#)";

	public void updateNewNote(String contentId) {
		Map<String, String> param = new HashMap();
		param.put(KEY_CONTENT_ID, contentId);

		this.databaseHandler.update(sqlUpdateNewNoteByApplyId, param, false);
	}

	private static String sqlUpdateChangeNoteStatus = "UPDATE PN_APP_CHG_NOTE SET STATUS=5 WHERE CHG_TYPE=#"
			+ KEY_WORKFLOW_TYPE + "# AND APPLY_ID=#" + KEY_CONTENT_ID + "#";

	public void setAppChangeNoteToComplete(String workflowType, String contentId) {
		Map<String, String> param = new HashMap();
		param.put(KEY_CONTENT_ID, contentId);
		param.put(KEY_WORKFLOW_TYPE, workflowType);

		this.databaseHandler.update(sqlUpdateChangeNoteStatus, param, false);
	}

	private static String sqlSelectPaperNoteByContentId = "SELECT NT_PROJECT_ID,USER_ID,PNOTE_SEQ,RTNT_PRD FROM PN_APP_NOTE_LIST WHERE APPLY_ID=#"+ KEY_CONTENT_ID + "#";
	private static String sqlUpdatePaperNoteStatus = "UPDATE PN_PAPER_NOTE SET STATUS_CD=#STATUS# WHERE NT_PROJECT_ID=#NT_PROJECT_ID# AND USER_ID=#USER_ID# AND PNOTE_SEQ=#PNOTE_SEQ#";
	private static String sqlUpdatePaperNoteStatusForSubmit = "UPDATE PN_PAPER_NOTE SET STATUS_CD=#STATUS#, RTNT_PRD=#RTNT_PRD# WHERE NT_PROJECT_ID=#NT_PROJECT_ID# AND USER_ID=#USER_ID# AND PNOTE_SEQ=#PNOTE_SEQ#";

	public void setAppChangePaperNoteStatus(String workflowType, String contentId) throws SQLException, IOException {
		Map<String, Object> param = new HashMap();
		param.put(KEY_CONTENT_ID, contentId);

		List<Map<String, Object>> list = this.databaseHandler.selectAll(sqlSelectPaperNoteByContentId, param);

		String status = "10";
		String query = sqlUpdatePaperNoteStatus;

		if ("SUBMIT".equals(workflowType)) {
			status = "10";
			query = sqlUpdatePaperNoteStatusForSubmit;
		} else if ("KEEP".equals(workflowType)) {
			status = "11";
		} else if ("TRANSFER".equals(workflowType)) {
			status = "10";
		} else if ("RENT".equals(workflowType)) {
			status = "20";
		} else if ("RETURN".equals(workflowType)) {
			status = "10";
		}
		for (Map<String, Object> data : list) {
			data.put("STATUS", status);
			if ("SUBMIT".equals(workflowType) && data.get("RTNT_PRD") == null)
				data.put("RTNT_PRD", 30);

			this.databaseHandler.update(query, data, false);
		}
	}

	private static String sqlGetNextPaperNoteSeq = "SELECT NVL(MAX(PNOTE_SEQ),0)+1 AS PNOTE_SEQ FROM PN_PAPER_NOTE WHERE (NT_PROJECT_ID,USER_ID)=(SELECT NT_PROJECT_ID,USER_ID FROM PN_APP_ADD_NOTE WHERE APPLY_ID=#"
			+ KEY_CONTENT_ID + "#)";


	public int getNextPaperNoteSeq(String contentId) throws SQLException, IOException {

		Map<String, Object> param = new HashMap();
		param.put(KEY_CONTENT_ID, contentId);

		if (isMySQL) {
			sqlGetNextPaperNoteSeq = "SELECT ifnull(MAX(PNOTE_SEQ),0)+1 AS PNOTE_SEQ FROM PN_PAPER_NOTE WHERE (NT_PROJECT_ID,USER_ID)=(SELECT NT_PROJECT_ID,USER_ID FROM PN_APP_ADD_NOTE WHERE APPLY_ID=#"
					+ KEY_CONTENT_ID + "#)";
		}

		List<Map<String, Object>> list = this.databaseHandler.selectAll(sqlGetNextPaperNoteSeq, param);
		if (list.size() == 0) {
			throw new RuntimeException("cannot create new papaer note sequence");
		}
		Object paperSeq = ((Map) list.get(0)).get("PNOTE_SEQ");
		return Integer.parseInt(paperSeq.toString());
	}

	private static String sqlGetNextBdlPaperNoteSeq = "SELECT NVL(MAX(PNOTE_SEQ),0)+1 AS PNOTE_SEQ FROM PN_PAPER_NOTE WHERE (NT_PROJECT_ID,USER_ID)=(SELECT NT_PROJECT_ID,USER_ID FROM PN_PAPER_NOTE_COWORK WHERE APPLY_ID=#"
			+ KEY_CONTENT_ID + "# AND main_rec_yn='Y')";

	public int getNextBdlPaperNoteSeq(String contentId) throws SQLException, IOException {
		String sqlMainWlnWriter = " SELECT DISTINCT USER_ID FROM PN_PAPER_NOTE_COWORK c WHERE c.APPLY_ID=#"+ KEY_CONTENT_ID + "#" ;

		Map<String, Object> param = new HashMap();
		param.put(KEY_CONTENT_ID, contentId);

		if (isMySQL) {
			sqlGetNextPaperNoteSeq = "SELECT ifnull(MAX(PNOTE_SEQ),0)+1 AS PNOTE_SEQ FROM PN_PAPER_NOTE WHERE (NT_PROJECT_ID,USER_ID)=(SELECT NT_PROJECT_ID,USER_ID FROM PN_PAPER_NOTE_COWORK WHERE APPLY_ID=#"
					 + KEY_CONTENT_ID + "# AND main_rec_yn='Y')";
		}

		List<Map<String, Object>> list = this.databaseHandler.selectAll(sqlGetNextBdlPaperNoteSeq, param);
		if (list.size() == 0) {
			throw new RuntimeException("cannot create new papaer note sequence");
		}
		Object paperSeq = ((Map) list.get(0)).get("PNOTE_SEQ");
		return Integer.parseInt(paperSeq.toString());
	}


	private static String sqlCreateNewPaperNote = "INSERT INTO PN_PAPER_NOTE (NT_PROJECT_ID, USER_ID, PNOTE_SEQ,NOTE_NO,NOTE_NM,STATUS_CD,INSDT, APPLY_ID) "
			+ " (SELECT a.NT_PROJECT_ID, USER_ID, #PNOTE_SEQ#, b.PROJECT_CODE||'-'||USER_ID||'-'||LPAD(#PNOTE_SEQ#,2,'0'), NOTE_NM, '01',SYSDATE , #"
			+ KEY_CONTENT_ID + "#"
			+ " FROM PN_APP_ADD_NOTE a left join PN_PROJECT b on a.NT_PROJECT_ID=b.NT_PROJECT_ID WHERE APPLY_ID=#"
			+ KEY_CONTENT_ID + "#)";

	/**
	 * apply_id 컬럼을 추가함 노트 신청시 여러명이 가능하도록
	 * 
	 * @since 2019-12-16
	 * @author momo31
	 * @param PNOTE_SEQ
	 * @param contentId
	 */
	public void createNewPapaerNote(int PNOTE_SEQ, String contentId) {
		Map<String, Object> param = new HashMap();
		param.put(KEY_CONTENT_ID, contentId);
		param.put("PNOTE_SEQ", Integer.valueOf(PNOTE_SEQ));

		if (isMySQL) {
			sqlCreateNewPaperNote = "INSERT INTO PN_PAPER_NOTE (NT_PROJECT_ID, USER_ID, PNOTE_SEQ,NOTE_NO,NOTE_NM,STATUS_CD,INSDT, APPLY_ID) "
					+ " (SELECT a.NT_PROJECT_ID, USER_ID, #PNOTE_SEQ#, CONCAT(b.PROJECT_CODE, '-', USER_ID, '-', LPAD(#pnote_seq#, 2, '0')), NOTE_NM, '01', now() , #"
					+ KEY_CONTENT_ID + "#"
					+ " FROM PN_APP_ADD_NOTE a left join PN_PROJECT b on a.NT_PROJECT_ID=b.NT_PROJECT_ID WHERE APPLY_ID=#"
					+ KEY_CONTENT_ID + "#)";
		}

		this.databaseHandler.update(sqlCreateNewPaperNote, param, false);
	}

 	private static String sqlCreateNewBdlPaperNote = " INSERT INTO PN_PAPER_NOTE (NT_PROJECT_ID, USER_ID, PNOTE_SEQ, NOTE_NO, NOTE_NM, STATUS_CD, INSDT, APPLY_ID) "
			+ " (SELECT a.NT_PROJECT_ID, #USER_ID#, #PNOTE_SEQ#, b.PROJECT_CODE||'-'||TRIM(#USER_ID#)||'-'||LPAD(#PNOTE_SEQ#,2,'0'), NOTE_NM, '01', SYSDATE , #"+ KEY_CONTENT_ID +"#"
			+ " FROM PN_APP_ADD_NOTE a left join PN_PROJECT b on a.NT_PROJECT_ID=b.NT_PROJECT_ID WHERE APPLY_ID=#"	+ KEY_CONTENT_ID + "#)";
	
 	public void createNewBdlPapaerNote(int PNOTE_SEQ, String contentId) throws SQLException, IOException {
		Map<String, Object> param = new HashMap();
		param.put(KEY_CONTENT_ID, contentId);
		param.put("PNOTE_SEQ", Integer.valueOf(PNOTE_SEQ));
		
		if (isMySQL) {
			sqlCreateNewBdlPaperNote = "INSERT INTO PN_PAPER_NOTE (NT_PROJECT_ID, USER_ID, PNOTE_SEQ,NOTE_NO,NOTE_NM,STATUS_CD,INSDT, APPLY_ID) "
					+ " (SELECT a.NT_PROJECT_ID, #USER_ID#, #PNOTE_SEQ#, CONCAT(b.PROJECT_CODE, '-', #USER_ID#, '-', LPAD(#pnote_seq#, 2, '0')), NOTE_NM, '01', now() , #"
					+ KEY_CONTENT_ID + "#"
					+ " FROM PN_APP_ADD_NOTE a left join PN_PROJECT b on a.NT_PROJECT_ID=b.NT_PROJECT_ID WHERE APPLY_ID=#"
					+ KEY_CONTENT_ID + "#)";
		}
		
		String sqlMainWlnWriter = " SELECT DISTINCT USER_ID FROM PN_PAPER_NOTE_COWORK c WHERE c.APPLY_ID=#"+ KEY_CONTENT_ID + "#" ;
		
		
		List<Map<String, Object>> list = this.databaseHandler.selectAll(sqlMainWlnWriter, param);
		
		if (list.size() == 0) {
			throw new RuntimeException("there is no list => "+param.toString()+"\r\n"+sqlMainWlnWriter);
		}
		Map<String, Object> map = list.get(0);
		
		String userId = map.get("USER_ID")+"";
		param.put("USER_ID", userId);

		System.out.println("PNOTE_SEQ=>"+PNOTE_SEQ);
		System.out.println("contentId=>"+contentId);
		System.out.println("USER_ID=>"+userId);
		System.out.println(sqlCreateNewBdlPaperNote);
		System.out.println("run createNewBdlPapaerNote =>");
		this.logger.debug("PNOTE_SEQ=>"+PNOTE_SEQ);
		this.logger.debug("contentId=>"+contentId);
		this.logger.debug("USER_ID=>"+userId);
		this.logger.debug(sqlCreateNewBdlPaperNote);
		this.logger.debug("run createNewBdlPapaerNote =>");
		this.databaseHandler.update(sqlCreateNewBdlPaperNote, param, false);
	}

	private static String sqlUpdateUnSubmitStatus = "UPDATE PN_APP_UNSUBMIT SET STATUS='5' WHERE APPLY_ID=#"
			+ KEY_CONTENT_ID + "#";

	public void updateUnSubmitStatus(String contentId) {
		Map<String, Object> param = new HashMap();
		param.put(KEY_CONTENT_ID, contentId);
		this.databaseHandler.update(sqlUpdateUnSubmitStatus, param, false);
	}

	private static String sqlInsertTimestamp = "INSERT INTO PN_TIMESTAMP (TS_ID,NODEREF,FILENAME,CREATOR,REVIEWER) (SELECT NVL(MAX(TS_ID),0)+1, #NODEREF#,#FILENAME#,#CREATOR#,#REVIEWER# FROM PN_TIMESTAMP)";

	public void insertTimestamp(Map<String, Serializable> param) {
		if (isMySQL) {
			sqlInsertTimestamp = "INSERT INTO PN_TIMESTAMP (TS_ID,NODEREF,FILENAME,CREATOR,REVIEWER) (SELECT ifnull(MAX(TS_ID),0)+1, #NODEREF#,#FILENAME#,#CREATOR#,#REVIEWER# FROM PN_TIMESTAMP)";
		}

		this.databaseHandler.update(sqlInsertTimestamp, param, false);
	}

	private static String sqlSelectMailInfo = "SELECT B.USER_NM AS FROMNAME, B.EMAIL AS FROMEMAIL, C.USER_ID AS TOUSERID, C.USER_NM AS TONAME, C.EMAIL AS TOEMAIL "
			+ " FROM PN_APP_CHG_NOTE A  LEFT JOIN PN_USER B ON A.USER_ID=B.USER_ID LEFT JOIN PN_USER C ON A.REVIEWER_ID=C.USER_ID "
			+ " WHERE A.APPLY_ID=#" + KEY_CONTENT_ID + "#";

	public Map<String, Object> getEmailInfo(String contentId) throws SQLException, IOException {
		Map<String, Object> param = new HashMap();
		param.put(KEY_CONTENT_ID, contentId);

		List<Map<String, Object>> list = this.databaseHandler.selectAll(sqlSelectMailInfo, param);
		if (list.size() == 0) {
			return null;
		}
		return (Map) list.get(0);
	}

	private static String sqlInsertEmail = "INSERT INTO PN_EMAIL(EMAIL_NO, EMAIL_ADDRESS, TITLE, CONTENTS, SENDDATE, USER_ID, USER_NM)"
			+ " VALUES( (SELECT NVL(MAX(EMAIL_NO), 0) + 1 FROM PN_EMAIL), #email_address#, #title#, #contents#, TO_DATE(#sendDate#, 'YYYY-MM-DD HH24:MI:SS'), #user_id#, #user_nm#)";

	public void inserEmail(String toMail, String subject, String contents, String user_id, String user_nm) {
		Map paramMap = new HashMap();

		paramMap.put("email_address", toMail);
		paramMap.put("title", subject);
		paramMap.put("contents", contents);
		paramMap.put("sendDate", dateFormat.format(new Date()));
		paramMap.put("user_id", user_id);
		paramMap.put("user_nm", user_nm);

		if (isMySQL) {
			sqlInsertEmail = "INSERT INTO PN_EMAIL(EMAIL_NO, EMAIL_ADDRESS, TITLE, CONTENTS, SENDDATE, USER_ID, USER_NM)"
					+ " VALUES( (SELECT ifnull(MAX(EMAIL_NO), 0) + 1 FROM PN_EMAIL), #email_address#, #title#, #contents#, "
					+ " STR_TO_DATE(#sendDate#, '%Y-%m-%d'), #user_id#, #user_nm#)";
		}

		this.databaseHandler.update(sqlInsertEmail, paramMap, false);
	}

	private static String sqlSelectChgNote = "SELECT a.CHG_TYPE, a.USER_ID, b.USER_NM, a.REVIEWER_ID, a.RECEIVER_ID "
			+ " FROM PN_APP_CHG_NOTE a left join PN_USER b on a.RECEIVER_ID=b.USER_ID WHERE CHG_TYPE=#workflowType# AND APPLY_ID=#contentId#";

	public Map<String, Object> getChangeNoteInfo(String contentId, String workflowType)
			throws SQLException, IOException {
		Map<String, Object> param = new HashMap();
		param.put(KEY_CONTENT_ID, contentId);
		param.put(KEY_WORKFLOW_TYPE, workflowType);

		List<Map<String, Object>> list = this.databaseHandler.selectAll(sqlSelectChgNote, param);
		if (list.size() == 0) {
			return null;
		}
		return (Map) list.get(0);
	}

	// 인수자의 노트가 존재하는지 확인하는 쿼리
	private static String sqlSelectNoteByUser = "SELECT USER_ID, NOTE_NM FROM PN_NOTE WHERE NT_PROJECT_ID=#NT_PROJECT_ID# AND USER_ID=#USER_ID#";

	// 인수자의 노트가 없다면 생성한다.
	private static String sqlInsertNote = "INSERT INTO PN_NOTE (NT_PROJECT_ID, USER_ID, NOTE_NM, RTNT_PRD, DISCARD_STEP, VALID_YN, IS_CREATED, NOTE_TYPE, NOTE_NO)  "
			+ "(SELECT #NT_PROJECT_ID#, #USER_ID#, #USER_NM#, null, null, 'Y', 'Y', 'E', PROJECT_CODE||'-'||#USER_ID#||'-00' FROM PN_PROJECT WHERE NT_PROJECT_ID=#NT_PROJECT_ID#)";

	public void confirmNoteInfo(String user_id, String user_nm, String nt_project_id, String fr_user_id)
			throws SQLException, IOException {
		Map<String, Object> param = new HashMap();
		param.put("USER_ID", user_id);
		param.put("USER_NM", user_nm);
		param.put("NT_PROJECT_ID", nt_project_id);
		param.put("FR_USER_ID", fr_user_id);

		if (isMySQL) {
			sqlInsertNote = " INSERT INTO PN_NOTE (NT_PROJECT_ID, USER_ID, NOTE_NM, RTNT_PRD, DISCARD_STEP, VALID_YN, IS_CREATED, NOTE_TYPE, NOTE_NO)  "
					+ " (SELECT #NT_PROJECT_ID#, #USER_ID#, #USER_NM#, null, null, 'Y', 'Y', 'E', CONCAT(PROJECT_CODE, '-', #USER_ID#, '-00') FROM PN_PROJECT WHERE NT_PROJECT_ID=#NT_PROJECT_ID#) ";
		}

		List<Map<String, Object>> list = this.databaseHandler.selectAll(sqlSelectNoteByUser, param);
		if (list.size() == 0) {
			this.databaseHandler.update(sqlInsertNote, param, false);
		}
	}

	// 인계하는 노트의 상태를
	private static String sqlUpdateNoteStatus = "UPDATE PN_PAPER_NOTE SET STATUS_CD=50 WHERE NT_PROJECT_ID=#NT_PROJECT_ID# AND USER_ID=#USER_ID# AND PNOTE_SEQ=#PNOTE_SEQ#";

	public void setStatusToTransfer(String nt_project_id, String user_id, String pnote_seq)
			throws SQLException, IOException {
		Map<String, Object> param = new HashMap();
		param.put("USER_ID", user_id);
		param.put("NT_PROJECT_ID", nt_project_id);
		param.put("PNOTE_SEQ", pnote_seq);

		this.databaseHandler.update(sqlUpdateNoteStatus, param, false);
	}

	private static String seqlSelectNextPNote_seq = "SELECT NVL(MAX(PNOTE_SEQ),0)+1 PNOTE_SEQ FROM PN_PAPER_NOTE WHERE NT_PROJECT_ID=#NT_PROJECT_ID# AND USER_ID=#USER_ID#";

	public String getNextPNote_seq(String nt_project_id, String user_id) throws SQLException, IOException {
		Map<String, Object> param = new HashMap();
		param.put("NT_PROJECT_ID", nt_project_id);
		param.put("USER_ID", user_id);

		if (isMySQL) {
			seqlSelectNextPNote_seq = "SELECT ifnull(MAX(PNOTE_SEQ),0)+1 PNOTE_SEQ FROM PN_PAPER_NOTE WHERE NT_PROJECT_ID=#NT_PROJECT_ID# AND USER_ID=#USER_ID#";
		}

		List<Map<String, Object>> list = this.databaseHandler.selectAll(seqlSelectNextPNote_seq, param);

		return list.get(0).get("PNOTE_SEQ").toString();
	}

	private static String sqlSelectAppNoteList = "SELECT NOTE_SEQ, NT_PROJECT_ID, USER_ID, PNOTE_SEQ FROM PN_APP_NOTE_LIST WHERE APPLY_ID=#contentId#";

	public List<Map<String, Object>> getAppNoteList(String contentId) throws SQLException, IOException {
		Map<String, Object> param = new HashMap();
		param.put(KEY_CONTENT_ID, contentId);

		List<Map<String, Object>> list = this.databaseHandler.selectAll(sqlSelectAppNoteList, param);
		return list;
	}

	// 인수자의 노트가 없다면 생성한다.
	private static String sqlInsertPNote = "INSERT INTO PN_PAPER_NOTE (NT_PROJECT_ID, USER_ID, PNOTE_SEQ, COVER_PATH, NOTE_NO, STATUS_CD, LOCATION,NOTE_NM,RTNT_PRD,INSDT,UPTDT) "
			+ "(SELECT NT_PROJECT_ID, #USER_ID#, #N_PNOTE_SEQ#, COVER_PATH, NOTE_NO, STATUS_CD, LOCATION,NOTE_NM,RTNT_PRD,INSDT,SYSDATE FROM PN_PAPER_NOTE WHERE NT_PROJECT_ID=#NT_PROJECT_ID# AND USER_ID=#FR_USER_ID# AND PNOTE_SEQ=#FR_PNOTE_SEQ#)";

	public void insertPaperNote(String nt_project_id, String user_id, String n_pnote_seq, String fr_user_id,
			String fr_pnote_seq) throws SQLException, IOException {
		Map<String, Object> param = new HashMap();
		param.put("USER_ID", user_id);
		param.put("NT_PROJECT_ID", nt_project_id);
		param.put("N_PNOTE_SEQ", n_pnote_seq);
		param.put("FR_USER_ID", fr_user_id);
		param.put("FR_PNOTE_SEQ", fr_pnote_seq);

		this.databaseHandler.update(sqlInsertPNote, param, false);
		this.connection.commit();
	}

	private static String sqlUpdateAppNoteList = "UPDATE PN_APP_NOTE_LIST SET USER_ID=#USER_ID#, PNOTE_SEQ=#PNOTE_SEQ# "
			+ "WHERE NT_PROJECT_ID=#NT_PROJECT_ID# AND USER_ID=#FR_USER_ID# AND PNOTE_SEQ=#FR_PNOTE_SEQ#";

	public void updateAppNoteList(String user_id, String n_pnote_seq, String nt_project_id, String fr_user_id,
			String fr_pnote_seq) {
		Map<String, Object> param = new HashMap();
		param.put("USER_ID", user_id);
		param.put("PNOTE_SEQ", n_pnote_seq);
		param.put("NT_PROJECT_ID", nt_project_id);
		param.put("FR_USER_ID", fr_user_id);
		param.put("FR_PNOTE_SEQ", fr_pnote_seq);

		this.databaseHandler.update(sqlUpdateAppNoteList, param, false);
	}

}
