package com.selimhorri.app.unit;

import com.selimhorri.app.domain.Cart;
import com.selimhorri.app.domain.Order;
import com.selimhorri.app.dto.CartDto;
import com.selimhorri.app.dto.OrderDto;
import com.selimhorri.app.exception.wrapper.OrderNotFoundException;
import com.selimhorri.app.helper.OrderMappingHelper;
import com.selimhorri.app.repository.OrderRepository;
import com.selimhorri.app.service.impl.OrderServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class OrderServiceImplTest {

    @Mock
    private OrderRepository orderRepository;

    @InjectMocks
    private OrderServiceImpl orderService;

    private Order order;
    private OrderDto orderDto;
    private Cart cart;
    private CartDto cartDto;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);

        cart = Cart.builder()
                .cartId(10)
                .userId(1)
                .build();

        order = Order.builder()
                .orderId(1)
                .orderDate(LocalDateTime.now())
                .orderDesc("Electronics Order")
                .orderFee(50.0)
                .cart(cart)
                .build();

        cartDto = CartDto.builder()
                .cartId(10)
                .userId(1)
                .build();

        orderDto = OrderDto.builder()
                .orderId(1)
                .orderDate(LocalDateTime.now())
                .orderDesc("Electronics Order")
                .orderFee(50.0)
                .cartDto(cartDto)
                .build();
    }

    @Test
    void testFindAll_success() {
        when(orderRepository.findAll()).thenReturn(List.of(order));

        List<OrderDto> result = orderService.findAll();

        assertFalse(result.isEmpty());
        assertEquals(1, result.size());
        assertEquals("Electronics Order", result.get(0).getOrderDesc());
        assertEquals(50.0, result.get(0).getOrderFee());
        verify(orderRepository, times(1)).findAll();
    }

    @Test
    void testFindById_success() {
        when(orderRepository.findById(1)).thenReturn(Optional.of(order));

        OrderDto result = orderService.findById(1);

        assertNotNull(result);
        assertEquals(1, result.getOrderId());
        assertEquals("Electronics Order", result.getOrderDesc());
        assertEquals(10, result.getCartDto().getCartId());
        verify(orderRepository, times(1)).findById(1);
    }

    @Test
    void testFindById_notFound() {
        when(orderRepository.findById(99)).thenReturn(Optional.empty());

        Exception exception = assertThrows(OrderNotFoundException.class,
                () -> orderService.findById(99));

        assertTrue(exception.getMessage().contains("Order with id: 99 not found"));
    }

    @Test
    void testSave_success() {
        when(orderRepository.save(any(Order.class))).thenReturn(order);

        OrderDto result = orderService.save(orderDto);

        assertNotNull(result);
        assertEquals("Electronics Order", result.getOrderDesc());
        assertEquals(50.0, result.getOrderFee());
        verify(orderRepository, times(1)).save(any(Order.class));
    }

    @Test
    void testDeleteById_success() {
        when(orderRepository.findById(1)).thenReturn(Optional.of(order));
        doNothing().when(orderRepository).delete(any(Order.class));

        orderService.deleteById(1);

        verify(orderRepository, times(1)).findById(1);
        verify(orderRepository, times(1)).delete(any(Order.class));
    }
}

