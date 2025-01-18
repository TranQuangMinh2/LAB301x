package com.trnqngmnh.library;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.trnqngmnh.library.entity.ProductReview;
import com.trnqngmnh.library.repository.ProductReviewReposiory;
import com.trnqngmnh.library.service.ProductReviewService;

class ProductReviewServiceTest {

	@Mock
	private ProductReviewReposiory productReviewRepository;

	@InjectMocks
	private ProductReviewService productReviewService;

	@BeforeEach
	void setUp() {
		MockitoAnnotations.openMocks(this);
	}

	@Test
	void testGetAllReviews_Success() {
		ProductReview review1 = new ProductReview();
		review1.setId(1L);
		review1.setContent("Great!");

		ProductReview review2 = new ProductReview();
		review2.setId(2L);
		review2.setContent("Not bad!");

		when(productReviewRepository.findAll()).thenReturn(Arrays.asList(review1, review2));

		List<ProductReview> result = productReviewService.getAllReviews();

		assertNotNull(result);
		assertEquals(2, result.size());
		assertEquals("Great!", result.get(0).getContent());
		assertEquals("Not bad!", result.get(1).getContent());
	}

	@Test
	void testSaveReview_Success() {
		ProductReview review = new ProductReview();
		review.setId(1L);
		review.setContent("Excellent!");

		doNothing().when(productReviewRepository).save(review);

		productReviewService.saveReview(review);

		verify(productReviewRepository, times(1)).save(review);
	}
}
