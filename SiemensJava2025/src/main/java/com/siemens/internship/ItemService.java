package com.siemens.internship;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.*;
import java.util.stream.Collectors;

/**
 * Service layer for item operations.
 * Handles CRUD and asynchronous processing of items.
 */
@Service
public class ItemService {
    @Autowired
    private ItemRepository itemRepository;


    public List<Item> findAll() {
        return itemRepository.findAll();
    }


    public Optional<Item> findById(Long id) {
        return itemRepository.findById(id);
    }

    public Item save(Item item) {
        return itemRepository.save(item);
    }

    public void deleteById(Long id) {
        itemRepository.deleteById(id);
    }


    /**
     * Asynchronously processes all items: sets status to PROCESSED,
     * saves them, and returns the list of successfully processed items.
     * @return CompletableFuture of processed item list.
     */
    @Async
    public CompletableFuture<List<Item>> processItemsAsync() {

        List<Long> ids = itemRepository.findAllIds();
        // Create a CompletableFuture for each item processing task

        List<CompletableFuture<Item>> futures = ids.stream()
    .map(id -> CompletableFuture.supplyAsync(() -> {
            try {
                Thread.sleep(100);  // simulate work
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                throw new IllegalStateException("Processing interrupted", e);
            }
            return itemRepository.findById(id).map(item -> {
                item.setStatus("PROCESSED");
                // Retrieve, update, and save each item

                return itemRepository.save(item);
            }).orElse(null);
        }))
                .toList();

        // Combine all futures and return the list once all complete
        CompletableFuture<Void> allDone = CompletableFuture
                .allOf(futures.toArray(new CompletableFuture[0]));

        return allDone.thenApply(v -> futures.stream()
                .map(CompletableFuture::join)
                .filter(java.util.Objects::nonNull)
                .collect(Collectors.toList()));
    }

}

