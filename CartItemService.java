package com.trnqngmnh.library;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CartItemService {

	@Autowired
	private CartItemRepository cartItemRepository;
	@Autowired
	private ProductSizeRepository productSizeRepository;
	@Autowired
	private UserRepository userRepository;

	public List<CartItem> getCartItemsByProductId(Long productId) {
		// Get all cart items
		List<CartItem> allCartItems = cartItemRepository.findAll();

		// Filter cart items by product id
		List<CartItem> cartItemsByProductId = allCartItems.stream()
				.filter(cartItem -> cartItem.getProductSize().getProduct().getId() == productId)
				.collect(Collectors.toList());

		return cartItemsByProductId;
	}

	public void removeCartItem(Long id) {
		// TODO Auto-generated method stub
		cartItemRepository.deleteById(id);
	}

	public void addCartItem(Long productSizeId, Long userId) {
		ProductSize productSize = productSizeRepository.findById(productSizeId).orElse(null);
		User user = userRepository.findById(userId).orElse(null);

		if (productSize != null && user != null) {
			CartItem cartItem = new CartItem();
			cartItem.setCreatedAt(Timestamp.valueOf(LocalDateTime.now()));
			cartItem.setProductSize(productSize);
			cartItem.setUser(user);
			cartItem.setQuantity(1); // Set quantity to 1 or any default value
			cartItemRepository.save(cartItem);
			System.out.println("CartItem saved: " + cartItem);
		} else {
			System.out.println("ProductSize or User is null. ProductSize: " + productSize + ", User: " + user);
		}
	}

	public void deleteAllCartItems() {
		cartItemRepository.deleteAll();
	}

	public List<CartItem> getCartItemsByUserId(Long userId) {
		return cartItemRepository.findByUserId(userId);
	}

}
