package com.selimhorri.app.unit;

import com.selimhorri.app.domain.Payment;
import com.selimhorri.app.domain.PaymentStatus;
import com.selimhorri.app.dto.OrderDto;
import com.selimhorri.app.dto.PaymentDto;
import com.selimhorri.app.exception.wrapper.PaymentNotFoundException;
import com.selimhorri.app.helper.PaymentMappingHelper;
import com.selimhorri.app.repository.PaymentRepository;
import com.selimhorri.app.service.impl.PaymentServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class PaymentServiceTest {

    @Mock
    private PaymentRepository paymentRepository;

    @Mock
    private RestTemplate restTemplate;

    @InjectMocks
    private PaymentServiceImpl paymentService;

    private Payment payment;
    private PaymentDto paymentDto;
    private OrderDto orderDto;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);

        payment = Payment.builder()
                .paymentId(1)
                .orderId(100)
                .isPayed(true)
                .paymentStatus(PaymentStatus.COMPLETED)
                .build();

        orderDto = OrderDto.builder()
                .orderId(100)
                .orderDate(LocalDateTime.now())
                .orderDesc("Order for electronics")
                .orderFee(50.0)
                .build();

        paymentDto = PaymentDto.builder()
                .paymentId(1)
                .isPayed(true)
                .paymentStatus(PaymentStatus.COMPLETED)
                .orderDto(orderDto)
                .build();
    }

    @Test
    void testFindAll_success() {
        when(paymentRepository.findAll()).thenReturn(List.of(payment));
        when(restTemplate.getForObject(anyString(), eq(OrderDto.class))).thenReturn(orderDto);

        List<PaymentDto> result = paymentService.findAll();

        assertFalse(result.isEmpty());
        assertEquals(1, result.size());
        assertEquals(PaymentStatus.COMPLETED, result.get(0).getPaymentStatus());
        assertTrue(result.get(0).getIsPayed());
        verify(paymentRepository, times(1)).findAll();
    }

    @Test
    void testFindById_success() {
        when(paymentRepository.findById(1)).thenReturn(Optional.of(payment));
        when(restTemplate.getForObject(anyString(), eq(OrderDto.class))).thenReturn(orderDto);

        PaymentDto result = paymentService.findById(1);

        assertNotNull(result);
        assertEquals(1, result.getPaymentId());
        assertEquals(PaymentStatus.COMPLETED, result.getPaymentStatus());
        assertEquals(100, result.getOrderDto().getOrderId());
        verify(paymentRepository, times(1)).findById(1);
    }

    @Test
    void testFindById_notFound() {
        when(paymentRepository.findById(99)).thenReturn(Optional.empty());

        Exception exception = assertThrows(PaymentNotFoundException.class,
                () -> paymentService.findById(99));

        assertTrue(exception.getMessage().contains("Payment with id: 99 not found"));
    }

    @Test
    void testSave_success() {
        when(paymentRepository.save(any(Payment.class))).thenReturn(payment);

        PaymentDto result = paymentService.save(paymentDto);

        assertNotNull(result);
        assertEquals(PaymentStatus.COMPLETED, result.getPaymentStatus());
        verify(paymentRepository, times(1)).save(any(Payment.class));
    }

    @Test
    void testDeleteById_success() {
        doNothing().when(paymentRepository).deleteById(1);

        paymentService.deleteById(1);

        verify(paymentRepository, times(1)).deleteById(1);
    }
}

