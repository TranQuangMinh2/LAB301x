package com.trnqngmnh.library;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.trnqngmnh.library.entity.CartItem;
import com.trnqngmnh.library.entity.Product;
import com.trnqngmnh.library.entity.ProductSize;
import com.trnqngmnh.library.entity.User;
import com.trnqngmnh.library.repository.CartItemRepository;
import com.trnqngmnh.library.repository.ProductSizeRepository;
import com.trnqngmnh.library.repository.UserRepository;
import com.trnqngmnh.library.service.CartItemService;

class CartItemServiceTest {

	@Mock
	private CartItemRepository cartItemRepository;

	@Mock
	private ProductSizeRepository productSizeRepository;

	@Mock
	private UserRepository userRepository;

	@InjectMocks
	private CartItemService cartItemService;

	@BeforeEach
	void setUp() {
		MockitoAnnotations.openMocks(this);
	}

	@Test
	void testGetCartItemsByProductId() {
		// Arrange
		Long productId = 1L;
		Product product = new Product();
		product.setId(productId);

		ProductSize productSize = new ProductSize();
		productSize.setProduct(product);

		CartItem cartItem1 = new CartItem();
		cartItem1.setProductSize(productSize);

		List<CartItem> cartItems = List.of(cartItem1);
		when(cartItemRepository.findAll()).thenReturn(cartItems);

		// Act
		List<CartItem> result = cartItemService.getCartItemsByProductId(productId);

		// Assert
		assertEquals(1, result.size());
		verify(cartItemRepository, times(1)).findAll();
	}

	@Test
	void testRemoveCartItem() {
		// Arrange
		Long cartItemId = 1L;

		// Act
		cartItemService.removeCartItem(cartItemId);

		// Assert
		verify(cartItemRepository, times(1)).deleteById(cartItemId);
	}

	@Test
	void testAddCartItem() {
		// Arrange
		Long productSizeId = 1L;
		Long userId = 2L;

		ProductSize productSize = new ProductSize();
		productSize.setId(productSizeId);

		User user = new User();
		user.setId(userId);

		when(productSizeRepository.findById(productSizeId)).thenReturn(Optional.of(productSize));
		when(userRepository.findById(userId)).thenReturn(Optional.of(user));

		// Act
		cartItemService.addCartItem(productSizeId, userId);

		// Assert
		verify(cartItemRepository, times(1)).save(any(CartItem.class));
	}

	@Test
	void testAddCartItem_ProductSizeOrUserIsNull() {
		// Arrange
		Long productSizeId = 1L;
		Long userId = 2L;

		when(productSizeRepository.findById(productSizeId)).thenReturn(Optional.empty());
		when(userRepository.findById(userId)).thenReturn(Optional.of(new User()));

		// Act
		cartItemService.addCartItem(productSizeId, userId);

		// Assert
		verify(cartItemRepository, never()).save(any(CartItem.class));
	}

	@Test
	void testDeleteAllCartItems() {
		// Act
		cartItemService.deleteAllCartItems();

		// Assert
		verify(cartItemRepository, times(1)).deleteAll();
	}

	@Test
	void testGetCartItemsByUserId() {
		// Arrange
		Long userId = 1L;
		List<CartItem> cartItems = new ArrayList<>();
		when(cartItemRepository.findByUserId(userId)).thenReturn(cartItems);

		// Act
		List<CartItem> result = cartItemService.getCartItemsByUserId(userId);

		// Assert
		assertEquals(cartItems, result);
		verify(cartItemRepository, times(1)).findByUserId(userId);
	}

	@Test
	void testSaveProductSize() {
		// Arrange
		ProductSize productSize = new ProductSize();

		// Act
		cartItemService.saveProductSize(productSize);

		// Assert
		verify(productSizeRepository, times(1)).save(productSize);
	}
}
