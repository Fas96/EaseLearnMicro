package com.argo.r2eln.repo.web.scripts.bbs;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import org.alfresco.model.ContentModel;
import org.alfresco.repo.policy.BehaviourFilter;
import org.alfresco.service.ServiceRegistry;
import org.alfresco.service.cmr.model.FileFolderService;
import org.alfresco.service.cmr.repository.ChildAssociationRef;
import org.alfresco.service.cmr.repository.ContentService;
import org.alfresco.service.cmr.repository.ContentWriter;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;
import org.alfresco.service.namespace.QName;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.extensions.surf.util.Content;
import org.springframework.extensions.webscripts.Cache;
import org.springframework.extensions.webscripts.DeclarativeWebScript;
import org.springframework.extensions.webscripts.Status;
import org.springframework.extensions.webscripts.WebScriptException;
import org.springframework.extensions.webscripts.WebScriptRequest;
import org.springframework.extensions.webscripts.servlet.FormData;

import com.argo.r2eln.repo.model.BbsContentModel;

/**
 * 게시판에서 첨부파일을 업로드한다.
 * @author SooMyung Lee (smlee0818@argonet.co.kr)
 *
 */
public class AttachmentUploadPost extends DeclarativeWebScript {

	protected static Log logger = LogFactory.getLog(AttachmentUploadPost.class);
	
	protected static final String ERROR_BAD_FORM = "invalid Form";
	protected static final String ERROR_BAD_FILE = "Invalid file format";
	
	private ServiceRegistry registry;
	private BehaviourFilter behaviourFilter;
	
	private NodeService nodeService;
	private ContentService contentService;
	private FileFolderService fileFolderService;
	
	public void init()
	{
	    this.nodeService = this.registry.getNodeService();
	    this.contentService = this.registry.getContentService();
	    this.fileFolderService = this.registry.getFileFolderService();
	}
	
	/**
     * Set the service registry
     * 
     * @param services  the service registry
     */
    public void setServiceRegistry(ServiceRegistry registry)
    {
        this.registry = registry;
    }
    
    public void setBehaviourFilter(BehaviourFilter behaviourFilter)
    {
        this.behaviourFilter = behaviourFilter;
    }
    
    /**
     * 
     */
    @Override
    protected Map<String, Object> executeImpl(WebScriptRequest req, Status status, Cache cache)
    {
    	
    	FormData form = (FormData)req.parseContent();
        if (form == null || !form.getIsMultiPart())
        {
            throw new WebScriptException(Status.STATUS_BAD_REQUEST, "File invalid: ", ERROR_BAD_FORM);
        }
        
    	Map<String, Object> model = new HashMap<String, Object>();

    	Content content = null;
    	String filename = null;
    	String destination = null;
    	String contenttype = null;
    	
    	for(FormData.FormField field : form.getFields()) {
    		
    		if(field.getIsFile()) {
    			content = field.getContent();
    		} else if("filename".equals(field.getName())) {
    			filename = field.getValue();
    		} else if("destination".equals(field.getName())) {
    			destination = field.getValue();
    		} else if("contenttype".equals(field.getName())) {
    			contenttype = field.getValue();
    		}
    	}
    	
    	if(content==null || destination == null) {
    		 throw new WebScriptException(Status.STATUS_BAD_REQUEST, "File invalid: ", ERROR_BAD_FORM);
    	}
    	
    	NodeRef destNodeRef = new NodeRef(destination);
    	
    	NodeRef childRef = createAttachmentNode(content, destNodeRef, filename);
    	
    	model.put("node", childRef);
    	
    	return model;
    }
    
    private NodeRef createAttachmentNode(Content content, NodeRef destNodeRef, String filename) {
    	
    	QName assocQName = QName.createQName(BbsContentModel.BBS_CONTENT_1_0_URI, filename);
    	
		Map<QName, Serializable> props = new HashMap<QName, Serializable>();
		props.put(ContentModel.PROP_NAME, filename);
		
		ChildAssociationRef childAssoc  = this.nodeService.createNode(destNodeRef, 
				BbsContentModel.ASSOC_ATTACHED, assocQName,
				BbsContentModel.TYPE_ATTACHMENT, props);
    	
		NodeRef childRef = childAssoc.getChildRef();
		
		ContentWriter writer = this.contentService.getWriter(childRef, ContentModel.PROP_CONTENT, true);
		String mimetype = content.getMimetype();
		
		writer.setMimetype(mimetype);
		writer.putContent(content.getInputStream());
		
		return childRef;
    }
    
}
