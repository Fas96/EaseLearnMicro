package com.argo.r2eln.repo.model;

import org.alfresco.service.namespace.QName;

public interface BbsContentModel {

    public static final String BBS_CONTENT_1_0_URI = "http://bbs.argonet.co.kr/model/content/1.0";
    
    public static final QName TYPE_CONTENT = QName.createQName(BBS_CONTENT_1_0_URI, "content");
    
    public static final QName TYPE_ATTACHMENT = QName.createQName(BBS_CONTENT_1_0_URI, "attachment");
    
    public static final QName ASSOC_ATTACHED = QName.createQName(BBS_CONTENT_1_0_URI, "attached");
    
}
