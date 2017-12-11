/**
 * @namespace org.bt.modules.serviceOperator
 * @desc Service operator
 */
org.bt.modules.maintaiArrangementRelationship = (function($, window, document) {

	var _DOMElements = {
		addressType : '#addressType'
	}
	$(_DOMElements.addressType).dropkick({
		inputClasses : [ 'inputStyleEight' ],
		change : function(value, label) {

		}
	});

	$("#address").keyup(function(){
		var d = new Date();
		var n = d.getTime();
    	var objData = {
    			data:$("#address").val(),
    			productKey:"NG-001"
  		      }
    	
    	$("#address").autocomplete({
		      source:function(request, response){
		    	  
		    		      var xmlhttp;;
		    		      if (window.XMLHttpRequest) {
		    		         xmlhttp = new XMLHttpRequest();
		    		      } else {
		    		         xmlhttp = new ActiveXObject("Microsoft.XMLHTTP");
		    		      }
		    		      xmlhttp.onreadystatechange = function () {
		    		         if (this.readyState == 4 && this.status == 200 ) {
		    		            var res = JSON.parse(this.responseText);
		    		            if(!res.HasErrorMessage){
				              		response($.map(res.Items, function(item) {
				                        return {
				                        	label: item.DisplayText,
				                        	value: item.AddressIdentifier
				                        };
				                    }));
				              	}
		    		         }
		    		      }

		    		 xmlhttp.open("POST", addressQasApi + "?" + n, true);
				xmlhttp.setRequestHeader("Content-Type", "application/json");
				xmlhttp.send(JSON.stringify(objData));
		      },
		      select: function (a, b) {
		         $("#addressKey").val(b.item.value);
		         document.getElementById('address').value = b.item.label
		         return false;
		      }
		});
	});

})(jQuery, window, document);
