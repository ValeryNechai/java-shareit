package ru.practicum.shareit.booking.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.BookingStatus;
import ru.practicum.shareit.booking.dao.BookingRepository;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.NewBookingRequest;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.dao.ItemRepository;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.dao.UserRepository;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
@Slf4j
@RequiredArgsConstructor
public class BookingServiceImpl implements BookingService {
    private final BookingRepository bookingRepository;
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;

    @Override
    @Transactional
    public BookingDto createBooking(NewBookingRequest newBooking, Long bookerId) {
        User booker = validationUser(bookerId);
        Item item = validateItem(newBooking.getItemId());
        validateBookingDates(newBooking);
        validateBookingPeriod(newBooking.getItemId(), newBooking.getStart(), newBooking.getEnd());

        Booking booking = Booking.builder()
                .booker(booker)
                .item(item)
                .start(newBooking.getStart())
                .end(newBooking.getEnd())
                .status(BookingStatus.WAITING)
                .build();
        Booking createdBooking = bookingRepository.save(booking);

        return BookingMapper.mapToBookingDto(createdBooking);
    }

    @Override
    @Transactional
    public BookingDto updateBookingStatus(Long bookingId, Boolean approved, Long ownerId) {
        Booking booking = validationBooking(bookingId);
        if (!ownerId.equals(booking.getItem().getOwner().getId())) {
            log.warn("Вносить изменения в параметры booking может только владелец вещи!");
            throw new ValidationException("Вносить изменения в параметры booking может только владелец вещи!");
        }
        validationUser(ownerId);
        BookingStatus newStatus = approved ? BookingStatus.APPROVED : BookingStatus.REJECTED;
        booking.setStatus(newStatus);
        Booking updatedBooking = bookingRepository.save(booking);

        return BookingMapper.mapToBookingDto(updatedBooking);
    }

    @Override
    public BookingDto getBooking(Long bookingId, Long userId) {
        Booking booking = validationBooking(bookingId);
        if (!(userId.equals(booking.getBooker().getId()) || userId.equals(booking.getItem().getOwner().getId()))) {
            log.warn("Просматривать данные бронирования может только владелец вещи или автор бронирования!");
            throw new ValidationException(
                    "Просматривать данные бронирования может только владелец вещи или автор бронирования!"
            );
        }

        return BookingMapper.mapToBookingDto(booking);
    }

    @Override
    public Collection<BookingDto> getAllBookingsByUser(BookingStatus status, Long bookerId) {
        validationUser(bookerId);
        if (status == null) {
            return bookingRepository.findByBookerIdOrderByStartDesc(bookerId)
                    .stream()
                    .map(BookingMapper::mapToBookingDto)
                    .collect(Collectors.toList());
        } else {
            return bookingRepository.findByBookerIdAndStatusOrderByStartDesc(bookerId, status)
                    .stream()
                    .map(BookingMapper::mapToBookingDto)
                    .collect(Collectors.toList());
        }
    }

    @Override
    public Collection<BookingDto> getAllBookingsByItemByUserId(BookingStatus status, Long userId) {
        validationUser(userId);
        if (status == null) {
            return bookingRepository.findAllBookingsByItemByUserId(userId)
                    .stream()
                    .map(BookingMapper::mapToBookingDto)
                    .collect(Collectors.toList());
        } else {
            return bookingRepository.findAllBookingsByItemByUserIdAndStatus(userId, status)
                    .stream()
                    .map(BookingMapper::mapToBookingDto)
                    .collect(Collectors.toList());
        }
    }

    private Booking validationBooking(Long bookingId) {
        return bookingRepository.findById(bookingId)
                .orElseThrow(() -> {
                    log.warn("Бронирование с id = {} не найдено", bookingId);
                    return new NotFoundException("Booking с id = " + bookingId + " не найден.");
                });
    }

    private void validateBookingPeriod(Long itemId, LocalDateTime start, LocalDateTime end) {
        if (bookingRepository.existsApprovedBookingInPeriod(itemId, start, end)) {
            log.warn("Период времени уже забронирован.");
            throw new ValidationException("Данное время уже забронировано!");
        }
    }

    private User validationUser(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> {
                    log.warn("Пользователь с id = {} не найден", userId);
                    return new NotFoundException("Пользователь с id = " + userId + " не найден.");
                });
    }

    private Item validateItem(Long itemId) {
        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> {
                    log.warn("Item с id = {} не найден", itemId);
                    return new NotFoundException("Item с id = " + itemId + " не найден.");
                });

        if (!item.getAvailable()) {
            log.warn("Item с id = {} недоступен для бронирования", itemId);
            throw new ValidationException("Item с id = " + itemId + " недоступен для бронирования.");
        }

        return item;
    }

    private void validateBookingDates(NewBookingRequest request) {
        if (!request.getEnd().isAfter(request.getStart())) {
            throw new ValidationException(
                    "Дата окончания должна быть позже даты начала бронирования"
            );
        }

        if (request.getStart().equals(request.getEnd())) {
            throw new ValidationException(
                    "Дата начала и окончания не могут совпадать"
            );
        }
    }
}
