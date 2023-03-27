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
         argMax = parseInt(args["max"], 10),
         maxItems = isNaN(argMax) ? -1 : argMax,
         projStatus = args.projStatus,
         maxNumChildren = 100;
      
      // Use helper function to get the arguments
      var parsedArgs = ParseArgs.getParsedArgs();
      if (parsedArgs === null)
      {
         return;
      }
      
      var query = "+PATH:\"" + parsedArgs.pathNode.qnamePath + "/*\" +TYPE:\"folder\"";

      if(args.projStatus != null && args.projStatus.length > 0) {
    	  query += " +@eln\\:projStatus:\"" + args.projStatus + "\"";
      }

      if(args.projYear != null && args.projYear.length > 0) {
    	  query += " +@eln\\:projYear:\"" + args.projYear + "\"";
      }

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

      for each (item in allNodes)
      {

    	  numChildren++;
         if (numChildren == maxNumChildren)
         {
           	evalChildFolders = false;
         }

         if (evalChildFolders)
         {
            hasSubfolders = item.childFileFolders(false, true, ignoredTypes, 1).page.length > 0;
         }
         
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
         items: items,
         query: query
      });
   }
   catch(e)
   {
      status.setCode(status.STATUS_INTERNAL_SERVER_ERROR, e.toString());
      return;
   }
}