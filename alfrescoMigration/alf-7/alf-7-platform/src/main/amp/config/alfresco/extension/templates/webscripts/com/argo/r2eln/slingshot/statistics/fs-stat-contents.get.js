<import resource="classpath:/alfresco/templates/webscripts/org/alfresco/slingshot/documentlibrary-v2/evaluator.lib.js">
<import resource="classpath:/alfresco/templates/webscripts/org/alfresco/slingshot/documentlibrary-v2/filters.lib.js">
<import resource="classpath:/alfresco/templates/webscripts/org/alfresco/slingshot/documentlibrary-v2/parse-args.lib.js">

function formatDate(d, seperator) {
	var year = d.getFullYear().toString();
	
	var month = d.getMonth()+1;
	if(month<10) month = "0"+month;
	
	var day = d.getDate();
	if(day<10) day = "0"+day;
	
	return year+seperator+month+seperator+day;
}

function formatMonth(d, seperator) {
	var year = d.getFullYear().toString();
	
	var month = d.getMonth()+1;
	if(month<10) month = "0"+month;
	
	return year+seperator+month;
}

function stat_main() {
	var maxItems = 200,
    skipCount=0,
    filter = args.filter,
    favourites = Common.getFavourites()
    mapCountByMonth = {};

	var parsedArgs = ParseArgs.getParsedArgs();
	
	var filterParams = Filters.getFilterParams(filter, parsedArgs,
		      {
		         favourites: favourites
		      })
	
    var date = new Date();
	date.setDate(date.getDate()-190);
	
	var parentNode = parsedArgs.pathNode;
	var query = "+PATH:\""+parentNode.qnamePath+"//*\" AND +TYPE:\"cm:content\" AND +@cm\\:created:[\""+formatDate(date,"-")+"\" TO \"*\"]";
	
	countContentByMonth(query, filterParams, skipCount, maxItems, mapCountByMonth);
	
	return mapCountByMonth;
}

function countContentByMonth(query, filterParams, skipCount, maxItems, mapCountByMonth) {
	
     var results = search.queryResultSet(
     {
        query: query,
        language: filterParams.language,
        page:
        {
           skipCount: skipCount,
           maxItems: maxItems
        },
        sort: filterParams.sort,
        templates: filterParams.templates,
        namespace: (filterParams.namespace ? filterParams.namespace : null)
     });
	 var length = results.nodes.length;
	 
     for each( node in results.nodes ) {
    	 
    	 if (node.hasAspect("{http://www.alfresco.org/model/content/1.0}workingcopy")) {
    		 
    		 model.display = node.qnamePath;
    		 continue;
    	 }
    	 var qnamepath = node.qnamePath;
    	 
    	 var d = node.properties["cm:created"];
    	 var displayMonth = formatMonth(d,"-");
    	 
    	 var num = mapCountByMonth[displayMonth];
    	 if(!num) num = 0;
    	 
    	 mapCountByMonth[displayMonth] = num + 1;
    	 
    	 skipCount++;
     }
    	 
     var hasMore = results.meta.hasMore;
     
     if(hasMore) {
    	 countContentByMonth(query, filterParams, skipCount, maxItems, mapCountByMonth);
     }
}

function getMonthList(seperator) {
	
	var d = new Date(),
	    year = d.getFullYear(),
	    month = d.getMonth()+1,
	    monthList=[];

	for(var ii=0;ii<6;ii++) {
		
		var key_month = year +seperator+ (month<10 ? "0"+month : +""+month);
		monthList.push(key_month);
		
		month--;
		
		if(month==0) {
			year--;
			month=12;
		}
	}
	
	return monthList.reverse();
}

model.mapCountByMonth = stat_main();
model.monthList = getMonthList("-");


