
function main()
{
	var bbsName = url.templateArgs.bbsName;
	
    if(!bbsName) {
        status.setCode(status.STATUS_INTERNAL_SERVER_ERROR, bbsName+" is required.");
        return;
    }
    
    var query = "+PATH:\"/app:company_home/cm:bbs/cm:"+bbsName+"\"";

    var sort = {
            column: "@cm:name",
            ascending: true  
      };
    
    var bbsdata = search.query(
   	     {
   	        query: query,
   	        language: "lucene",
   	        sort: [sort]
   	     });
    
    if(bbsdata.length<1) {
        status.setCode(status.STATUS_INTERNAL_SERVER_ERROR, bbsName+" doesn't exist");
        return;
    }
    
    var bbs = {
    	nodeRef: bbsdata[0].nodeRef,
    	name: bbsdata[0].name
    };
    
    return bbs;
}

model.bbs = main();
