{
  "items" : 
   [
   <#list items as item>
     {
         "nodeRef" : "${item.nodeRef}",
         "name" : "${item.properties.name}",
        "title" : <#if item.properties.title??>"${item.properties.title}"<#else>""</#if>
        "displayPath" : "${item.displayPath}"
     }<#if item_has_next>,</#if>
   </#list>
   ]
}