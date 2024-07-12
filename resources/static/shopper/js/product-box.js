$('.tt-btn-wishlist').prop("onclick", null).off("click");
onClickWishlist();

function onClickWishlist (argument) {
	$('.tt-btn-wishlist').click(function(event) {
		event.preventDefault();
		$(this).toggleClass('active');
		toggleWishlist($(this).siblings('.productBoxId').val());
	});
}

function toggleWishlist (productBoxId) {
	$.ajax({
		url: '/product-wishlist/' + productBoxId,
		type: 'POST',
		dataType: 'html',
		data: [
			{ topshoe: 'topshoe' }
		]
	})
	.done(function(res) {
		if (res == 'added') {
			toastr.options.positionClass = 'toast-bottom-right';
			toastr.success("Đã thêm vào danh sách yêu thích");
		} else if (res == 'removed') {
			toastr.options.positionClass = 'toast-bottom-right';
			toastr.info("Đã xóa khỏi danh sách yêu thích");
		}
	})
	.fail(function() {
		console.log("error");
	})
	.always(function(res) {

	});
}
