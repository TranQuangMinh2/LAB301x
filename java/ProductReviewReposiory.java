package com.trnqngmnh.library;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductReviewReposiory extends JpaRepository<ProductReview, Long> {

}
