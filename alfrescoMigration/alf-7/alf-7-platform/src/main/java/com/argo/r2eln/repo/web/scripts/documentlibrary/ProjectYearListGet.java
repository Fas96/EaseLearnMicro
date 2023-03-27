package com.argo.r2eln.repo.web.scripts.documentlibrary;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.alfresco.repo.search.impl.solr.facet.SolrFacetHelper;
import org.alfresco.service.ServiceRegistry;
import org.alfresco.service.cmr.repository.NodeService;
import org.alfresco.service.cmr.repository.StoreRef;
import org.alfresco.service.cmr.search.ResultSet;
import org.alfresco.service.cmr.search.SearchParameters;
import org.alfresco.service.cmr.search.SearchService;
import org.alfresco.util.Pair;
import org.springframework.extensions.webscripts.Cache;
import org.springframework.extensions.webscripts.DeclarativeWebScript;
import org.springframework.extensions.webscripts.Status;
import org.springframework.extensions.webscripts.WebScriptRequest;

import com.argo.r2eln.repo.model.R2elnContentModel;

public class ProjectYearListGet extends DeclarativeWebScript {

	private SearchService searchService;
	
	private NodeService nodeService;
	
	   /** Service registry */
    protected ServiceRegistry services;
    
	public void setSearchService(SearchService service) {
		this.searchService = service;
	}
	
	public void setNodeService(NodeService service) {
		this.nodeService = service;
	}
	
    /**
     * Set the service registry
     * 
     * @param services  the service registry
     */
    public void setServiceRegistry(ServiceRegistry services)
    {
        this.services = services;
    }
    
    @Override
    protected Map<String, Object> executeImpl(WebScriptRequest req, Status status, Cache cache)
    {
    	Map<String, Object> model = new HashMap<String, Object>();
    	
    	
    	SearchParameters searchParameters = new SearchParameters();
    	searchParameters.addStore(StoreRef.STORE_REF_WORKSPACE_SPACESSTORE);
    	searchParameters.setLanguage(SearchService.LANGUAGE_FTS_ALFRESCO);
    	searchParameters.setQuery("PATH:\"/app:company_home/st:sites/cm:elnworkingspace/cm:documentLibrary//*\"");
    	
    	String yearFacetField = "{"+R2elnContentModel.R2ELN_CONTENT_1_0_URI+"}projYear";
    	try {
			yearFacetField = URLEncoder.encode(yearFacetField, "UTF-8");
	    	searchParameters.addFacetQuery("facet=on&facet.field="+yearFacetField);
	    	
	    	SolrFacetHelper solrFacetHelper = this.services.getSolrFacetHelper();
	    	
	    	String query = searchParameters.getQuery();
	    	
	    	ResultSet resultSet = this.searchService.query(searchParameters);
	    	double numFound = resultSet.getNumberFound();
	    	List<Pair<String,Integer>> facets = resultSet.getFieldFacet(yearFacetField);
	    	
	    	
	    	model.put("result", "this is test result");
	    	
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
 
    	
        return model;
    }
}
