package com.argo.r2eln.repo.web.scripts.workflow;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.alfresco.model.ContentModel;
import org.alfresco.repo.security.authentication.AuthenticationUtil;
import org.alfresco.repo.web.scripts.workflow.WorkflowModelBuilder;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;
import org.alfresco.service.cmr.security.PersonService;
import org.alfresco.service.cmr.workflow.WorkflowInstance;
import org.alfresco.service.cmr.workflow.WorkflowService;
import org.alfresco.service.cmr.workflow.WorkflowTask;
import org.alfresco.service.cmr.workflow.WorkflowTaskQuery;
import org.alfresco.service.namespace.QName;

import com.argo.r2eln.action.executer.CertifyArciveActionExecuter;
import com.argo.r2eln.repo.model.R2elnContentModel;
import com.argo.r2eln.repo.model.R2elnWorkflowModel;

public class WorkflowUtils {


    public static final String PROP_REVIEWER = "reviewer";
    public static final String PROP_WORKFLOW_TYPE = "workflowType";
    public static final String PROP_CURRENT_TASK = "currentTaskName";
    public static final String PROP_CONTENT_ID = "contentId";
    
    /**
     * 
     * @param model
     * @param task
     */
    @SuppressWarnings({ "unchecked", "rawtypes" })
	public static void appendCustomProp(final WorkflowService workflowService, final PersonService personService, final NodeService nodeService,
    		WorkflowModelBuilder modelBuilder, final  Map<String, Object> model, final  WorkflowInstance wfInstance) {
        
    	AuthenticationUtil.runAs(new AuthenticationUtil.RunAsWork()
        {
          public Void doWork()
            throws Exception
          {
              WorkflowTaskQuery tasksQuery = new WorkflowTaskQuery();
              tasksQuery.setTaskState(null);
              tasksQuery.setActive(null);
              tasksQuery.setProcessId(wfInstance.getId());
              List<WorkflowTask> tasks = workflowService.queryTasks(tasksQuery, false);
              if(tasks.size()==0) return null;
              
              Serializable reviewer = null;
              
              for(int i=tasks.size()-1;i>=0;i--) {
              	WorkflowTask wfTask = tasks.get(i);
              	
              	Map<QName, Serializable> wfProps = wfTask.getProperties();
              	
              	reviewer = (NodeRef)wfProps.get(R2elnWorkflowModel.ASSOC_ASSIGNEE);
              	if(reviewer!=null) break;
              	
              	if("wf:activitiReviewTask".equals(wfTask.getName())) {
                      reviewer = wfTask.getProperties().get(ContentModel.PROP_OWNER);
                      break;
              	}
              }
              
              if(reviewer!=null) model.put(PROP_REVIEWER, getPersonModel(personService, nodeService, reviewer));
              
              Serializable workflowType = tasks.get(tasks.size()-1).getProperties().get(R2elnWorkflowModel.PROP_WORKFLOW_TYPE);
              model.put(PROP_WORKFLOW_TYPE, workflowType);

              Serializable currentTaskName = tasks.get(0).getName();
              model.put(PROP_CURRENT_TASK, currentTaskName);
              
              Serializable contentId = tasks.get(tasks.size()-1).getProperties().get(R2elnWorkflowModel.PORP_CONTENT_ID);
              model.put(PROP_CONTENT_ID, contentId);            
              
              return null;
          }
        }, AuthenticationUtil.getAdminUserName());
        

    }
    
    private static Map<String, Object> getPersonModel(PersonService personService, NodeService nodeService, Serializable personVal)
    {
        Map<String, Object> model = new HashMap<String, Object>();
        
    	if(personVal instanceof NodeRef) {
            Map<QName, Serializable> properties = nodeService.getProperties((NodeRef)personVal);
            model.put(WorkflowModelBuilder.PERSON_USER_NAME, properties.get(ContentModel.PROP_USERNAME));
            model.put(WorkflowModelBuilder.PERSON_FIRST_NAME, properties.get(ContentModel.PROP_FIRSTNAME));
            model.put(WorkflowModelBuilder.PERSON_LAST_NAME, properties.get(ContentModel.PROP_LASTNAME));
            
            return model;
    	}
    	
        if (!(personVal instanceof String))
            return null;

        String name = (String) personVal;
        
        // TODO Person URL?

        model.put(WorkflowModelBuilder.PERSON_USER_NAME, name);
        
        if (personService.personExists(name))
        {
            NodeRef person = personService.getPerson(name);
            Map<QName, Serializable> properties = nodeService.getProperties(person);
            model.put(WorkflowModelBuilder.PERSON_FIRST_NAME, properties.get(ContentModel.PROP_FIRSTNAME));
            model.put(WorkflowModelBuilder.PERSON_LAST_NAME, properties.get(ContentModel.PROP_LASTNAME));
        }
        
        return model;
    }
    
    
}
