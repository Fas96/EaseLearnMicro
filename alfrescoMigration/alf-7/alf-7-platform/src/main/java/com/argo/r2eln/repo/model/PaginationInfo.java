package com.argo.r2eln.repo.model;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.extensions.webscripts.WebScriptRequest;

public class PaginationInfo {
	
	private static Logger log = LoggerFactory.getLogger(PaginationInfo.class);
	/**
	 * Required Fields
	 * - 이 필드들은 페이징 계산을 위해 반드시 입력되어야 하는 필드 값들이다.  
	 * 
	 * currentPageNo : 현재 페이지 번호
	 * recordCountPerPage : 한 페이지당 게시되는 게시물 건 수
	 * pageSize : 페이지 리스트에 게시되는 페이지 건수,
	 * totalRecordCount : 전체 게시물 건 수. 
	 */
	
	private int currentPageNo;
	private int recordCountPerPage = 10;
	private int pageSize = 10;
	private int totalRecordCount;
	
	public int getRecordCountPerPage() {
		return recordCountPerPage;
	}
	
	public void setRecordCountPerPage(int recordCountPerPage) {
		this.recordCountPerPage = recordCountPerPage;
	}
	
	public int getPageSize() {
		return pageSize;
	}
	
	public void setPageSize(int pageSize) {
		this.pageSize = pageSize;
	}
	
	public int getCurrentPageNo() {
		return currentPageNo;
	}
	
	public void setCurrentPageNo(int currentPageNo) {
		this.currentPageNo = currentPageNo;
	}
	
	public void setTotalRecordCount(int totalRecordCount) {
		this.totalRecordCount = totalRecordCount;
	}
	
	public int getTotalRecordCount() {
		return totalRecordCount;
	}
	
	/**
	 * Not Required Fields
	 * - 이 필드들은 Required Fields 값을 바탕으로 계산해서 정해지는 필드 값이다.
	 * 
	 * totalPageCount: 페이지 개수
	 * firstPageNoOnPageList : 페이지 리스트의 첫 페이지 번호
	 * lastPageNoOnPageList : 페이지 리스트의 마지막 페이지 번호
	 * firstRecordIndex : 페이징 SQL의 조건절에 사용되는 시작 rownum. 
	 * lastRecordIndex : 페이징 SQL의 조건절에 사용되는 마지막 rownum.
	 */
	
	private int totalPageCount;
	private int firstPageNoOnPageList;
	private int lastPageNoOnPageList;
	private int firstRecordIndex;
	private int lastRecordIndex;	
	
	public int getTotalPageCount() {
		totalPageCount = ((getTotalRecordCount()-1)/getRecordCountPerPage()) + 1;
		return totalPageCount;
	}
	
	public int getFirstPageNo(){
		return 1;
	}
	
	public int getLastPageNo(){
		return getTotalPageCount();		
	}
	
	public int getFirstPageNoOnPageList() {
		firstPageNoOnPageList = ((getCurrentPageNo()-1)/getPageSize())*getPageSize() + 1;
		return firstPageNoOnPageList;
	}
	
	public int getLastPageNoOnPageList() {		
		lastPageNoOnPageList = getFirstPageNoOnPageList() + getPageSize() - 1;		
		if(lastPageNoOnPageList > getTotalPageCount()){
			lastPageNoOnPageList = getTotalPageCount();
		}
		return lastPageNoOnPageList;
	}

	public int getFirstRecordIndex() {
		firstRecordIndex = (getCurrentPageNo() - 1) * getRecordCountPerPage();
		return firstRecordIndex;
	}

	public int getLastRecordIndex() {
		lastRecordIndex = getCurrentPageNo() * getRecordCountPerPage();
		return lastRecordIndex;
	}

	public String getPagingHtmlTag(String type) {
		return getPagingHtmlTag(type, null);
	}
	
	public String getPagingHtmlTag(String type, String path) {
		String htmlTag = "";
		
		if(type.intern() == "ajax" || type.intern() == "patent") {
			htmlTag = getAjaxPaging(type, path);
		} else if(type.intern() == "workflow") {
			htmlTag = getWorkflowPaging(path);
		}
		
		return htmlTag;
	}
	
