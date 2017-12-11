$(window).on("load", function() {
	data = {
		"key" : $("#key").html(),
		"cssftoken" : $("#token").val()
	};
	$.ajax({
		type : "POST",
		url : "siloMovementResponse",
		data : data,
		success : function(data, textStatus, jQxhr) {
			switch (data.errState) {
			case 'MNT256_CRT':
				$("#service258").addClass("icon-notification-success");
				$("#service324").addClass("icon-notification-success");
				$("#service336").addClass("icon-notification-success");
				$("#service325").addClass("icon-notification-success");
				$("#service256_CRT").addClass("icon-notification-fail");
				$("#service256_END").addClass("icon-notification-fail");
				break;
			case 'MNT256_END':
				$("#service258").addClass("icon-notification-success");
				$("#service324").addClass("icon-notification-success");
				$("#service336").addClass("icon-notification-success");
				$("#service325").addClass("icon-notification-success");
				$("#service256_CRT").addClass("icon-notification-success");
				$("#service256_END").addClass("icon-notification-fail");
				break;
			case "MNT325":
				$("#service258").addClass("icon-notification-success");
				$("#service324").addClass("icon-notification-success");
				$("#service336").addClass("icon-notification-success");
				$("#service325").addClass("icon-notification-fail");
				$("#service256_CRT").addClass("icon-notification-fail");
				$("#service256_END").addClass("icon-notification-fail");
				break;
			case "MNT336":
				$("#service258").addClass("icon-notification-success");
				$("#service324").addClass("icon-notification-success");
				$("#service336").addClass("icon-notification-fail");
				$("#service325").addClass("icon-notification-fail");
				$("#service256_CRT").addClass("icon-notification-fail");
				$("#service256_END").addClass("icon-notification-fail");
				break;
			case "RTRDET":
				$("#service258").addClass("icon-notification-fail");
				$("#service324").addClass("icon-notification-fail");
				$("#service336").addClass("icon-notification-fail");
				$("#service325").addClass("icon-notification-fail");
				$("#service256_CRT").addClass("icon-notification-fail");
				$("#service256_END").addClass("icon-notification-fail");
				break;
			default: {
				$("#service258").addClass("icon-notification-success");
				$("#service324").addClass("icon-notification-success");
				$("#service336").addClass("icon-notification-success");
				$("#service325").addClass("icon-notification-success");
				$("#service256_CRT").addClass("icon-notification-success");
				$("#service256_END").addClass("icon-notification-success");
				break;
			}
			}
		},
		error : function(jqXhr, textStatus, errorThrown) {
			// icon-notification-fail
			console.log(errorThrown);
		}
	});

});