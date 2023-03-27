package com.argo.r2eln.repo.freeproject;

import com.argo.r2eln.repo.authority.AuthorityService;
import com.argo.r2eln.repo.model.R2elnContentModel;
import org.alfresco.model.ContentModel;
import org.alfresco.repo.policy.BehaviourFilter;
import org.alfresco.service.cmr.repository.AssociationRef;
import org.alfresco.service.cmr.repository.ChildAssociationRef;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;
import org.alfresco.service.cmr.security.PermissionService;
import org.alfresco.service.cmr.security.PersonService;
import org.alfresco.service.cmr.site.SiteService;
import org.alfresco.service.namespace.NamespaceService;
import org.alfresco.service.namespace.QName;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.*;

public class CreateFreeProjectServiceImpl implements CreateFreeProjectService{
    private String SITE_FREESPACE = "elnfreespace";
    private static SimpleDateFormat SDF_YEAR = new SimpleDateFormat("yyyy");

    public static Log log = LogFactory.getLog(CreateFreeProjectServiceImpl.class);

    private NodeService nodeService;
    private SiteService siteService;
    private BehaviourFilter behaviourFilter;
    private PersonService personService;
    private PermissionService permissionService;



    public void setNodeService(NodeService nodeService) {
        this.nodeService = nodeService;
    }
    public void setSiteService(SiteService siteService) {
        this.siteService = siteService;
    }
    public void setBehaviourFilter(BehaviourFilter behaviourFilter) {
        this.behaviourFilter = behaviourFilter;
    }
    public void setPersonService(PersonService personService) {
        this.personService = personService;
    }

    public void setPermissionService(PermissionService permissionService) {
        this.permissionService = permissionService;
    }

    private final ProjectCodeGenerator projectCodeGenerator;
    public CreateFreeProjectServiceImpl() {
        projectCodeGenerator = new ProjectCodeGenerator();
    }

