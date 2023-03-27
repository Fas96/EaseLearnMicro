package com.argo.r2eln.repo.web.scripts.certify;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.alfresco.repo.security.authentication.AuthenticationUtil;
import org.alfresco.service.ServiceRegistry;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;
import org.alfresco.service.cmr.repository.StoreRef;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;
import org.springframework.extensions.webscripts.Cache;
import org.springframework.extensions.webscripts.DeclarativeWebScript;
import org.springframework.extensions.webscripts.Status;
import org.springframework.extensions.webscripts.WebScriptException;
import org.springframework.extensions.webscripts.WebScriptRequest;

import com.argo.r2eln.repo.model.R2elnContentModel;

public class StatusSettingPost extends DeclarativeWebScript {

	  private ServiceRegistry services;
	  protected NodeService nodeService;
	  
	  public void init()
	  {
		  this.nodeService = this.services.getNodeService(); 
	  }
	  
	  protected Map<String, Object> executeImpl(WebScriptRequest req, Status status, Cache cache)
	  {
		  Map<String, Object> model = new HashMap();
		  
		  final NodeRef nodeRef = parseRequestForNodeRef(req);

		try {
			String content = req.getContent().getContent();
			
	        // read request json
	   	    JSONObject json = new JSONObject(new JSONTokener(content));
	       
	        final String noteStatus = json.getString("noteStatus");
		  
		    setFailToNoteStatus(nodeRef, noteStatus);
		  
		    model.put("node", nodeRef);
		    
		} catch (IOException e) {
			throw new WebScriptException(Status.STATUS_BAD_REQUEST, "Could not read content from req.", e);
		} catch (JSONException e) {
			throw new WebScriptException(Status.STATUS_BAD_REQUEST, "Could not read content from req.", e);
		}

		  return model;
	  }
	  
	  private void setFailToNoteStatus(final NodeRef actionedUponNodeRef, final String status)
	  {
	    AuthenticationUtil.runAs(new AuthenticationUtil.RunAsWork()
	    {
	      public Void doWork()
	        throws Exception
	      {
	    	  StatusSettingPost.this.services.getNodeService().setProperty(actionedUponNodeRef,
	        		R2elnContentModel.PROP_NOTESTATUS, status);
	        return null;
	      }
	    }, AuthenticationUtil.getAdminUserName());
	  }
	  
	  private NodeRef parseRequestForNodeRef(WebScriptRequest req)
	  {
	    Map<String, String> templateVars = req.getServiceMatch().getTemplateVars();
	    String storeType = (String)templateVars.get("store_type");
	    String storeId = (String)templateVars.get("store_id");
	    String nodeId = (String)templateVars.get("id");
	    
	    StoreRef storeRef = new StoreRef(storeType, storeId);
	    NodeRef nodeRef = new NodeRef(storeRef, nodeId);
	    if (!this.nodeService.exists(nodeRef)) {
	      throw new WebScriptException(404, "Unable to find node: " + nodeRef.toString());
	    }
	    return nodeRef;
	  }
	  
	  public void setServiceRegistry(ServiceRegistry serviceRegistry)
	  {
	    this.services = serviceRegistry;
	  }
	  
}
