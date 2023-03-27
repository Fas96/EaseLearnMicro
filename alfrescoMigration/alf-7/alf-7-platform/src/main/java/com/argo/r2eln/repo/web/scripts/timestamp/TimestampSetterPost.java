package com.argo.r2eln.repo.web.scripts.timestamp;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.springframework.extensions.webscripts.Cache;
import org.springframework.extensions.webscripts.Status;
import org.springframework.extensions.webscripts.WebScriptRequest;

import com.argo.r2eln.repo.dao.R2ElnCommonDAO;
import com.argo.r2eln.repo.web.scripts.authority.AbstractAuthorityWebScript;

public class TimestampSetterPost extends AbstractAuthorityWebScript {

	private R2ElnCommonDAO commonDao;

	public void setCommonDAO(R2ElnCommonDAO dao) {
		this.commonDao = dao;
	}

	@Override
	protected Map<String, Object> executeImpl(WebScriptRequest req, Status status, Cache cache) {

		String noderef = req.getParameter("nodeRef");
		String filename = null;
		String creator = null;
		try {
			filename = URLDecoder.decode(req.getParameter("filename"), "UTF-8");
			creator = URLDecoder.decode(req.getParameter("creator"), "UTF-8");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		String lastuptdt = req.getParameter("lastuptdt");
		String user_id = req.getParameter("user_id");
		String nt_project_id = req.getParameter("nt_project_id");
		Map<String, Object> model = new HashMap<String, Object>();
		
		if(StringUtils.isNoneBlank(noderef)) {
			model.put("noderef", noderef);
			model.put("filename", filename);
			model.put("creator", creator);
			model.put("lastuptdt", lastuptdt);
			model.put("user_id", user_id);
			model.put("nt_project_id", nt_project_id);
			model.put("isSucess",this.commonDao.insertPnTimestamp(model));
		} else {
			model.put("isSucess",false);
		}

		return model;
	}
}
