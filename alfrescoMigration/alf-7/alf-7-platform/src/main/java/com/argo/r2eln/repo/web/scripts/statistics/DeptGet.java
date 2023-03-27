package com.argo.r2eln.repo.web.scripts.statistics;

import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.springframework.extensions.webscripts.Cache;
import org.springframework.extensions.webscripts.Status;
import org.springframework.extensions.webscripts.WebScriptRequest;

import com.argo.r2eln.repo.dao.R2ElnCommonDAO;
import com.argo.r2eln.repo.web.scripts.authority.AbstractAuthorityWebScript;

public class DeptGet extends AbstractAuthorityWebScript {

	private R2ElnCommonDAO commonDao;

	public void setCommonDAO(R2ElnCommonDAO dao) {
		this.commonDao = dao;
	}
	private static SimpleDateFormat SDF_DAY = new SimpleDateFormat("yyyy-MM-dd");
	
	private static final String LIST_TYPE="LIST";
	private static final String VIEW_TYPE="VIEW";

	@Override
	protected Map<String, Object> executeImpl(WebScriptRequest req, Status status, Cache cache) {
		
		Map<String, Object> model = new HashMap<String, Object>();
		String type = req.getParameter("type");
		String depnName = req.getParameter("dept_nm");

		model.put("type", type);
		
		if(StringUtils.equalsIgnoreCase(VIEW_TYPE, type)) {
			model.put("dept_nm", depnName);
			List<Map> empList = commonDao.selectPnStatisticsEmpList(model);
			model.put("emplist", empList);
			
		}
		
		List<Map> deptlist = commonDao.selectPnStatisticsDeptList(model);
		model.put("deptlist", deptlist);
		
		return model;
	}
}
