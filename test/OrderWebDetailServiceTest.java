package com.trnqngmnh.library;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
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

import com.trnqngmnh.library.entity.OrderWebDetail;
import com.trnqngmnh.library.repository.OrderWebDetailRepository;
import com.trnqngmnh.library.service.OrderWebDetailService;

class OrderWebDetailServiceTest {

	@Mock
	private OrderWebDetailRepository orderWebDetailRepository;

	@InjectMocks
	private OrderWebDetailService orderWebDetailService;

	@BeforeEach
	void setUp() {
		MockitoAnnotations.openMocks(this);
	}

	@Test
	void testFindByOrderWebId_Success() {
		// Arrange
		Long orderId = 1L;
		OrderWebDetail detail1 = new OrderWebDetail();
		detail1.setId(1L);
		detail1.setOrderWebId(orderId);

		OrderWebDetail detail2 = new OrderWebDetail();
		detail2.setId(2L);
		detail2.setOrderWebId(orderId);

		List<OrderWebDetail> mockDetails = Arrays.asList(detail1, detail2);

		when(orderWebDetailRepository.findByOrderWebId(orderId)).thenReturn(mockDetails);

		// Act
		List<OrderWebDetail> result = orderWebDetailService.findByOrderWebId(orderId);

		// Assert
		assertNotNull(result);
		assertEquals(2, result.size());
		assertEquals(orderId, result.get(0).getOrderWebId());
		verify(orderWebDetailRepository, times(1)).findByOrderWebId(orderId);
	}

	@Test
	void testFindByOrderWebId_EmptyResult() {
		// Arrange
		Long orderId = 999L; // Order ID that doesn't exist
		when(orderWebDetailRepository.findByOrderWebId(orderId)).thenReturn(List.of());

		// Act
		List<OrderWebDetail> result = orderWebDetailService.findByOrderWebId(orderId);

		// Assert
		assertNotNull(result);
		assertTrue(result.isEmpty());
		verify(orderWebDetailRepository, times(1)).findByOrderWebId(orderId);
	}

	@Test
	void testFindByOrderWebId_Exception() {
		// Arrange
		Long orderId = 1L;
		when(orderWebDetailRepository.findByOrderWebId(orderId)).thenThrow(new RuntimeException("Database error"));

		// Act & Assert
		Exception exception = assertThrows(RuntimeException.class,
				() -> orderWebDetailService.findByOrderWebId(orderId));
		assertEquals("Database error", exception.getMessage());
		verify(orderWebDetailRepository, times(1)).findByOrderWebId(orderId);
	}
}
