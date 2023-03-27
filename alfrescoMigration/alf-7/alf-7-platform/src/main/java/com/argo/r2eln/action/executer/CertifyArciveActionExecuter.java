package com.argo.r2eln.action.executer;

import com.argo.r2eln.repo.model.R2elnContentModel;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.io.Serializable;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.alfresco.model.ContentModel;
import org.alfresco.repo.action.ParameterDefinitionImpl;
import org.alfresco.repo.action.executer.ActionExecuterAbstractBase;
import org.alfresco.repo.policy.BehaviourFilter;
import org.alfresco.repo.security.authentication.AuthenticationUtil;
import org.alfresco.repo.security.authentication.AuthenticationUtil.RunAsWork;
import org.alfresco.service.ServiceRegistry;
import org.alfresco.service.cmr.action.Action;
import org.alfresco.service.cmr.action.ParameterDefinition;
import org.alfresco.service.cmr.dictionary.DataTypeDefinition;
import org.alfresco.service.cmr.rendition.RenditionService;
import org.alfresco.service.cmr.repository.AssociationRef;
import org.alfresco.service.cmr.repository.ChildAssociationRef;
import org.alfresco.service.cmr.repository.ContentService;
import org.alfresco.service.cmr.repository.ContentWriter;
import org.alfresco.service.cmr.repository.MimetypeService;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;
import org.alfresco.service.cmr.repository.Path;
import org.alfresco.service.cmr.security.AccessPermission;
import org.alfresco.service.cmr.security.AuthorityType;
import org.alfresco.service.cmr.security.OwnableService;
import org.alfresco.service.cmr.security.PermissionService;
import org.alfresco.service.cmr.security.PersonService;
import org.alfresco.service.cmr.site.SiteService;
import org.alfresco.service.namespace.NamespaceService;
import org.alfresco.service.namespace.QName;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

