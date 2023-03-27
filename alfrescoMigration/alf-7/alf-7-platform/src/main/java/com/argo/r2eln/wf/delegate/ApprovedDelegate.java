package com.argo.r2eln.wf.delegate;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.Serializable;
import java.io.StringWriter;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.activiti.engine.delegate.DelegateExecution;
import org.activiti.engine.impl.cfg.ProcessEngineConfigurationImpl;
import org.activiti.engine.impl.context.Context;
import org.alfresco.error.AlfrescoRuntimeException;
import org.alfresco.model.ContentModel;
import org.alfresco.repo.security.authentication.AuthenticationUtil;
import org.alfresco.repo.workflow.activiti.ActivitiScriptNode;
import org.alfresco.repo.workflow.activiti.BaseJavaDelegate;
import org.alfresco.service.ServiceRegistry;
import org.alfresco.service.cmr.action.ActionService;
import org.alfresco.service.cmr.model.FileFolderService;
import org.alfresco.service.cmr.model.FileNotFoundException;
import org.alfresco.service.cmr.repository.AssociationRef;
import org.alfresco.service.cmr.repository.ChildAssociationRef;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;
import org.alfresco.service.cmr.repository.StoreRef;
import org.alfresco.service.cmr.search.ResultSet;
import org.alfresco.service.cmr.search.SearchService;
import org.alfresco.service.cmr.security.PermissionService;
import org.alfresco.service.cmr.security.PersonService;
import org.alfresco.service.cmr.workflow.WorkflowService;
import org.alfresco.service.namespace.QName;
import org.alfresco.util.ISO9075;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.argo.r2eln.repo.dao.R2ElnProcessDAO;
import com.argo.r2eln.repo.model.R2elnContentModel;

public class ApprovedDelegate extends BaseJavaDelegate {
	Log logger = LogFactory.getLog(ApprovedDelegate.class);
	private static String ACTION_NAME = "action-certify-archive";
	private R2ElnProcessDAO dao;

	public void execute(DelegateExecution execution) throws Exception {
		String instanceid = execution.getProcessInstanceId();
		this.logger.debug("instanceId>>" + instanceid);

		String workflowType = (String) execution.getVariable("elnwf_workflowType");
		String contentId = (String) execution.getVariable("elnwf_contentId");

		ServiceRegistry registry = getServiceRegistry();

		this.dao = new R2ElnProcessDAO();
		try {
			this.dao.initConnection();

			logger.info("workflowType-" + workflowType);
			this.dao.completeWorkflow(workflowType, contentId);
			if ("TIMESTAMP".equals(workflowType)) {
				completeTimestamp(execution, registry);
			} else if ("CREATENOTE".equals(workflowType)) {
				completeCreateNode(execution, contentId, registry, this.dao);
			} else if ("ADDNOTE".equals(workflowType)) {
				completeAddNote(contentId, this.dao);

			} else if ("ADDBDLNOTE".equals(workflowType)) {
				completeAddBdlNote(contentId, this.dao);

			} else if ("TRANSFER".equals(workflowType)) {
				completeTransfer(workflowType, contentId, this.dao);

			} else if (("SUBMIT".equals(workflowType)) || ("KEEP".equals(workflowType)) || ("RENT".equals(workflowType))
					|| ("RETURN".equals(workflowType))) {
				completeApplication(workflowType, contentId, this.dao);

			} else if ("TAKEOUT".equals(workflowType)) {
				completeTakeout(workflowType, contentId, this.dao);

			} else if ("UNSUBMIT".equals(workflowType)) {
				completeUnsubmit(contentId, this.dao);
			}
			this.logger.debug("success");
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			this.dao.closeConnection();
		}

		this.logger.debug("instanceId>>" + instanceid + " is completed");
	}

	private void completeTimestamp(DelegateExecution execution, ServiceRegistry registry) {
		WorkflowService workfowService = registry.getWorkflowService();
		PersonService personService = registry.getPersonService();
		NodeService nodeService = registry.getNodeService();

		List<ChildAssociationRef> packages = getPackageItems(registry, execution);
		if ((packages == null) || (packages.isEmpty())) {
			return;
		}
		String reviewer = getReviewer(execution, personService);

		certifyAndArchive(registry, packages, reviewer);
	}

	private static String workingSpaceContainerPath = "/Company Home/Sites/elnworkingspace/documentLibrary/";

