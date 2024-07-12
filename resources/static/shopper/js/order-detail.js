
$('#cancel_order').click(function(event) {
	swal.fire({
	    title: 'Xác nhận hủy đơn hàng?',
	    text: "",
	    icon: 'error',
	    showCancelButton: true,
	    confirmButtonText: 'Xác nhận',
	    cancelButtonText: 'Quay lại'
	}).then((result) => {
	    if (result.value) {
	        swal.fire(
	            'Hủy đơn hàng thành công!',
	            '',
	            'success'
	        );
	    }
	})
});

function customer_cancel_order (argument) {
	$.ajax({
		url: '/admin/orders/change-status',
		type: 'POST',
		dataType: 'html',
		data: [
			{ id: id }
		]
	})
	.done(function(res) {
		// console.log(res);
		swal.fire(
	      'Thay đổi trạng thái thành công!',
	      '',
	      'success'
	    );
	    view_order_detail(id);
	})
	.fail(function() {
		console.log("error");
	})
	.always(function() {

	});
}