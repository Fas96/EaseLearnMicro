package com.argo.r2eln.wf.listener;

import org.activiti.engine.delegate.DelegateExecution;
import org.activiti.engine.delegate.ExecutionListener;
import org.activiti.engine.impl.cfg.ProcessEngineConfigurationImpl;
import org.activiti.engine.impl.context.Context;
import org.activiti.engine.impl.el.Expression;
import org.alfresco.repo.workflow.activiti.ActivitiConstants;
import org.alfresco.service.ServiceRegistry;
import org.alfresco.service.cmr.action.ActionService;
import org.alfresco.service.cmr.repository.NodeService;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class ProcessStartedListener implements ExecutionListener {

	Log logger = LogFactory.getLog(ProcessStartedListener.class);
	
	@Override
	public void notify(DelegateExecution execution) {
//		String instanceId = execution.getProcessInstanceId();
//		logger.debug("instanceId>>"+instanceId);
//		
//		String required = (String)execution.getVariable("eln_requiredReview");
//		logger.debug("required>>"+required);
//		
//		ActionService actionService = getServiceRegistry().getActionService();
//		actionService.hashCode();
//		
//		NodeService nodeService = getServiceRegistry().getNodeService();
//		nodeService.getClass();
		
		logger.debug("ProcessStartedListener-notify");
	}


    /* taken from ActivitiScriptBase.java */
    protected ServiceRegistry getServiceRegistry() {
        ProcessEngineConfigurationImpl config = Context.getProcessEngineConfiguration();
        if (config != null) {
            // Fetch the registry that is injected in the activiti spring-configuration
            ServiceRegistry registry = (ServiceRegistry) config.getBeans().get(ActivitiConstants.SERVICE_REGISTRY_BEAN_KEY);
            if (registry == null) {
                throw new RuntimeException(
                            "Service-registry not present in ProcessEngineConfiguration beans, expected ServiceRegistry with key" + 
                            ActivitiConstants.SERVICE_REGISTRY_BEAN_KEY);
            }
            return registry;
        }
        throw new IllegalStateException("No ProcessEngineCOnfiguration found in active context");
    }

}
