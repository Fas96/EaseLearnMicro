<#escape x as jsonUtils.encodeJSONString(x)>
{
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
	]<#if items??>,</#if>
</#if>
<#if items??>
	"items" : [
		<#list items as item>
			{
			"statistics_dt":"${(item.statistics_dt!"")}",
			"user_id":"${(item.user_id!"")}",
			"dept_cd":"${(item.dept_cd!"")}",
			"dept_nm":"${(item.dept_nm!"")}",
			"user_nm":"${(item.user_nm!"")}",
			"project_name":"${(item.project_name!"")}",
			"project_startdt":"${(item.project_startdt!"")}",
			"project_enddt":"${(item.project_enddt!"")}",
			"upload":"${(item.upload!"")}",
			"timestamp":"${(item.timestamp!"")}",
			"add_note":"${(item.add_note!"")}",
			"submit_note":"${(item.submit_note!"")}",
			"keep_note":"${(item.keep_note!"")}",
			"rent_note":"${(item.rent_note!"")}",
			"return_note":"${(item.return_note!"")}",
			"unsubmit_note":"${(item.unsubmit_note!"")}",
			"transfer_note":"${(item.transfer_note!"")}"
			}<#if item_has_next>,</#if>
		</#list>
	]
</#if>
}
</#escape>