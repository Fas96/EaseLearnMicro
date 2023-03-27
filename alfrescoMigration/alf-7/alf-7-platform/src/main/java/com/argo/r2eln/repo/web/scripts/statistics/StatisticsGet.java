package com.argo.r2eln.repo.web.scripts.statistics;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.springframework.extensions.webscripts.Cache;
import org.springframework.extensions.webscripts.Status;
import org.springframework.extensions.webscripts.WebScriptRequest;

import com.argo.r2eln.repo.dao.R2ElnCommonDAO;
import com.argo.r2eln.repo.model.PaginationInfo;
import com.argo.r2eln.repo.web.scripts.authority.AbstractAuthorityWebScript;

public class StatisticsGet extends AbstractAuthorityWebScript {

	private R2ElnCommonDAO commonDao;

	public void setCommonDAO(R2ElnCommonDAO dao) {
		this.commonDao = dao;
	}
	private static SimpleDateFormat SDF_DAY = new SimpleDateFormat("yyyy-MM-dd");

	private final String TYPE_TOTAL = "TOTAL";
	private final String TYPE_DEPT = "DEPT"; 
	private final String TYPE_PROJ = "PROJ"; 
	private final String TYPE_EMP = "EMP"; 


	@Override
	protected Map<String, Object> executeImpl(WebScriptRequest req, Status status, Cache cache) {
		
		Map<String, Object> model = new HashMap<String, Object>();
		String type = PaginationInfo.getParameterValue(req, "type", TYPE_TOTAL);

		String startedAfter = PaginationInfo.getParameterValue(req, "startedAfter", "2017-01-01");
		String startedBefore = PaginationInfo.getParameterValue(req, "startedBefore", SDF_DAY.format(new Date()));
		
		model.put("startedAfter", startedAfter);
		model.put("startedBefore", startedBefore);
		model.put("type", type);
		
		totalProcess(req, model, startedAfter, startedBefore);

		return model;
	}
	
	private void totalProcess(WebScriptRequest req, Map<String, Object> model, String startedAfter,
			String startedBefore) {
		
		
		List<Map> list = commonDao.selectPnStatisticsList(model);
		
		List<Map> chartData = commonDao.selectPnStatisticsTotal(model);
		makeChartData(model, chartData);
		model.put("items", list);
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
