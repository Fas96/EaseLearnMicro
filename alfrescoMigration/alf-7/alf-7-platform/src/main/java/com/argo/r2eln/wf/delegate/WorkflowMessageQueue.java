package com.argo.r2eln.wf.delegate;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class WorkflowMessageQueue {

	public static class Message {
		
		protected Map<String, Object> payload = new HashMap<String, Object>();
		
		public Message(Map<String, Object> payload) {
			this.payload = payload;
		}
		
		public Map<String, Object> getPayload() {
			return this.payload;
		}
	}
	
	protected List<Message> queue = new LinkedList<WorkflowMessageQueue.Message>();
	
	public final static WorkflowMessageQueue INSTANCE = new WorkflowMessageQueue();
	
	public void send(Message m) {
		queue.add(m);
	}
	
	public Message getNextMessage() {
		return queue.remove(0);
	}
	
}
