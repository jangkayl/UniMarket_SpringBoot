package com.appdevf2.maiteam.controller;

import com.appdevf2.maiteam.dto.ItemDTO;
import com.appdevf2.maiteam.entity.Item;
import com.appdevf2.maiteam.entity.Student;
import com.appdevf2.maiteam.service.ItemService;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.net.MalformedURLException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/items")
public class ItemController {

    private final ItemService itemService;
    // Define where your images are stored (Must match where you save them in Service)
    private final Path fileStorageLocation = Paths.get("uploads/items").toAbsolutePath().normalize();

    public ItemController(ItemService itemService) {
        this.itemService = itemService;
    }

    // --- 1. SERVE IMAGE ENDPOINT ---
    @GetMapping("/images/{filename}")
    public ResponseEntity<Resource> getImage(@PathVariable String filename) {
        try {
            Path filePath = fileStorageLocation.resolve(filename).normalize();
            Resource resource = new UrlResource(filePath.toUri());

            if (resource.exists() && resource.isReadable()) {
                // Dynamically determine content type or default to JPEG
                return ResponseEntity.ok()
                        .contentType(MediaType.IMAGE_JPEG)
                        .body(resource);
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (MalformedURLException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/getAllItems")
    public List<ItemDTO> getAllItems() {
        return itemService.getAllItems().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @GetMapping("/getAllItemsByStudentId/{studentId}")
    public ResponseEntity<List<ItemDTO>> getItemsBySeller(@PathVariable Long studentId) {
        List<ItemDTO> items = itemService.getItemsBySeller(studentId).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(items);
    }

    @GetMapping("/getItemById/{id}")
    public ResponseEntity<ItemDTO> getItemById(@PathVariable Long id) {
        return itemService.getItemById(id)
                .map(item -> ResponseEntity.ok(convertToDTO(item)))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    // --- 2. CREATE ITEM (MULTIPART) ---
    @PostMapping(value = "/addItem", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ItemDTO> saveItem(
            @ModelAttribute ItemDTO itemDTO, // Binds text fields
            @RequestParam(value = "file", required = false) MultipartFile file // Binds file
    ) {
        // Convert DTO to Entity
        Item item = convertToEntity(itemDTO);

        // Handle File if present
        if (file != null && !file.isEmpty()) {
            String fileName = itemService.saveImageFile(file);
            item.setItemPhoto(fileName); // Save filename to DB
        }

        // Save to DB
        Item savedItem = itemService.saveItem(item);
        return new ResponseEntity<>(convertToDTO(savedItem), HttpStatus.CREATED);
    }

    // --- 3. UPDATE ITEM (MULTIPART) ---
    @PutMapping(value = "/updateItem/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ItemDTO> updateItem(
            @PathVariable Long id,
            @ModelAttribute ItemDTO itemDTO, // Use ModelAttribute for Form Data
            @RequestParam(value = "file", required = false) MultipartFile file
    ) {
        Item itemDetails = convertToEntity(itemDTO);

        // Logic to handle the new image if uploaded
        if (file != null && !file.isEmpty()) {
            String fileName = itemService.saveImageFile(file);
            itemDetails.setItemPhoto(fileName);
        }
        // Note: If file is null, the service logic (or controller logic here) 
        // should typically ensure we don't overwrite the existing photo with null 
        // unless intended. The current service implementation maps whatever is passed.
        // Ideally, the DTO passed from frontend should contain the *old* photo name 
        // if no new file is selected, or you handle that check inside Service.

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
        
        // Map the String filename
        dto.setItemPhoto(item.getItemPhoto());
        
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
            // Map Seller Names
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
        
        // Map the String filename
        item.setItemPhoto(dto.getItemPhoto());

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