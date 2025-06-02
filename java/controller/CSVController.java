package com.trnqngmnh.library.controller;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.trnqngmnh.library.config.CSVHelper;
import com.trnqngmnh.library.service.CSVService;

import jakarta.servlet.http.HttpServletResponse;

@Controller
@RequestMapping("/api/csv")
public class CSVController {

	@Autowired
	private CSVService csvService;

	// API để upload CSV và thêm dữ liệu vào bảng product
	@PostMapping("/upload")
	public String uploadCSVFile(@RequestParam("file") MultipartFile file, RedirectAttributes redirectAttributes) {
		if (CSVHelper.hasCSVFormat(file)) {
			try {
				csvService.saveProductsFromCSV(file); // Lưu sản phẩm vào cơ sở dữ liệu
				redirectAttributes.addFlashAttribute("message", "File CSV được tải lên và xử lý thành công!");
			} catch (Exception e) {
				redirectAttributes.addFlashAttribute("message", "Có lỗi xảy ra khi xử lý file CSV!");
			}
		} else {
			redirectAttributes.addFlashAttribute("message", "Vui lòng tải lên một file CSV hợp lệ!");
		}

		// Redirect về trang danh sách sản phẩm
		return "redirect:/product";
	}

	@PostMapping("/export")
	public void exportToCSV(HttpServletResponse response) throws IOException {
		csvService.exportToCSV(response);
	}
}
