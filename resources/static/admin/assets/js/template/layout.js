/*!
 * Hoangnqph06760@fpt.edu.vn
 * Date: 2020-04-20
 */
getContent();

$.ajaxPrefilter(function (options, original_Options, jqXHR) {
	options.async = true;
});

function getContent() {
	$.ajax({
		url: '',
		type: 'POST',
		dataType: 'html',
		data: [
			{ topshoe: 'topshoe' }
		]
	})
		.done(function (res) {
			topshoe.log("Navigated to " + window.location.href);
			$('#router-outlet').html(res);
			initActiveMenu();

		})
		.fail(function () {
			console.error("Error routing page");
		})
		.always(function () {
			preventSharp();
		});
}

$('#kt_aside_menu li.kt-menu__item > a.kt-menu__link').click(function (e) {
	let url = $(this).attr('routerLink');
	if (url) {
		e.preventDefault();
		history.pushState('data', 'title', '/admin/' + url);
		getContent();

		$('#kt_aside_menu li.kt-menu__item').removeClass('kt-menu__item--active');
		setActiveMenu($(this));
	}
});

function preventSharp() {
	setTimeout(() => {
		$('a').click(function (e) {
			if ($(this).attr('href') == '#') e.preventDefault();
		});
	}, 2000);
}

function setActiveMenu(menu) {
	menu.parent('li.kt-menu__item').addClass('kt-menu__item--active');
	let subHeaderTitle = menu.children('span.kt-menu__link-text').text();
	$('.kt-subheader__title').text(subHeaderTitle);
	document.title = 'Admin | ' + subHeaderTitle;

	let ul = menu.parent().parent();
	if (ul.attr('class') == "kt-menu__subnav") {
		ul.parent().parent().addClass('kt-menu__item--open');
	}
}

function initActiveMenu() {
	let baseHref = '/admin/'
	$('#kt_aside_menu li.kt-menu__item > a.kt-menu__link').each(function (index, el) {
		let href = baseHref + $(this).attr('routerLink');
		if (href == window.location.pathname) {
			setActiveMenu($(this));
			return false;
		}
	});
}
