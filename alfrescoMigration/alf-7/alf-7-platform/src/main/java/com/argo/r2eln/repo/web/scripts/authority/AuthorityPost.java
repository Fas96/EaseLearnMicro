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

import com.argo.r2eln.repo.authority.AuthorityService;

public class AuthorityPost extends AbstractAuthorityWebScript {    
    
   @Override
    protected Map<String, Object> executeImpl(WebScriptRequest req, Status status, Cache cache)
    {
	   Map<String, Object> model = new HashMap<String, Object>();
	   
	   NodeRef targetNode = parseRequestForNodeRef(req);
	   
	   JSONObject json = null;
	   
       try
       {
    	   String content = req.getContent().getContent();
           // read request json
           json = new JSONObject(new JSONTokener(req.getContent().getContent()));
           
           String userName = json.getString("userName");
           String permission = json.getString("permission");
           if(permission==null) permission = AuthorityService.PERMISSION_CONSUMER;
           
           boolean downloadable = json.getBoolean("downloadable");
           boolean printable = json.getBoolean("printable");
           
           PersonInfo personInfo = authorityService.applyAuthority(targetNode, userName, permission, downloadable, printable);
           
           model.put("userName", personInfo.getUserName());
           model.put("firstName", personInfo.getFirstName());
           model.put("lastName", personInfo.getLastName());
           
       }
       catch (IOException iox)
       {
           throw new WebScriptException(Status.STATUS_BAD_REQUEST, "Could not read content from req.", iox);
       }
       catch (JSONException je)
       {
           throw new WebScriptException(Status.STATUS_BAD_REQUEST, "Could not parse JSON from req.", je);
       }
       
	   return model;
    }
}
