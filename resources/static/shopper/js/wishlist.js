checkEmpytWishlist();
console.log($('.wishlist-item').length);

$('.js-removeitem').click(function(event) {
	event.preventDefault();
	toggleWishlist($(this).attr('href'));
	console.log($('.wishlist-item').length);
	setTimeout(function() {
		checkEmpytWishlist();
	}, 100)
});

function checkEmpytWishlist() {
	if ($('.wishlist-item').length == 0) $('.tt-empty-wishlist').removeClass('d-none');
}

$('.bg-group-table').click(function(event) {
	
});
