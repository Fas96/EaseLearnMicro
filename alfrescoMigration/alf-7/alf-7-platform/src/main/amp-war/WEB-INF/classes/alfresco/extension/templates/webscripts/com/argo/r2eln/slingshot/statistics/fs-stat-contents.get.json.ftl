<#escape x as jsonUtils.encodeJSONString(x)>
{
  "chart" : {
        "caption": "",
        "subCaption": "",
        "xAxisName": "",
        "yAxisName": "Number",
        "rotateValues":0,
        "numberPrefix": "",
        "theme": "fint"  
   },
   "data" : [
    <#list monthList as month>
      {
         "label": "${month}",
         "value": <#if mapCountByMonth[month]??>${mapCountByMonth[month]?c}<#else>0</#if>
      }<#if month_has_next>,</#if>
    </#list>
   ]
 }
</#escape>
