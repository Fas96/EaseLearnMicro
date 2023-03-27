package com.argo.r2eln.repo.authority;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.alfresco.model.ContentModel;
import org.alfresco.repo.policy.BehaviourFilter;
import org.alfresco.repo.security.authentication.AuthenticationUtil;
import org.alfresco.service.cmr.repository.ChildAssociationRef;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;
import org.alfresco.service.cmr.security.PermissionService;
import org.alfresco.service.cmr.security.PersonService;
import org.alfresco.service.cmr.security.PersonService.PersonInfo;
import org.alfresco.service.namespace.QName;
import org.alfresco.service.namespace.RegexQNamePattern;

import com.argo.r2eln.repo.model.R2elnContentModel;

public class AuthorityServiceImpl implements AuthorityService {

    // Injected services
    private NodeService nodeService;
    private BehaviourFilter behaviourFilter;
    private PersonService personService;
    private PermissionService permissionService;
    
    public void setNodeService(NodeService nodeService)
    {
        this.nodeService = nodeService;
    }
    
    public void setPersonService(PersonService personService) {
    	this.personService = personService;
    }
    
    public void setPermissionService(PermissionService permissionService) {
    	this.permissionService = permissionService;
    }
    
    public void setBehaviourFilter(BehaviourFilter behaviourFilter)
    {
        this.behaviourFilter = behaviourFilter;
    }
    
	@Override
	public PersonInfo applyAuthority(final NodeRef targetNode,final String userName, final String permission, final boolean downloadable, final boolean printable)
			throws AuthorityServiceException {
		
		final NodeRef personNode = personService.getPerson(userName);
	    final PersonInfo personInfo = personService.getPerson(personNode);
	    
        AuthenticationUtil.runAs(new AuthenticationUtil.RunAsWork<Void>() 
        {
            public Void doWork() throws Exception
            {
            	applyAuthority(targetNode, personNode, downloadable, printable, personInfo.getUserName(), permission);
                return null;
            }
        }, AuthenticationUtil.getSystemUserName());
        
        return personInfo;
	}

	public void applyAuthority(NodeRef targetNode, NodeRef personNode, boolean downloadable, boolean printable, String userName, String permission)
			throws AuthorityServiceException {
		
        // Ensure that the application of a rating does not cause updates
        // to the modified, modifier properties on the rated node.
        if (nodeService.hasAspect(targetNode, R2elnContentModel.ASPECT_AUTHORIZABLE) == false)
        {
            behaviourFilter.disableBehaviour(targetNode, R2elnContentModel.ASPECT_AUTHORIZABLE);
            try
            {
                // Add the cm:rateable aspect if it's not there already.
                nodeService.addAspect(targetNode, R2elnContentModel.ASPECT_AUTHORIZABLE, null);
                
            }
            finally
            {
                behaviourFilter.enableBehaviour(targetNode, R2elnContentModel.ASPECT_AUTHORIZABLE);
            }
        }
        
        QName assocQName = getAuthorityAssocNameFor(userName);
        
        List<ChildAssociationRef> assocChildren = nodeService.getChildAssocs(targetNode, R2elnContentModel.ASSOC_AUTHMEMBERS, assocQName);
        if(assocChildren.isEmpty()) {
        	
            Map<QName, Serializable> authorizableProps = new HashMap<QName, Serializable>();
            authorizableProps.put(R2elnContentModel.PROP_AUTHPERSON, personNode);
            authorizableProps.put(R2elnContentModel.PROP_DOWNLOADABLE, downloadable);
            authorizableProps.put(R2elnContentModel.PROP_PRINTABLE, printable);
            authorizableProps.put(R2elnContentModel.PROP_PERMISSION, permission);
            authorizableProps.put(R2elnContentModel.PROP_SHAREDDATE, new Date());
            
            behaviourFilter.disableBehaviour(targetNode, R2elnContentModel.ASPECT_AUTHORIZABLE);
            try
            {
                nodeService.createNode(targetNode, 
                		R2elnContentModel.ASSOC_AUTHMEMBERS, 
                		assocQName, 
                		R2elnContentModel.TYPE_AUTHORITY, 
                		authorizableProps);
                
                permissionService.setPermission(targetNode, userName, permission, true);
            }
            finally
            {
                behaviourFilter.enableBehaviour(targetNode, R2elnContentModel.ASPECT_AUTHORIZABLE);
            }
        }
	}
	
	public PersonInfo removeAuthority(NodeRef targetNode, String userName)  throws AuthorityServiceException {
		
		final NodeRef personNode = personService.getPerson(userName);
	    final PersonInfo personInfo = personService.getPerson(personNode);
		
        QName assocQName = getAuthorityAssocNameFor(userName);
        
        List<ChildAssociationRef> assocChildren = nodeService.getChildAssocs(targetNode, R2elnContentModel.ASSOC_AUTHMEMBERS, assocQName);
        if(assocChildren.size()==0) {
        	return personInfo;
        }
        
        ChildAssociationRef child = assocChildren.get(0);

        nodeService.deleteNode(child.getChildRef());
        
        permissionService.clearPermission(targetNode, userName);
        
		return personInfo;
	}

	public List<ElnAuthority> getAuthorities(NodeRef targetNode) throws AuthorityServiceException {
		
		List<ElnAuthority> authorites = new ArrayList<ElnAuthority>();
		
		Collection<ChildAssociationRef> assocChildren = nodeService.getChildAssocs(targetNode, R2elnContentModel.ASSOC_AUTHMEMBERS, RegexQNamePattern.MATCH_ALL);
		Iterator<ChildAssociationRef> iterAssoc = assocChildren.iterator();
		while(iterAssoc.hasNext()) {
			ChildAssociationRef assocRef = iterAssoc.next();
			NodeRef childRef = assocRef.getChildRef();
			
			Serializable strNodeRef = nodeService.getProperty(childRef, R2elnContentModel.PROP_AUTHPERSON);
			PersonInfo personInfo = personService.getPerson((NodeRef)strNodeRef);
			
			Serializable downloadable = nodeService.getProperty(childRef, R2elnContentModel.PROP_DOWNLOADABLE);
			Serializable printable = nodeService.getProperty(childRef, R2elnContentModel.PROP_PRINTABLE);
			Serializable permission = nodeService.getProperty(childRef, R2elnContentModel.PROP_PERMISSION);
			
			ElnAuthority elnAuthority = new ElnAuthority();
			elnAuthority.setPersonInfo(personInfo);
			elnAuthority.setDownloadable((Boolean)downloadable);
			elnAuthority.setPrintable((Boolean)printable);
			elnAuthority.setPermission((String)permission);
			
			authorites.add(elnAuthority);
		}
		
		return authorites;
	}
	
	private QName getAuthorityAssocNameFor(String userName) {
		String strQName = userName+"__authorityScheme";
		return QName.createQName(
        		R2elnContentModel.R2ELN_CONTENT_1_0_URI, strQName);
	}
}
