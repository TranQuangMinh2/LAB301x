package com.trnqngmnh.library;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;

@Cacheable("listProducts")
@Service
public class ProductService {
	private final ProductRepository repository;

	@PersistenceContext
	EntityManager entityManager;

	public ProductService(ProductRepository repository) {
		this.repository = repository;
	}

	public Product saveProduct(Product product) {
		return repository.save(product);
	}

	public List<Product> saveProducts(List<Product> products) {
		return repository.saveAll(products);
	}

	public List<Product> getAllProducts() {
		return repository.findAll();
	}

	public List<Product> findProducts(String sortBy) {
		if (sortBy.equals("priceAsc")) {
			return repository.findAllByOrderByPriceAsc();
		} else if (sortBy.equals("priceDesc")) {
			return repository.findAllByOrderByPriceDesc();
		}
		// Add more conditions here for other sort options
		return new ArrayList<>();
	}

	public List<Product> searchProducts(String text, Long brandId, Long categoryId) {
		List<Product> products = repository.findByNameContaining(text);

		if (brandId != null) {
			products = products.stream().filter(p -> p.getBrand().getId().equals(brandId)).collect(Collectors.toList());
		}

		if (categoryId != null) {
			products = products.stream().filter(p -> p.getCategory().getId().equals(categoryId))
					.collect(Collectors.toList());
		}

		return products;
	}

	@SuppressWarnings("unchecked")
	public List<Object[]> getProductsDetails() {
		String sql = "SELECT p.id, p.name, p.description, p.price, p.status, p.version_name, pc.color_id, pi.path AS image_path, ps.size, ps.quantity "
				+ "FROM product p " + "LEFT JOIN product_color pc ON p.id = pc.product_id "
				+ "LEFT JOIN product_image pi ON p.id = pi.product_id AND pi.is_primary = 1 "
				+ "LEFT JOIN product_size ps ON p.id = ps.product_id ";

		return entityManager.createNativeQuery(sql).getResultList();
	}

	@SuppressWarnings("unchecked")
	public List<Object[]> getProductsByStatus(String status) {
		String sql = "SELECT p.id, p.name, p.description, p.price, p.status, p.version_name, pc.color_id, pi.path AS image_path, ps.size, ps.quantity "
				+ "FROM product p " + "LEFT JOIN product_color pc ON p.id = pc.product_id "
				+ "LEFT JOIN product_image pi ON p.id = pi.product_id AND pi.is_primary = 1 "
				+ "LEFT JOIN product_size ps ON p.id = ps.product_id " + "WHERE p.status = :status";

		return entityManager.createNativeQuery(sql).setParameter("status", status).getResultList();
	}

	@SuppressWarnings("unchecked")
	public List<Object[]> getProductsByName(String name) {
		String sql = "SELECT p.id, p.name, p.description, p.price, p.status, p.version_name, pc.color_id, pi.path AS image_path, ps.size, ps.quantity "
				+ "FROM product p " + "LEFT JOIN product_color pc ON p.id = pc.product_id "
				+ "LEFT JOIN product_image pi ON p.id = pi.product_id AND pi.is_primary = 1 "
				+ "LEFT JOIN product_size ps ON p.id = ps.product_id " + "WHERE p.name LIKE :name";

		return entityManager.createNativeQuery(sql).setParameter("name", "%" + name + "%").getResultList();
	}

	public Product getProductById(Long id) {
		return repository.findById(id).orElse(null);
	}

//    public Product getProductByName(String name) {
//        return repository.findByName(name);
//    }

//    public String deleteProduct(Long id) {
//        repository.deleteById(id);
//        return "removed: " + id;
//    }

	public Product updateProduct(Product product) {
		Product existingProduct = repository.findById(product.getId()).orElse(null);
		existingProduct.setId(product.getId());
		existingProduct.setName(product.getName());
		existingProduct.setDescription(product.getDescription());
		existingProduct.setPrice(product.getPrice());
		String status = product.getStatus();
		String statusValue;
		switch (status) {
		case "Tất cả":
			statusValue = "Tất cả";
			break;
		case "Còn sách":
			statusValue = "Còn sách";
			break;
		case "Hết sách":
			statusValue = "Hết sách";
			break;
		default:
			statusValue = "Tất cả"; // Giả sử mặc định là "Tất cả"
		}
		existingProduct.setStatus(statusValue);
		existingProduct.setVersionName(product.getVersionName());
		existingProduct.setBrandId(product.getBrandId());
		existingProduct.setCategoryId(product.getCategoryId());
		return repository.save(existingProduct);
	}

	public List<Product> getProductsByBrand(Brand brand) {
		return repository.findByBrand(brand);
	}

	public Page<Product> findPaginated(int pageNo, int pageSize, Long brandId, Long categoryId, String sortBy) {
		Sort sort = Sort.by("price").descending(); // default sort
		if (sortBy != null && !sortBy.isEmpty()) {
			String[] sortParams = sortBy.split("(?=[A-Z])"); // Split at the capital letter
			if (sortParams.length == 2) {
				String sortField = sortParams[0];
				String sortDirection = sortParams[1];
				sort = sortDirection.equalsIgnoreCase("Asc") ? Sort.by(sortField).ascending()
						: Sort.by(sortField).descending();
			}
		}

		Pageable pageable = PageRequest.of(pageNo - 1, pageSize, sort);
		if (brandId != null || categoryId != null) {
			return repository.findAll(new Specification<Product>() {
				@Override
				public Predicate toPredicate(Root<Product> root, CriteriaQuery<?> query,
						CriteriaBuilder criteriaBuilder) {
					List<Predicate> predicates = new ArrayList<>();
					if (brandId != null) {
						predicates.add(criteriaBuilder.equal(root.get("brand").get("id"), brandId));
					}
					if (categoryId != null) {
						predicates.add(criteriaBuilder.equal(root.get("category").get("id"), categoryId));
					}
					return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
				}
			}, pageable);
		} else {
			return repository.findAll(pageable);
		}
	}

	public Page<Product> findPaginatedByName(int pageNo, int pageSize, String productName, Long brandId,
			Long categoryId) {
		Pageable pageable = PageRequest.of(pageNo - 1, pageSize);
		if (productName != null && !productName.isEmpty()) {
			return repository.findAll(new Specification<Product>() {
				@Override
				public Predicate toPredicate(Root<Product> root, CriteriaQuery<?> query,
						CriteriaBuilder criteriaBuilder) {
					List<Predicate> predicates = new ArrayList<>();
					if (productName != null && !productName.isEmpty()) {
						predicates.add(criteriaBuilder.like(root.get("name"), "%" + productName + "%"));
					}
					if (brandId != null) {
						predicates.add(criteriaBuilder.equal(root.get("brand").get("id"), brandId));
					}
					if (categoryId != null) {
						predicates.add(criteriaBuilder.equal(root.get("category").get("id"), categoryId));
					}
					return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
				}
			}, pageable);
		} else {
			return repository.findAll(pageable);
		}
	}

	public List<Product> getProductsByCategory(Category category) {
		return repository.findByCategory(category);
	}

}