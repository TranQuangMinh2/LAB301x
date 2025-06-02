package com.trnqngmnh.library.service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.opencsv.CSVWriter;
import com.trnqngmnh.library.entity.Product;
import com.trnqngmnh.library.repository.ProductRepository;

import jakarta.servlet.http.HttpServletResponse;

@Service
public class CSVService {

	@Autowired
	private ProductRepository productRepository; // Repository lưu sản phẩm vào CSDL

	public void exportToCSV(HttpServletResponse response) throws IOException {
		List<Product> products = productRepository.findAll(); // Lấy tất cả sản phẩm từ cơ sở dữ liệu

		response.setContentType("text/csv");
		response.setHeader("Content-Disposition", "attachment; filename=products.csv");

		try (CSVWriter csvWriter = new CSVWriter(new OutputStreamWriter(response.getOutputStream()))) {
			// Write header
			String[] header = { "ID", "Tên", "Mô tả", "Trạng thái", "Tác giả", "Thể loại" };
			csvWriter.writeNext(header);

			// Write data
			for (Product product : products) {
				String[] record = { String.valueOf(product.getId()), product.getName(), product.getDescription(),
						product.getStatus(), product.getBrand().getName(), // Assuming you have a getName() method in
																			// Brand
						product.getCategory().getName() // Assuming you have a getName() method in Category
				};
				csvWriter.writeNext(record);
			}
		}
	}

	public void saveProductsFromCSV(MultipartFile file) {
		try (BufferedReader br = new BufferedReader(new InputStreamReader(file.getInputStream()))) {
			String line;
			List<Product> products = new ArrayList<>();
			boolean isFirstLine = true; // Bỏ qua dòng tiêu đề CSV

			while ((line = br.readLine()) != null) {
				if (isFirstLine) {
					isFirstLine = false;
					continue; // Bỏ qua tiêu đề
				}

				String[] data = line.split(","); // Giả định file CSV có dấu phẩy ngăn cách

				if (data.length != 9) {
					System.err.println("Dữ liệu không hợp lệ trên dòng: " + line);
					continue; // Bỏ qua dòng không hợp lệ
				}

				try {
					Product product = new Product();
					product.setName(data[0]); // Ví dụ: Cột thứ hai là tên sản phẩm
					product.setDescription(data[1]); // Cột thứ ba là mô tả
					product.setPrice(Long.parseLong(data[2])); // Cột thứ tư là giá
					product.setStatus(data[3]); // Cột thứ năm là trạng thái
					product.setVersionName(data[4]); // Cột thứ sáu là tên phiên bản
					product.setBrandId(Long.parseLong(data[5])); // Cột thứ bảy là ID thương hiệu
					product.setCategoryId(Long.parseLong(data[6])); // Cột thứ tám là ID thể loại

					products.add(product);
				} catch (NumberFormatException e) {
					System.err.println("Lỗi định dạng số trên dòng: " + line + " - " + e.getMessage());
				}
			}

			// Lưu tất cả sản phẩm vào CSDL
			productRepository.saveAll(products);

		} catch (Exception e) {
			throw new RuntimeException("Lỗi khi đọc file CSV: " + e.getMessage(), e);
		}
	}
}
