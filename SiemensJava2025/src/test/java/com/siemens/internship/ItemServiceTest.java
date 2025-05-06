package com.siemens.internship;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;
class ItemServiceTest {

    @Mock
    private ItemRepository itemRepository;

    @InjectMocks
    private ItemService itemService;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testFindAll() {
        when(itemRepository.findAll()).thenReturn(Arrays.asList(
                new Item(1L, "A", "D", "NEW", "a@a.com"),
                new Item(2L, "B", "D", "NEW", "b@b.com")
        ));
        List<Item> results = itemService.findAll();
        assertThat(results).hasSize(2);
        verify(itemRepository).findAll();
    }

    @Test
    void testProcessItemsAsync() throws Exception {
        // Prepare two items
        when(itemRepository.findAllIds()).thenReturn(Arrays.asList(1L, 2L));
        when(itemRepository.findById(1L)).thenReturn(Optional.of(new Item(1L, "A", "D", "NEW", "a@a.com")));
        when(itemRepository.findById(2L)).thenReturn(Optional.of(new Item(2L, "B", "D", "NEW", "b@b.com")));
        when(itemRepository.save(any(Item.class))).thenAnswer(inv -> inv.getArgument(0));

        CompletableFuture<List<Item>> future = itemService.processItemsAsync();
        List<Item> processed = future.get();

        assertThat(processed).hasSize(2);
        processed.forEach(item -> assertThat(item.getStatus()).isEqualTo("PROCESSED"));
        verify(itemRepository, times(2)).save(any(Item.class));
    }
}