(function(){var b=YAHOO.util.Dom,i=YAHOO.util.Event,c=YAHOO.lang.substitute;Alfresco.DocumentSync=function e(j){Alfresco.DocumentSync.superclass.constructor.call(this,"Alfresco.DocumentSync",j);YAHOO.Bubbling.on("metadataRefresh",this.doRefresh,this);return this};YAHOO.extend(Alfresco.DocumentSync,Alfresco.component.Base);YAHOO.lang.augmentProto(Alfresco.DocumentSync,Alfresco.doclib.Actions);YAHOO.lang.augmentObject(Alfresco.DocumentSync.prototype,{options:{nodeRef:null,site:null,documentDetails:null},_getSyncActionButtons:function a(){var j="";var k=this.options.documentDetails.item.actions;if(Alfresco.util.findInArray(k,"document-cloud-sync","id")){j+='<a href="#" class="document-sync-link" title="'+this.msg("label.document.cloud-sync")+'">&nbsp;</a>'}if(Alfresco.util.findInArray(k,"document-cloud-unsync","id")){j+='<a href="#" class="document-unsync-link" title="'+this.msg("label.document.cloud-unsync")+'">&nbsp;</a>'}if(Alfresco.util.findInArray(k,"document-request-sync","id")){j+='<a href="#" class="document-requestsync-link" title="'+this.msg("label.document.cloud-request-sync")+'">&nbsp;</a>'}return j},onReady:function d(){if(Alfresco.util.arrayContains(this.options.documentDetails.item.node.aspects,"sync:syncSetMemberNode")){Alfresco.util.Ajax.request({url:Alfresco.constants.PROXY_URI+"slingshot/doclib2/node/"+this.options.nodeRef.replace("://","/"),successCallback:{fn:this.onSyncInfoLoaded,scope:this},failureMessage:this.msg("message.failure")})}else{b.get(this.id+"-formContainer").innerHTML=this.msg("content.not.synced")}b.get(this.id+"-heading").innerHTML=c(b.get(this.id+"-heading").innerHTML,{syncActionButtons:this._getSyncActionButtons()});var j=b.get(this.id+"-document-sync-twister-actions");b.removeClass(j,"hidden");var l=this.id+"-heading";var k=this._buildRecord();i.on(b.getElementsByClassName("document-sync-link","a",l),"click",function n(p){i.preventDefault(p);this.onActionCloudSync(k)},{},this);i.on(b.getElementsByClassName("document-unsync-link","a",l),"click",function m(p){i.preventDefault(p);this.onActionCloudUnsync(k)},{},this);i.on(b.getElementsByClassName("document-requestsync-link","a",l),"click",function o(p){i.preventDefault(p);this.onActionCloudSyncRequest(k)},{},this)},_buildRecord:function h(){var j=this.options.documentDetails.item;j.jsNode=new Alfresco.util.Node(j.node);return j},onSyncInfoLoaded:function g(k){var l=this;var j={showTitle:false,showRequestSyncButton:false,showUnsyncButton:false,showMoreInfoLink:false};Alfresco.util.getSyncStatus(this,this._buildRecord(),k.json,j,function(n){if(n!=null){var o=l.id+"-formContainer";var m=b.get(o);m.innerHTML=n.html;Alfresco.util.syncClickOnShowDetailsLinkEvent(l,o);Alfresco.util.syncClickOnHideLinkEvent(l,o);Alfresco.util.syncClickOnTransientErrorShowDetailsLinkEvent(l,o);Alfresco.util.syncClickOnTransientErrorHideLinkEvent(l,o)}else{Alfresco.util.PopupManager.displayMessage({text:l.msg("message.failure")})}})},doRefresh:function f(l,k,m){var j=b.get(this.id+"-document-sync-twister-actions");b.addClass(j,"hidden");YAHOO.Bubbling.unsubscribe("metadataRefresh",this.doRefresh,this);if(m.options.documentDetails.item.jsNode.isContainer){this.refresh("components/folder-details/folder-sync?nodeRef={nodeRef}"+(this.options.site?"&site={site}":""))}else{this.refresh("components/document-details/document-sync?nodeRef={nodeRef}"+(this.options.site?"&site={site}":""))}}},true)})();