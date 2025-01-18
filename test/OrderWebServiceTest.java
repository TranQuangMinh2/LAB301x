package com.trnqngmnh.library;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.trnqngmnh.library.entity.CartItem;
import com.trnqngmnh.library.entity.OrderWeb;
import com.trnqngmnh.library.repository.OrderWebDetailRepository;
import com.trnqngmnh.library.repository.OrderWebRepository;
import com.trnqngmnh.library.service.OrderWebService;

class OrderWebServiceTest {

	@Mock
	private OrderWebRepository orderWebRepository;

	@Mock
	private OrderWebDetailRepository orderWebDetailRepository;

	@InjectMocks
	private OrderWebService orderWebService;

	@BeforeEach
	void setUp() {
		MockitoAnnotations.openMocks(this);
	}

	@Test
	void testGetOrdersByUserId_Success() {
		Long userId = 1L;
		List<OrderWeb> mockOrders = Arrays.asList(new OrderWeb(), new OrderWeb());

		when(orderWebRepository.findByUserId(userId)).thenReturn(mockOrders);

		List<OrderWeb> result = orderWebService.getOrdersByUserId(userId);

		assertNotNull(result);
		assertEquals(2, result.size());
		verify(orderWebRepository, times(1)).findByUserId(userId);
	}

	@Test
	void testGetAllOrders_Success() {
		List<OrderWeb> mockOrders = Arrays.asList(new OrderWeb(), new OrderWeb());

		when(orderWebRepository.findAll()).thenReturn(mockOrders);

		List<OrderWeb> result = orderWebService.getAllOrders();

		assertNotNull(result);
		assertEquals(2, result.size());
		verify(orderWebRepository, times(1)).findAll();
	}

	@Test
	void testSaveOrderWeb_Success() {
		OrderWeb mockOrder = new OrderWeb();
		when(orderWebRepository.save(mockOrder)).thenReturn(mockOrder);

		OrderWeb result = orderWebService.save(mockOrder);

		assertNotNull(result);
		verify(orderWebRepository, times(1)).save(mockOrder);
	}

	@Test
	void testDeleteOldOrders() {
		orderWebService.deleteOldOrders();

		verify(orderWebRepository, times(1)).deleteByCreatedAtBefore(any(Date.class));
	}

	@Test
	void testSaveOrderWebWithCartItem() {
		OrderWeb orderWeb = new OrderWeb();
		CartItem cartItem = new CartItem();
		cartItem.setCreatedAt(new Date());
		cartItem.setUpdatedAt(new Date());

		orderWebService.saveOrderWeb(orderWeb, cartItem);

		assertEquals(cartItem.getCreatedAt(), orderWeb.getCreatedAt());
		assertEquals(cartItem.getUpdatedAt(), orderWeb.getUpdatedAt());
		verify(orderWebRepository, times(1)).save(orderWeb);
	}

	@Test
	void testHasReachedOrderLimit_NotReached() {
		Long userId = 1L;
		LocalDate now = LocalDate.now();
		LocalDate firstDayOfMonth = now.withDayOfMonth(1);
		LocalDateTime startOfMonth = firstDayOfMonth.atStartOfDay();
		LocalDateTime endOfMonth = now.atTime(23, 59, 59);

		when(orderWebDetailRepository.countBooksByUserIdAndCreatedAtBetween(userId, startOfMonth, endOfMonth))
				.thenReturn(2);

		boolean result = orderWebService.hasReachedOrderLimit(userId);

		assertFalse(result);
		verify(orderWebDetailRepository, times(1)).countBooksByUserIdAndCreatedAtBetween(userId, startOfMonth,
				endOfMonth);
	}

	@Test
	void testHasReachedOrderLimit_Reached() {
		Long userId = 1L;
		LocalDate now = LocalDate.now();
		LocalDate firstDayOfMonth = now.withDayOfMonth(1);
		LocalDateTime startOfMonth = firstDayOfMonth.atStartOfDay();
		LocalDateTime endOfMonth = now.atTime(23, 59, 59);

		when(orderWebDetailRepository.countBooksByUserIdAndCreatedAtBetween(userId, startOfMonth, endOfMonth))
				.thenReturn(3);

		boolean result = orderWebService.hasReachedOrderLimit(userId);

		assertTrue(result);
		verify(orderWebDetailRepository, times(1)).countBooksByUserIdAndCreatedAtBetween(userId, startOfMonth,
				endOfMonth);
	}
}
