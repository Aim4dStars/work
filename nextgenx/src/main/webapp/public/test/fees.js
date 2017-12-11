QUnit.module("Fees");

/**
*******************************
Can I get products
*******************************
**/
QUnit.test( "Get yearly fees", function( assert ) {

   org.bt.utils.communicate.ajax2({
    url:"secure/api/v1_0/accounts/1D4E90038CB5B92BD6A324EEC2887393B8C7A9E0D241B84A/yearlyfees",
    async:false,
    onSuccess:function(response, textStatus){
      assert.ok(_.isNull(response.error), "Got a response: " + JSON.stringify(response));
    },
    onError:function(jqXHR, textStatus, errorThrown){
      assert.ok(false, "Problem " + textStatus + " " + errorThrown);
    }
   });
   });
