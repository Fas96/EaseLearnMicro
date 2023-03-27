package com.argo.r2eln.repo.behavior;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.alfresco.repo.node.NodeServicePolicies;
import org.alfresco.repo.policy.Behaviour;
import org.alfresco.repo.policy.Behaviour.NotificationFrequency;
import org.alfresco.repo.policy.JavaBehaviour;
import org.alfresco.repo.policy.PolicyComponent;
import org.alfresco.service.cmr.repository.ChildAssociationRef;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;
import org.alfresco.service.namespace.NamespaceService;
import org.alfresco.service.namespace.QName;

import com.argo.r2eln.repo.model.R2elnContentModel;

public class ProjectPropertySetting 
	implements NodeServicePolicies.OnCreateNodePolicy, 
				NodeServicePolicies.OnUpdateNodePolicy {

	private NodeService nodeService;
	private PolicyComponent policyComponent;
	
	private Behaviour onCreated;
	private Behaviour onUpdated;
	
	private static SimpleDateFormat SDF_YEAR = new SimpleDateFormat("yyyy");
	
	public void init() {
		
		this.onCreated = new JavaBehaviour(this, "onCreateNode", NotificationFrequency.TRANSACTION_COMMIT);
		
		this.onUpdated = new JavaBehaviour(this, "onUpdateNode", NotificationFrequency.TRANSACTION_COMMIT);
		
		this.policyComponent.bindClassBehaviour(
				QName.createQName(NamespaceService.ALFRESCO_URI, "onCreateNode"), 
				R2elnContentModel.TYPE_PROJECT, 
				this.onCreated);
		
		this.policyComponent.bindClassBehaviour(
				QName.createQName(NamespaceService.ALFRESCO_URI, "onUpdateNode"), 
				R2elnContentModel.TYPE_PROJECT, 
				this.onUpdated);
	}
	
	@Override
	public void onUpdateNode(NodeRef nodeRef) {
		
		Serializable startdate = nodeService.getProperty(nodeRef, R2elnContentModel.PROP_PROJSTARTDATE);
		
		if(startdate!=null) {
			try {
				String year = SDF_YEAR.format((Date)startdate);
				nodeService.setProperty(nodeRef, R2elnContentModel.PROP_PROJYEAR, year);
			} catch (Exception e) {}
		}
	}

	@Override
	public void onCreateNode(ChildAssociationRef childAssocRef) {
		
		NodeRef nodeRef = childAssocRef.getChildRef();
		
		Serializable startdate = nodeService.getProperty(nodeRef, R2elnContentModel.PROP_PROJSTARTDATE);
		
		if(startdate!=null) {
			try {
				String year = SDF_YEAR.format((Date)startdate);
				nodeService.setProperty(nodeRef, R2elnContentModel.PROP_PROJYEAR, year);
			} catch (Exception e) {}
		}
	}

	public void setNodeService(NodeService nodeService) {
		this.nodeService = nodeService;
	}
	
	public void setPolicyComponent(PolicyComponent component) {
		this.policyComponent = component;
	}
	
}
