package com.argo.r2eln.repo.web.scripts.authority;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.alfresco.service.cmr.repository.NodeRef;
import org.springframework.extensions.webscripts.Cache;
import org.springframework.extensions.webscripts.Status;
import org.springframework.extensions.webscripts.WebScriptRequest;

import com.argo.r2eln.repo.authority.ElnAuthority;

public class AuthoritiesGet extends AbstractAuthorityWebScript {

   @Override
    protected Map<String, Object> executeImpl(WebScriptRequest req, Status status, Cache cache)
    {
	   Map<String, Object> model = new HashMap<String, Object>();

	   NodeRef nodeRef = parseRequestForNodeRef(req);
       
       // These are the data for the current user's ratings of this node, if any.
       List<ElnAuthority> authorities = authorityService.getAuthorities(nodeRef);

       model.put(NODE_REF, nodeRef.toString());
       model.put(AUTHORITIES, authorities);
     
       return model;
    }
}
