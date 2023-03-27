<#escape x as jsonUtils.encodeJSONString(x)>
{
   "parentRef": "${nodeRef}",
   "data":
	[
	<#list authorities as authority>
	   <#assign person = authority.personInfo>
	   {
	      "nodeRef": "${person.nodeRef}",
	      "userName": "${person.userName}",<#if person.firstName??>
	      "firstName": "${person.firstName}",</#if><#if person.lastName??>
	      "lastName": "${person.lastName}",</#if><#if authority.permission??>
	      "permission": "${authority.permission}",</#if>
	      "downloadable": "${authority.downloadable?c}",
	      "printable": "${authority.printable?c}"
	   }<#if authority_has_next>,</#if>
	</#list>
	]
}
</#escape>