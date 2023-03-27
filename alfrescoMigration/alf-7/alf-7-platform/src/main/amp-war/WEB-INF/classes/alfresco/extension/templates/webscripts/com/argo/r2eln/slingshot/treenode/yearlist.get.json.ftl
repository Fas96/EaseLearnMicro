<#escape x as jsonUtils.encodeJSONString(x)>
[
<#if facets??><#list facets?keys as field>
	<#if field == "@{http://r2eln.argonet.co.kr/model/content/1.0}projYear" >
		<#assign eachFacets=facets[field]><#list eachFacets as f>
		{
		"label": "${f.facetLabel}",
		"value": "${f.facetValue}",
		"hits": ${f.hits?c},
		"index": ${f.facetLabelIndex?c}
		}<#if f_has_next>,</#if>
		</#list>
	</#if>
</#list></#if>
]
</#escape>
