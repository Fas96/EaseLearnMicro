(function(){var g=YAHOO.util.Dom;var d=Alfresco.util.encodeHTML;Alfresco.dashlet.ForumSummary=function i(k){Alfresco.dashlet.ForumSummary.superclass.constructor.call(this,"Alfresco.dashlet.ForumSummary",k,["container","datasource","datatable"]);this.services.preferences=new Alfresco.service.Preferences();return this};YAHOO.extend(Alfresco.dashlet.ForumSummary,Alfresco.component.Base,{PREFERENCES_FORUM_SUMMARY_DASHLET:"",options:{searchRootNode:"",resultSize:"10",filters:"",filterPreferences:[]},onReady:function c(){var k=this.id;this.PREFERENCES_FORUM_SUMMARY_DASHLET=this.services.preferences.getDashletId(this,"forum.summary");var t=this.services.preferences.get();for(var p=0;p<this.options.filters.length;p++){var l=this.options.filters[p];var s=Alfresco.util.createYUIButton(this,l.name,this.onFilterChanged,{type:"menu",menu:l.name+"-menu",lazyloadmenu:false});var m=this.PREFERENCES_FORUM_SUMMARY_DASHLET+"."+l.name;var n=Alfresco.util.findValueByDotNotation(t,m);if(n!==null){for(var o=0;o<l.options.length;o++){var r=l.options[o];if(r.value==n){s.set("label",this.msg("filter."+l.name+"."+r.label)+" "+Alfresco.constants.MENU_ARROW_SYMBOL);s.value=n;break}}}else{this.options.filterPreferences[m]=l.options[0].value;var q=this.services.preferences;q.set(m,this.options.filterPreferences[m]);s.set("label",this.msg("filter."+l.name+"."+l.options[0].label)+" "+Alfresco.constants.MENU_ARROW_SYMBOL);s.value=l.options[0].value}this.widgets[l.name+"MenuButton"]=s;this.options.filterPreferences[m]=n}this.doRequest();g.removeClass(Selector.query(".toolbar div",k,true),"hidden")},doRequest:function f(k){var l=this;l.widgets.alfrescoDataTable=new Alfresco.util.DataTable({dataSource:{url:this.buildUrl(),config:{responseSchema:{resultsList:"items"}}},dataTable:{container:this.id+"-filtered-topics",columnDefinitions:[{key:"avatar",formatter:l.bind(l.buildThumbnail),width:32},{key:"topic",formatter:l.bind(l.buildDescription)}],config:{MSG_EMPTY:this.msg("no.result")}}})},buildDescription:function e(v,z,r,o){var n=z.getData("name"),t=z.getData("title"),p=z.getData("author"),s=z.getData("totalReplyCount"),u=z.getData("lastReplyBy"),x=z.getData("isUpdated"),w=Alfresco.util.relativeTime(z.getData("createdOn")),y=Alfresco.util.relativeTime(z.getData("updatedOn")),q=Alfresco.util.relativeTime(z.getData("lastReplyOn")),m=z.getData("site"),l=Alfresco.constants.URL_PAGECONTEXT+"site/"+m+"/discussions-topicview?topicId="+n+"&listViewLinkBack=true";var k='<div class="node topic">';k+='<span class="nodeTitle"><a href="'+l+'">'+t+"</a>";if(x){k+='<span class="theme-color-2 nodeStatus"> ('+this.msg("topicList.updated")+" "+y+")</span>"}k+="</span>";k+='<div class="published">';k+="<span>"+this.msg("topicList.createdBy",this.getAuthorLink(p,"theme-color-1"),w)+"</span>";if(s>0){k+="<br>";if(s==1){k+='<span class="nodeAttrLabel">'+this.msg("topicList.replies.single")+" </span>"}else{k+='<span class="nodeAttrLabel">'+this.msg("topicList.replies.plural",s)+" </span>"}k+='<span class="nodeAttrLabel">'+this.msg("topicList.lastReplyBy",this.getAuthorLink(u,"theme-color-1"),q)+"</span>"}k+="</div>";v.innerHTML=k},buildThumbnail:function b(l,k,m,n){l.innerHTML=Alfresco.Share.userAvatar(k.getData("author").username,32)},buildUrl:function h(){var n="";for(var l=0;l<this.options.filters.length;l++){var m=this.options.filters[l];var o=this.options.filterPreferences[this.PREFERENCES_FORUM_SUMMARY_DASHLET+"."+m.name];n+=m.name+"="+o+"&"}n+="resultSize="+this.options.resultSize;var k="";if(this.options.siteId.length>0){k=Alfresco.constants.PROXY_URI+"api/forum/site/{site}/discussions/posts/filtered?{parameters}"}else{k=Alfresco.constants.PROXY_URI+"api/forum/discussions/posts/filtered?{parameters}"}return YAHOO.lang.substitute(k,{site:this.options.siteId,parameters:n})},onFilterChanged:function a(n,m){var q=m[1];var p=m[2];var o=this.widgets[p+"MenuButton"];if(q!==null){var k=this.PREFERENCES_FORUM_SUMMARY_DASHLET+"."+p;this.options.filterPreferences[k]=q.value;var l=this.services.preferences;l.set(k,q.value);o.set("label",q.srcElement.text+" "+Alfresco.constants.MENU_ARROW_SYMBOL);o.value=q.value;this.doRequest()}},getAuthorLink:function j(l,m){var n=l.firstName+((l.firstName!==""&&l.lastName!=="")?" ":"")+l.lastName,k=!l.firstName&&!l.lastName;return Alfresco.util.userProfileLink(l.username,k?"":n,'class="'+m+'"',k)}})})();