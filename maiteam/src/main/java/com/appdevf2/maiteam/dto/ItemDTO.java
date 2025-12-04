package com.appdevf2.maiteam.dto;

import java.time.Instant;

public class ItemDTO {
    private Long itemId;
    private Long sellerId;
    private String itemName;
    private String description;
    private Double price;
    private Long itemPhotoId;
    private String category;
    private String condition;
    private String availabilityStatus;
    private String transactionType;
    private Double rentalFee;
    private Integer rentalDurationDays;
    private Instant createdAt;
    private Instant updatedAt;

    public ItemDTO() {}

    public Long getItemId() { return itemId; }
    public void setItemId(Long itemId) { this.itemId = itemId; }

    public Long getSellerId() { return sellerId; }
    public void setSellerId(Long sellerId) { this.sellerId = sellerId; }

    public String getItemName() { return itemName; }
    public void setItemName(String itemName) { this.itemName = itemName; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public Double getPrice() { return price; }
    public void setPrice(Double price) { this.price = price; }

    public Long getItemPhotoId() { return itemPhotoId; }
    public void setItemPhotoId(Long itemPhotoId) { this.itemPhotoId = itemPhotoId; }

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
}