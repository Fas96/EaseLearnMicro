<import resource="classpath:/alfresco/templates/webscripts/org/alfresco/slingshot/documentlibrary/parse-args.lib.js">

/**
 * Document List Component: treenode
 */
model.treenode = getTreeNode();

/* Create collection of folders in the given space */
function getTreeNode()
{
   try
   {
      var items = new Array(),
         hasSubfolders = true,
         ignoredTypes = ['fm:forum','fm:topic'],
         evalChildFolders = args["children"] !== "false",
         resultsTrimmed = false,
         argMax = parseInt(args["max"], 1000),
         maxItems = isNaN(argMax) ? -1 : argMax,
         projStatus = args.projStatus,
         maxNumChildren = 1000;
      
      // Use helper function to get the arguments
      var parsedArgs = ParseArgs.getParsedArgs();
      if (parsedArgs === null || args.user == null)
      {
         return;
      }
      
      var personNode = people.getPerson(args.user);
      
      var query = "+PATH:\"" + parsedArgs.pathNode.qnamePath + "//*\" +@eln\\:authPerson:\""+personNode.nodeRef+"\"";

      // Sorting parameters specified?
      var sortAscending = args.sortAsc,
         sortField = args.sortField;

      var sort = {
            column: "@cm:name",
            ascending: true  
      };
      
      if (sortAscending == "false")
      {
    	  sort.ascending = false;
      }
      if (sortField !== null)
      {
    	  sort.column = (sortField.indexOf(":") != -1 ? "@" : "") + sortField;
      }
	  
      allNodes = search.query(
	     {
	        query: query,
	        language: "lucene",
	        page:
	        {
	           maxItems: maxItems
	        },
	        sort: [sort]
	     });
      
      var numChildren = 1;

      for each (authority in allNodes)
      {
    	  var item = authority.parent;
          hasSubfolders = item.childFileFolders(false, true, ignoredTypes, 1).page.length > 0;
          
          if(item.typeShort != 'cm:folder') continue;

         items.push(
         {
            node: item,
            hasSubfolders: hasSubfolders,
            aspects: item.aspectsShort
         });
      }
      
      return (
      {
         parent: parsedArgs.pathNode,
         resultsTrimmed: resultsTrimmed,
         items: items
      });
   }
   catch(e)
   {
      status.setCode(status.STATUS_INTERNAL_SERVER_ERROR, e.toString());
      return;
   }
}