	private void completeCreateNode(DelegateExecution execution, String contentId, final ServiceRegistry registry,
			R2ElnProcessDAO dao) throws SQLException, IOException, FileNotFoundException {
		final Map<String, Object> appInfo = dao.getAppNewNoteInfo(contentId);

		AuthenticationUtil.runAs(new AuthenticationUtil.RunAsWork() {
			public Void doWork() throws Exception {
				ApprovedDelegate.this.createNodeFolder(registry, appInfo);
				return null;
			}
		}, AuthenticationUtil.getSystemUserName());

		Map noteInfo = dao.getNoteByContentId(contentId);
		if (noteInfo == null) {
			dao.createNewNote(contentId);
		} else {
			dao.updateNewNote(contentId);
		}
	}

	private void createNodeFolder(ServiceRegistry registry, Map<String, Object> appInfo) throws FileNotFoundException {
		String projectId = (String) appInfo.get("NT_PROJECT_ID");
		String noteId = (String) appInfo.get("USER_ID");
		String noteTitle = (String) appInfo.get("USER_NM");
		String noteType = (String) appInfo.get("NOTE_TYPE");

		FileFolderService fileFolderService = registry.getFileFolderService();
		NodeService nodeService = registry.getNodeService();
		PersonService personService = registry.getPersonService();
		PermissionService permissionService = registry.getPermissionService();

		SearchService searchService = registry.getSearchService();
		NodeRef projectNodeRef = getProjectNode(searchService, projectId);

		NodeRef userNodeRef = personService.getPerson(noteId);

		Map<QName, Serializable> properties = new HashMap();

		properties.put(ContentModel.PROP_NAME, noteId);
		properties.put(ContentModel.PROP_TITLE, noteTitle);
		properties.put(R2elnContentModel.PROP_NOTEKIND, noteType);

		QName noteQName = QName.createQName("http://www.alfresco.org/model/content/1.0", noteId);
		ChildAssociationRef assocRef = nodeService.createNode(projectNodeRef, ContentModel.ASSOC_CONTAINS, noteQName,
				R2elnContentModel.TYPE_NOTE, properties);

		NodeRef noteNodeRef = assocRef.getChildRef();

		permissionService.setInheritParentPermissions(noteNodeRef, false);
		permissionService.setPermission(noteNodeRef, noteId, "SiteManager", true);

		List<AssociationRef> managerList = nodeService.getTargetAssocs(projectNodeRef, R2elnContentModel.ASSOC_MANAGER);
		if (managerList.size() > 0) {
			NodeRef managerRef = ((AssociationRef) managerList.get(0)).getTargetRef();
			PersonService.PersonInfo managerInfo = personService.getPerson(managerRef);

			permissionService.setPermission(noteNodeRef, managerInfo.getUserName(), "SiteCollaborator", true);
		}
	}

	private NodeRef getProjectNode(SearchService searchService, String projectId) {
		StoreRef storeRef = new StoreRef("workspace", "SpacesStore");

		String xpath = "/app:company_home/st:sites/cm:elnworkingspace/cm:documentLibrary/cm:"
				+ ISO9075.encode(projectId);
		ResultSet rs = searchService.query(storeRef, "xpath", xpath);

		NodeRef projectNodeRef = null;
		try {
			if (rs.length() == 0) {
				throw new AlfrescoRuntimeException("cannot find Company Home");
			}
			projectNodeRef = rs.getNodeRef(0);
		} finally {
			rs.close();
		}
		return projectNodeRef;
	}

	private void completeAddNote(String contentId, R2ElnProcessDAO dao) throws SQLException, IOException {
		int paperNoteSeq = dao.getNextPaperNoteSeq(contentId);
		dao.createNewPapaerNote(paperNoteSeq, contentId);
	}

	private void completeAddBdlNote(String contentId, R2ElnProcessDAO dao) throws SQLException, IOException {
		int paperNoteSeq = dao.getNextBdlPaperNoteSeq(contentId);
		dao.createNewBdlPapaerNote(paperNoteSeq, contentId);
	}
	
	private void completeApplication(String workflowType, String contentId, R2ElnProcessDAO dao)
			throws SQLException, IOException {
		dao.setAppChangeNoteToComplete(workflowType, contentId);
		dao.setAppChangePaperNoteStatus(workflowType, contentId);
	}

	private void completeTakeout(String workflowType, String contentId, R2ElnProcessDAO dao)
			throws SQLException, IOException {
		dao.setAppChangeNoteToComplete(workflowType, contentId);
	}

