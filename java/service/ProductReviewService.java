package com.trnqngmnh.library.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.trnqngmnh.library.entity.ProductReview;
import com.trnqngmnh.library.repository.ProductReviewReposiory;

@Service
public class ProductReviewService {
	@Autowired
	private final ProductReviewReposiory productReviewRepository;

	public ProductReviewService(ProductReviewReposiory productReviewRepository) {
		super();
		this.productReviewRepository = productReviewRepository;
	}

	public List<ProductReview> getAllReviews() {
		// TODO Auto-generated method stub
		return productReviewRepository.findAll();
	}

	public void saveReview(ProductReview review) {
		productReviewRepository.save(review);
	}

}
