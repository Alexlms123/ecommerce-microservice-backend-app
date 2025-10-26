package com.selimhorri.app.unit;

import com.selimhorri.app.domain.Favourite;
import com.selimhorri.app.domain.id.FavouriteId;
import com.selimhorri.app.dto.FavouriteDto;
import com.selimhorri.app.dto.ProductDto;
import com.selimhorri.app.dto.UserDto;
import com.selimhorri.app.exception.wrapper.FavouriteNotFoundException;
import com.selimhorri.app.helper.FavouriteMappingHelper;
import com.selimhorri.app.repository.FavouriteRepository;
import com.selimhorri.app.service.impl.FavouriteServiceImpl;
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

class FavouriteServiceImplTest {

    @Mock
    private FavouriteRepository favouriteRepository;

    @Mock
    private RestTemplate restTemplate;

    @InjectMocks
    private FavouriteServiceImpl favouriteService;

    private Favourite favourite;
    private FavouriteDto favouriteDto;
    private FavouriteId favouriteId;
    private UserDto userDto;
    private ProductDto productDto;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);

        LocalDateTime likeDate = LocalDateTime.now();

        favourite = Favourite.builder()
                .userId(1)
                .productId(10)
                .likeDate(likeDate)
                .build();

        userDto = UserDto.builder()
                .userId(1)
                .firstName("John")
                .lastName("Doe")
                .email("john@example.com")
                .build();

        productDto = ProductDto.builder()
                .productId(10)
                .productTitle("Laptop")
                .priceUnit(2500.0)
                .build();

        favouriteDto = FavouriteDto.builder()
                .userId(1)
                .productId(10)
                .likeDate(likeDate)
                .userDto(userDto)
                .productDto(productDto)
                .build();
    }

    @Test
    void testFindAll_success() {
        when(favouriteRepository.findAll()).thenReturn(List.of(favourite));
        when(restTemplate.getForObject(anyString(), eq(UserDto.class))).thenReturn(userDto);
        when(restTemplate.getForObject(anyString(), eq(ProductDto.class))).thenReturn(productDto);

        List<FavouriteDto> result = favouriteService.findAll();

        assertFalse(result.isEmpty());
        assertEquals(1, result.size());
        assertEquals(1, result.get(0).getUserId());
        assertEquals(10, result.get(0).getProductId());
        assertEquals("John", result.get(0).getUserDto().getFirstName());
        assertEquals("Laptop", result.get(0).getProductDto().getProductTitle());
        verify(favouriteRepository, times(1)).findAll();
    }

    @Test
    void testFindById_success() {
        when(favouriteRepository.findById(favouriteId)).thenReturn(Optional.of(favourite));
        when(restTemplate.getForObject(anyString(), eq(UserDto.class))).thenReturn(userDto);
        when(restTemplate.getForObject(anyString(), eq(ProductDto.class))).thenReturn(productDto);

        FavouriteDto result = favouriteService.findById(favouriteId);

        assertNotNull(result);
        assertEquals(1, result.getUserId());
        assertEquals(10, result.getProductId());
        assertEquals("John", result.getUserDto().getFirstName());
        assertEquals("Laptop", result.getProductDto().getProductTitle());
        verify(favouriteRepository, times(1)).findById(favouriteId);
    }

    @Test
    void testFindById_notFound() {
        when(favouriteRepository.findById(favouriteId)).thenReturn(Optional.empty());

        Exception exception = assertThrows(FavouriteNotFoundException.class,
                () -> favouriteService.findById(favouriteId));

        assertTrue(exception.getMessage().contains("Favourite with id:"));
        assertTrue(exception.getMessage().contains("not found!"));
    }

    @Test
    void testSave_success() {
        when(favouriteRepository.save(any(Favourite.class))).thenReturn(favourite);

        FavouriteDto result = favouriteService.save(favouriteDto);

        assertNotNull(result);
        assertEquals(1, result.getUserId());
        assertEquals(10, result.getProductId());
        verify(favouriteRepository, times(1)).save(any(Favourite.class));
    }

    @Test
    void testDeleteById_success() {
        doNothing().when(favouriteRepository).deleteById(favouriteId);

        favouriteService.deleteById(favouriteId);

        verify(favouriteRepository, times(1)).deleteById(favouriteId);
    }
}