	private void completeTransfer(String workflowType, String contentId, R2ElnProcessDAO dao)
			throws SQLException, IOException {
		dao.setAppChangeNoteToComplete(workflowType, contentId);
//		dao.setAppChangePaperNoteStatus(workflowType, contentId);

		Map<String, Object> changeNoteInfo = dao.getChangeNoteInfo(contentId, workflowType);
		String to_user_id = (String) changeNoteInfo.get("RECEIVER_ID");
		String to_user_nm = (String) changeNoteInfo.get("USER_NM");

		List<Map<String, Object>> appNoteList = dao.getAppNoteList(contentId);
		for (Map<String, Object> appNote : appNoteList) {
			String nt_project_id = (String) appNote.get("NT_PROJECT_ID");
			String fr_user_id = (String) appNote.get("USER_ID");
			String fr_pnote_seq = appNote.get("PNOTE_SEQ").toString();
			String note_seq = appNote.get("NOTE_SEQ").toString();

			if (fr_user_id.equals(to_user_id))
				continue;

			// 노트가 존재하는지 확인한다. 존재하지 않으면 생성한다.
			dao.confirmNoteInfo(to_user_id, to_user_nm, nt_project_id, fr_user_id);

			String n_pnote_seq = dao.getNextPNote_seq(nt_project_id, to_user_id);
			dao.insertPaperNote(nt_project_id, to_user_id, n_pnote_seq, fr_user_id, fr_pnote_seq);

			// 인계된 노트의 상태를 '인계'로 변경한다.
			dao.setStatusToTransfer(nt_project_id, fr_user_id, fr_pnote_seq);

			dao.updateAppNoteList(to_user_id, n_pnote_seq, nt_project_id, fr_user_id, fr_pnote_seq);
		}

	}

	private void completeUnsubmit(String contentId, R2ElnProcessDAO dao) {
		dao.updateUnSubmitStatus(contentId);
	}

	private List<ChildAssociationRef> getPackageItems(ServiceRegistry registry, DelegateExecution execution) {
		ActivitiScriptNode scriptNode = (ActivitiScriptNode) execution.getVariable("bpm_package");
		NodeRef packageNode = scriptNode.getNodeRef();
		List<ChildAssociationRef> assocList = registry.getNodeService().getChildAssocs(packageNode);

		return assocList;
	}

	private String getReviewer(DelegateExecution execution, PersonService personService) {
		Serializable reviewer = null;

		ActivitiScriptNode scriptNode = (ActivitiScriptNode) execution.getVariable("elnwf_assignee");
		NodeRef assigneeNode = scriptNode.getNodeRef();

		PersonService.PersonInfo person = personService.getPerson(assigneeNode);
		if (person == null) {
			return null;
		}
		return person.getFirstName();
	}

	public static String PARAM_NODEREF = "NODEREF";
	public static String PARAM_FILENAME = "FILENAME";
	public static String PARAM_CREATOR = "CREATOR";
	public static String PARAM_REVIEWER = "REVIEWER";

	private void certifyAndArchive(ServiceRegistry registry, List<ChildAssociationRef> packages, String reviewer) {
		ActionService actionService = registry.getActionService();

		final NodeService nodeService = registry.getNodeService();
		PersonService personService = registry.getPersonService();

		Map<String, Serializable> params = new HashMap();
		if (reviewer != null) {
			params.put(PARAM_REVIEWER, reviewer);
		}
		for (ChildAssociationRef itemRef : packages) {
			final NodeRef nodeRef = itemRef.getChildRef();

			Serializable creator = nodeService.getProperty(nodeRef, ContentModel.PROP_CREATOR);
			String fileName = (String) nodeService.getProperty(nodeRef, ContentModel.PROP_NAME);

			NodeRef creatorNodeRef = personService.getPerson((String) creator);
			PersonService.PersonInfo personInfo = personService.getPerson(creatorNodeRef);
			String firstName = personInfo.getFirstName();

			params.put(PARAM_NODEREF, nodeRef.toString());
			params.put(PARAM_FILENAME, fileName);
			params.put(PARAM_CREATOR, firstName);

			this.dao.insertTimestamp(params);

			try {
				AuthenticationUtil.runAs(new AuthenticationUtil.RunAsWork() {
					public Void doWork() throws Exception {
						nodeService.setProperty(nodeRef, R2elnContentModel.PROP_NOTESTATUS, "approved");
						return null;
					}
				}, AuthenticationUtil.getAdminUserName());
			} catch (Exception e) {
				logger.warn(getErrorMessage(e));
			}

		}
	}

	private String getErrorMessage(Exception e) {
		StringWriter writer = new StringWriter();
		e.printStackTrace(new PrintWriter(writer));

		return writer.toString();
	}

	protected ServiceRegistry getServiceRegistry() {
		ProcessEngineConfigurationImpl config = Context.getProcessEngineConfiguration();
		if (config != null) {
			ServiceRegistry registry = (ServiceRegistry) config.getBeans().get("services");
			if (registry == null) {
				throw new RuntimeException(
						"Service-registry not present in ProcessEngineConfiguration beans, expected ServiceRegistry with keyservices");
			}
			return registry;
		}
		throw new IllegalStateException("No ProcessEngineCOnfiguration found in active context");
	}
}
