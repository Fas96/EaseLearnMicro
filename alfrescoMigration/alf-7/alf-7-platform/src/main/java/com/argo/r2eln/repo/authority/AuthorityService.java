package com.argo.r2eln.repo.authority;

import java.util.List;

import org.alfresco.service.NotAuditable;
import org.alfresco.service.cmr.rating.RatingServiceException;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.security.PersonService.PersonInfo;

public interface AuthorityService {

	public static String PERMISSION_CONSUMER = "SiteConsumer";
	public static String PERMISSION_MANAGER = "SiteManager";
	public static String PERMISSION_COLLABORATOR = "Collaborator";
	public static String PERMISSION_CONTRIBUTER = "Contributor";
	
    @NotAuditable
    PersonInfo applyAuthority(NodeRef targetNode, String userName, String permission, boolean downloadable, boolean printable) throws AuthorityServiceException;
    
    PersonInfo removeAuthority(NodeRef targetNode, String userName)  throws AuthorityServiceException;
    
    List<ElnAuthority> getAuthorities(NodeRef targetNode) throws AuthorityServiceException;
}
