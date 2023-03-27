package com.argo.r2eln.repo.web.scripts.authority;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.security.PersonService.PersonInfo;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;
import org.springframework.extensions.webscripts.Cache;
import org.springframework.extensions.webscripts.Status;
import org.springframework.extensions.webscripts.WebScriptException;
import org.springframework.extensions.webscripts.WebScriptRequest;

public class AuthorityDelete extends AbstractAuthorityWebScript {

   @Override
    protected Map<String, Object> executeImpl(WebScriptRequest req, Status status, Cache cache)
    {
	   Map<String, Object> model = new HashMap<String, Object>();
	   
	   NodeRef targetNode = parseRequestForNodeRef(req);
	   
	   JSONObject json = null;
	   

       // read request json
       String userName = req.getParameter("userName");
       if(userName==null) {
           model.put("userName", "username missing");
           model.put("firstName", " ");
           model.put("lastName", " ");
           return model;
       }
       
       PersonInfo personInfo = authorityService.removeAuthority(targetNode, userName);
       
       model.put("userName", personInfo.getUserName());
       model.put("firstName", personInfo.getFirstName());
       model.put("lastName", personInfo.getLastName());

       
	   return model;
    }
}
