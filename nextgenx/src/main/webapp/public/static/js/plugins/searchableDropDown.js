/*
 * Copyright (c) 2009 Tom Coote (http://www.tomcoote.co.uk)
 * This is licensed under GPL (http://www.opensource.org/licenses/gpl-license.php) licenses.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *
 */
/**
 * Done some modifications to match the business requirements
 */

/**
 * @fileOverview searchableDropDown jQuery plugin
 * @version 1.00
 * @author Tom Coote http://www.tomcoote.co.uk
 * @requires jquery
 * @example
 $('#form').searchableDropDown({searchData:'payeeList',onSelect:function(obj,item){
            _onSelect(obj,item);
        },formatter:function(data){
            return _formatPayeeData(data);
        },
            onError:function(){
                _onError();
            },
            filters:null
        })
 */

/**
 * See <a href="http://jquery.com">http://jquery.com</a>.
 * @name $
 * @class
 * See the jQuery Library  (<a href="http://jquery.com">http://jquery.com</a>) for full details.  This just
 * documents the function and classes that are added to jQuery by this plug-in.
 */

/**
 * See <a href="http://jquery.com">http://jquery.com</a>
 * @name fn
 * @class
 * See the jQuery Library  (<a href="http://jquery.com">http://jquery.com</a>) for full details.  This just
 * documents the function and classes that are added to jQuery by this plug-in.
 * @memberOf $
 */
/**
 * @namespace $.fn.searchableDropDown
 */
