package com.appdevf2.maiteam.service;

import com.appdevf2.maiteam.entity.Item;
import com.appdevf2.maiteam.entity.Student;
import com.appdevf2.maiteam.repository.ItemRepository;
import com.appdevf2.maiteam.repository.StudentRepository;

import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

@Service
public class ItemService {
    private final ItemRepository itemRepository;
    private final StudentRepository studentRepository;

    public ItemService(ItemRepository itemRepository, StudentRepository studentRepository) {
        this.itemRepository = itemRepository;
        this.studentRepository = studentRepository;
    }

    public List<Item> getAllItems() {
        return itemRepository.findAll();
    }

    public Optional<Item> getItemById(Long itemId) {
        return itemRepository.findById(itemId);
    }

    @Transactional
    public Item saveItem(Item item) {
        if (item.getSeller() != null && item.getSeller().getStudentId() != null) {
            Student seller = studentRepository.findById(item.getSeller().getStudentId())
                    .orElseThrow(() -> new RuntimeException("Seller not found with id: " + item.getSeller().getStudentId()));
            item.setSeller(seller);
        } else {
            throw new RuntimeException("Seller ID is required to create an item.");
        }

        if(item.getCreatedAt() == null) item.setCreatedAt(Instant.now());
        item.setUpdatedAt(Instant.now());

        return itemRepository.save(item);
    }

    @Transactional
    public Item updateItem(Long itemId, Item itemDetails) {
        return itemRepository.findById(itemId).map(item -> {
            item.setItemName(itemDetails.getItemName());
            item.setDescription(itemDetails.getDescription());
            item.setPrice(itemDetails.getPrice());
            item.setItemPhotoId(itemDetails.getItemPhotoId());
            item.setCategory(itemDetails.getCategory());
            item.setCondition(itemDetails.getCondition());
            item.setAvailabilityStatus(itemDetails.getAvailabilityStatus());
            item.setTransactionType(itemDetails.getTransactionType());
            item.setRentalFee(itemDetails.getRentalFee());
            item.setRentalDurationDays(itemDetails.getRentalDurationDays());
            item.setUpdatedAt(Instant.now());
            
            if (itemDetails.getSeller() != null && itemDetails.getSeller().getStudentId() != null) {
                Student seller = studentRepository.findById(itemDetails.getSeller().getStudentId())
                        .orElseThrow(() -> new RuntimeException("Seller not found"));
                item.setSeller(seller);
            }

            return itemRepository.save(item);
        }).orElse(null);
    }
    
    public void deleteItem(Long itemId) {
        if(itemRepository.existsById(itemId)){
             itemRepository.deleteById(itemId);
        } else {
             throw new RuntimeException("Item not found with id " + itemId);
        }
    }
}