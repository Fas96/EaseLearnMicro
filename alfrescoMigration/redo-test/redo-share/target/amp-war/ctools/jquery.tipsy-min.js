/*!
 * Copyright 2002 - 2013 Webdetails, a Pentaho company.  All rights reserved.
 *
 * This software was developed by Webdetails and is provided under the terms
 * of the Mozilla Public License, Version 2.0, or any later version. You may not use
 * this file except in compliance with the license. If you need a copy of the license,
 * please go to  http://mozilla.org/MPL/2.0/. The Initial Developer is Webdetails.
 *
 * Software distributed under the Mozilla Public License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or  implied. Please refer to
 * the license for the specific language governing your rights and limitations.
 */
;
/*!
 * tipsy, facebook style tooltips for jquery
 * version 1.0.0a
 * (c) 2008-2010 jason frame [jason@onehackoranother.com]
 * released under the MIT license
 */
;!function(c){function a(e){var d=e.attr("title");(d||"string"!=typeof e.attr("original-title"))&&e.attr("original-title",d||"").removeAttr("title")}function b(e,d){this.$element=c(e);this.options=d;this.enabled=!0;a(this.$element)}b.prototype={enter:function(){var e=this,d=this.options;if(0==d.delayIn){e.hoverState=null;e.show()}else{e.hoverState="in";setTimeout(function(){if("in"===e.hoverState){e.hoverState=null;e.show()}},d.delayIn)}},leave:function(){var e=this,d=this.options;if(0==d.delayOut){e.hide()}else{e.hoverState="out";setTimeout(function(){"out"===e.hoverState&&e.hide()},d.delayOut)}},visible:function(){var d;return"in"===this.hoverState||"out"!==this.hoverState&&!(!this.$tip||!(d=this.$tip[0].parentNode)||11===d.nodeType)},update:function(){this.visible()?this.show(!0):this.enter()},show:function(s){function j(u){var t;switch(u.charAt(0)){case"n":t={top:l.top+l.height+i,left:l.left+l.width/2-e/2};break;case"s":t={top:l.top-g-i,left:l.left+l.width/2-e/2};break;case"e":t={top:l.top+l.height/2-g/2,left:l.left-e-i};break;case"w":t={top:l.top+l.height/2-g/2,left:l.left+l.width+i}}2===u.length&&(t.left="w"==u.charAt(1)?k?l.left+l.width+i:l.left+l.width/2-15:k?l.left-e-i:l.left+l.width/2-e+15);return t}if("in"!==this.hoverState){var o=this.getTitle();if(this.enabled&&o){var f=this.tip();f.find(".tipsy-inner")[this.options.html?"html":"text"](o);f[0].className="tipsy";s||f.remove();var p=f[0].parentNode;p&&11!==p.nodeType||f.css({top:0,left:0,visibility:"hidden",display:"block"}).appendTo(document.body);var l=c.extend({},this.$element.offset());if(this.$element[0].nearestViewportElement){var m=this.$element[0].getBoundingClientRect();l.width=m.width;l.height=m.height}else{l.width=this.$element[0].offsetWidth||0;l.height=this.$element[0].offsetHeight||0}var i=this.options.offset,k=this.options.useCorners,h=this.options.arrowVisible,e=f[0].offsetWidth,g=f[0].offsetHeight;h||(i-=4);var q="function"==typeof this.options.gravity?this.options.gravity.call(this.$element[0],{width:e,height:g},j):this.options.gravity,n=j(q);f.css(n).addClass("tipsy-"+q+(k&&q.length>1?q.charAt(1):""));if(h){var r=k&&2===q.length;f.find(".tipsy-arrow")[r?"hide":"show"]()}var d=this.options.fade&&(!s||!this._prevGravity||this._prevGravity!==q);d?f.stop().css({opacity:0,display:"block",visibility:"visible"}).animate({opacity:this.options.opacity}):f.css({visibility:"visible",opacity:this.options.opacity});this._prevGravity=q;this.hoverState=null}else{this.hoverState=null;this.hide()}}},hide:function(){this.options.fade?this.tip().stop().fadeOut(function(){c(this).remove()}):this.$tip&&this.tip().remove();this.hoverState=null},setTitle:function(d){d=null==d?"":""+d;this.$element.attr("original-title",d).removeAttr("title")},getTitle:function(){var f,d=this.$element,e=this.options;a(d);"string"==typeof e.title?f=d.attr("title"==e.title?"original-title":e.title):"function"==typeof e.title&&(f=e.title.call(d[0]));f=(""+f).replace(/(^\s*|\s*$)/,"");return f||e.fallback},tip:function(){if(!this.$tip){this.$tip=c('<div class="tipsy"></div>');this.$tip.html(this.options.arrowVisible?'<div class="tipsy-arrow"></div><div class="tipsy-inner"/></div>':'<div class="tipsy-inner"/></div>');this.$tip.remove()}return this.$tip},validate:function(){var d=this.$element[0].parentNode;if(!d||11===d.nodeType){this.hide();this.$element=null;this.options=null}},enable:function(){this.enabled=!0},disable:function(){this.enabled=!1},toggleEnabled:function(){this.enabled=!this.enabled}};c.fn.tipsy=function(i,d){function h(l){var m=c.data(l,"tipsy");if(!m){m=new b(l,c.fn.tipsy.elementOptions(l,i));c.data(l,"tipsy",m)}return m}function k(){h(this).enter()}function g(){h(this).leave()}if(i===!0){return this.data("tipsy")}if("string"==typeof i){return this.data("tipsy")[i](d)}i=c.extend({},c.fn.tipsy.defaults,i);null==i.arrowVisible&&(i.arrowVisible=!i.useCorners);i.live||this.each(function(){h(this)});if("manual"!=i.trigger){var e=i.live?"live":"bind",j="hover"==i.trigger?"mouseenter":"focus",f="hover"==i.trigger?"mouseleave":"blur";this[e](j,k)[e](f,g)}return this};c.fn.tipsy.defaults={delayIn:0,delayOut:0,fade:!1,fallback:"",gravity:"n",html:!1,live:!1,offset:0,opacity:0.8,title:"title",trigger:"hover",useCorners:!1,arrowVisible:null};c.fn.tipsy.elementOptions=function(e,d){return c.metadata?c.extend({},d,c(e).metadata()):d};c.fn.tipsy.autoNS=function(){return c(this).offset().top>c(document).scrollTop()+c(window).height()/2?"s":"n"};c.fn.tipsy.autoWE=function(){return c(this).offset().left>c(document).scrollLeft()+c(window).width()/2?"e":"w"}}(jQuery);