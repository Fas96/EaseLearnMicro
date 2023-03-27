<#escape x as jsonUtils.encodeJSONString(x)>
{"paginghtmltag" : "${(paginghtmltag)}",
"totalRecordCount":"${paginationinfo.totalRecordCount}",
"currentPageNo":"${paginationinfo.currentPageNo}",
"recordCountPerPage":"${paginationinfo.recordCountPerPage}",
"items" : [
<#list items as item>
	{
	"ts_id":"${(item.ts_id!"")}",
	"noderef":"${(item.noderef!"") }",
	"filename":"${(item.filename!"")}",
	"complete_yn":"${(item.complete_yn!"")}",
	"insdt":"${(item.insdt!"")}",
	"creator":"${(item.creator!"")}",
	"reviewer":"${(item.reviewer!"")}",
	"lastuptdt":"${(item.lastuptdt!"")}",
	"user_id":"${(item.user_id!"")}"
	}<#if item_has_next>,</#if>
</#list>
]}
</#escape>