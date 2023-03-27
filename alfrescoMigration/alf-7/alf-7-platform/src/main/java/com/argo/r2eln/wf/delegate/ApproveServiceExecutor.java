package com.argo.r2eln.wf.delegate;

import java.util.Collections;
import java.util.Map;

import org.activiti.engine.ProcessEngine;

import com.argo.r2eln.wf.delegate.WorkflowMessageQueue.Message;

public class ApproveServiceExecutor {

	public static final String SHOULD_FAIL_VAR_NAME = "shouldFail";
	
	public static final String PRICE_VAR_NAME = "price";
	
	public static final float PRICE = 199.00f;
	
	public static ApproveServiceExecutor INSTANCE = new ApproveServiceExecutor();
	
	public void invoke(Message message, ProcessEngine processEngine) {
		
		Map<String, Object> requestPayload = message.getPayload();
		
		String executionId = (String)requestPayload.get(SHOULD_FAIL_VAR_NAME);
		
		Map<String, Object> callbackPayload = Collections.<String,Object>singletonMap(PRICE_VAR_NAME, PRICE);
		processEngine.getRuntimeService().signal(executionId);
	}
}