	/**
	 * 
	 * @param request
	 * @param name
	 * @param defultValue
	 * @return
	 */
	public static String getParameterValue(WebScriptRequest request, String name, String defultValue) {
		if(request.getParameter(name)==null)
			return defultValue;
		
		return request.getParameter(name);
	}
	
	
	public static String[] getParameterValues(WebScriptRequest request, String name) {
		if(request.getParameter(name)==null)
			return null;
		
		return request.getParameterValues(name);
	}
	
	public static Map getNoTypeParamMap(WebScriptRequest request) {
		Map paramMap = new HashMap();
		String searchWord = getParameterValue(request, "searchWord", "");

		paramMap.put("searchWord", searchWord);
		
		return paramMap;
	}
	
	public static Map getParamMap(WebScriptRequest request) {
		Map paramMap = new HashMap();
		String[] em = request.getParameterNames();
		
		for (int x = 0; x < em.length; x++) {
			String key = em[x];
			String value = getParameterValue(request, key, "");
			paramMap.put(key, value);
		}
		
		return paramMap;
	}
	
	public static PaginationInfo getPaginationInfoAndSetParamMap(WebScriptRequest request, int totalCount, Map paramMap) {
		PaginationInfo paginationInfo = new PaginationInfo();
		String currentPageNo = getParameterValue(request, "currentPageNo", "1");
		int intCurrentPageNo = 0;
		
		try {
			intCurrentPageNo = Integer.parseInt(currentPageNo);
		} catch(NumberFormatException e) {
			intCurrentPageNo = 1;
		}
		
		paginationInfo.setCurrentPageNo(intCurrentPageNo);
		paginationInfo.setTotalRecordCount(totalCount);
		
		paramMap.put("firstRecordIndex", paginationInfo.getFirstRecordIndex());
		paramMap.put("lastRecordIndex", paginationInfo.getLastRecordIndex());
		
		return paginationInfo;
	}
	
	private String getAjaxPaging(String type, String path) {
		StringBuffer sBuffer = new StringBuffer();
		int prevNo = currentPageNo > 1 ? currentPageNo - 1 : currentPageNo;
		int nextNo = currentPageNo < getTotalPageCount() ? currentPageNo + 1 : currentPageNo;
		int tenPrevNo = currentPageNo - 10 > 1 ? currentPageNo - 10 : 1;
		int tenNextNo = currentPageNo + 10 < getTotalPageCount() ? currentPageNo + 10 : getTotalPageCount();
		
		sBuffer.append("<div class=\"paging_nav\">");
		sBuffer.append("<a href=\"#\" class=\"page_select first_page\" onclick=\"" + getOnclickEvent(type, path, tenPrevNo) + " return false;\">처음으로 이동</a>");
		sBuffer.append("<a href=\"#\" class=\"page_select prev_page\" onclick=\"" + getOnclickEvent(type, path, prevNo) + " return false;\">이전페이지 이동</a>");
		sBuffer.append("<span>");
		
		for(int i = getFirstPageNoOnPageList(); i < getLastPageNoOnPageList() + 1; i++) {
			if(i == currentPageNo) {
				sBuffer.append("<strong>" + i + "</strong>");
			} else {
				sBuffer.append("<a href=\"#\" onclick=\"" + getOnclickEvent(type, path, i) + " return false;\">" + i + "</a>");
			}
		}
		
		sBuffer.append("</span>");
		sBuffer.append("<a href=\"#\" class=\"page_select next_page\" onclick=\"" + getOnclickEvent(type, path, nextNo) + " return false;\">다음페이지 이동</a>");
		sBuffer.append("<a href=\"#\" class=\"page_select last_page\" onclick=\"" + getOnclickEvent(type, path, tenNextNo) + " return false;\">마지막 페이지로 이동</a>");
		sBuffer.append("</div>");
		
		return sBuffer.toString();
	}
	
	private String getOnclickEvent(String type, String path, int num) {
		String onclickEvent = "AC_ajaxList(";
		
		if(type == "ajax") {
			onclickEvent += "'" + path + "', "; 
		}
		
		onclickEvent += num;
		
		if(type == "ajax") {
			onclickEvent += ", null"; 
		}
		onclickEvent += ");";
		
		return onclickEvent;
	}
	
