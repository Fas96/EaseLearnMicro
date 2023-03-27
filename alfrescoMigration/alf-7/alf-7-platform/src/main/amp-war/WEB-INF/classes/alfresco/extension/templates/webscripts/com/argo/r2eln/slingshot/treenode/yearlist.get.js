<import resource="classpath:/alfresco/templates/webscripts/org/alfresco/slingshot/documentlibrary-v2/parse-args.lib.js">

const DEFAULT_MAX_RESULTS = 250;
const DEFAULT_PAGE_SIZE = 50;
const SITES_SPACE_QNAME_PATH = "/app:company_home/st:sites/";

function getSearchResults(params)
{
	var ftsQuery = params.query;    
    
    if(params.projStatus != null && params.projStatus.length > 0) {
    	ftsQuery += " +@eln\\:projStatus:\"" + params.projStatus + "\"";
    }
    
	var queryDef = {
         query: ftsQuery,
         onerror: "no-results",
         fieldFacets: params.facetFields,
         searchTerm: params.term,
         spellCheck: params.spell
      };
 
      return search.queryResultSet(queryDef);
}

function main()
{
   var facetFields = ["{http://r2eln.argonet.co.kr/model/content/1.0}projYear"];
   
   var parsedArgs = ParseArgs.getParsedArgs();
   
   var query = "+PATH:\"" + parsedArgs.pathNode.qnamePath + "/*\"";
   
   var params =
   {
      term: args.term,
      query: query,
      rootNode: args.rootNode,
      projStatus : args.projStatus,
      sort: args.sort,
      maxResults: 1,
      startIndex: 0,
      facetFields: facetFields,
      spell: false
   };
   
   var rs = getSearchResults(params);

   model.facets = rs.meta.facets;
   
}

main();