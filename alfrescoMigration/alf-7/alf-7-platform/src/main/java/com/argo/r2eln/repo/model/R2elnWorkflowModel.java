package com.argo.r2eln.repo.model;

import org.alfresco.service.namespace.QName;

public interface R2elnWorkflowModel {
	
    public static final String R2ELN_WORKFLOW_1_0_URI = "http://r2eln.argonet.co.kr/model/workflow/1.0";
	 
    public static final QName TYPE_SUBMITREVIEWTASK = QName.createQName(R2ELN_WORKFLOW_1_0_URI, "submitReviewTask");
    public static final QName PROP_WORKFLOW_TYPE = QName.createQName(R2ELN_WORKFLOW_1_0_URI, "workflowType");
    public static final QName PORP_CONTENT_ID = QName.createQName(R2ELN_WORKFLOW_1_0_URI, "contentId");
    public static final QName ASSOC_ASSIGNEE = QName.createQName(R2ELN_WORKFLOW_1_0_URI, "assignee");
    public static final QName PORP_PROJSTARTDATE = QName.createQName(R2ELN_WORKFLOW_1_0_URI, "projStartDate");

    public static final QName TYPE_REJECTEDTASK = QName.createQName(R2ELN_WORKFLOW_1_0_URI, "rejectedTask");

}
