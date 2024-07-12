// swal.fire('Good job!');

$('#cancel_order_header').click(function(event) {
	swal.fire({
	  title: 'Xác nhận hủy đơn hàng này?',
	  text: "",
	  type: 'error',
	  showCancelButton: true,
	  confirmButtonText: 'Xác nhận',
	  cancelButtonText: 'Quay lại'
	}).then((result) => {
	  if (result.value) {
	  	let id = $('#order_id').text();
	  	cancel_order(id);
	  }
	})
});

$('.btn_change_status').click(function(event) {
	let status = $(this).attr('status');
	swal.fire({
	  title: 'Xác nhận thay đổi trạng thái?',
	  text: "Trạng thái thay đổi thành: " + status,
	  type: 'info',
	  showCancelButton: true,
	  confirmButtonText: 'Xác nhận',
	  cancelButtonText: 'Quay lại'
	}).then((result) => {
	  if (result.value) {
		change_status_order(status);
	  }
	})
});

function change_status_order(status) {
	let id = $('#order_id').text();
	$.ajax({
		url: '/admin/orders/change-status?id='+id+'&status='+status,
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

$('.undo_action').click(function(event) {
	event.preventDefault();
	let undoStatus = $(this).attr('status');
	swal.fire({
	  title: 'Xác nhận quay lại trạng thái trước?',
	  text: "Trạng thái thay đổi thành: " + undoStatus,
	  type: 'warning',
	  showCancelButton: true,
	  confirmButtonText: 'Xác nhận',
	  cancelButtonText: 'Quay lại'
	}).then((result) => {
	  if (result.value) {
		change_status_order(undoStatus);
	  }
	})
});

function cancel_order(id) {
	$.ajax({
		url: '/admin/orders/cancel/' + id,
		type: 'POST',
		dataType: 'html',
		data: [
			{ id: id }
		]
	})
	.done(function(res) {
		// console.log(res);
		swal.fire(
	      'Đã hủy đơn hàng!',
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

function view_order_detail(id) {
	let url = '/admin/orders/detail/' + id;
	$.ajax({
		url: url,
		type: 'POST',
		dataType: 'html',
		data: [
			{ topshoe: 'topshoe' }
		]
	})
	.done(function(res) {
		$('#router-outlet').html(res);
		history.pushState('data', 'title', url);
	})
	.fail(function() {
		console.log("error");
	})
	.always(function() {

	});
}