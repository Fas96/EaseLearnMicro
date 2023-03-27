package com.argo.r2eln.repo.authority;

import org.alfresco.service.cmr.security.PersonService.PersonInfo;

public class ElnAuthority {

	PersonInfo personInfo;
	
	boolean downloadable = false;
	
	boolean printable = false;
	
	String permission = AuthorityService.PERMISSION_CONSUMER;

	public PersonInfo getPersonInfo() {
		return personInfo;
	}

	public void setPersonInfo(PersonInfo personInfo) {
		this.personInfo = personInfo;
	}

	public boolean isDownloadable() {
		return downloadable;
	}

	public void setDownloadable(boolean downloadable) {
		this.downloadable = downloadable;
	}

	public boolean isPrintable() {
		return printable;
	}

	public void setPrintable(boolean printable) {
		this.printable = printable;
	}

	public String getPermission() {
		return permission;
	}

	public void setPermission(String permission) {
		this.permission = permission;
	}
}
