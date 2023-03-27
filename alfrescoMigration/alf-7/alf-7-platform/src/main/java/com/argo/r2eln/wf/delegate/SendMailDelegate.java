package com.argo.r2eln.wf.delegate;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.activiti.engine.delegate.DelegateExecution;
import org.alfresco.repo.workflow.activiti.BaseJavaDelegate;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.argo.r2eln.repo.dao.R2ElnProcessDAO;

public class SendMailDelegate extends BaseJavaDelegate {

	private static Log logger = LogFactory.getLog(SendMailDelegate.class);
	
	//private static final String ELN = "전자연구노트(https://ssogw16.kaist.ac.kr)";
	private static final String ELN = "전자연구노트(https://ern.dgist.ac.kr)";
	private static SimpleDateFormat  dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	  
	@Override
	public void execute(DelegateExecution execution) throws Exception {
		logger.info("SendMailDelegate.executing");
		
	    R2ElnProcessDAO dao = new R2ElnProcessDAO();
	    
		try {

		    String contentId = (String)execution.getVariable("elnwf_contentId");
		    String title = (String)execution.getVariable("bpm_description");
		    
		    dao.initConnection();
		    
		    Map<String, Object> emailInfo = dao.getEmailInfo(contentId);
		    
		    String fromName = (String)emailInfo.get("FROMNAME");
		    String fromEmail = (String)emailInfo.get("FROMEMAIL");
		    String toName = (String)emailInfo.get("TONAME");
		    String toEmail = (String)emailInfo.get("TOEMAIL");
		    String toUserId = (String)emailInfo.get("TOUSERID");
		    
			String today = dateFormat.format(new Date());
			String contents = getApplicationContents(title, fromName, today);
			
			SendMailService service = new SendMailService();
			service.sendMail(toEmail, title, contents, toUserId, toName, fromEmail, fromName);
			
			dao.inserEmail(toEmail, title, contents, toUserId, toName);
			
		} catch(Exception e) {
			logger.error(SendMailService.getErrorMessage(e));
		} finally {
			try {
				if(dao!=null) dao.closeConnection();
			}catch(Exception e) {
				logger.error(SendMailService.getErrorMessage(e));
			}
		}
		
	}

	private String getApplicationContents(String title, String fromUser_nm, String today) {
		String contents = ELN + " : 전자연구노트에서 아래와 같이 결재 신청되었습니다.\n\n";
		contents = getPlusApplicationContents(contents, title, fromUser_nm, today);
		
		return contents;
	}
	
	
	private String getPlusApplicationContents(String contents, String title, String fromUser_nm, String today) {
		contents += "제목 : " + title + "\n\n";
		contents += "신청자 : " + fromUser_nm + "\n";
		contents += "날짜 : " + today;
		
		return contents;
	}

}
