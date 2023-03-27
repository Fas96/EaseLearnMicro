package com.argo.r2eln.repo.dao;

import java.util.Map;

import org.mybatis.spring.SqlSessionTemplate;

public class KribbCommonDAOImpl implements KribbCommonDAO {

	protected SqlSessionTemplate template;

	public void setSqlSessionTemplate(SqlSessionTemplate sqlSessionTemplate) {
		this.template = sqlSessionTemplate;
	}
	
	@Override
	public Map<String, Object> selectPerson(String username) {
		return this.template.selectOne("kribb.common.selectPerson", username);
	}

	public static class Oracle extends KribbCommonDAOImpl {

		public void setSqlSessionTemplate(SqlSessionTemplate sqlSessionTemplate) {
			this.template = sqlSessionTemplate;
		}
	}
	
}
