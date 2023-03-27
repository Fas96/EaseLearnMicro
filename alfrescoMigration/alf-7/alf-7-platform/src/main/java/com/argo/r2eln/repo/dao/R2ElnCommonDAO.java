package com.argo.r2eln.repo.dao;

import java.util.List;
import java.util.Map;

public interface R2ElnCommonDAO {

	public boolean insertPnTimestamp(Map paramMap);
	
	public List<Map> selectPnTimestampList(Map paramMap);
	
	public int selectPnTimestampTotalCount(Map paramMap);
	
	public boolean updatePnTimestampCompleteYn(Map paramMap);
	
	public List<Map> selectPnTimestampExcelList(Map paramMap);
	
	public List<Map> selectPnStatisticsList(Map paramMap);
	
	public List<Map> selectPnStatisticsTotal(Map paramMap);
	
	public List<Map> selectPnStatisticsDeptList(Map paramMap);

	public List<Map> selectPnStatisticsEmpList(Map paramMap);
	
	public List<Map> selectPnStatisticsProjList(Map paramMap);
   
}