@Deprecated
public class CertifyArciveActionExecuter
  extends ActionExecuterAbstractBase
{
  public static Log log = LogFactory.getLog(CertifyArciveActionExecuter.class);
  public static final String PARAM_NODEREF = "nodeRef";
  public static final String PARAM_REVIEWER = "reviewer";
  public static final String CONTAINER_NAME = "documentLibrary";
  private static String MIMETYPE_PDF = "application/pdf";
  private static SimpleDateFormat SDF_TEXT = new SimpleDateFormat("yyyy-MM-dd hh:mm:dd");
  private static String EXTENTION_PDF = ".pdf";
  private static QName pefRenditionName = QName.createQName(NamespaceService.CONTENT_MODEL_1_0_URI, "pdf");
  private ServiceRegistry services;
  protected NodeService nodeService;
  private RenditionService renditionService;
  private ContentService contentService;
  private PersonService personService;
  private PermissionService permissionService;
  private SiteService siteService;
  private BehaviourFilter behaviourFilter;
  private String SITE_ARCHIVESPACE = "elnarchivespace";
  private String SITE_WORKINGSPACE = "elnworkingspace";
  protected String pdfServerIpAddress = "143.248.5.97";
  protected int pdfServerPort = 729;
  protected String hostTimestampUrl = "http://143.248.5.117:8089/ts";
  protected String workTimestampDir = "R:\\certi";
  protected String certiDir = "/data/eln/pdf/certi";
  private Map<QName, QName> assocMap = new HashMap();
  private List<QName> r2elnAspects = new ArrayList();
  private Set<QName> r2elnAssocs = new HashSet();
  
  public void setHostTimestampUrl(String url)
  {
    if ((url == null) || (url.length() == 0)) {
      return;
    }
    this.hostTimestampUrl = url;
  }
  
  public void setWorkTimestampDir(String dir)
  {
    if ((dir == null) || (dir.length() == 0)) {
      return;
    }
    this.workTimestampDir = dir;
  }
  
  public void setCertiDir(String dir)
  {
    if ((dir == null) || (dir.length() == 0)) {
      return;
    }
    this.certiDir = dir;
  }
  
  public void setHostPdfAddress(String address)
  {
    if ((address == null) || (address.length() == 0)) {
      return;
    }
    this.pdfServerIpAddress = address;
  }
  
  public void setHostPdfPort(String port)
  {
    if ((port == null) || (port.length() == 0)) {
      return;
    }
    this.pdfServerPort = Integer.parseInt(port);
  }
  
  public void init()
  {
    this.renditionService = this.services.getRenditionService();
    this.contentService = this.services.getContentService();
    this.nodeService = this.services.getNodeService();
    this.permissionService = this.services.getPermissionService();
    this.siteService = this.services.getSiteService();
    this.personService = this.services.getPersonService();
    
    this.assocMap.put(R2elnContentModel.ASSOC_MANAGER, R2elnContentModel.ASPECT_PEOPLE);
    this.assocMap.put(R2elnContentModel.ASSOC_PARTICIPANTS, R2elnContentModel.ASPECT_PEOPLE);
    this.assocMap.put(R2elnContentModel.ASSOC_AUTHMEMBERS, R2elnContentModel.ASPECT_AUTHORIZABLE);
    this.assocMap.put(R2elnContentModel.ASSOC_ARCHIVED, R2elnContentModel.ASPECT_CONTENT);
    
    this.r2elnAssocs.add(R2elnContentModel.ASSOC_MANAGER);
    this.r2elnAssocs.add(R2elnContentModel.ASSOC_PARTICIPANTS);
    this.r2elnAssocs.add(R2elnContentModel.ASSOC_AUTHMEMBERS);
    this.r2elnAssocs.add(R2elnContentModel.ASSOC_ARCHIVED);
    
    this.r2elnAspects.add(R2elnContentModel.ASPECT_PEOPLE);
    this.r2elnAspects.add(R2elnContentModel.ASPECT_AUTHORIZABLE);
    this.r2elnAspects.add(R2elnContentModel.ASPECT_CONTENT);
  }
  
  protected void executeImpl(Action action, NodeRef actionedUponNodeRef)
  {
    NodeRef archiveContainerNode = this.siteService.getContainer(this.SITE_ARCHIVESPACE, "documentLibrary");
    NodeRef workingContainerNode = this.siteService.getContainer(this.SITE_WORKINGSPACE, "documentLibrary");
    try
    {
      String displayPath = this.nodeService.getPath(actionedUponNodeRef).toDisplayPath(this.nodeService, this.permissionService);
      
      String filename = (String)this.nodeService.getProperty(actionedUponNodeRef, ContentModel.PROP_NAME);
      displayPath = displayPath + "/" + filename;
      
      List<String> pathFragmentsWorking = Arrays.asList(StringUtils.split(displayPath, "/"));
      if (pathFragmentsWorking.size() < 5) {
        throw new RuntimeException(displayPath + "is not the path in a site documentLibrary.");
      }
      List<String> pathFragmentsArchive = new ArrayList(pathFragmentsWorking);
      
      pathFragmentsArchive.set(2, this.SITE_ARCHIVESPACE);
      
      NodeRef rootNode = this.nodeService.getRootNode(actionedUponNodeRef.getStoreRef());
      displayPath = this.nodeService.getPath(rootNode).toDisplayPath(this.nodeService, this.permissionService);
      
      NodeRef parentNodeArchive = archiveContainerNode;
      NodeRef parentNodeWorking = workingContainerNode;
      
      NodeRef archiveNodeRef = createAndCopyNode(pathFragmentsWorking, parentNodeWorking, parentNodeArchive);
      
      this.services.getNodeService().setProperty(actionedUponNodeRef, R2elnContentModel.PROP_NOTESTATUS, "archived");
      System.out.println("step-6");
      
      action.setParameterValue("archiveNodeRef", archiveNodeRef);
    }
    catch (Exception e)
    {
      e.printStackTrace();
      setFailToNoteStatus(actionedUponNodeRef);
    }
  }
  
  private void setFailToNoteStatus(final NodeRef actionedUponNodeRef)
  {
    AuthenticationUtil.runAs(new AuthenticationUtil.RunAsWork()
    {
      public Void doWork()
        throws Exception
      {
        CertifyArciveActionExecuter.this.services.getNodeService().setProperty(actionedUponNodeRef, R2elnContentModel.PROP_NOTESTATUS, "archiveFail");
        return null;
      }
    }, AuthenticationUtil.getAdminUserName());
  }
  
  private NodeRef createAndCopyNode(List<String> pathFragmentsWorking, NodeRef parentNodeWorking, NodeRef parentNodeArchive)
  {
    for (int i = 4; i < pathFragmentsWorking.size(); i++)
    {
      String name = (String)pathFragmentsWorking.get(i);
      if (i == pathFragmentsWorking.size() - 1)
      {
        int extPos = name.lastIndexOf(".");
        if (extPos != -1) {
          name = name.substring(0, extPos) + ".pdf";
        }
      }
      NodeRef nodeArchive = this.nodeService.getChildByName(parentNodeArchive, ContentModel.ASSOC_CONTAINS, (String)pathFragmentsWorking.get(i));
      NodeRef nodeWorking = this.nodeService.getChildByName(parentNodeWorking, ContentModel.ASSOC_CONTAINS, name);
      if (nodeArchive != null)
      {
        parentNodeArchive = nodeArchive;
        parentNodeWorking = nodeWorking;
      }
      else
      {
        ChildAssociationRef childAssoc = createChildNode(parentNodeArchive, nodeWorking, name);
        nodeArchive = childAssoc.getChildRef();
        this.services.getOwnableService().setOwner(nodeArchive, AuthenticationUtil.getAdminUserName());
        
        copyAspects(nodeWorking, nodeArchive);
        
        copyAssociations(nodeWorking, nodeArchive);
        
        copyPermissions(nodeWorking, nodeArchive);
        if (i == pathFragmentsWorking.size() - 1)
        {
          ContentWriter writer = this.contentService.getWriter(nodeArchive, ContentModel.PROP_CONTENT, true);
          String mimetype = this.services.getMimetypeService().getMimetype("pdf");
          
          writer.setMimetype(mimetype);
          writer.putContent("");
          
          this.nodeService.setProperty(nodeArchive, R2elnContentModel.PROP_NOTESTATUS, "archived");
          
          List<NodeRef> targetRefs = new ArrayList();
          targetRefs.add(nodeArchive);
          this.nodeService.setAssociations(nodeWorking, R2elnContentModel.ASSOC_ARCHIVED, targetRefs);
        }
        parentNodeArchive = nodeArchive;
        parentNodeWorking = nodeWorking;
      }
    }
    return parentNodeArchive;
  }
  
  private ChildAssociationRef createChildNode(NodeRef parentNodeArchive, NodeRef nodeWorking, String name)
  {
    QName qTypeName = this.nodeService.getType(nodeWorking);
    
    Map<QName, Serializable> archiveProperties = new HashMap();
    
    archiveProperties.put(ContentModel.PROP_NAME, name);
    
    Serializable property = this.nodeService.getProperty(nodeWorking, ContentModel.PROP_TITLE);
    if (property != null) {
      archiveProperties.put(ContentModel.PROP_TITLE, property);
    }
    property = this.nodeService.getProperty(nodeWorking, R2elnContentModel.PROP_ENGTITLE);
    if (property != null) {
      archiveProperties.put(R2elnContentModel.PROP_ENGTITLE, property);
    }
    property = this.nodeService.getProperty(nodeWorking, R2elnContentModel.PROP_PROJCODE);
    if (property != null) {
      archiveProperties.put(R2elnContentModel.PROP_PROJCODE, property);
    }
    property = this.nodeService.getProperty(nodeWorking, R2elnContentModel.PROP_LEADERID);
    if (property != null) {
      archiveProperties.put(R2elnContentModel.PROP_LEADERID, property);
    }
    property = this.nodeService.getProperty(nodeWorking, R2elnContentModel.PROP_PROJSTARTDATE);
    if (property != null) {
      archiveProperties.put(R2elnContentModel.PROP_PROJSTARTDATE, property);
    }
    property = this.nodeService.getProperty(nodeWorking, R2elnContentModel.PROP_PROJENDDATE);
    if (property != null) {
      archiveProperties.put(R2elnContentModel.PROP_PROJENDDATE, property);
    }
    property = this.nodeService.getProperty(nodeWorking, R2elnContentModel.PROP_PROJYEAR);
    if (property != null) {
      archiveProperties.put(R2elnContentModel.PROP_PROJYEAR, property);
    }
    property = this.nodeService.getProperty(nodeWorking, R2elnContentModel.PROP_PROJSTATUS);
    if (property != null) {
      archiveProperties.put(R2elnContentModel.PROP_PROJSTATUS, property);
    }
    property = this.nodeService.getProperty(nodeWorking, R2elnContentModel.PROP_NOTEKIND);
    if (property != null) {
      archiveProperties.put(R2elnContentModel.PROP_NOTEKIND, property);
    }
    property = this.nodeService.getProperty(nodeWorking, R2elnContentModel.PROP_NOTENO);
    if (property != null) {
      archiveProperties.put(R2elnContentModel.PROP_NOTENO, property);
    }
    ChildAssociationRef childAssoc = this.nodeService.createNode(parentNodeArchive, ContentModel.ASSOC_CONTAINS, 
      QName.createQName("http://www.alfresco.org/model/content/1.0", name), qTypeName, archiveProperties);
    
    return childAssoc;
  }
  
  private void copyAspects(NodeRef nodeWorking, NodeRef nodeArchive)
  {
    Set<QName> aspects = this.nodeService.getAspects(nodeWorking);
    Iterator<QName> aspectIter = aspects.iterator();
    while (aspectIter.hasNext())
    {
      QName aspectQName = (QName)aspectIter.next();
      if (this.r2elnAspects.contains(aspectQName))
      {
        this.behaviourFilter.disableBehaviour(nodeArchive, aspectQName);
        try
        {
          this.nodeService.addAspect(nodeArchive, aspectQName, null);
        }
        finally
        {
          this.behaviourFilter.enableBehaviour(nodeArchive, aspectQName);
        }
      }
    }
  }
  
  private void copyAssociations(NodeRef nodeWorking, NodeRef nodeArchive)
  {
    for (QName assocQName : this.r2elnAssocs)
    {
      List<AssociationRef> targetAssocs = this.nodeService.getTargetAssocs(nodeWorking, assocQName);
      if (!targetAssocs.isEmpty())
      {
        QName aspectName = (QName)this.assocMap.get(assocQName);
        
        this.behaviourFilter.disableBehaviour(nodeArchive, aspectName);
        try
        {
          List<NodeRef> tagetNodeRefs = new ArrayList();
          for (AssociationRef assocRef : targetAssocs) {
            tagetNodeRefs.add(assocRef.getTargetRef());
          }
          this.nodeService.setAssociations(nodeArchive, ((AssociationRef)targetAssocs.get(0)).getTypeQName(), tagetNodeRefs);
        }
        finally
        {
          this.behaviourFilter.enableBehaviour(nodeArchive, aspectName);
        }
      }
    }
  }
  
  private void copyPermissions(NodeRef nodeWorking, NodeRef nodeArchive)
  {
    Set<AccessPermission> accessSetPermissions = this.permissionService.getAllSetPermissions(nodeWorking);
    
    boolean isInherited = this.permissionService.getInheritParentPermissions(nodeWorking);
    
    Iterator<AccessPermission> accessPermissionIter = accessSetPermissions.iterator();
    
    Set<String> authoritySet = new HashSet();
    while (accessPermissionIter.hasNext())
    {
      AccessPermission permission = (AccessPermission)accessPermissionIter.next();
      AuthorityType authorityType = permission.getAuthorityType();
      boolean isDirectly = permission.isSetDirectly();
      boolean inherited = permission.isInherited();
      if ((isDirectly) && ("USER".equals(authorityType.name())) && 
      
        (!authoritySet.contains(permission.getAuthority())))
      {
        this.permissionService.setPermission(nodeArchive, permission
          .getAuthority(), "SiteConsumer", true);
        
        this.permissionService.setInheritParentPermissions(nodeArchive, isInherited);
        
        authoritySet.add(permission.getAuthority());
      }
    }
  }
  
  protected String pdfTSCertify(String fileid)
  {
    String filename = fileid + EXTENTION_PDF;
    String result = null;
    
    String urlStr = this.hostTimestampUrl + "/certify?pdfF=" + filename;
    
    System.out.println("timstamp-url:" + urlStr);
    log.info("timstamp-start:" + urlStr);
    try
    {
      URLConnection conn = new URL(urlStr).openConnection();
      conn.setConnectTimeout(90000);
      conn.setReadTimeout(60000);
      
      InputStream stream = conn.getInputStream();
      BufferedReader br = new BufferedReader(new InputStreamReader(stream));
      String line;
      while ((line = br.readLine()) != null) {
        result = line.trim();
      }
      br.close();
    }
    catch (MalformedURLException e)
    {
      e.printStackTrace();
      return null;
    }
    catch (IOException e)
    {
      e.printStackTrace();
      return null;
    }
    catch (Exception e)
    {
      e.printStackTrace();
      return null;
    }
    System.out.println("result>>" + result);
    log.info("timstamp-end:" + urlStr + "/" + result);
    
    return "1".equals(result) ? "_n.pdf" : null;
  }
  
  protected void addParameterDefinitions(List<ParameterDefinition> paramList)
  {
    paramList.add(new ParameterDefinitionImpl("reviewer", DataTypeDefinition.TEXT, false, 
    
      getParamDisplayLabel("reviewer")));
  }
  
  public void setServiceRegistry(ServiceRegistry serviceRegistry)
  {
    this.services = serviceRegistry;
  }
  
  public void setBehaviourFilter(BehaviourFilter filter)
  {
    this.behaviourFilter = filter;
  }
}
