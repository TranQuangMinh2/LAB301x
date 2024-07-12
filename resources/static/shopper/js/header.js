if (typeof toastr  !== 'undefined') {
	toastr.options = {
	    "escapeHtml": true,
	    "closeButton": true,
	    "debug": false,
	    "newestOnTop": false,
	    "progressBar": true,
	    "positionClass": "toast-top-right",
	    "preventDuplicates": false,
	    "onclick": null,
	    "showDuration": "300",
	    "hideDuration": "1000",
	    "timeOut": "2500",
	    "extendedTimeOut": "1000",
	    "showEasing": "swing",
	    "hideEasing": "linear",
	    "showMethod": "slideDown",
	    "hideMethod": "slideUp"
	}
}

$.ajax({
	url: '/brands',
	type: 'get',
	dataType: 'html',
	data: [
		{ topshoe: 'topshoe' }
	]
})
.done(function(res) {
	if (res) {
		let brands = $.parseJSON(res);
		brands.forEach(item => {
			$('#header-dropdown').append(`<li><a href="/product-listing?brandId=${item.id}">${item.name}</a></li>`)
		})
	}
})
.fail(function() {
	console.log("error init brands js");
})
.always(function() {
	preventSharp();
});

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
		getCartHeaederContent();
		$('#menu_login').addClass('d-none');
		$('#menu_reg').addClass('d-none');
		$('#menu_username').text(res);

	} else {
		$('#menu_myaccount').addClass('d-none');
		$('#menu_logout').addClass('d-none');
		$('#menu_user').addClass('d-none');
		$('#menu_favor').addClass('d-none');
	}
})
.fail(function() {
	console.log("error init header js");
})
.always(function() {
	preventSharp();
});

function getCartHeaederContent() {
	$.ajax({
		url: '/cart-header',
		type: 'POST',
		dataType: 'html',
		data: [
			{ topshoe: 'topshoe' }
		]
	})
	.done(function (res) {
		$('#cart-header-layout').html(res);
		$('#cartTotalQuantity').text($('#totalQuantityInput').val());
	})
	.fail(function () {
		console.error("Error routing page");
	})
	.always(function () {
		fixCartToolTip();
		removeCartHeader();
		checkEmptyCart();
	});
}

function preventSharp() {
	setTimeout(() => {
		$('a').click(function (e) {
			if ($(this).attr('href') == '#') e.preventDefault();
		});
	}, 2000);
}

function fixCartToolTip() {
	$('.tt-cart-content').hover(function() {
		setTimeout(function() {
			$('#tt-tooltip-popup').hide();
		},50);
	}, function() {});
}

function removeCartHeader() {
	$('.remove-cart-header').click(function(event) {
		event.preventDefault();
		let name = $(this).parent().siblings().children('.tt-item-descriptions').children('.tt-title').text();
		$(this).parent().parent().hide();

		$.ajax({
			url: $(this).attr('href'),
			type: 'POST',
			dataType: 'html',
			data: [
				{ topshoe: 'topshoe' }
			]
		})
		.done(function (res) {
			let cartInfo = JSON.parse(res);
			$('.tt-cart-total-price').text(cartInfo.totalAmount);
			$('#cartTotalQuantity').text(cartInfo.totalQuantity)
			toastr.options.positionClass = 'toast-bottom-right';
			toastr.info("Đã xóa "+name+" khỏi giỏ hàng!");
		})
		.fail(function () {
			console.error("Error routing page");
			toastr.error("Lỗi xóa sản phẩm!");
		})
		.always(function () {
			checkEmptyCart();
		});
	});
}

function checkEmptyCart() {
	if ($('#cartTotalQuantity').text() == '0'){
		$('.tt-cart-empty').removeClass('d-none');
		$('.tt-cart-list').addClass('d-none');
		$('.tt-cart-total-row').addClass('d-none');
		$('.tt-cart-btn').addClass('d-none');
	}
}

