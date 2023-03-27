package com.argo.r2eln.repo.web.scripts.authority;

import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;
import org.alfresco.service.cmr.repository.StoreRef;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.extensions.webscripts.DeclarativeWebScript;
import org.springframework.extensions.webscripts.WebScriptException;
import org.springframework.extensions.webscripts.WebScriptRequest;

import com.argo.r2eln.repo.authority.AuthorityService;

public class AbstractAuthorityWebScript extends DeclarativeWebScript  {

    protected static final String NODE_REF = "nodeRef";
    protected static final String AUTHORITIES = "authorities";
    
    protected NodeService nodeService;
    protected AuthorityService authorityService;

    /**
     * Sets the node service instance
     * 
     * @param nodeService the node service to set
     */
    public void setNodeService(NodeService nodeService)
    {
        this.nodeService = nodeService;
    }

    public void setAuthorityService(AuthorityService authorityService) {
    	this.authorityService = authorityService;
    }
    
    protected NodeRef getPersonNode(JSONObject json) throws JSONException {
		String strPersonNode = (String)json.get("authPerson");
		   NodeRef personNodeRef = new NodeRef(strPersonNode);
		   if (!this.nodeService.exists(personNodeRef))
		   {
		       throw new WebScriptException(HttpServletResponse.SC_NOT_FOUND, "Unable to find node: " + personNodeRef.toString());
		   }
		return personNodeRef;
	}
   

   protected NodeRef parseRequestForNodeRef(WebScriptRequest req)
   {
       // get the parameters that represent the NodeRef, we know they are present
       // otherwise this webscript would not have matched
       Map<String, String> templateVars = req.getServiceMatch().getTemplateVars();
       String storeType = templateVars.get("store_type");
       String storeId = templateVars.get("store_id");
       String nodeId = templateVars.get("id");

       // create the NodeRef and ensure it is valid
       StoreRef storeRef = new StoreRef(storeType, storeId);
       NodeRef nodeRef = new NodeRef(storeRef, nodeId);

       if (!this.nodeService.exists(nodeRef))
       {
           throw new WebScriptException(HttpServletResponse.SC_NOT_FOUND, "Unable to find node: " + nodeRef.toString());
       }

       return nodeRef;
   }
   
}
