package com.selimhorri.app.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.selimhorri.app.domain.Product;
import com.selimhorri.app.domain.Category;
import com.selimhorri.app.repository.ProductRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class ProductControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ProductRepository repository;

    @Autowired
    private ObjectMapper mapper;

    @BeforeEach
    void setup() {
        repository.deleteAll();

        Category category = Category.builder()
                .categoryTitle("Computadores")
                .imageUrl("img.png")
                .build();

        Product product = Product.builder()
                .productTitle("Laptop HP")
                .sku("HP123")
                .priceUnit(3500.0)
                .quantity(5)
                .category(category)
                .build();

        repository.save(product);
    }

    @Test
    void testGetAllProducts() throws Exception {
        mockMvc.perform(get("/api/products"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)));
    }

    @Test
    void testGetProductById() throws Exception {
        Product product = repository.findAll().get(0);
        mockMvc.perform(get("/api/products/{id}", product.getProductId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.productTitle").value("Laptop HP"));
    }

    @Test
    void testCreateProduct() throws Exception {
        Product newProduct = Product.builder()
                .productTitle("Mouse Logitech")
                .sku("M123")
                .priceUnit(120.0)
                .quantity(15)
                .category(Category.builder().categoryTitle("Periféricos").build())
                .build();

        mockMvc.perform(post("/api/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(newProduct)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.productTitle").value("Mouse Logitech"));
    }

    @Test
    void testUpdateProduct() throws Exception {
        Product product = repository.findAll().get(0);
        product.setPriceUnit(4000.0);

        mockMvc.perform(put("/api/products/{id}", product.getProductId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(product)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.priceUnit").value(4000.0));
    }

    @Test
    void testDeleteProduct() throws Exception {
        Product product = repository.findAll().get(0);

        mockMvc.perform(delete("/api/products/{id}", product.getProductId()))
                .andExpect(status().isNoContent());
    }
}
