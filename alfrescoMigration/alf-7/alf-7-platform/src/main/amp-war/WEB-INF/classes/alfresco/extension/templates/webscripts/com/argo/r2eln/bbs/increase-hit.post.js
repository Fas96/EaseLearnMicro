<import resource="classpath:/alfresco/templates/webscripts/org/alfresco/slingshot/documentlibrary/parse-args.lib.js">

function main()
{
	var storeType = url.templateArgs.store_type,
	storeId = url.templateArgs.store_id,
	id = url.templateArgs.id;
	
	var nodeRef =  storeType + "://" + storeId + "/" + id;
	
    var itemNode = ParseArgs.resolveNode(nodeRef);
    
    var sessNodeRef = session.getValue(nodeRef);
    
    if(!sessNodeRef) {
    	var hit = itemNode.properties["bbs:hit"];

    	itemNode.properties["bbs:hit"] = parseInt(hit)+1;
    	itemNode.save();
    	
    	session.setValue(nodeRef,'Y');
    	
    	model.status = "increased!";
    } else {
    	model.status = "not increased!";
    }

}

main();
