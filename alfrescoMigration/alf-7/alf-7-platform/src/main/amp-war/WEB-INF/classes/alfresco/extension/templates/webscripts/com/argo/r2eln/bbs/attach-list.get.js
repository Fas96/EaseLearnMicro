<import resource="classpath:/alfresco/templates/webscripts/org/alfresco/slingshot/documentlibrary/parse-args.lib.js">

function main()
{
	var nodeRef = args.nodeRef;
	
    if(!nodeRef) {
        status.setCode(status.STATUS_INTERNAL_SERVER_ERROR, "nodeRef is required.");
        return;
    }
    
    var pathNode = ParseArgs.resolveNode(nodeRef);
    
    var  attachments = pathNode.childAssocs["bbs:attached"];
    
    if(!attachments) attachments = [];
    
    return attachments;
    
}

model.attachments = main();
