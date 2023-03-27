function main() 
{
    var packageNode,
        items=[];

	try {
		var nodeRef = url.templateArgs.store_type + "://" + url.templateArgs.store_id + "/" + url.templateArgs.id;
logger.log(nodeRef);
		packageNode = search.findNode(nodeRef);
logger.log(packageNode);
		if(packageNode) {
		    items = packageNode.children;
		}
	} catch(e)
	{
		var msg = e.message;
		if(logger.isLoggingEnabled())
		    logger.log(msg);

		status.setCode(500,msg);
	}

	model.items = items;
}

main();