    @Override
    public void create(String projectName, Date startDate, Date endDate, String leaderId, List<String> partResearcherIds) {


        log.info("====== 자유과제 코드 생성 ======");

        String newFreeProjectCode = projectCodeGenerator.createNewFreeProjectCode();

        log.info("자유과제 코드 : " + newFreeProjectCode);

        log.info("====== 자유과제 프로젝트 폴더 생성 ======");

        NodeRef freeSpaceNodeRef = this.siteService.getContainer(SITE_FREESPACE, "documentLibrary");

        Map<QName, Serializable> projectProperties = new HashMap();

        projectProperties.put(ContentModel.PROP_NAME, newFreeProjectCode);
        projectProperties.put(ContentModel.PROP_TITLE, projectName);
        projectProperties.put(ContentModel.PROP_DESCRIPTION, projectName);
        projectProperties.put(R2elnContentModel.PROP_PROJCODE, newFreeProjectCode);
        projectProperties.put(R2elnContentModel.PROP_PROJSTATUS, "open");
        projectProperties.put(R2elnContentModel.PROP_PROJSTARTDATE, startDate);
        projectProperties.put(R2elnContentModel.PROP_PROJENDDATE, endDate);
        projectProperties.put(R2elnContentModel.PROP_PROJYEAR,  SDF_YEAR.format(startDate));
        projectProperties.put(R2elnContentModel.PROP_LEADERID,  SDF_YEAR.format(startDate));

        QName projectQName = QName.createQName("http://www.alfresco.org/model/content/1.0", newFreeProjectCode);

        ChildAssociationRef childAssociationRef = nodeService.createNode(freeSpaceNodeRef, ContentModel.ASSOC_CONTAINS, projectQName,
                R2elnContentModel.TYPE_PROJECT, projectProperties);

        log.info("====== 자유과제 프로젝트 폴더 완료 ======");
        log.info("자유과제 프로젝트 폴더 nodeRef : " + childAssociationRef.getChildRef());

        log.info("====== 자유과제 리더 설정 ======");

        behaviourFilter.disableBehaviour(childAssociationRef.getChildRef(), R2elnContentModel.ASPECT_PEOPLE);

        nodeService.addAspect(childAssociationRef.getChildRef(), R2elnContentModel.ASPECT_PEOPLE, null);

        nodeService.setAssociations(childAssociationRef.getChildRef(), R2elnContentModel.ASSOC_MANAGER
                , Collections.singletonList(personService.getPerson(leaderId)));
        behaviourFilter.enableBehaviour(childAssociationRef.getChildRef(), R2elnContentModel.ASPECT_PEOPLE);

        log.info("====== 자유과제 리더 설정 완료 ======");


        log.info("====== 자유과제 폴더 권한 설정중 ======");
        permissionService.setInheritParentPermissions(childAssociationRef.getChildRef(), false);
        for (String partResearcherId : partResearcherIds) {
            permissionService.setPermission(childAssociationRef.getChildRef(), partResearcherId, AuthorityService.PERMISSION_CONSUMER, true);
        }
        permissionService.setPermission(childAssociationRef.getChildRef(), leaderId, AuthorityService.PERMISSION_CONSUMER, true);
        log.info("====== 자유과제 폴더 권한 설정 완료 ======");


        log.info("====== 노트 폴더 생성중 ======");
        for (String partResearcherId : partResearcherIds) {
            NodeRef userNodeRef = personService.getPerson(partResearcherId);
            PersonService.PersonInfo person = personService.getPerson(userNodeRef);

            Map<QName, Serializable> noteProperties = new HashMap();

            noteProperties.put(ContentModel.PROP_NAME, partResearcherId);
            noteProperties.put(ContentModel.PROP_TITLE, person.getFirstName());
            noteProperties.put(R2elnContentModel.PROP_NOTEKIND, "E");

            QName noteQName = QName.createQName("http://www.alfresco.org/model/content/1.0", partResearcherId);
            ChildAssociationRef assocRef = nodeService.createNode(childAssociationRef.getChildRef(), ContentModel.ASSOC_CONTAINS, noteQName,
                    R2elnContentModel.TYPE_NOTE, noteProperties);

            NodeRef noteNodeRef = assocRef.getChildRef();

            permissionService.setInheritParentPermissions(noteNodeRef, false);
            permissionService.setPermission(noteNodeRef, partResearcherId, "SiteManager", true);

            //manager
            NodeRef leaderNodeRef = personService.getPerson(leaderId);
            PersonService.PersonInfo leaderInfo = personService.getPerson(leaderNodeRef);
            permissionService.setPermission(noteNodeRef, leaderInfo.getUserName(), "SiteCollaborator", true);
        }
        log.info("====== 노트 폴더 생성 완료 ======");


        log.info("====== 과제자료실 폴더 생성중 ======");
        Map<QName, Serializable> sharedFolderProperties = new HashMap();

        sharedFolderProperties.put(ContentModel.PROP_NAME, "과제자료실");
        sharedFolderProperties.put(ContentModel.PROP_TITLE, "과제자료실");
        sharedFolderProperties.put(ContentModel.PROP_DESCRIPTION, "과제자료실");

        ChildAssociationRef sharedFolderNodeInfo = nodeService.createNode(
                childAssociationRef.getChildRef(),
                ContentModel.ASSOC_CONTAINS,
                QName.createQName(NamespaceService.CONTENT_MODEL_1_0_URI, "과제자료실"),
                ContentModel.TYPE_FOLDER, sharedFolderProperties);

        permissionService.setPermission(sharedFolderNodeInfo.getChildRef(), leaderId, AuthorityService.PERMISSION_MANAGER, true);
        for (String partResearcherId : partResearcherIds) {
            permissionService.setPermission(sharedFolderNodeInfo.getChildRef(), partResearcherId, AuthorityService.PERMISSION_CONTRIBUTER, true);
        }

        log.info("====== 과제자료실 폴더 생성 완료 ======");





    }

}
