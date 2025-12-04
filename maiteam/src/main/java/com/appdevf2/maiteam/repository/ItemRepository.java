package com.appdevf2.maiteam.repository;

import java.util.List;

import com.appdevf2.maiteam.entity.Item;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ItemRepository extends JpaRepository<Item, Long> {
    List<Item> findBySeller_StudentId(Long studentId);
}
