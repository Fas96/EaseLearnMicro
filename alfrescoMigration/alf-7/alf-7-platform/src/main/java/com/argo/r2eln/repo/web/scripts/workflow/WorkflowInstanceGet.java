/*
 * Copyright (C) 2005-2011 Alfresco Software Limited.
 *
 * This file is part of Alfresco
 *
 * Alfresco is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Alfresco is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with Alfresco. If not, see <http://www.gnu.org/licenses/>.
 */
package com.argo.r2eln.repo.web.scripts.workflow;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import org.alfresco.repo.web.scripts.workflow.AbstractWorkflowWebscript;
import org.alfresco.repo.web.scripts.workflow.WorkflowModelBuilder;
import org.alfresco.service.cmr.workflow.WorkflowInstance;
import org.alfresco.service.cmr.workflow.WorkflowInstanceQuery;
import org.alfresco.service.namespace.QName;
import org.springframework.extensions.webscripts.Cache;
import org.springframework.extensions.webscripts.Status;
import org.springframework.extensions.webscripts.WebScriptException;
import org.springframework.extensions.webscripts.WebScriptRequest;

import com.argo.r2eln.repo.model.R2elnWorkflowModel;

/**
 * @author unknown
 * @since 3.4
 */
public class WorkflowInstanceGet extends AbstractWorkflowWebscript
{
    public static final String PARAM_INCLUDE_TASKS = "includeTasks";

    @Override
    protected Map<String, Object> buildModel(WorkflowModelBuilder modelBuilder, WebScriptRequest req, Status status, Cache cache)
    {
        Map<String, String> params = req.getServiceMatch().getTemplateVars();
        
        WorkflowInstanceQuery workflowInstanceQuery = new WorkflowInstanceQuery();
        
        // getting workflow instance id from request parameters
        String workflowInstanceId = params.get("workflow_instance_id");
        String contentId = params.get("content_id");
        
        boolean includeTasks = getIncludeTasks(req);

        WorkflowInstance workflowInstance = null;

        if(contentId!=null) {
        	Map<QName, Object> filters = new HashMap<QName, Object>(9);
        	filters.put(R2elnWorkflowModel.PORP_CONTENT_ID, contentId);
        	workflowInstanceQuery.setCustomProps(filters);
        	
        	List<WorkflowInstance> instances = workflowService.getWorkflows(workflowInstanceQuery, 1, 0);
        	if(instances.size()>0) workflowInstance = instances.get(0);
        } else {
        	workflowInstance = workflowService.getWorkflowById(workflowInstanceId);
        }
        
        // task was not found -> return 404
        if (workflowInstance == null)
        {
            throw new WebScriptException(HttpServletResponse.SC_NOT_FOUND, "Unable to find workflow instance with id: " + workflowInstanceId);
        }

        Map<String, Object> model = new HashMap<String, Object>();
        // build the model for ftl
        model.put("workflowInstance", modelBuilder.buildDetailed(workflowInstance, includeTasks));

        return model;
    }

    private boolean getIncludeTasks(WebScriptRequest req)
    {
        String includeTasks = req.getParameter(PARAM_INCLUDE_TASKS);
        if (includeTasks != null)
        {
            try
            {
                return Boolean.valueOf(includeTasks);
            }
            catch (Exception e)
            {
                // do nothing, false will be returned
            }
        }

        // Defaults to false.
        return false;
    }

}
