package com.selimhorri.app.unit;

import com.selimhorri.app.domain.Product;
import com.selimhorri.app.domain.Category;
import com.selimhorri.app.dto.ProductDto;
import com.selimhorri.app.dto.CategoryDto;
import com.selimhorri.app.helper.ProductMappingHelper;
import com.selimhorri.app.repository.ProductRepository;
import com.selimhorri.app.service.impl.ProductServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import java.util.List;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ProductServiceTest {

    @Mock
    private ProductRepository productRepository;

    @InjectMocks
    private ProductServiceImpl productService;

    private Product product;
    private ProductDto productDto;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);

        var category = Category.builder()
                .categoryId(1)
                .categoryTitle("Electrónicos")
                .imageUrl("cat.jpeg")
                .build();

        product = Product.builder()
                .productId(1)
                .productTitle("Laptop")
                .imageUrl("dell.jpg")
                .sku("D123")
                .priceUnit(2500.0)
                .quantity(10)
                .category(category)
                .build();

        var categoryDto = CategoryDto.builder()
                .categoryId(1)
                .categoryTitle("Electrónicos")
                .imageUrl("cat.jpeg")
                .build();

        productDto = ProductDto.builder()
                .productId(1)
                .productTitle("Laptop")
                .imageUrl("dell.jpg")
                .sku("D123")
                .priceUnit(2500.0)
                .quantity(10)
                .categoryDto(categoryDto)
                .build();
    }

    @Test
    void testFindById_success() {
        when(productRepository.findById(1)).thenReturn(Optional.of(product));

        ProductDto result = ProductMappingHelper.map(product);

        assertEquals(productDto.getProductTitle(), result.getProductTitle());
        assertEquals(productDto.getCategoryDto().getCategoryTitle(), result.getCategoryDto().getCategoryTitle());
    }

    @Test
    void testFindById_notFound() {
        when(productRepository.findById(99)).thenReturn(Optional.empty());
        assertThrows(RuntimeException.class, () -> productService.findById(99));
    }

    @Test
    void testSave_createsProduct() {
        when(productRepository.save(any(Product.class))).thenReturn(product);
        when(productRepository.findById(1)).thenReturn(Optional.of(product));

        Product saved = productRepository.save(ProductMappingHelper.map(productDto));
        assertEquals(1, saved.getProductId());
    }

    @Test
    void testFindAll_returnsList() {
        when(productRepository.findAll()).thenReturn(List.of(product));

        List<Product> products = productRepository.findAll();
        assertFalse(products.isEmpty());
        assertEquals("Laptop", products.get(0).getProductTitle());
    }

    @Test
    void testDeleteById() {
        doNothing().when(productRepository).deleteById(1);
        productRepository.deleteById(1);
        verify(productRepository, times(1)).deleteById(1);
    }
}

