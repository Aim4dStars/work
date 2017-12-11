/**
 * BT Tooltip Customized
 * 
 * Removing the mouse events on the Jquery UI Tooltip and bind with the Click event
 * 
 */
(function( $ ) {
  $.widget( "custom.btToolTip", $.ui.tooltip, {
    options: {
      autoHide:true,
      content: function() {
          return $( this ).attr( "bttitle" );
      },
      items: "[bttitle]:not([disabled])",
    },

    _create: function() {
      this._super();
      if(!this.options.autoHide){
        this._off(this.element, "mouseover focusin");
      }
    },

    _open: function( event, target, content ) {
      this._superApply(arguments);

      if(!this.options.autoHide){
        this._off(this.element, "mouseleave focusout");
      }
    }
  });

}( jQuery ) );