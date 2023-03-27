package com.argo.r2eln.repo.web.scripts.statistics;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.springframework.extensions.webscripts.Cache;
import org.springframework.extensions.webscripts.Status;
import org.springframework.extensions.webscripts.WebScriptRequest;

import com.argo.r2eln.repo.dao.R2ElnCommonDAO;
import com.argo.r2eln.repo.model.PaginationInfo;
import com.argo.r2eln.repo.web.scripts.authority.AbstractAuthorityWebScript;

public class ProjGet extends AbstractAuthorityWebScript {

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
		String nt_project_id = req.getParameter("nt_project_id");

		model.put("type", type);
		
		if(StringUtils.equalsIgnoreCase(VIEW_TYPE, type)) {
			model.put("nt_project_id", nt_project_id);
			List<Map> projlist = commonDao.selectPnStatisticsProjList(model);
			model.put("projlist", projlist);
			List<Map> empList = commonDao.selectPnStatisticsEmpList(model);
			model.put("emplist", empList);

			List<Map> chartData = commonDao.selectPnStatisticsTotal(model);
			makeChartData(model, chartData);
		} else {
			
			List<Map> projlist = commonDao.selectPnStatisticsProjList(model);
			model.put("projlist", projlist);
			
		}
		
		
		return model;
	}
	
	private void makeChartData(Map<String, Object> model, List<Map> monthlyData) {
		Iterator<Map> it = monthlyData.iterator();
		String date = null;
		int timestamp = 0;
		
		List<Map<String, String>> labelList = new ArrayList<>();
		List<Map<String, Integer>> valueList = new ArrayList<>();
		
		while (it.hasNext()) {
			Map map = (Map) it.next();
			date = map.get("date")+"";
            Map<String, String> label = new HashMap<>();
            label.put("label", date);
			labelList.add(label);
					
			timestamp = timestamp + ((Long)map.get("count")).intValue();
			Map<String, Integer> value = new HashMap<>();
			value.put("value", timestamp);
			valueList.add(value);
		}
		model.put("chartLabel", labelList);
		model.put("chartValue", valueList);
	}
}