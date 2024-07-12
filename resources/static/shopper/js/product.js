$('ul.size_list li.size_number:eq(0)').addClass('active');

if ($('.productSizeId').length > 0) {
	let sizeId = $('.productSizeId:eq(0)').val();
	$('#addToCart').attr('href', '/add-to-cart-header/' + sizeId);

} else {
	
}

$('.size_number').click(function (e) { 
    e.preventDefault();
    let sizeId = $(this).children('.productSizeId').val();
    $('#addToCart').attr('href', '/add-to-cart-header/' + sizeId);
});

$('#addToCart').click(function(e) {
    e.preventDefault();
	let url = $(this).attr('href');
    $.ajax({
		url: '/signed-in',
		type: 'POST',
		dataType: 'html',
		data: [
			{ topshoe: 'topshoe' }
		]
	})
	.done(function(res) {
		if (res != '') {
			addToCart(url);
		} else {
			window.location.replace("/customer/login");
		}
	})
	.fail(function() {
		console.log("error");
	})
	.always(function() {
		preventSharp();
	});
});

function addToCart (url) {
	fixPositionTopRightToast();
	$.ajax({
		url: url,
		type: 'POST',
		dataType: 'html',
		data: [
			{ topshoe: 'topshoe' }
		]
	})
	.done(function (res) {
		if (!res) {
			getCartHeaederContent();
			toastr.success("Thêm vào giỏ hàng thành công!");
		} else {
			toastr.warning(res);
		}
	})
	.fail(function () {
		console.error("Error add to cart");
		toastr.error("Lỗi thêm giỏ hàng!");
	})
	.always(function () {

	});
}

function fixPositionTopRightToast() {
	if (!$('.tt-stuck-nav').hasClass('stuck')) {
		toastr.options.positionClass = 'toast-top90-right';
	} else {
		toastr.options.positionClass = 'toast-top50-right';
	}
}

setTimeout(function () {

	$.ajax({
		url: '/product-view/' + $('#productId').val(),
		type: 'POST',
		dataType: 'html',
		data: [
			{ topshoe: 'topshoe' }
		]
	})
	.done(function() {

	})
	.fail(function() {
		console.log("error");
	})
	.always(function() {

	});
	
}, 3000)

$('#toogle-wishlist').click(function(event) {
	event.preventDefault();

	let wishlistIcon = $(this).children('i');
	let wishlistText = $(this).children('span');
	if (wishlistIcon.attr('class') == 'icon-n-072') {
		wishlistIcon.attr('class', 'icon-h-13');
		wishlistText.text('Đã thích');
	} else if (wishlistIcon.attr('class') == 'icon-h-13'){
		wishlistIcon.attr('class', 'icon-n-072');
		wishlistText.text('Yêu thích');
	}
	toggleWishlist($('#productId').val());
});
