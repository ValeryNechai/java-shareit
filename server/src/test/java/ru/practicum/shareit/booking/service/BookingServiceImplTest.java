package ru.practicum.shareit.booking.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.dao.BookingRepository;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingStatus;
import ru.practicum.shareit.booking.dto.NewBookingRequest;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.dao.ItemRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.dao.UserRepository;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
class BookingServiceImplTest {

    @InjectMocks
    private BookingServiceImpl bookingService;

    @Mock
    private BookingRepository bookingRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private ItemRepository itemRepository;

    @Test
    void createBooking_WhenBookingDatesValid_thenSavedBooking() {
        Long bookerId = 1L;
        Long itemId = 100L;

        NewBookingRequest request = new NewBookingRequest();
        request.setItemId(itemId);
        request.setStart(LocalDateTime.now().plusDays(1));
        request.setEnd(LocalDateTime.now().plusDays(3));

        User booker = new User();
        booker.setId(bookerId);
        booker.setName("Test User");

        Item item = new Item();
        item.setId(itemId);
        item.setName("Test Item");
        item.setAvailable(true);

        when(userRepository.findById(bookerId)).thenReturn(Optional.of(booker));

        when(itemRepository.findById(itemId)).thenReturn(Optional.of(item));

        Booking expectedBookingToSave = Booking.builder()
                .booker(booker)
                .item(item)
                .start(request.getStart())
                .end(request.getEnd())
                .status(BookingStatus.WAITING)
                .build();

        Booking savedBooking = Booking.builder()
                .id(99L)
                .booker(booker)
                .item(item)
                .start(request.getStart())
                .end(request.getEnd())
                .status(BookingStatus.WAITING)
                .build();

        when(bookingRepository.save(any(Booking.class))).thenReturn(savedBooking);

        BookingDto expectedDto = BookingMapper.mapToBookingDto(savedBooking);

        BookingDto actualDto = bookingService.createBooking(request, bookerId);

        assertEquals(expectedDto, actualDto);
        verify(bookingRepository).save(expectedBookingToSave);
    }

    @Test
    void createBooking_WhenBookingDatesNotValid_thenNotSavedBooking() {
        Long bookerId = 1L;
        Long itemId = 100L;
        NewBookingRequest request = new NewBookingRequest();
        request.setItemId(itemId);
        request.setStart(LocalDateTime.now().plusDays(3));
        request.setEnd(LocalDateTime.now().plusDays(1));

        User booker = new User();
        booker.setId(bookerId);
        booker.setName("Test User");

        Item item = new Item();
        item.setId(itemId);
        item.setName("Test Item");
        item.setAvailable(true);

        when(userRepository.findById(bookerId)).thenReturn(Optional.of(booker));
        when(itemRepository.findById(itemId)).thenReturn(Optional.of(item));

        assertThrows(ValidationException.class,
                () -> bookingService.createBooking(request, bookerId));
        verify(bookingRepository, never()).save(any(Booking.class));
    }
}