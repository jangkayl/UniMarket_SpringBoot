package com.appdevf2.maiteam.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

import java.time.Instant;
import java.util.List;

@Entity
@Table(name="item")
public class Item {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long itemId;

    @ManyToOne 
    @JoinColumn(name = "seller_id", nullable = false)
    private Student seller;

    @Column(name = "item_name", nullable = false)
    private String itemName;

    @Column(name = "description")
    private String description;

    @Column(name = "price")
    private Double price;

    @Column(name = "item_photo_id")
    private Long itemPhotoId; 

    @Column(name = "item_photo")
    private String itemPhoto; 

    @Column(name = "category")
    private String category;

    @Column(name = "condition_status")
    private String condition;

    @Column(name = "availability_status")
    private String availabilityStatus;

    @Column(name = "transaction_type")
    private String transactionType; 

    @Column(name = "rental_fee")
    private Double rentalFee;

    @Column(name = "rental_duration_days")
    private Integer rentalDurationDays;

    @Column(name = "created_at")
    private Instant createdAt = Instant.now();

    @Column(name = "updated_at")
    private Instant updatedAt;

    @OneToMany(mappedBy = "item")
    private List<Message> messages;

    public Item() {}

    public Item(String itemName, Double price, Student seller) {
        this.itemName = itemName;
        this.price = price;
        this.seller = seller;
        this.createdAt = Instant.now();
    }

    // Getters and Setters
    public Long getItemId() { return itemId; }
    public void setItemId(Long itemId) { this.itemId = itemId; }
    
    public Student getSeller() { return seller; }
    public void setSeller(Student seller) { this.seller = seller; }
    
    public String getItemName() { return itemName; }
    public void setItemName(String itemName) { this.itemName = itemName; }
    
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    
    public Double getPrice() { return price; }
    public void setPrice(Double price) { this.price = price; }
    
    public Long getItemPhotoId() { return itemPhotoId; }
    public void setItemPhotoId(Long itemPhotoId) { this.itemPhotoId = itemPhotoId; }

    // --- NEW GETTER/SETTER ---
    public String getItemPhoto() { return itemPhoto; }
    public void setItemPhoto(String itemPhoto) { this.itemPhoto = itemPhoto; }
    // -------------------------

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }
    
    public String getCondition() { return condition; }
    public void setCondition(String condition) { this.condition = condition; }
    
    public String getAvailabilityStatus() { return availabilityStatus; }
    public void setAvailabilityStatus(String availabilityStatus) { this.availabilityStatus = availabilityStatus; }
    
    public String getTransactionType() { return transactionType; }
    public void setTransactionType(String transactionType) { this.transactionType = transactionType; }
    
    public Double getRentalFee() { return rentalFee; }
    public void setRentalFee(Double rentalFee) { this.rentalFee = rentalFee; }
    
    public Integer getRentalDurationDays() { return rentalDurationDays; }
    public void setRentalDurationDays(Integer rentalDurationDays) { this.rentalDurationDays = rentalDurationDays; }
    
    public Instant getCreatedAt() { return createdAt; }
    public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }
    
    public Instant getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(Instant updatedAt) { this.updatedAt = updatedAt; }
    
    public List<Message> getMessages() { return messages; }
    public void setMessages(List<Message> messages) { this.messages = messages; }
}