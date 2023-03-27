package com.argo.r2eln.repo.authority;

import org.alfresco.error.AlfrescoRuntimeException;

public class AuthorityServiceException extends AlfrescoRuntimeException {

	private static final long serialVersionUID = 3585916109294995769L;

	public AuthorityServiceException(String msg) {
		super(msg);
	}

	public AuthorityServiceException(String msg, Throwable source) {
		super(msg, source);
	}
	
}