	private String getWorkflowPaging(String path) {
		StringBuffer sBuffer = new StringBuffer();
		totalPageCount = getTotalPageCount() - 1;
		int prevNo = currentPageNo > 0 ? currentPageNo - 1 : currentPageNo;
		int nextNo = currentPageNo < totalPageCount ? currentPageNo + 1 : currentPageNo;
		int tenPrevNo = currentPageNo - 10 > 0 ? currentPageNo - 10 : 0;
		int tenNextNo = currentPageNo + 10 < totalPageCount ? currentPageNo + 10 : totalPageCount;
		prevNo = prevNo * getRecordCountPerPage();
		nextNo = nextNo * getRecordCountPerPage();
		tenPrevNo = tenPrevNo * getRecordCountPerPage();
		tenNextNo = tenNextNo * getRecordCountPerPage();
		
		setCurrentPageNo(currentPageNo + 1);
		
		sBuffer.append("<div class=\"paging_nav\">");
		sBuffer.append("<a href=\"" + path + "&skipCount=" + tenPrevNo + "&maxItems=" + recordCountPerPage + "\" class=\"page_select first_page\">처음으로 이동</a>");
		sBuffer.append("<a href=\"" + path + "&skipCount=" + prevNo + "&maxItems=" + recordCountPerPage + "\" class=\"page_select prev_page\">이전페이지 이동</a>");
		sBuffer.append("<span>");
		
		for(int i = getFirstPageNoOnPageList(); i < getLastPageNoOnPageList() + 1; i++) {
			if(i == currentPageNo) {
				sBuffer.append("<strong>" + i + "</strong>");
			} else {
				sBuffer.append("<a href=\"" + path + "&skipCount=" + ((i - 1) * getRecordCountPerPage()) + "&maxItems=" + recordCountPerPage + "\">" + i + "</a>");
			}
		}
		
		sBuffer.append("</span>");
		sBuffer.append("<a href=\"" + path + "&skipCount=" + nextNo + "&maxItems=" + recordCountPerPage + "\" class=\"page_select next_page\">다음페이지 이동</a>");
		sBuffer.append("<a href=\"" + path + "&skipCount=" + tenNextNo + "&maxItems=" + recordCountPerPage + "\" class=\"page_select last_page\">마지막 페이지로 이동</a>");
		sBuffer.append("</div>");
		
		return sBuffer.toString();
	}
	
	public String getBoardPaging(String path, Map map) {
		String search = "";

		Set<String> keySet = map.keySet();
		Iterator<String> itr = keySet.iterator();
		
		while(itr.hasNext()) {
			String key = (String) itr.next();
			Object valueObj =  map.get(key);
			String value = valueObj instanceof String ? (String) valueObj : "";
			search += getEmptyCheck(search, value, key);
		}
		
		return getBoardPagingHtml(path, search);
	}
	
	public String getBoardPaging(String path, String searchWord) {
		String search = getEmptyCheck("", searchWord, "searchWord");
		
		return getBoardPagingHtml(path, search);
	}
	
	public String getBoardPaging(String path, String searchType, String searchWord) {
		String search = "";
		
		search = getEmptyCheck(search, searchType, "searchType");
		search = getEmptyCheck(search, searchWord, "searchWord");
		
		return getBoardPagingHtml(path, search);
	}
	
	public String getMgrProjectPaging(String path, String userId, String searchUser, String searchType, String searchWord, String periodType, String startedAfter, String startedBefore) {
		String search = "";
		
		search = getEmptyCheck(search, userId, "userId");
		search = getEmptyCheck(search, searchUser, "searchUser");
		search = getEmptyCheck(search, searchType, "searchType");
		search = getEmptyCheck(search, searchWord, "searchWord");
		search = getEmptyCheck(search, periodType, "periodType");
		search = getEmptyCheck(search, startedAfter, "startedAfter");
		search = getEmptyCheck(search, startedBefore, "startedBefore");
		
		return getBoardPagingHtml(path, search);
	}
	
