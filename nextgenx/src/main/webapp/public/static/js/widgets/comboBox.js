/*
 *  Combobox jQuery UI widget
 *	written by Samitha Fernando
 *	Built for jQuery UI library
 *  http://jqueryui.com
 *  extended default jQuery auto-complete
 *  Combobox supports both static and dynamic SELECT
 */
(function($,document) {
    $.widget("ui.combobox", {
        tmplCache : {},
        options: {
            class: "combo-theme-1",
            collection:null,
            mapping:null,
            template:null,
            default:false,
            appendTo: null,
            search:null
        },
        _getTemplate: function(templateId){
            var self = this,
                template = self.tmplCache[templateId];
            if(!template){
                self.tmplCache[templateId] = document.getElementById(templateId).innerHTML;
            }
            return self.tmplCache[templateId];
        },
        _createView: function(tmpl,data){
            for(var prop in data){
                tmpl = tmpl.replace(new RegExp('{'+prop+'}','g'), data[prop]);
            }
            return tmpl;
        },
        _addOptions: function(){
            //get the data source from collection
            var self        = this,
                o           = self.options,
                collection  = o.collection,
                defaultIndex= -1,
                item;

            if(collection){
                for (var i = 0; i < collection.length; i++) {
                    item    = collection[i];
                    self._addOption(item);
                    if(item.selected){
                        defaultIndex = i;
                    }
                }
                //create a empty option
                if(defaultIndex < 0){
                    self._addOption.call(self,{selected:true});
                }
            }
        },
        _addOption: function(item){
            var self    = this,
                mapping = self.options.mapping,
                text    = item[mapping.text],
                option  = new Option(text ? $.trim(text) : '', item[mapping.value] || '');

            $(option).html(text ? $.trim(text) : '').data('combo.item',item);
            //add selected attribute
            if(item.selected){
                $(option).attr('selected','selected');
            }
            self.element.append(option);
        },
        _matcherRegExp: function(txt){
            //Was new RegExp("^" + $.ui.autocomplete.escapeRegex($(this).val()) + "$", "i") and removed $ since we don't need exact match also ^ since we match entire string rather than start with,
            return new RegExp($.ui.autocomplete.escapeRegex(txt), "i");
        },
        _match: function(option,term){
            var self    = this,
                o       = this.options,
                matcher = self._matcherRegExp(term),
                value   = option.value,
                $option = $(option),
                item    = $option.data('combo.item'),
                text    = $option.text().escapeHtml(),
                matched = false;
            if(value){
                //check search is defined
                if(o.search){
                    for (var i=0; i<o.search.length; i++){
                        text = item[o.search[i]];
                        if(!term || matcher.test(text)){
                            matched = true;
                            break;
                        }
                    }
                } else {
                    if(!term || matcher.test(text)){
                        matched = true;
                    }
                }
            }
            return matched;
        },
        _create: function() {
            //create options
            this._addOptions();

            var self    = this,
                o       = this.options,
                select  = this.element.hide(),
                selected= select.children(":selected"),
                value   = selected.val() ? selected.text() : "",
                id      = 'ui-combo-' + new Date().getTime(),
                wrap    = this.wrap = $("<div>",{
                    'class':'ui-combo '+ o.class,
                    'id':id
                });

            select.wrap(wrap);

            var input = this.input = $("<input>").insertAfter(select).val(value).autocomplete({
                appendTo: o.appendTo || '#'+id,
                delay: 0,
                minLength: 0,
                source: function(request, response) {
                    response(select.children("option").map(function() {
                            var text       = $(this).text().escapeHtml(),
                                $option    = $(this),
                                item       = $option.data('combo.item'),
                                essential  = {option:this,value:text},
                                result     = {};
                            if (self._match(this,request.term)) {
                                if(o.search){
                                    for (var i=0; i<o.search.length; i++){
                                        result[o.search[i]] = self._highlight.call(item[o.search[i]],request.term);
                                    }
                                } else {
                                    result.label = self._highlight.call(text,request.term);
                                }
                            //return the match
                            return $.extend({}, item, result,essential);
                            }
                    }));
                },
                select: function(event, ui) {
                    ui.item.option.selected = true;
                    self._trigger("selected", event, {
                        item: ui.item.option
                    });
                },
                change: function(event, ui) {
                    if (!ui.item) {
                        var value   = $(this).val(),
                            matcher = self._matcherRegExp(value),
                            valid   = false;
                        select.children("option").each(function() {
                            var option = this,
                                text   = $(option).text();

                            if (value !== "" && self._match(option,value)) {
                                this.selected = valid = true;
                                //set input value
                                input.val(text);
                                //trigger selected event
                                self._trigger("selected", event, {
                                    item: option
                                });
                                return false;
                            }
                        });

                        if (!valid) {

                            // remove invalid value, as it didn't match anything
                            $(this).val("");
                            select.val("");
                            input.data("autocomplete").term = "";
                            //trigger callback with null value
                            self._trigger("selected", event, {
                                item: null
                            });
                            return false;
                        }
                    }
                }
            }).addClass("ui-widget ui-widget-content ui-corner-left");

            input.data("autocomplete")._renderItem = function(ul, item) {
                var $li = $("<li></li>").data("item.autocomplete", item),
                    html,tmpl;
                //custom template
                if(o.template){
                    tmpl = self._getTemplate(o.template);
                    html = self._createView(tmpl,item);
                } else {
                    //create default view
                    html = "<a>" + item.label + "</a>";
                }
                return $li.append(html).appendTo(ul);
            };

            this.button = $("<button type='button' class='ui-button ui-widget ui-state-default ui-button-icon-only ui-corner-right ui-button-icon ui-state-hover'><span class='ui-button-icon-primary ui-icon ui-icon-triangle-1-s'></span><span class='ui-button-text'>&nbsp;</span></button>").attr("tabIndex", -1).attr("title", "Show All Items").attr("role", "button").attr("aria-disabled", "false").insertAfter(input).button({
                icons: {
                    primary: "ui-icon-triangle-1-s"
                },
                text: false
            }).click(function() {
                    // close if already visible
                    if (input.autocomplete("widget").is(":visible")) {
                        input.autocomplete("close");
                        return;
                    }

                    // work around a bug (likely same cause as #5265)
                    $(this).blur();

                    // pass empty string as value to search for, displaying all results
                    input.autocomplete("search", "");
                    input.focus();
                });
        },
        _highlight: function(term){
            return this.replace(
                new RegExp("(?![^&;]+;)(?!<[^<>]*)(" + $.ui.autocomplete.escapeRegex(term) + ")(?![^<>]*>)(?![^&;]+;)", "gi"), "<strong>$1</strong>");
        },
        destroy: function() {
            this.input.remove();
            this.button.remove();
            this.element.show();
            $.Widget.prototype.destroy.call(this);
        },
        addOption: function(item){
            this._addOption(item);
        }
    });
})(jQuery,document);
/**
 * How to use combo box
 * @example
 * JavaScript
 *$('#combo1').combobox({
 *   collection:org.bt.collections.payeeList,
 *   mapping:{
 *            text:'name',
 *            value:'id'
 *           },
 *   appendTo: "body",
 *   selected:function(ev,data){
 *       }
 * HTML
 * <select id="combo1"></select>
 });
 */