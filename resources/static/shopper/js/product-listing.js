let url = new URL(window.location.href);
let pageRequest = 0;

$('#show_more').click(function(event) {
	event.preventDefault();
	pageRequest++;
	url.searchParams.set('pageRequest', pageRequest);

	$.ajax({
		url: url.href,
		type: 'GET',
		dataType: 'html',
		data: [
			{ topshoe: 'topshoe' }
		]
	})
	.done(function(res) {
		if (res == 0) {
			$('#show_more').addClass('d-none');
			$('#last_page').removeClass('d-none');
		} else {
			$('.tt-product-listing').append(res);
			onClickWishlist();
		}
	})
	.fail(function() {
		console.log("error");
	})
	.always(function() {
		url.searchParams.delete('pageRequest');
	});
});

let sortSelect = url.searchParams.get('sortSelect');
if (!sortSelect) sortSelect = 0;
$('#sortSelect').val(sortSelect);
$('#sortSelect').change(function(event) {
	url.searchParams.set('sortSelect', $(this).val());
	window.location.replace(url);
});

setListFilter('categoryFilter');
setListFilter('brandFilter');
setListFilter('priceFilter');
setListFilter('sizeFilter');
setListFilter('colorFilter');

function setListFilter (filter) {
	let filterName = localStorage.getItem(filter);
	if (filterName) $('#list-filter').append('<li><a class="list-filter-item" href="'+filter+'" title="Bỏ lọc này">'+filterName+'</a></li>');
}

$('.list-filter-item').click(function(event) {
	event.preventDefault();
	let filterCode;
	let filter = $(this).attr('href')

	localStorage.removeItem(filter);
	if (filter == 'categoryFilter') url.searchParams.delete('categoryId');
	if (filter == 'brandFilter') url.searchParams.delete('brandId');
	if (filter == 'priceFilter') {
		url.searchParams.delete('priceMin');
		url.searchParams.delete('priceMax');
	}
	if (filter == 'sizeFilter') url.searchParams.delete('size');
	if (filter == 'colorFilter') url.searchParams.delete('colorId');
	window.location.replace(url);
});

$('#remove-all-filter').click(function(event) {
	event.preventDefault();
	localStorage.removeItem('categoryFilter');
	localStorage.removeItem('brandFilter');
	localStorage.removeItem('priceFilter');
	localStorage.removeItem('sizeFilter');
	localStorage.removeItem('colorFilter');
	window.location.replace(url.origin + url.pathname);
});

$('#list-gategory li a').click(function(event) {
	event.preventDefault();
	url.searchParams.set('categoryId', $(this).attr('href'));
	localStorage.setItem('categoryFilter', $(this).text());
	window.location.replace(url);
});

$('#list-brand li a').click(function(event) {
	event.preventDefault();
	url.searchParams.set('brandId', $(this).attr('href'));
	localStorage.setItem('brandFilter', $(this).text());
	window.location.replace(url);
});

$('#list-price li a').click(function(event) {
	event.preventDefault();
	let priceMin = $(this).attr('priceMin');
	let priceMax = $(this).attr('priceMax');

	if (priceMin) url.searchParams.set('priceMin', $(this).attr('priceMin'));
	else url.searchParams.delete('priceMin');

	if (priceMax) url.searchParams.set('priceMax', $(this).attr('priceMax'));
	else url.searchParams.delete('priceMax');

	localStorage.setItem('priceFilter', $(this).text());
	window.location.replace(url);
});

$('#list-size li a').click(function(event) {
	event.preventDefault();
	url.searchParams.set('size', $(this).text());
	localStorage.setItem('sizeFilter', 'Size ' + $(this).text());
	window.location.replace(url);
});

$('#list-color li a').click(function(event) {
	event.preventDefault();
	url.searchParams.set('colorId', $(this).attr('href'));
	localStorage.setItem('colorFilter', 'Màu ' + $(this).attr('title'));
	window.location.replace(url);
});
