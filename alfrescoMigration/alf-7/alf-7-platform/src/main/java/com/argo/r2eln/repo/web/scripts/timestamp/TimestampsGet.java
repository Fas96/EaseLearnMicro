package com.argo.r2eln.repo.web.scripts.timestamp;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.extensions.webscripts.Cache;
import org.springframework.extensions.webscripts.Status;
import org.springframework.extensions.webscripts.WebScriptRequest;

import com.argo.r2eln.repo.dao.R2ElnCommonDAO;
import com.argo.r2eln.repo.model.PaginationInfo;
import com.argo.r2eln.repo.web.scripts.authority.AbstractAuthorityWebScript;

public class TimestampsGet extends AbstractAuthorityWebScript {

	private R2ElnCommonDAO commonDao;

	public void setCommonDAO(R2ElnCommonDAO dao) {
		this.commonDao = dao;
	}
	private static SimpleDateFormat SDF_DAY = new SimpleDateFormat("yyyy-MM-dd");


	@Override
	protected Map<String, Object> executeImpl(WebScriptRequest req, Status status, Cache cache) {
		Map<String, Object> paramMap = PaginationInfo.getParamMap(req);
		String currentPageNo = PaginationInfo.getParameterValue(req, "currentPageNo", "1");
		paramMap.put("currentPageNo", currentPageNo);
		

		Calendar cal = Calendar.getInstance();
		cal.add(Calendar.MONTH, -6);
		
		String startedAfter = PaginationInfo.getParameterValue(req, "startedAfter", SDF_DAY.format(cal.getTime()));
		String startedBefore = PaginationInfo.getParameterValue(req, "startedBefore", SDF_DAY.format(new Date()));


		String type = req.getParameter("type");
		String searchType = req.getParameter("searchType");
		String searchWord = req.getParameter("searchWord");

		Map<String, Object> model = new HashMap<String, Object>();
		model.put("type",type);
		model.put("searchType", searchType);
		model.put("searchWord", searchWord);
		model.put("startedAfter", startedAfter);
		model.put("startedBefore", startedBefore);
		
		System.out.println("executeImpl:"+paramMap.hashCode()+paramMap);
		int totalCount = this.commonDao.selectPnTimestampTotalCount(paramMap);
		PaginationInfo paginationInfo = PaginationInfo.getPaginationInfoAndSetParamMap(req, totalCount, paramMap);
		List<Map> items = this.commonDao.selectPnTimestampList(paramMap);

		model.put("items", items);
		model.put("paginationinfo", paginationInfo);
		model.put("paginghtmltag", paginationInfo.getBoardPaging("/share/page/console/admin-console/timestamp-list", paramMap));
		

		return model;
	}
}
