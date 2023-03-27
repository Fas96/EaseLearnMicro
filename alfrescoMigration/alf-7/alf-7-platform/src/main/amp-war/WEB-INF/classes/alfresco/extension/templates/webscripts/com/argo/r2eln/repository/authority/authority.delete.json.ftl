<#escape x as jsonUtils.encodeJSONString(x)>
{
   "data":
   {
      "userName": "${userName}",
      "firstName": "${firstName}"<#if lastName??>,
      "lastName": "${lastName}"</#if>
   }
}
</#escape>