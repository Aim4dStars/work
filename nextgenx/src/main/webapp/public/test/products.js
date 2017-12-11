QUnit.module("Products");

/**
*******************************
Can I get products
*******************************
**/
QUnit.test( "Product list works", function( assert ) {

   org.bt.utils.communicate.ajax2({
    url:"secure/api/v1_0/products",
    async:false,
    onSuccess:function(response, textStatus){
      assert.ok(_.isNull(response.error), "Got a response: " + JSON.stringify(response));
    },
    onError:function(jqXHR, textStatus, errorThrown){
      assert.ok(false, "Problem " + textStatus + " " + errorThrown);
    }
   });
   });