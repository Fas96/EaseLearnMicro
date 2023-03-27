package com.argo.r2eln.repo.model;

import org.alfresco.service.namespace.QName;

public interface R2elnContentModel {

    public static final String R2ELN_CONTENT_1_0_URI = "http://r2eln.argonet.co.kr/model/content/1.0";
    
    public static final QName TYPE_PROJECT = QName.createQName(R2ELN_CONTENT_1_0_URI, "project");
    public static final QName PROP_ENGTITLE = QName.createQName(R2ELN_CONTENT_1_0_URI, "engTitle");
    public static final QName PROP_PROJCODE = QName.createQName(R2ELN_CONTENT_1_0_URI, "projCode");
    public static final QName PROP_LEADERID = QName.createQName(R2ELN_CONTENT_1_0_URI, "leaderId");
    public static final QName PROP_PROJSTARTDATE = QName.createQName(R2ELN_CONTENT_1_0_URI, "projStartDate");
    public static final QName PROP_PROJENDDATE = QName.createQName(R2ELN_CONTENT_1_0_URI, "projEndDate");
    public static final QName PROP_PROJYEAR = QName.createQName(R2ELN_CONTENT_1_0_URI, "projYear");
    public static final QName PROP_PROJSTATUS = QName.createQName(R2ELN_CONTENT_1_0_URI, "projStatus");
    
    public static final QName TYPE_NOTE = QName.createQName(R2ELN_CONTENT_1_0_URI, "note");
    public static final QName PROP_NOTEKIND = QName.createQName(R2ELN_CONTENT_1_0_URI, "noteKind");
    public static final QName PROP_NOTENO = QName.createQName(R2ELN_CONTENT_1_0_URI, "noteNo");
    
    public static final QName TYPE_AUTHORITY = QName.createQName(R2ELN_CONTENT_1_0_URI, "authority");
    public static final QName PROP_AUTHPERSON = QName.createQName(R2ELN_CONTENT_1_0_URI, "authPerson");
    public static final QName PROP_DOWNLOADABLE = QName.createQName(R2ELN_CONTENT_1_0_URI, "downloadable");
    public static final QName PROP_PRINTABLE = QName.createQName(R2ELN_CONTENT_1_0_URI, "printable");
    public static final QName PROP_PERMISSION = QName.createQName(R2ELN_CONTENT_1_0_URI, "authPermission");
    public static final QName PROP_SHAREDDATE = QName.createQName(R2ELN_CONTENT_1_0_URI, "shareDate");
    
    public static final QName ASPECT_PEOPLE = QName.createQName(R2ELN_CONTENT_1_0_URI, "people");
    public static final QName ASSOC_MANAGER = QName.createQName(R2ELN_CONTENT_1_0_URI, "manager");
    public static final QName ASSOC_PARTICIPANTS = QName.createQName(R2ELN_CONTENT_1_0_URI, "participants");
    
    public static final QName ASPECT_AUTHORIZABLE = QName.createQName(R2ELN_CONTENT_1_0_URI, "authorizable");
    public static final QName ASSOC_AUTHMEMBERS = QName.createQName(R2ELN_CONTENT_1_0_URI, "authMembers");

    public static final QName ASPECT_CONTENT = QName.createQName(R2ELN_CONTENT_1_0_URI, "content");
    public static final QName PROP_NOTESTATUS = QName.createQName(R2ELN_CONTENT_1_0_URI, "noteStatus");
    public static final QName ASSOC_ARCHIVED = QName.createQName(R2ELN_CONTENT_1_0_URI, "archived");
    
    public static final String CONSTRAINT_NOTE_STATUS_DRAFT = "draft";
    public static final String CONSTRAINT_NOTE_STATUS_PENDING = "pending";
    public static final String CONSTRAINT_NOTE_STATUS_APPROVED = "approved";
    public static final String CONSTRAINT_NOTE_STATUS_ARCHIVEFAIL = "archiveFail";
    public static final String CONSTRAINT_NOTE_STATUS_EXCEPTION = "exception";
    public static final String CONSTRAINT_NOTE_STATUS_ARCHIVED = "archived";

	public static final String WORKFLOW_TYPE_TIMESTAMP = "TIMESTAMP";
	public static final String WORKFLOW_TYPE_CREATENOTE = "CREATENOTE";
	public static final String WORKFLOW_TYPE_ADDNOTE = "ADDNOTE";
	public static final String WORKFLOW_TYPE_SUBMIT = "SUBMIT";
	public static final String WORKFLOW_TYPE_KEEP = "KEEP";
	public static final String WORKFLOW_TYPE_TRANSFER = "TRANSFER";
	public static final String WORKFLOW_TYPE_RENT = "RENT";
	public static final String WORKFLOW_TYPE_RETURN = "RETURN";
	public static final String WORKFLOW_TYPE_UNSUBMIT = "UNSUBMIT";
}
