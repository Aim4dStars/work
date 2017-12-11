/**
 * 
 */
org.bt.modules.maintaiArrangementRelationship = ( function ( $, window, document ) {

 $.duplicate = function(){
        var body = $('body');
        body.off('duplicate');
        var templates = {};
        var settings = {};
       
       init();
    
      };
      var init = function(){
    	  var counter=1;
      	var body = $('form');
          body.off('duplicate');
          var templates = {};
          var settings = {};
        $('[data-duplicate]').each(function(){
          var name = $(this).data('duplicate');
          var template = $('<tr>').html( $(this).clone(true) ).html();
          var options = {};
          var min = +$(this).data('duplicate-min');
          options.minimum = isNaN(min) ? 1 : min;
          options.maximum = +$(this).data('duplicate-max') || Infinity;
          options.parent = $(this).parent();

          settings[name] = options;
          templates[name] = template;
        });
        
        body.on('click.duplicate', '[data-duplicate-add]', add);
        body.on('click.duplicate', '[data-duplicate-remove]', remove);
        
        function add(){
      
          var targetName = $(this).data('duplicate-add');
          var selector = $('[data-duplicate=' + targetName + ']');
          var target = $(selector).last();
          if(!target.length) target = $(settings[targetName].parent);
          var newElement = $(templates[targetName]).clone(true);
          var drpElement= newElement.find('select');
          drpElement.attr('id','drp_' + counter);
          var drpId=drpElement.attr('id');
         
         drpElement.css('display','block');
         //drpElement.css('position','absolute');
         drpElement.addClass('selectToggle dk_input inputStyleFive ');
          if($(selector).length >= settings[targetName].maximum) {
            $(this).trigger('duplicate.error');
            return;
          }
          target.after(newElement);
          $(this).trigger('duplicate.add');
     	 counter++;
        }
        
        function remove(){
      	 
          var targetName = $(this).data('duplicate-remove');
          var selector = '[data-duplicate=' + targetName + ']';
          var target = $(this).closest(selector);
          if(!target.length) target = $(this).siblings(selector).eq(0);
          if(!target.length) target = $(selector).last();
          
          if($(selector).length <= settings[targetName].minimum) {
            $(this).trigger('duplicate.error');
            return;
          }
          target.remove();
          $(this).trigger('duplicate.remove');
     	 counter--;
        }
      };
      
      $.duplicate();   
      
} )( jQuery, window, document );