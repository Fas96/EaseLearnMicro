package com.argo.r2eln.repo.dao;

import java.util.List;
import java.util.Map;

import org.mybatis.spring.SqlSessionTemplate;

public class R2ElnCommonDAOImpl implements R2ElnCommonDAO {

	private static final String INSERT_PnTimestamp = "r2eln.timestamp.insertPnTimestamp";
	private static final String SELECT_PnTimestampList = "r2eln.timestamp.selectPnTimestampList";
	private static final String SELECT_PnTimestampTotalCount = "r2eln.timestamp.selectPnTimestampTotalCount";
	private static final String UPDATE_PnTimestampCompleteYn = "r2eln.timestamp.updatePnTimestampCompleteYn";
	private static final String SELECT_PnTimestampExcelList = "r2eln.timestamp.selectPnTimestampExcelList";

	private static final String SELECT_PnStatisticsList =	"r2eln.statistics.selectPnStatisticsList";
	private static final String SELECT_PnStatisticsTotal =	"r2eln.statistics.monthlyStat";
	private static final String SELECT_PnStatisticsDeptList =	"r2eln.statistics.deptList";
	private static final String SELECT_PnStatisticsEmpList =	"r2eln.statistics.empList";
	private static final String SELECT_PnStatisticsProjList =	"r2eln.statistics.projList";

	protected SqlSessionTemplate template;

	public void setSqlSessionTemplate(SqlSessionTemplate sqlSessionTemplate) {
		this.template = sqlSessionTemplate;
	}

	public static class PostgreSQL extends R2ElnCommonDAOImpl {

		public void setSqlSessionTemplate(SqlSessionTemplate sqlSessionTemplate) {
			this.template = sqlSessionTemplate;
		}
	}
	
	public boolean insertPnTimestamp(Map paramMap) {
		return this.template.insert(INSERT_PnTimestamp, paramMap) > 0; 
	}
	public List<Map> selectPnTimestampList(Map paramMap) {
		return this.template.selectList(SELECT_PnTimestampList, paramMap);
	}
	public int selectPnTimestampTotalCount(Map paramMap) {
		return (int) this.template.selectOne(SELECT_PnTimestampTotalCount, paramMap);
	}
	public boolean updatePnTimestampCompleteYn(Map paramMap) {
		return this.template.update(UPDATE_PnTimestampCompleteYn, paramMap) > 0;
	}
	public List<Map> selectPnTimestampExcelList(Map paramMap) {
		return this.template.selectList(SELECT_PnTimestampExcelList, paramMap);
	}
	public List<Map> selectPnStatisticsList(Map paramMap) {
		return this.template.selectList(SELECT_PnStatisticsList, paramMap);
	}
	public List<Map> selectPnStatisticsTotal(Map paramMap) {
		return this.template.selectList(SELECT_PnStatisticsTotal, paramMap);
	}

	public List<Map> selectPnStatisticsDeptList(Map paramMap) {
		return this.template.selectList(SELECT_PnStatisticsDeptList, paramMap);
	}

	public List<Map> selectPnStatisticsEmpList(Map paramMap) {
		return this.template.selectList(SELECT_PnStatisticsEmpList, paramMap);
	}
	
	public List<Map> selectPnStatisticsProjList(Map paramMap) {
		return this.template.selectList(SELECT_PnStatisticsProjList, paramMap);
	}
}
