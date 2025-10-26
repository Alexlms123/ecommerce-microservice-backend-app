package com.selimhorri.app.unit;

import com.selimhorri.app.domain.OrderItem;
import com.selimhorri.app.domain.id.OrderItemId;
import com.selimhorri.app.dto.OrderDto;
import com.selimhorri.app.dto.OrderItemDto;
import com.selimhorri.app.dto.ProductDto;
import com.selimhorri.app.exception.wrapper.OrderItemNotFoundException;
import com.selimhorri.app.helper.OrderItemMappingHelper;
import com.selimhorri.app.repository.OrderItemRepository;
import com.selimhorri.app.service.impl.OrderItemServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class OrderItemServiceImplTest {

    @Mock
    private OrderItemRepository orderItemRepository;

    @Mock
    private RestTemplate restTemplate;

    @InjectMocks
    private OrderItemServiceImpl orderItemService;

    private OrderItem orderItem;
    private OrderItemDto orderItemDto;
    private OrderItemId orderItemId;
    private ProductDto productDto;
    private OrderDto orderDto;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);

        orderItem = OrderItem.builder()
                .productId(1)
                .orderId(100)
                .orderedQuantity(5)
                .build();

        productDto = ProductDto.builder()
                .productId(1)
                .productTitle("Laptop")
                .priceUnit(2500.0)
                .build();

        orderDto = OrderDto.builder()
                .orderId(100)
                .orderDesc("Order for Laptop")
                .orderFee(50.0)
                .build();

        orderItemDto = OrderItemDto.builder()
                .productId(1)
                .orderId(100)
                .orderedQuantity(5)
                .productDto(productDto)
                .orderDto(orderDto)
                .build();
    }

    @Test
    void testFindAll_success() {
        when(orderItemRepository.findAll()).thenReturn(List.of(orderItem));
        when(restTemplate.getForObject(anyString(), eq(ProductDto.class))).thenReturn(productDto);
        when(restTemplate.getForObject(anyString(), eq(OrderDto.class))).thenReturn(orderDto);

        List<OrderItemDto> result = orderItemService.findAll();

        assertFalse(result.isEmpty());
        assertEquals(1, result.size());
        assertEquals(5, result.get(0).getOrderedQuantity());
        verify(orderItemRepository, times(1)).findAll();
    }

    @Test
    void testFindById_success() {
        when(orderItemRepository.findById(orderItemId)).thenReturn(Optional.of(orderItem));
        when(restTemplate.getForObject(anyString(), eq(ProductDto.class))).thenReturn(productDto);
        when(restTemplate.getForObject(anyString(), eq(OrderDto.class))).thenReturn(orderDto);

        OrderItemDto result = orderItemService.findById(orderItemId);

        assertNotNull(result);
        assertEquals(1, result.getProductId());
        assertEquals(100, result.getOrderId());
        assertEquals("Laptop", result.getProductDto().getProductTitle());
    }

    @Test
    void testFindById_notFound() {
        when(orderItemRepository.findById(orderItemId)).thenReturn(Optional.empty());

        assertThrows(OrderItemNotFoundException.class, () -> orderItemService.findById(orderItemId));
    }

    @Test
    void testSave_success() {
        when(orderItemRepository.save(any(OrderItem.class))).thenReturn(orderItem);

        OrderItemDto result = orderItemService.save(orderItemDto);

        assertNotNull(result);
        assertEquals(5, result.getOrderedQuantity());
        verify(orderItemRepository, times(1)).save(any(OrderItem.class));
    }

    @Test
    void testDeleteById_success() {
        doNothing().when(orderItemRepository).deleteById(orderItemId);

        orderItemService.deleteById(orderItemId);

        verify(orderItemRepository, times(1)).deleteById(orderItemId);
    }
}



