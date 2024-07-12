package com.trnqngmnh.library;

import java.text.NumberFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;

@Entity
@Table(name = "order_web")
public class OrderWeb {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	public String getFormatId() {
		return String.format("DH%05d", id);
	}

	@Column(name = "created_at")
	private Date createdAt;

	@Column(name = "updated_at")
	private Date updatedAt;

	@PrePersist
	public void prePersist() {
		createdAt = new Date();
		updatedAt = new Date();
	}

	@PreUpdate
	public void preUpdate() {
		updatedAt = new Date();
	}

	@Column(name = "consignee")
	private String consignee;

	@Column(name = "consignee_phone")
	private String consigneePhone;

	@Column(name = "consignee_email")
	private String consigneeEmail;

	@Column(name = "delivery_address")
	private String deliveryAddress;

	@Column(name = "delivery_status")
	private String deliveryStatus;

	@Column(name = "payment_method")
	private String paymentMethod;

	@Column(name = "payment_status")
	private String paymentStatus;

	@Column(name = "sent_mail")
	private Boolean sentMail;

	@Column(name = "total_amount")
	private Long totalAmount;

	@Column(name = "user_id")
	private Long userId;

	// các trường và phương thức khác của bạn ở đây

	public String formatPrice(double amount) {
		NumberFormat format = NumberFormat.getCurrencyInstance(new Locale("vi", "VN"));
		return format.format(amount);
	}

//    @OneToMany(mappedBy = "orderWeb", cascade = CascadeType.ALL)
//    private List<OrderWebDetail> orderWebDetails;
	@OneToMany(mappedBy = "orderWeb", fetch = FetchType.EAGER)
	private List<OrderWebDetail> orderWebDetails;

	public List<OrderWebDetail> getOrderWebDetails() {
		return this.orderWebDetails;
	}

	// Getters and setters
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Date getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(Date createdAt) {
		this.createdAt = createdAt;
	}

	public Date getUpdatedAt() {
		return updatedAt;
	}

	public void setUpdatedAt(Date updatedAt) {
		this.updatedAt = updatedAt;
	}

	public String getConsignee() {
		return consignee;
	}

	public void setConsignee(String consignee) {
		this.consignee = consignee;
	}

	public String getConsigneePhone() {
		return consigneePhone;
	}

	public void setConsigneePhone(String consigneePhone) {
		this.consigneePhone = consigneePhone;
	}

	public String getConsigneeEmail() {
		return consigneeEmail;
	}

	public void setConsigneeEmail(String consigneeEmail) {
		this.consigneeEmail = consigneeEmail;
	}

	public String getDeliveryAddress() {
		return deliveryAddress;
	}

	public void setDeliveryAddress(String deliveryAddress) {
		this.deliveryAddress = deliveryAddress;
	}

	public String getDeliveryStatus() {
		return deliveryStatus;
	}

	public void setDeliveryStatus(String deliveryStatus) {
		this.deliveryStatus = deliveryStatus;
	}

	public String getPaymentMethod() {
		return paymentMethod;
	}

	public void setPaymentMethod(String paymentMethod) {
		this.paymentMethod = paymentMethod;
	}

	public String getPaymentStatus() {
		return paymentStatus;
	}

	public void setPaymentStatus(String paymentStatus) {
		this.paymentStatus = paymentStatus;
	}

	public Boolean getSentMail() {
		return sentMail;
	}

	public void setSentMail(Boolean sentMail) {
		this.sentMail = sentMail;
	}

	public Long getTotalAmount() {
		return totalAmount;
	}

	public void setTotalAmount(Long totalAmount) {
		this.totalAmount = totalAmount;
	}

	public Long getUserId() {
		return userId;
	}

	public void setUserId(Long userId) {
		this.userId = userId;
	}

}
