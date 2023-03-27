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
	]<#if deptlist??>,</#if>
</#if>
<#if deptlist??>
	"dept" : [
		<#list deptlist as dept>
			{
			"dept_nm":"${(dept.dept_nm!"")}",
			"count":"${(dept.count!"")}"
			}<#if dept_has_next>,</#if>
		</#list>
	]
</#if>
}
</#escape>