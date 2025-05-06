package com.siemens.internship;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ItemRepository extends JpaRepository<Item, Long> {
    // Custom query to fetch only IDs for batch processing
    @Query("SELECT id FROM Item")
    List<Long> findAllIds();
}
