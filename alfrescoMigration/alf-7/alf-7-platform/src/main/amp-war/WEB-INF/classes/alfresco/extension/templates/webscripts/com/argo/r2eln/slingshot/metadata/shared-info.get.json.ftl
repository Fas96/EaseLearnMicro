<#macro dateFormat date=""><#if date?is_date>${xmldate(date)}</#if></#macro>

<#escape x as jsonUtils.encodeJSONString(x)>
{
	"node": <#noescape>${data.item.nodeJSON}</#noescape>,
	"path": "${data.path}"<#if data.authorities??>,
    "sharedInfos": [
      <#list data.authorities as authority>
       {
		 "authPermission" : "${authority.properties["eln:authPermission"]}",
		 "downloadable" : ${authority.properties["eln:downloadable"]?c},
		 "printable" : ${authority.properties["eln:printable"]?c},
		 "sharedDate" : "<@dateFormat authority.properties["eln:shareDate"]/>"
	    }<#if authority_has_next>,</#if>
	  </#list>
    ]</#if>
}
</#escape>