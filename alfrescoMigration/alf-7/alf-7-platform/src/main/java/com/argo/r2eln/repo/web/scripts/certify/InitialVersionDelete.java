package com.argo.r2eln.repo.web.scripts.certify;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.Map;
import org.alfresco.service.ServiceRegistry;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;
import org.alfresco.service.cmr.repository.StoreRef;
import org.alfresco.service.cmr.version.Version;
import org.alfresco.service.cmr.version.VersionHistory;
import org.alfresco.service.cmr.version.VersionService;
import org.springframework.extensions.webscripts.Cache;
import org.springframework.extensions.webscripts.DeclarativeWebScript;
import org.springframework.extensions.webscripts.Match;
import org.springframework.extensions.webscripts.Status;
import org.springframework.extensions.webscripts.WebScriptException;
import org.springframework.extensions.webscripts.WebScriptRequest;

public class InitialVersionDelete
  extends DeclarativeWebScript
{
  private ServiceRegistry services;
  private NodeService nodeService;
  private VersionService versionService;
  
  public void init()
  {
    this.nodeService = this.services.getNodeService();
    this.versionService = this.services.getVersionService();
  }
  
  protected Map<String, Object> executeImpl(WebScriptRequest req, Status status, Cache cache)
  {
    String retMessage = "";
    Map<String, Object> model = new HashMap();
    try
    {
      NodeRef nodeRef = parseRequestForNodeRef(req);
      
      VersionHistory history = this.versionService.getVersionHistory(nodeRef);
      Version version = null;
      try
      {
        version = history.getVersion("1.0");
      }
      catch (Exception e)
      {
        model.put("result", "initial version does not exist.");
        return model;
      }
      if (version != null)
      {
        version.getVersionLabel();
        this.versionService.deleteVersion(nodeRef, version);
        retMessage = "successfully deleted.";
      }
      else
      {
        retMessage = "initial version does not exist.";
      }
    }
    catch (Exception e)
    {
      StringWriter writer = new StringWriter();
      e.printStackTrace(new PrintWriter(writer));
      retMessage = "error occurred. \n" + writer.toString();
    }
    model.put("result", retMessage);
    
    return model;
  }
  
  private NodeRef parseRequestForNodeRef(WebScriptRequest req)
  {
    Map<String, String> templateVars = req.getServiceMatch().getTemplateVars();
    String storeType = (String)templateVars.get("store_type");
    String storeId = (String)templateVars.get("store_id");
    String nodeId = (String)templateVars.get("id");
    
    StoreRef storeRef = new StoreRef(storeType, storeId);
    NodeRef nodeRef = new NodeRef(storeRef, nodeId);
    if (!this.nodeService.exists(nodeRef)) {
      throw new WebScriptException(404, "Unable to find node: " + nodeRef.toString());
    }
    return nodeRef;
  }
  
  public void setServiceRegistry(ServiceRegistry serviceRegistry)
  {
    this.services = serviceRegistry;
  }
}
