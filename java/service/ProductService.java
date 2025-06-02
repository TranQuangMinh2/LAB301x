package com.trnqngmnh.library.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import com.trnqngmnh.library.entity.Brand;
import com.trnqngmnh.library.entity.Category;
import com.trnqngmnh.library.entity.Product;
import com.trnqngmnh.library.repository.ProductRepository;

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
	private EntityManager entityManager;

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
		if ("priceAsc".equals(sortBy)) {
			return repository.findAllByOrderByPriceAsc();
		} else if ("priceDesc".equals(sortBy)) {
			return repository.findAllByOrderByPriceDesc();
		}
		return new ArrayList<>();
	}

	public List<Product> searchProducts(String text, Long brandId, Long categoryId) {
		return repository.findAll(new Specification<Product>() {
			@Override
			public Predicate toPredicate(Root<Product> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
				List<Predicate> predicates = new ArrayList<>();
				if (text != null && !text.isEmpty()) {
					predicates.add(cb.like(cb.lower(root.get("name")), "%" + text.toLowerCase() + "%"));
				}
				if (brandId != null) {
					predicates.add(cb.equal(root.get("brand").get("id"), brandId));
				}
				if (categoryId != null) {
					predicates.add(cb.equal(root.get("category").get("id"), categoryId));
				}
				return cb.and(predicates.toArray(new Predicate[0]));
			}
		});
	}

	@SuppressWarnings("unchecked")
	public List<Object[]> getProductsDetails() {
		String sql = "SELECT p.id, p.name, p.description, p.price, p.status, p.version_name, pc.color_id, pi.path AS image_path, ps.size, ps.quantity "
				+ "FROM product p " + "LEFT JOIN product_color pc ON p.id = pc.product_id "
				+ "LEFT JOIN product_image pi ON p.id = pi.product_id AND pi.is_primary = 1 "
				+ "LEFT JOIN product_size ps ON p.id = ps.product_id";
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

	public Product updateProduct(Product product) {
		Product existingProduct = repository.findById(product.getId()).orElse(null);
		if (existingProduct != null) {
			existingProduct.setName(product.getName());
			existingProduct.setDescription(product.getDescription());
			existingProduct.setPrice(product.getPrice());
			existingProduct.setStatus(product.getStatus());
			existingProduct.setVersionName(product.getVersionName());
			existingProduct.setBrandId(product.getBrandId());
			existingProduct.setCategoryId(product.getCategoryId());
			return repository.save(existingProduct);
		}
		return null;
	}

	public List<Product> getProductsByBrand(Brand brand) {
		return repository.findByBrand(brand);
	}

	public Page<Product> findPaginated(int pageNo, int pageSize, Long brandId, Long categoryId, String sortBy) {
		Sort sort = Sort.by("price").descending(); // Default sort
		if (sortBy != null && !sortBy.isEmpty()) {
			String[] sortParams = sortBy.split("(?=[A-Z])");
			if (sortParams.length == 2) {
				String sortField = sortParams[0];
				String sortDirection = sortParams[1];
				sort = "Asc".equalsIgnoreCase(sortDirection) ? Sort.by(sortField).ascending()
						: Sort.by(sortField).descending();
			}
		}

		Pageable pageable = PageRequest.of(pageNo - 1, pageSize, sort);
		return repository.findAll((Root<Product> root, CriteriaQuery<?> query, CriteriaBuilder cb) -> {
			List<Predicate> predicates = new ArrayList<>();
			if (brandId != null) {
				predicates.add(cb.equal(root.get("brand").get("id"), brandId));
			}
			if (categoryId != null) {
				predicates.add(cb.equal(root.get("category").get("id"), categoryId));
			}
			return cb.and(predicates.toArray(new Predicate[0]));
		}, pageable);
	}

	public Page<Product> findPaginatedByName(int pageNo, int pageSize, String productName, Long brandId,
			Long categoryId) {
		Pageable pageable = PageRequest.of(pageNo - 1, pageSize);
		return repository.findAll((Root<Product> root, CriteriaQuery<?> query, CriteriaBuilder cb) -> {
			List<Predicate> predicates = new ArrayList<>();
			if (productName != null && !productName.isEmpty()) {
				predicates.add(cb.like(cb.lower(root.get("name")), "%" + productName.toLowerCase() + "%"));
			}
			if (brandId != null) {
				predicates.add(cb.equal(root.get("brand").get("id"), brandId));
			}
			if (categoryId != null) {
				predicates.add(cb.equal(root.get("category").get("id"), categoryId));
			}
			return cb.and(predicates.toArray(new Predicate[0]));
		}, pageable);
	}

	public List<Product> getProductsByCategory(Category category) {
		return repository.findByCategory(category);
	}

	public void deleteProduct(Long id) {
		repository.deleteById(id);
	}

	public List<Product> getLatestProducts(int i) {
		// TODO Auto-generated method stub
		return getLatestProducts(i);
	}
}