(function($){

    $.fn.searchableDropDown = function(settings) {
        var defaults = {
            errorMessageClass	:['jq-inputError','formFieldMessageError'],
            errorClass			:'textInputError',
            minCharacters       : 1,
            maxResults          : undefined,
            wildCard            : "",
            caseSensitive       : false,
            setDefaultValue     : false,
            notCharacter        : "!",
            maxHeight           : 350,
            highlightMatches    : true,
            onSelect            :undefined,
            ajaxResults         :false,
            width               :undefined,
            autoHide            :true,
            hideOnWindowClick   :false,
            filters             :[{label:'All',value:'*',clazz:'firstToggle'},{label:'Pay anyone',value:'PAY_ANYONE',clazz:'middleToggle'},{label:'BPAY',value:'BPAY',clazz:'lastToggle'}],
            match               :['displayName','codeRaw','refRaw'],
            formatter           :function(d){},
            onError             :function(){},
            onInputClear        :function(){},
            searchData          :{}
        };
        settings = $.extend(defaults, settings);
        var timeoutId = null;

        return this.each(function() {
            var obj = $(this),
                wildCardPatt = new RegExp(regexEscape(settings.wildCard || ''),'g'),
                timeStamp = new Date().getTime(),
                uniqueId='searchable-'+timeStamp+'',
                listId  = 'searchable-list-'+timeStamp+'',
                results = $('<ul />',{'id':listId}),
                parent  = obj.parents('span:first'),
                currentSelection, pageX, pageY;

            parent.attr({'id':uniqueId,'class':'inputWrapper iconWrapper jq-searchableDropDownWrap'});

            var clearBtn    = parent.find('.jq-textClear'),
                showAllBtn  = parent.find('.jq-searchableDropDownShowAllButton'),
                liveRegion  = $( '<span>', {
                    'role':'status',
                    'aria-live': 'polite'
                }).addClass( 'ui-helper-hidden-accessible' ).insertAfter( obj );

            clearBtn.addClass('jq-preventAutoHide').attr({'title':'Clear selection','href':'#nogo'});

            //check plugin is disabled

            if(obj.hasClass('jq-disabled')){
                obj.attr('disabled','disabled').addClass('textInputDisabled').removeAttr('placeholder');
                showAllBtn.addClass('disabled').attr('href','#nogo');
                clearBtn.hide();
                return false;
            }

            /**
             * hide on window click
             */
            if(settings.hideOnWindowClick){
                $(document).click(function(e){
                    var target  = $(e.target);
                    if(target.parents().filter('.jq-searchableDropDownWrap, .jq-filterContainer').length === 0){
                        fadeOutResults();
                    }
                });
            }

            /**
             * set ARIA tags
             */
            obj.attr({'aria-required':'true','role':'combobox','aria-autocomplete':'inline','aria-owns':listId});
            showAllBtn.attr({'aria-controls':listId});

            //remove submit value on keyup
            obj.bind('change', function(){
                var $element 			= $(this),
                    submitValue				= (typeof $element.attr('data-submit-value') !== "undefined" && $element.attr('data-submit-value') !== false) ? $element.attr('data-submit-value'): false;

                if(submitValue){
                    $element.attr('data-submit-value','NULL').removeClass(settings.errorClass);
                }
                //remove message
                $.removeMessage(obj,settings['errorMessageClass'],settings['errorClass'])
            });

            //on focus
            obj.bind('focusin', function(event){
                if(obj.val() === obj.attr('placeholder')){ //remove placeholder
                    obj.val('');
                } else {
                    if($.trim(obj.val()) !== ''){
                        clearBtn.show();
                    }
                }
                var applyFilter = ($.trim(obj.val()) !== ''),
                    submitValue = obj.attr('data-submit-value'),
                    searchKey   = (submitValue !== 'NULL') ? $.trim(obj.val().split('-')[0]) : undefined;
                runSuggest.apply(obj[0], [event,applyFilter,undefined,searchKey]);
            });

            //on enter
            obj.keyup(function(event) {
                if ( event.which === 13 ) {
                    obj.siblings('ul.jq-selectSearch').find('li.resultItem:first').trigger('click');
                    event.preventDefault();
                } else if (event.which === 8 || event.which === 46){
                    settings.onInputClear();
                } else {
                    if($.trim(obj.val()) !== ''){
                        clearBtn.show();
                    } else {
                        clearBtn.hide();
                    }
                }
            });

            //on clear button click
            clearBtn.click(function(e){
                e.preventDefault();
                clearBtn.hide();
                obj.val('');
                obj.attr('data-submit-value','NULL');
                obj.trigger('focus');
            });

            obj.addClass('jq-preventAutoHide');
            showAllBtn.addClass('jq-preventAutoHide').children().addClass('jq-preventAutoHide');

            function regexEscape(txt, omit) {
                var specials = ['/', '.', '*', '+', '?', '|',
                    '(', ')', '[', ']', '{', '}', '\\'];

                if (omit) {
                    for (var i=0; i < specials.length; i++) {
                        if (specials[i] === omit) { specials.splice(i,1); }
                    }
                }

                var escapePatt = new RegExp('(\\' + specials.join('|\\') + ')', 'g');
                return txt.replace(escapePatt, '\\$1');
            }


            //bind filter events
            results.on("click", "a.jq-filterButton", function(e){
                e.preventDefault();
                if(obj.val() === obj.attr('placeholder')){ //remove placeholder
                    obj.val('');
                }
                var type = $(this).attr('data-type'),
                    submitValue = obj.attr('data-submit-value'),
                    searchKey   = (submitValue !== 'NULL') ? $.trim(obj.val().split('-')[0]) : undefined;

                runSuggest.apply(obj[0], [e,true,type,searchKey]);

            });

            //bind show all event to element
            showAllBtn.click(function(e){
                e.preventDefault();
                if($(results).is(':visible')){
                    fadeOutResults();
                } else {
                    if(obj.val() === obj.attr('placeholder')){ //remove placeholder
                        obj.val('');
                    }
                    var applyFilter = ($.trim(obj.val()) !== ''),
                        submitValue = obj.attr('data-submit-value'),
                        searchKey   = (submitValue !== 'NULL') ? $.trim(obj.val().split('-')[0]) : undefined;
                    runSuggest.apply(obj[0], [e,applyFilter,undefined,searchKey]);
                }
            });
            //bind auto hide list on mouseleave
            function autoHideList(){
                //on mouse leave
                $('.jq-preventAutoHide').bind('mouseenter', function (e) {
                    clearTimeout(timeoutId);
                    timeoutId = null;
                })
                    .bind('mouseleave', function (e) {
                        timeoutId = setTimeout(function(){
                            if(timeoutId !== null){
                                var placeholder = obj.attr('placeholder'),
                                    value = obj.val();
                                //check palceholder value before call
                                if(placeholder !== value){
                                    fadeOutResults();
                                }
                            }
                        }, 300);
                    });
            }
            // When an item has been selected then update the input box,
            // hide the results again and if set, call the onSelect function
            function selectResultItem(item) {
                obj.attr('data-submit-value',item.id).removeClass(settings.errorClass);
                obj.removeClass('placeholder');
                $.removeMessage(obj,settings['errorMessageClass'],settings['errorClass']);
                fadeOutResults();

                if (typeof settings.onSelect === 'function') {
                    settings.onSelect(obj,item);
                }
                clearBtn.show();
            }

            // Used to get rid of the hover class on all result item elements in the
            // current set of results and add it only to the given element. We also
            // need to set the current selection to the given element here.
            function setHoverClass(el) {
                if(!$(el).hasClass('filterContainer')){
                    $('li.resultItem', results).removeClass('resultItemHover');
                    $(el).addClass('resultItemHover');

                    currentSelection = el;
                }
            }
            function fadeOutResults(){
                //return false;
                $(results).fadeOut(400,function(){
                    $(results).html('').attr({'aria-expanded':'false','aria-hidden':'true'});
                });

                //do validation
                validateInput();
            }
            function validateInput(){
                var submitValue = obj.attr('data-submit-value'),
                    inputValue = obj.val();
                if (submitValue === 'NULL'){
                    if($.trim(inputValue) === '' || inputValue === obj.attr('placeholder')){
                        obj.trigger('focusout'); //display place holder
                        promptMessage(obj,'required');
                    } else {
                        promptMessage(obj,'custom');
                    }
                    settings.onError();
                } else {
                    $.removeMessage(obj,settings['errorMessageClass'],settings['errorClass'])
                }
            }

            /**
             * promptMessage private function
             * @param $element
             * @param rule
             */
            function promptMessage($element,rule){
                $.promptMessage($element,rule,settings['errorMessageClass'],settings['errorClass'],settings['genericErrorMessage'])
            }
            // Build the results HTML based on an array of objects that matched
            // the search criteria, highlight the matches if feature is turned on in
            // the settings.
            function buildResults(resultObjects, sFilterTxt, type) {
                sFilterTxt = (sFilterTxt !== null && sFilterTxt !== '.*') ? "(" + sFilterTxt + ")" : null;

                var bOddRow = true, i, iFound = 0,filterElements = [],
                    filterPatt = (sFilterTxt !== null) ? (settings.caseSensitive) ? new RegExp(sFilterTxt, "g") : new RegExp(sFilterTxt, "ig") : null;

                $(results).html('').hide().addClass('jq-preventAutoHide');

                //add filters
                if($.isArray(settings.filters)){
                    filterElements.push(
                        '<li class="filterContainer">',
                        '<ul class="toggleWidget">'
                    );
                    $.each(settings.filters,function(k,v){
                        filterElements.push(
                            '<li class="'+ v.clazz+'Container jq-filterContainer">',
                            '<a class="'+ v.clazz+'Button jq-filterButton" href="#" data-type="'+ v.value+'">',
                            v.label,
                            '</a>',
                            '</li>'
                        );
                    });
                    filterElements.push(
                        '</ul>',
                        '</li>'
                    );
                    $(results).append(filterElements.join(''));
                }

                if(type){
                    $(results).find('a[data-type="'+type+'"]').addClass('activeSecondaryButton');
                } else {
                    $(results).find('.allResults').addClass('activeSecondaryButton filterButtonSelected');
                }
                for (i = 0; i < resultObjects.length; i += 1) {
                    var item	    = $('<li />',{'role':'option'}),
                        name	    = resultObjects[i].displayName,
                        type	    = resultObjects[i].type,
                        code	    = resultObjects[i].code,
                        ref		    = resultObjects[i].ref,
                        refRaw      = resultObjects[i].refRaw,
                        payeeType	= resultObjects[i].payeeType,
                        primary 	= resultObjects[i].primary,
                        noRef      = (resultObjects[i].crnType === 'ICRN' || resultObjects[i].crnType === 'VCRN'),
                        itemHtml    = [];

                    if (filterPatt !== null && settings.highlightMatches === true) {
                        name = name.replace(filterPatt, "<span class='emphasis'>$1</span>");
                        code = code.replace(filterPatt, "<span class='emphasis'>$1</span>");
                        ref = ref.replace(filterPatt, "<span class='emphasis'>$1</span>");
                    }

                    itemHtml.push('<dl class="accontSelectContainer clearFix">');
                    itemHtml.push('<dt class="accountDef">');


                    if (primary === true){
                    	itemHtml.push('<em class="iconbest iconbestMod1"><span class="Icon Nominated Account"></span></em>');
                    }
                    
                    itemHtml.push('<span class="emphasis jq-name">'+name+'</span>');
                    
                    itemHtml.push('</dt>');
                    itemHtml.push('<dd class="accountIcon floatRight">');
                    if (payeeType.toUpperCase() === "LINKED"){
                    	itemHtml.push('<em class="iconmoneyin positive"><span>Icon Money In</span></em>');
                    }
                    if (payeeType.toUpperCase() !== "BPAY"){
                    	itemHtml.push('<em class="iconmoneyout"><span>Icon Money out</span></em>');
                    }else{
                    	itemHtml.push('<em class="iconbpay iconbpayMod1"><span>Icon BPAY</span></em>');
                    }
                    itemHtml.push('</dd>');
                    itemHtml.push('<dd class="accountLabel code clearBoth">'+code+'</dd>');

                    if(!noRef){
                        itemHtml.push(
                            '<dt class="accountLabel ref">',
                            ref,
                            '</dt>'
                        );
                    }
                    itemHtml.push('</dl>');

                    $(item).append(itemHtml.join(' '));

                    $(item).addClass('resultItem').
                        addClass((bOddRow) ? 'odd' : 'even').
                        click(function(n) { return function() {
                        selectResultItem(resultObjects[n]);
                    };}(i)).
                        mouseover(function(el) { return function() {
                        setHoverClass(el);
                    };}(item));

                    $(results).append(item);

                    bOddRow = !bOddRow;

                    iFound += 1;
                    if (typeof settings.maxResults === 'number' && iFound >= settings.maxResults) {
                        break;
                    }
                }
                //if there is no result display a message
                if(resultObjects.length === 0){
                    $(results).append([
                        '<li>',
                        '<h5 class="selectDataMessage">',
                        '<p>No results found</p>',
                        '</h5>',
                        '</li>'
                    ].join(''));
                }
                //bind auto hide list
                if(settings.autoHide){
                    autoHideList();
                }
                if ($(results).children().length > 0) {
                    currentSelection = undefined;
                    $(results).show().css('height', 'auto');
                    $(results).attr({'aria-expanded':'true','aria-hidden':'false'});

                    if ($(results).height() > settings.maxHeight) {
                        $(results).css({'overflow': 'auto', 'height': settings.maxHeight + 'px'});
                    }
                }
            }

            // Prepare the search string based on the settings for this plugin,
            // run it against each item in the searchData and display any
            // results on the page allowing selection by the user.
            function runSuggest(e,filter,type,searchKey) {
                filter      = (typeof filter === 'undefined') ? true : filter;
                type        = (typeof type === 'undefined') ? false : type;
                searchKey   = (typeof searchKey === 'undefined') ? false : searchKey;


                var value   = (searchKey) ? searchKey : this.value,
                    searchData;

                if (filter && value.length < settings.minCharacters) {
                    if(!type){
                        $(results).html('').hide();
                        return false;
                    }
                }

                //call data formatter
                searchData = settings.formatter(org.bt.collections[settings.searchData]);

                var resultObjects = [],
                    sFilterTxt = (!settings.wildCard) ? regexEscape(value) : regexEscape(value, settings.wildCard).replace(wildCardPatt, '.*'),
                    bMatch = true,
                    filterPatt, i;
                if (settings.notCharacter && sFilterTxt.indexOf(settings.notCharacter) === 0) {
                    sFilterTxt = sFilterTxt.substr(settings.notCharacter.length,sFilterTxt.length);
                    if (sFilterTxt.length > 0) { bMatch = false; }
                }
                sFilterTxt = sFilterTxt || '.*';
                sFilterTxt = settings.wildCard ? '^' + sFilterTxt : sFilterTxt;
                filterPatt = settings.caseSensitive ? new RegExp(sFilterTxt) : new RegExp(sFilterTxt,"i");
                sFilterTxt = (filter) ? sFilterTxt : null;


                // Look for the required match against each single search data item. When the not
                // character is used we are looking for a false match.
                for (i = 0; i < searchData.length; i += 1) {
                    if(filter){
                        $.each(settings['match'],function(k,v){
                            //check matched object already in the array
                            var result = $.grep(resultObjects, function(e){ return e.id == searchData[i].id; });
                            if(result.length === 0){
                                if (filterPatt.test(searchData[i][v]) === bMatch){
                                    //search matches! now checking the type
                                    if(type && type !== '*'){
                                        //filter by type
                                        if(searchData[i].typeRaw == type){
                                            resultObjects.push(searchData[i]);
                                        }
                                    } else {
                                        resultObjects.push(searchData[i]);
                                    }
                                }
                            }
                        });
                    } else {
                        //filter not required
                        resultObjects.push(searchData[i]);
                    }

                }

                buildResults(resultObjects, sFilterTxt,type);
                updateScreenReaderText(resultObjects);
            }

            function updateScreenReaderText(result){
                var message = result.length > 0 ? result.length + ( result.length > 1 ? ' results are' : ' result is' ) +
                    ' available, use up and down arrow keys to navigate.' : 'No search results';
                liveRegion.html(message);
            }

            function onKeyDown(e){
                switch (e.which) {
                    case 13:
                        obj.parents('form:first').addClass('jq-preventSubmit');
                        break;
                }
            }
            // To call specific actions based on the keys pressed in the input
            // box. Special keys are up, down and return. All other keys
            // act as normal.
            function keyListener(e) {
                switch (e.keyCode) {
                    case 13: // return key
                        $(currentSelection).trigger('click');
                        obj.parents('form:first').removeClass('jq-preventSubmit');
                        return false;
                    case 40: // down key
                        if (typeof currentSelection === 'undefined') {
                            currentSelection = $('.resultItem:first', results).get(0);
                        }
                        else {
                            currentSelection = $(currentSelection).next().get(0);
                        }

                        setHoverClass(currentSelection);
                        if (currentSelection) {
                            $(results).scrollTop(currentSelection.offsetTop);
                            liveRegion.html('press enter to select '+ $(currentSelection).find('.jq-name').text());
                        }
                        return false;
                    case 38: // up key
                        if (typeof currentSelection === 'undefined') {
                            currentSelection = $('.resultItem:last', results).get(0);
                        }
                        else {
                            currentSelection = $(currentSelection).prev().get(0);
                        }

                        setHoverClass(currentSelection);
                        if (currentSelection) {
                            $(results).scrollTop(currentSelection.offsetTop);
                            liveRegion.html('press enter to select '+ $(currentSelection).find('.jq-name').text());
                        }
                        return false;
                    case 9:
                        //skip tab
                        break;
                    default:
                        if(obj.val() === obj.attr('placeholder')){ //remove placeholder
                            obj.val('');
                        }
                        var applyFilter = ($.trim(obj.val()) !== ''),
                            submitValue = obj.attr('data-submit-value'),
                            searchKey   = (submitValue !== 'NULL') ? $.trim(obj.val().split('-')[0]) : undefined;
                        runSuggest.apply(obj[0], [e,applyFilter,undefined,searchKey]);
                    //runSuggest.apply(this, [e]);
                }
            }

            // Prepare the input box to show suggest results by adding in the events
            // that will initiate the search and placing the element on the page
            // that will show the results.
            $(results).addClass('jq-selectSearch selectSearch inputStyleSix').hide();

            obj.after(results).
                keyup(keyListener).
                keydown(onKeyDown).
                blur(function(e) {
                    // We need to make sure we don't hide the result set
                    // if the input blur event is called because of clicking on
                    // a result item.
                    var resPos = $(results).offset();
                    resPos.bottom = resPos.top + $(results).height();
                    resPos.right = resPos.left + $(results).width();

                    if (pageY < resPos.top || pageY > resPos.bottom || pageX < resPos.left || pageX > resPos.right) {
                        $(results).hide();
                    }
                }).
                focus(function(e) {
                    $(results).css({
                        //'top': (obj.position().top + obj.height() + 5) + 'px',
                        //'left': obj.position().left + 'px'
                    });

                    if ($(results).filter('li').length > 0) {
                        $(results).show();
                    }
                }).
                attr('autocomplete', 'off');
            $().mousemove(function(e) {
                pageX = e.pageX;
                pageY = e.pageY;
            });

            // Opera doesn't seem to assign a keyCode for the down
            // key on the keyup event. why?
            if ($.browser.opera) {
                obj.keydown(function(e) {
                    if (e.keyCode === 40) { // up key
                        return keyListener(e);
                    }
                });
            }

            // Escape the not character if present so that it doesn't act in the regular expression
            settings.notCharacter = regexEscape(settings.notCharacter || '');

        });
    };

})(jQuery);
/**
 * usage
 * $('input[name="pay"]').searchableDropDown(org.bt.collections.payList, {onSelect:function(item){
		$('input[name="pay"]').val(item.name);

	}});
 */
