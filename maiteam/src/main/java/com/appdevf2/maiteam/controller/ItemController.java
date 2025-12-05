package com.appdevf2.maiteam.controller;

import com.appdevf2.maiteam.dto.ItemDTO;
import com.appdevf2.maiteam.entity.Item;
import com.appdevf2.maiteam.entity.Student;
import com.appdevf2.maiteam.service.ItemService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/items")
public class ItemController {

    private final ItemService itemService;

    public ItemController(ItemService itemService) {
        this.itemService = itemService;
    }

    @GetMapping("/getAllItems")
    public List<ItemDTO> getAllItems() {
        return itemService.getAllItems().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @GetMapping("/getAllItemsByStudentId/{studentId}")
    public ResponseEntity<List<Item>> getItemsBySeller(@PathVariable Long studentId) {
        List<Item> items = itemService.getItemsBySeller(studentId);
        return ResponseEntity.ok(items);
    }

    @GetMapping("/getItemById/{id}")
    public ResponseEntity<ItemDTO> getItemById(@PathVariable Long id) {
        return itemService.getItemById(id)
                .map(item -> ResponseEntity.ok(convertToDTO(item)))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping("/addItem")
    public ResponseEntity<ItemDTO> saveItem(@RequestBody ItemDTO itemDTO) {
        Item item = convertToEntity(itemDTO);
        Item savedItem = itemService.saveItem(item);
        return new ResponseEntity<>(convertToDTO(savedItem), HttpStatus.CREATED);
    }

    @PutMapping("/updateItem/{id}")
    public ResponseEntity<ItemDTO> updateItem(@PathVariable Long id, @RequestBody ItemDTO itemDTO) {
        Item itemDetails = convertToEntity(itemDTO);
        Item updatedItem = itemService.updateItem(id, itemDetails);
        if (updatedItem != null) {
            return ResponseEntity.ok(convertToDTO(updatedItem));
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/deleteItem/{id}")
    public ResponseEntity<String> deleteItem(@PathVariable Long id) {
        try {
            itemService.deleteItem(id);
            return ResponseEntity.ok("Item deleted successfully");
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    // --- Helper: Entity to DTO ---
    private ItemDTO convertToDTO(Item item) {
        ItemDTO dto = new ItemDTO();
        dto.setItemId(item.getItemId());
        dto.setItemName(item.getItemName());
        dto.setDescription(item.getDescription());
        dto.setPrice(item.getPrice());
        dto.setItemPhotoId(item.getItemPhotoId());
        dto.setCategory(item.getCategory());
        dto.setCondition(item.getCondition());
        dto.setAvailabilityStatus(item.getAvailabilityStatus());
        dto.setTransactionType(item.getTransactionType());
        dto.setRentalFee(item.getRentalFee());
        dto.setRentalDurationDays(item.getRentalDurationDays());
        dto.setCreatedAt(item.getCreatedAt());
        dto.setUpdatedAt(item.getUpdatedAt());

        if (item.getSeller() != null) {
            dto.setSellerId(item.getSeller().getStudentId());
            dto.setSellerFirstName(item.getSeller().getFirstName());
            dto.setSellerLastName(item.getSeller().getLastName());
        }
        return dto;
    }

    // --- Helper: DTO to Entity ---
    private Item convertToEntity(ItemDTO dto) {
        Item item = new Item();
        item.setItemName(dto.getItemName());
        item.setDescription(dto.getDescription());
        item.setPrice(dto.getPrice());
        item.setItemPhotoId(dto.getItemPhotoId());
        item.setCategory(dto.getCategory());
        item.setCondition(dto.getCondition());
        item.setAvailabilityStatus(dto.getAvailabilityStatus());
        item.setTransactionType(dto.getTransactionType());
        item.setRentalFee(dto.getRentalFee());
        item.setRentalDurationDays(dto.getRentalDurationDays());

        if (dto.getSellerId() != null) {
            Student seller = new Student();
            seller.setStudentId(dto.getSellerId());
            item.setSeller(seller);
        }
        return item;
    }
}