<import resource="classpath:/alfresco/templates/webscripts/org/alfresco/slingshot/documentlibrary-v2/evaluator.lib.js">

/**
 * Node Metadata Retrieval Service GET method
 */
function main()
{
   var item, authorities, displayPaths;
   
   // allow for content to be loaded from id
   if (args["nodeRef"] != null)
   {
   	var nodeRef = args["nodeRef"];
   	node = search.findNode(nodeRef);
   	
   	if (node != null)
   	{
   		item = Evaluator.run(node);
        authorities = node.childAssocs["eln:authMembers"];
   	}
   }
   
   displayPaths = node.displayPath.split("/");
   
   // store node onto model
   model.data = {
	   item : item,
	   path : "/" + displayPaths.slice(5, displayPaths.length).join("/"),
	   authorities : authorities
   };
}

main();