package com.trnqngmnh.library.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.trnqngmnh.library.entity.ProductReview;

@Repository
public interface ProductReviewReposiory extends JpaRepository<ProductReview, Long> {

}
