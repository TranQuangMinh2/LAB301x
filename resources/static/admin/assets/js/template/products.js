var datatable = $('.kt-datatable').KTDatatable({
	// datasource definition
	data: {
		type: 'remote',
		source: {
			read: {
				url: '/admin/products/data',
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
			sortable: 'asc',
			width: 40,
			type: 'number',
			selector: false,
			textAlign: 'center',
		},
		{
			field: 'name',
			title: 'Name',
		},
		{
			field: 'versionName',
			title: 'Version',
		},
		{
			field: 'formatPrice',
			title: 'Price',
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
//		{
//			field: 'Actions',
//			title: 'Actions',
//			sortable: false,
//			width: 130,
//			overflow: 'visible',
//			textAlign: 'center',
//			template: function (row, index, datatable) {
//				var dropup = (datatable.getPageSize() - index) <= 4 ? 'dropup' : '';
//				return '<div class="dropdown ' + dropup + ' d-none">\
//		                        <a href="#" class="btn btn-clean btn-icon btn-pill d-none" data-toggle="dropdown">\
//		                            <i class="la la-ellipsis-h"></i>\
//		                        </a>\
//		                        <div class="dropdown-menu dropdown-menu-right">\
//		                            <a class="dropdown-item" href="#"><i class="la la-edit"></i> Edit Details</a>\
//		                            <a class="dropdown-item" href="#"><i class="la la-leaf"></i> Update Status</a>\
//		                            <a class="dropdown-item" href="#"><i class="la la-print"></i> Generate Report</a>\
//		                        </div>\
//		                    </div>\
//		                    <a href="#" class="btn btn-clean btn-icon btn-pill" title="Edit details">\
//		                        <i class="la la-edit"></i>\
//		                    </a>\
//		                    <a href="#" class="btn btn-clean btn-icon btn-pill" title="Delete">\
//		                        <i class="la la-trash"></i>\
//		                    </a>';
//			},
//		}
		],

});

$('#kt_form_status').on('change', function () {
	datatable.search($(this).val().toLowerCase(), 'Status');
});

$('#kt_form_type').on('change', function () {
	datatable.search($(this).val().toLowerCase(), 'Type');
});