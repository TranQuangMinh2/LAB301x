var datatable = $('.kt-datatable').KTDatatable({
	// datasource definition
	data: {
		type: 'remote',
		source: {
			read: {
				url: '/admin/orders/data',
				map: function (raw) {
					// sample data mapping
					var dataSet = raw;
					if (typeof raw.data !== 'undefined') {
						dataSet = raw.data;
					}
					return dataSet;
				},
			},
		},
		pageSize: 10,
		serverPaging: true,
		serverFiltering: true,
		serverSorting: true,
	},

	// layout definition
	layout: {
		scroll: false,
		footer: false,
	},

	// column sorting
	sortable: true,

	pagination: true,

	search: {
		input: $('#generalSearch'),
		delay: 500,
	},

	// columns definition
	columns: [
		{
			field: 'id',
			title: '#',
			sortable: 'desc',
			width: 40,
			type: 'number',
			selector: false,
			textAlign: 'center',
		},
		{
			field: 'consignee',
			title: 'Người nhận hàng',
		},
		{
			field: 'paymentMethod',
			title: 'Phương thức thanh toán',
//			template: function (row) {
//				var status = {
//					'ATM': { 'title': 'ATM', 'class': 'kt-badge--success' },
//					'COD': { 'title': 'COD', 'class': ' kt-badge--primary' },
//				};
//				return '<span class="kt-badge ' + status[row.paymentMethod].class + ' kt-badge--inline kt-badge--pill">' + status[row.paymentMethod].title + '</span>';
//			}, 
		},
		{
			field: 'paymentStatus',
			title: 'Trạng thái thanh toán',
			// callback function support for column rendering
			template: function (row) {
				var status = {
					'Chờ thanh toán ATM': { 'title': 'Chờ thanh toán ATM', 'state': 'primary' },
					'Chưa thanh toán': { 'title': 'Chưa thanh toán', 'state': 'danger' },
					'Đã thanh toán': { 'title': 'Đã thanh toán', 'state': 'success' },
					'Đã hoàn tiền': { 'title': 'Đã hoàn tiền', 'state': 'primary' },
					'Đã hủy bỏ': { 'title': 'Đã hủy bỏ', 'state': 'danger' },
				};
				if (status[row.paymentStatus]) {
					return '<span class="kt-badge kt-badge--' + status[row.paymentStatus].state + ' kt-badge--dot"></span>&nbsp;<span class="kt-font-bold kt-font-' + status[row.paymentStatus].state + '">' +
					status[row.paymentStatus].title + '</span>';
				} else return '';
				
			},
		},
//		{
//			field: 'paymentStatus',
//			title: 'Trạng thái thanh toán',
//		},
		{
			field: 'deliveryStatus',
			title: 'Trạng thái vận chuyển',
		},
		{
			field: 'formatTotalAmount',
			title: 'Tổng tiền',
		},
		// 					{
		// 						field: 'updatedAt',
		// 						title: 'updated_at',
		// 						type: 'date',
		// 						format: 'MM/DD/YYYY',
		// 					}, 
		// 					{
		// 						field: 'isDelete',
		// 						title: 'Status',
		// 						// callback function support for column rendering
		// 						template: function (row) {
		// 							var status = {
		// 								false: { 'title': 'Active', 'class': 'kt-badge--success' },
		// 								true: { 'title': 'Inactive', 'class': ' kt-badge--danger' },
		// 							};
		// 							return '<span class="kt-badge ' + status[row.isDelete].class + ' kt-badge--inline kt-badge--pill">' + status[row.isDelete].title + '</span>';
		// 						},
		// 					}, 
//		{
//			field: 'status',
//			title: 'Status',
//			// callback function support for column rendering
//			template: function (row) {
////				console.log(row);
//				var status = {
//					'Hết hàng': { 'title': 'Hết hàng', 'state': 'danger' },
//					'Đang bán': { 'title': 'Đang bán', 'state': 'success' },
//					'Sắp có': { 'title': 'Sắp có', 'state': 'accent' },
//					'Ẩn trên webshop': { 'Ẩn trên webshop': 'Direct', 'state': 'primary' },
//					'Ngừng kinh doanh': { 'Ngừng kinh doanh': 'Direct', 'state': 'accent' },
//				};
//				return '<span class="kt-badge kt-badge--' + status[row.status].state + ' kt-badge--dot"></span>&nbsp;<span class="kt-font-bold kt-font-' + status[row.status].state + '">' +
//					status[row.status].title + '</span>';
//			},
//		},
		{
			field: 'Actions',
			title: '',
			sortable: false,
			width: 130,
			overflow: 'visible',
			textAlign: 'center',
			template: function (row, index, datatable) {
				var dropup = (datatable.getPageSize() - index) <= 4 ? 'dropup' : '';
				return '<div class="dropdown ' + dropup + '">\
		                        <a href="#" class="btn btn-clean btn-icon btn-pill d-none" data-toggle="dropdown">\
		                            <i class="la la-ellipsis-h"></i>\
		                        </a>\
		                        <div class="dropdown-menu dropdown-menu-right">\
		                            <a class="dropdown-item" href="#"><i class="la la-edit"></i> Edit Details</a>\
		                            <a class="dropdown-item" href="#"><i class="la la-leaf"></i> Update Status</a>\
		                            <a class="dropdown-item" href="#"><i class="la la-print"></i> Generate Report</a>\
		                        </div>\
		                    </div>\
		                    <a href="javascript:view_order_detail('+row.id+');" class="btn btn-clean btn-icon btn-pill view_order_detail" title="Chi tiết">\
		                        <i class="la la-edit"></i>\
		                    </a>\
		                    <a style="display:none;" href="#" class="btn btn-clean btn-icon btn-pill" title="Delete">\
		                        <i class="la la-trash"></i>\
		                    </a>';
			},
		}],

});

$('.kt_form_method').on('change', function () {
	datatable.search($(this).val().toLowerCase(), 'method');
});

$('.kt_form_payment').on('change', function () {
	datatable.search($(this).val().toLowerCase(), 'payment');
});

$('.kt_form_delivery').on('change', function () {
	datatable.search($(this).val().toLowerCase(), 'delivery');
});

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
