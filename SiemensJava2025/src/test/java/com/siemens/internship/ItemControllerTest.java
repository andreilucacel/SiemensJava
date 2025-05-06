package com.siemens.internship;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.Collections;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ItemController.class)
class ItemControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ItemService itemService;

    @Autowired
    private ObjectMapper mapper;

    private Item sample;

    @BeforeEach
    void setup() {
        sample = new Item(1L, "Test", "Desc", "NEW", "test@example.com");
    }

    @Test
    void testCreateItem() throws Exception {
        when(itemService.save(any(Item.class))).thenReturn(sample);
        mockMvc.perform(post("/api/items")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(sample)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1));
    }

    @Test
    void testGetByIdFound() throws Exception {
        when(itemService.findById(1L)).thenReturn(Optional.of(sample));
        mockMvc.perform(get("/api/items/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Test"));
    }

    @Test
    void testGetByIdNotFound() throws Exception {
        when(itemService.findById(1L)).thenReturn(Optional.empty());
        mockMvc.perform(get("/api/items/1"))
                .andExpect(status().isNotFound());
    }

    @Test
    void testDeleteNotFound() throws Exception {
        when(itemService.findById(1L)).thenReturn(Optional.empty());
        mockMvc.perform(delete("/api/items/1"))
                .andExpect(status().isNotFound());
    }

    @Test
    void testProcessItems() throws Exception {
        when(itemService.processItemsAsync())
                .thenReturn(CompletableFuture.completedFuture(Collections.singletonList(sample)));
        mockMvc.perform(get("/api/items/process"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].status").value("NEW"));
    }
}