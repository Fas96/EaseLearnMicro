<#escape x as jsonUtils.encodeJSONString(x)>
{
<#if emplist??>
	"emp": [
		<#list emplist as emp>
			{
			"user_id":"${emp.user_id}",
			"user_nm":"${emp.user_nm}",
			"dept_nm":"${emp.dept_nm}",
			"count":"${emp.count}"
			}<#if emp_has_next>,</#if>
		</#list>
	]<#if projlist??>,</#if>
</#if>
<#if projlist??>
	"proj": [
		<#list projlist as proj>
			{
			"nt_project_id":"${proj.nt_project_id}",
			"project_code":"${proj.project_code}",
			"project_name":"${proj.project_name}",
			"project_startdt":"${proj.project_startdt}",
			"project_enddt":"${proj.project_enddt}",
			"count":"${proj.count}"
			}<#if proj_has_next>,</#if>
		</#list>
	]<#if chartLabel??>,</#if>
</#if>
<#if chartLabel??>
	"chartLabel": [
		<#list chartLabel as label>
			{
			"label":"${label.label}"
			}<#if label_has_next>,</#if>
		</#list>
	],
</#if>
<#if chartValue??>
	"chartValue": [
		<#list chartValue as value>
			{
			"value":"${value.value}"
			}<#if value_has_next>,</#if>
		</#list>
	]
</#if>
}
</#escape>