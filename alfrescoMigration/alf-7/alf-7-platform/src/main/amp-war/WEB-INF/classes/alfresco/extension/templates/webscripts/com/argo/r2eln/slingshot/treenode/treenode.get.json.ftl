<#assign p = treenode.parent>
<#escape x as jsonUtils.encodeJSONString(x)>
[
<#list treenode.items as item>
   <#assign t = item.node>
   {
      "key": "${t.nodeRef}",
      "name": "${t.name}",<#if t.properties.title??>
      "title": "${t.properties.title}",</#if>
      "lazy": "true"
      <#if item.hasSubfolders>,"folder":"true"<#else>,"folder":"false"</#if>
   }<#if item_has_next>,</#if>
</#list>
]
</#escape>
