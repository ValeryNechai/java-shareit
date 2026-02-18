package ru.practicum.shareit.booking.service;

import org.junit.jupiter.api.BeforeEach;
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
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.dao.ItemRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.dao.UserRepository;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
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

    private User booker;
    private User owner;
    private Item item;
    private NewBookingRequest request;
    private List<Booking> bookings;
    private Booking waitingBooking;
    private Booking approvedBooking;
    private Booking rejectedBooking;

    @BeforeEach
    void createBookerAndItem() {
        Long bookerId = 1L;
        Long ownerId = 2L;
        Long itemId = 100L;
        Long bookingId = 10L;

        request = new NewBookingRequest();
        request.setItemId(itemId);
        request.setStart(LocalDateTime.now().plusDays(1));
        request.setEnd(LocalDateTime.now().plusDays(3));

        booker = new User();
        booker.setId(bookerId);
        booker.setName("Test Booker");

        owner = new User();
        owner.setId(ownerId);
        owner.setName("Test Owner");


        item = new Item();
        item.setId(itemId);
        item.setName("Test Item");
        item.setAvailable(true);
        item.setOwner(owner);

        bookings = List.of(
                createBooking(1L, BookingStatus.WAITING),
                createBooking(2L, BookingStatus.APPROVED),
                createBooking(3L, BookingStatus.REJECTED)
        );

        waitingBooking = Booking.builder()
                .id(bookingId)
                .booker(booker)
                .item(item)
                .start(request.getStart())
                .end(request.getEnd())
                .status(BookingStatus.WAITING)
                .build();

        approvedBooking = Booking.builder()
                .id(bookingId)
                .booker(booker)
                .item(item)
                .start(request.getStart())
                .end(request.getEnd())
                .status(BookingStatus.APPROVED)
                .build();

        rejectedBooking = Booking.builder()
                .id(bookingId)
                .booker(booker)
                .item(item)
                .start(request.getStart())
                .end(request.getEnd())
                .status(BookingStatus.REJECTED)
                .build();
    }

    private Booking createBooking(Long id, BookingStatus status) {
        return Booking.builder()
                .id(id)
                .booker(booker)
                .item(item)
                .start(request.getStart())
                .end(request.getEnd())
                .status(status)
                .build();
    }

    @Test
    void createBooking_WhenBookingDatesValid_thenSavedBooking() {
        when(userRepository.findById(booker.getId())).thenReturn(Optional.of(booker));
        when(itemRepository.findById(item.getId())).thenReturn(Optional.of(item));

        Booking expectedBooking = Booking.builder()
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

        BookingDto actualDto = bookingService.createBooking(request, booker.getId());

        assertEquals(expectedDto, actualDto);
        verify(bookingRepository).save(any(Booking.class));
    }

    @Test
    void createBooking_WhenBookingDatesNotValid_thenNotSavedBooking() {
        when(userRepository.findById(booker.getId())).thenReturn(Optional.of(booker));
        when(itemRepository.findById(item.getId())).thenReturn(Optional.of(item));

        request.setStart(LocalDateTime.now().plusDays(3));
        request.setEnd(LocalDateTime.now().plusDays(1));

        assertThrows(ValidationException.class,
                () -> bookingService.createBooking(request, booker.getId()));
        verify(bookingRepository, never()).save(any(Booking.class));
    }

    @Test
    void createBooking_whenItemNotAvailable_thenThrowValidationException() {
        item.setAvailable(false);
        when(userRepository.findById(booker.getId())).thenReturn(Optional.of(booker));
        when(itemRepository.findById(item.getId())).thenReturn(Optional.of(item));

        assertThrows(ValidationException.class,
                () -> bookingService.createBooking(request, booker.getId()));
        verify(bookingRepository, never()).save(any(Booking.class));
    }

    @Test
    void getBookingById_whenBookingFound_thenReturnedBooking() {
        Long bookingId = 1L;
        Booking expectedBooking = Booking.builder()
                .id(bookingId)
                .booker(booker)
                .item(item)
                .start(request.getStart())
                .end(request.getEnd())
                .status(BookingStatus.WAITING)
                .build();

        when(bookingRepository.findById(bookingId)).thenReturn(Optional.of(expectedBooking));

        BookingDto expectedBookingDto = BookingMapper.mapToBookingDto(expectedBooking);

        BookingDto actualBooking = bookingService.getBooking(bookingId, booker.getId());

        assertEquals(expectedBookingDto, actualBooking);
    }

    @Test
    void createBooking_whenUserNotFound_thenThrowNotFoundException() {
        when(userRepository.findById(booker.getId())).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class,
                () -> bookingService.createBooking(request, booker.getId()));
        verify(bookingRepository, never()).save(any(Booking.class));
    }

    @Test
    void createBooking_whenItemNotFound_thenThrowNotFoundException() {
        when(userRepository.findById(booker.getId())).thenReturn(Optional.of(booker));
        when(itemRepository.findById(item.getId())).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class,
                () -> bookingService.createBooking(request, booker.getId()));
        verify(bookingRepository, never()).save(any(Booking.class));
    }

    @Test
    void getAllBookingsByUser_whenStatusIsNull_thenReturnAllBookings() {
        Long bookerId = booker.getId();
        when(userRepository.findById(bookerId)).thenReturn(Optional.of(booker));
        when(bookingRepository.findByBookerIdOrderByStartDesc(bookerId)).thenReturn(bookings);

        Collection<BookingDto> result = bookingService.getAllBookingsByUser(null, bookerId);

        assertThat(result).hasSize(3);
        assertThat(result)
                .extracting(BookingDto::getId)
                .containsExactly(1L, 2L, 3L);

        verify(bookingRepository, times(1)).findByBookerIdOrderByStartDesc(bookerId);
        verify(bookingRepository, never()).findByBookerIdAndStatusOrderByStartDesc(anyLong(), any());
    }

    @Test
    void getAllBookingsByUser_whenStatusIsNullAndNoBookings_thenReturnEmptyList() {
        Long bookerId = booker.getId();
        when(userRepository.findById(bookerId)).thenReturn(Optional.of(booker));
        when(bookingRepository.findByBookerIdOrderByStartDesc(bookerId)).thenReturn(Collections.emptyList());

        Collection<BookingDto> result = bookingService.getAllBookingsByUser(null, bookerId);

        assertThat(result).isEmpty();
        verify(bookingRepository).findByBookerIdOrderByStartDesc(bookerId);
    }

    @Test
    void getAllBookingsByUser_whenStatusIsWaiting_thanReturnWaitingBookings() {
        Long bookerId = booker.getId();
        BookingStatus status = BookingStatus.WAITING;
        List<Booking> waitingBookings = List.of(
                createBooking(1L, BookingStatus.WAITING),
                createBooking(4L, BookingStatus.WAITING)
        );

        when(userRepository.findById(bookerId)).thenReturn(Optional.of(booker));
        when(bookingRepository.findByBookerIdAndStatusOrderByStartDesc(bookerId, status))
                .thenReturn(waitingBookings);

        Collection<BookingDto> result = bookingService.getAllBookingsByUser(status, bookerId);

        assertThat(result).hasSize(2);
        assertThat(result)
                .extracting(BookingDto::getStatus)
                .allMatch(s -> s == BookingStatus.WAITING);

        verify(bookingRepository).findByBookerIdAndStatusOrderByStartDesc(bookerId, status);
    }

    @Test
    void updateBookingStatus_whenApprovedTrue_thenChangeStatusToApproved() {
        Long bookingId = 10L;
        Long ownerId = 2L;

        when(bookingRepository.findById(bookingId)).thenReturn(Optional.of(waitingBooking));
        when(userRepository.findById(ownerId)).thenReturn(Optional.of(owner));
        when(bookingRepository.save(any(Booking.class))).thenReturn(approvedBooking);

        BookingDto result = bookingService.updateBookingStatus(bookingId, true, ownerId);

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(bookingId);
        assertThat(result.getStatus()).isEqualTo(BookingStatus.APPROVED);

        verify(bookingRepository, times(1)).save(argThat(booking ->
                booking.getStatus() == BookingStatus.APPROVED
        ));
    }

    @Test
    void updateBookingStatus_whenApprovedFalse_thanChangeStatusToRejected() {
        Long bookingId = 10L;
        Long ownerId = 2L;

        when(bookingRepository.findById(bookingId)).thenReturn(Optional.of(waitingBooking));
        when(userRepository.findById(ownerId)).thenReturn(Optional.of(owner));
        when(bookingRepository.save(any(Booking.class))).thenReturn(rejectedBooking);

        BookingDto result = bookingService.updateBookingStatus(bookingId, false, ownerId);

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(bookingId);
        assertThat(result.getStatus()).isEqualTo(BookingStatus.REJECTED);

        verify(bookingRepository, times(1)).save(argThat(booking ->
                booking.getStatus() == BookingStatus.REJECTED
        ));
    }

    @Test
    void updateBookingStatus_whenBookingNotFound_thenThrowNotFoundException() {
        Long bookingId = 10L;
        Long ownerId = 2L;

        when(bookingRepository.findById(bookingId)).thenReturn(Optional.empty());

        NotFoundException exception = assertThrows(NotFoundException.class,
                () -> bookingService.updateBookingStatus(bookingId, true, ownerId));

        assertThat(exception.getMessage()).contains("не найден");
        verify(bookingRepository, never()).save(any());
    }

    @Test
    void updateBookingStatus_whenUserIsNotOwner_thenThrowValidationException() {
        Long bookingId = 10L;
        Long wrongOwnerId = 111L;

        when(bookingRepository.findById(bookingId)).thenReturn(Optional.of(waitingBooking));

        ValidationException exception = assertThrows(ValidationException.class,
                () -> bookingService.updateBookingStatus(bookingId, true, wrongOwnerId));

        assertThat(exception.getMessage())
                .isEqualTo("Вносить изменения в параметры booking может только владелец вещи!");

        verify(bookingRepository, never()).save(any());
        verify(userRepository, never()).findById(any());
    }

    @Test
    void getAllBookingsByItemByUserId_whenStatusIsNull_thenReturnAllItemBookings() {
        Long userId = 2L;
        when(userRepository.findById(userId)).thenReturn(Optional.of(owner));
        when(bookingRepository.findAllBookingsByItemByUserId(userId)).thenReturn(bookings);

        Collection<BookingDto> result = bookingService.getAllBookingsByItemByUserId(null, userId);

        assertThat(result).hasSize(3);
        assertThat(result)
                .extracting(BookingDto::getId)
                .containsExactly(1L, 2L, 3L);

        verify(bookingRepository, times(1)).findAllBookingsByItemByUserId(userId);
        verify(bookingRepository, never()).findAllBookingsByItemByUserIdAndStatus(anyLong(), any());
    }

    @Test
    void getAllBookingsByItemByUserId_whenStatusIsNullAndNoBookings_thenReturnEmptyList() {
        Long userId = 2L;

        when(userRepository.findById(userId)).thenReturn(Optional.of(owner));
        when(bookingRepository.findAllBookingsByItemByUserId(userId)).thenReturn(Collections.emptyList());

        Collection<BookingDto> result = bookingService.getAllBookingsByItemByUserId(null, userId);

        assertThat(result).isEmpty();
        verify(bookingRepository).findAllBookingsByItemByUserId(userId);
    }

    @Test
    void getAllBookingsByItemByUserId_whenStatusIsApproved_thenReturnApprovedBookings() {
        Long userId = 2L;

        BookingStatus status = BookingStatus.APPROVED;
        List<Booking> approvedBookings = List.of(
                createBooking(2L, BookingStatus.APPROVED)
        );

        when(userRepository.findById(userId)).thenReturn(Optional.of(owner));
        when(bookingRepository.findAllBookingsByItemByUserIdAndStatus(userId, status))
                .thenReturn(approvedBookings);

        Collection<BookingDto> result = bookingService.getAllBookingsByItemByUserId(status, userId);

        assertThat(result).hasSize(1);
        assertThat(result.iterator().next().getStatus()).isEqualTo(BookingStatus.APPROVED);
    }
}