	private String getBoardPagingHtml(String path, String search) {
		StringBuffer sBuffer = new StringBuffer();
		int prevNo = currentPageNo > 1 ? currentPageNo - 1 : currentPageNo;
		int nextNo = currentPageNo < getTotalPageCount() ? currentPageNo + 1 : currentPageNo;
		int tenPrevNo = currentPageNo - 10 > 1 ? currentPageNo - 10 : 1;
		int tenNextNo = currentPageNo + 10 < getTotalPageCount() ? currentPageNo + 10 : getTotalPageCount();
		
		sBuffer.append("<div class=\"paging_nav\">");
		sBuffer.append("<a href=\"" + path + "?currentPageNo=" + tenPrevNo + search + "\" class=\"page_select first_page\">처음으로 이동</a>");
		sBuffer.append("<a href=\"" + path + "?currentPageNo=" + prevNo + search + "\" class=\"page_select prev_page\">이전페이지 이동</a>");
		sBuffer.append("<span>");
		
		for(int i = getFirstPageNoOnPageList(); i < getLastPageNoOnPageList() + 1; i++) {
			if(i == currentPageNo) {
				sBuffer.append("<strong>" + i + "</strong>");
			} else {
				sBuffer.append("<a href=\"" + path + "?currentPageNo=" + i + search + "\">" + i + "</a>");
			}
		}
		
		sBuffer.append("</span>");
		sBuffer.append("<a href=\"" + path + "?currentPageNo=" + nextNo + search + "\" class=\"page_select next_page\">다음페이지 이동</a>");
		sBuffer.append("<a href=\"" + path + "?currentPageNo=" + tenNextNo + search + "\" class=\"page_select last_page\">마지막 페이지로 이동</a>");
		sBuffer.append("</div>");
		
		return sBuffer.toString();
	}
	
	private String getSearchPaging(String term) {
		StringBuffer sBuffer = new StringBuffer();
		totalPageCount = getTotalPageCount() - 1;
		int prevNo = currentPageNo > 0 ? currentPageNo - 1 : currentPageNo;
		int nextNo = currentPageNo < totalPageCount ? currentPageNo + 1 : currentPageNo;
		int tenPrevNo = currentPageNo - 10 > 0 ? currentPageNo - 10 : 0;
		int tenNextNo = currentPageNo + 10 < totalPageCount ? currentPageNo + 10 : totalPageCount;
		prevNo = prevNo * getRecordCountPerPage();
		nextNo = nextNo * getRecordCountPerPage();
		tenPrevNo = tenPrevNo * getRecordCountPerPage();
		tenNextNo = tenNextNo * getRecordCountPerPage();
		
		sBuffer.append("<div class=\"paging_nav\">");
		sBuffer.append("<a href=\"#\" class=\"page_select first_page\" onclick=\"AC_search('" + term + "', " + getPlusOne(tenPrevNo) + ");\">처음으로 이동</a>");
		sBuffer.append("<a href=\"#\" class=\"page_select prev_page\" onclick=\"AC_search('" + term + "', " + getPlusOne(prevNo) + ");\">이전페이지 이동</a>");
		sBuffer.append("<span>");
		
		for(int i = getFirstPageNoOnPageList(); i < getLastPageNoOnPageList() + 1; i++) {
			if(i == currentPageNo + 1) {
				sBuffer.append("<strong>" + i + "</strong>");
			} else {
				sBuffer.append("<a href=\"#\" onclick=\"AC_search('" + term + "', " + getPlusOne(((i - 1) * getRecordCountPerPage())) + ");\">" + i + "</a>");
			}
		}
		
		sBuffer.append("</span>");
		sBuffer.append("<a href=\"#\" class=\"page_select next_page\" onclick=\"AC_search('" + term + "', " + getPlusOne(nextNo) + ");\">다음페이지 이동</a>");
		sBuffer.append("<a href=\"#\" class=\"page_select last_page\" onclick=\"AC_search('" + term + "', " + getPlusOne(tenNextNo) + ");\">마지막 페이지로 이동</a>");
		sBuffer.append("</div>");
		
		return sBuffer.toString();
	}
	
	private int getPlusOne(int num) {
		if(num > 0 && num < totalRecordCount) {
			num++;
		}
		
		return num;
	}

	private String getEmptyCheck(String str, String value, String name) {
		if(!value.isEmpty()) {
			str += "&" + name + "=" + value;
		}
		
		return str;
	}
	
}