package ru.practicum.shareit.item.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.dao.BookingRepository;
import ru.practicum.shareit.booking.dto.BookingStatus;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.dao.CommentRepository;
import ru.practicum.shareit.item.dao.ItemRepository;
import ru.practicum.shareit.item.dto.*;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.request.dao.ItemRequestRepository;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.dao.UserRepository;

import java.time.LocalDateTime;
import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ItemServiceImplTest {

    @Mock
    private ItemRepository itemRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private ItemServiceImpl itemService;

    @Mock
    private ItemRequestRepository itemRequestRepository;

    @Mock
    private CommentRepository commentRepository;

    @Mock
    private BookingRepository bookingRepository;

    @Captor
    private ArgumentCaptor<Item> itemArgumentCaptor;

    @Captor
    private ArgumentCaptor<Comment> commentArgumentCaptor;

    private User owner;
    private User booker;
    private ItemRequest itemRequest;
    private Item item;
    private NewItemDto newItemDto;
    private UpdateItemRequest updateItemRequest;
    private NewCommentRequest newCommentRequest;
    private Comment comment;
    private Booking pastBooking;
    private Booking futureBooking;

    @BeforeEach
    void setUp() {
        owner = new User();
        owner.setId(1L);
        owner.setName("Test Owner");
        owner.setEmail("owner@test.com");

        booker = new User();
        booker.setId(2L);
        booker.setName("Test Booker");
        booker.setEmail("booker@test.com");

        // Запрос на вещь
        itemRequest = new ItemRequest();
        itemRequest.setId(10L);
        itemRequest.setDescription("Test Request");
        itemRequest.setRequester(booker);
        itemRequest.setCreated(LocalDateTime.now().minusDays(5));

        // Вещь
        item = Item.builder()
                .id(100L)
                .name("Test Item")
                .description("Test Description")
                .available(true)
                .owner(owner)
                .request(itemRequest)
                .build();

        // DTO для создания вещи
        newItemDto = new NewItemDto();
        newItemDto.setName("New Test Item");
        newItemDto.setDescription("New Test Description");
        newItemDto.setAvailable(true);
        newItemDto.setRequestId(10L);

        // DTO для обновления вещи
        updateItemRequest = new UpdateItemRequest();
        updateItemRequest.setName("Updated Item Name");
        updateItemRequest.setDescription("Updated Description");
        updateItemRequest.setAvailable(false);

        // DTO для комментария
        newCommentRequest = new NewCommentRequest();
        newCommentRequest.setText("Great item!");
        newCommentRequest.setCreated(LocalDateTime.now());

        // Комментарий
        comment = Comment.builder()
                .id(200L)
                .text("Great item!")
                .author(booker)
                .item(item)
                .createdDate(LocalDateTime.now())
                .build();

        // Бронирования
        pastBooking = Booking.builder()
                .id(300L)
                .booker(booker)
                .item(item)
                .start(LocalDateTime.now().minusDays(10))
                .end(LocalDateTime.now().minusDays(5))
                .status(BookingStatus.APPROVED)
                .build();

        futureBooking = Booking.builder()
                .id(301L)
                .booker(booker)
                .item(item)
                .start(LocalDateTime.now().plusDays(5))
                .end(LocalDateTime.now().plusDays(10))
                .status(BookingStatus.APPROVED)
                .build();
    }

    @Test
    void createItem_whenAllFieldsValid_thenSaveAndReturnItem() {
        NewItemDto testDto = new NewItemDto();
        testDto.setName("Test Item");
        testDto.setDescription("Test Description");
        testDto.setAvailable(true);
        testDto.setRequestId(10L);

        Item expectedItem = Item.builder()
                .id(100L)
                .name("Test Item")
                .description("Test Description")
                .available(true)
                .owner(owner)
                .request(itemRequest)
                .build();

        when(userRepository.findById(owner.getId())).thenReturn(Optional.of(owner));
        when(itemRequestRepository.findById(itemRequest.getId())).thenReturn(Optional.of(itemRequest));
        when(itemRepository.save(any(Item.class))).thenReturn(expectedItem);

        ItemDto result = itemService.createItem(testDto, owner.getId());

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(100L);
        assertThat(result.getName()).isEqualTo("Test Item");
        assertThat(result.getDescription()).isEqualTo("Test Description");
        assertThat(result.getAvailable()).isTrue();

        verify(itemRepository).save(itemArgumentCaptor.capture());
        Item savedItem = itemArgumentCaptor.getValue();

        assertEquals("Test Item", savedItem.getName());
        assertEquals("Test Description", savedItem.getDescription());
        assertTrue(savedItem.getAvailable());
        assertEquals(owner.getId(), savedItem.getOwner().getId());
        assertEquals(itemRequest.getId(), savedItem.getRequest().getId());
    }

    @Test
    void createItem_whenUserNotFound_thenThrowNotFoundException() {
        when(userRepository.findById(owner.getId())).thenReturn(Optional.empty());

        NotFoundException exception = assertThrows(NotFoundException.class,
                () -> itemService.createItem(newItemDto, owner.getId()));

        assertThat(exception.getMessage()).contains("Пользователь", "не найден");
        verify(itemRepository, never()).save(any());
    }

    @Test
    void createItem_whenRequestNotFound_thenThrowNotFoundException() {
        when(userRepository.findById(owner.getId())).thenReturn(Optional.of(owner));
        when(itemRequestRepository.findById(itemRequest.getId())).thenReturn(Optional.empty());

        NotFoundException exception = assertThrows(NotFoundException.class,
                () -> itemService.createItem(newItemDto, owner.getId()));

        assertThat(exception.getMessage()).contains("Запрос", "не найден");
        verify(itemRepository, never()).save(any());
    }

    @Test
    void updateItem_whenItemFound_thenUpdatedAvailableFields() {
        Long userId = 1L;
        Long itemId = 100L;

        User oldUser = new User();
        oldUser.setId(userId);
        oldUser.setName("Test User");

        Item item = new Item();
        item.setId(itemId);
        item.setName("Test Item");
        item.setAvailable(true);
        item.setOwner(oldUser);

        when(userRepository.findById(userId)).thenReturn(Optional.of(oldUser));

        when(itemRepository.findById(itemId)).thenReturn(Optional.of(item));

        UpdateItemRequest request = new UpdateItemRequest();
        request.setName("Test User 2");

        ItemDto actualItemDto = itemService.updateItem(userId, request, itemId);

        verify(itemRepository).save(itemArgumentCaptor.capture());
        Item savedItem = itemArgumentCaptor.getValue();

        assertEquals("Test User 2", savedItem.getName());
    }

    @Test
    void getItem_whenItemFound_thenReturnItemWithCommentsAndBookings() {
        Long itemId = item.getId();

        when(itemRepository.findById(itemId)).thenReturn(Optional.of(item));
        when(commentRepository.findByItemId(itemId)).thenReturn(List.of(comment));
        when(bookingRepository.findByItemId(itemId)).thenReturn(List.of(pastBooking, futureBooking));

        ItemWithCommentsDto result = itemService.getItem(itemId);

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(itemId);
        assertThat(result.getName()).isEqualTo(item.getName());
        assertThat(result.getComments()).hasSize(1);
        assertThat(result.getLastBooking()).isNotNull();
        assertThat(result.getNextBooking()).isNotNull();
    }

    @Test
    void getOwnerItems_whenOwnerHasItems_thenReturnItemsWithBookingsAndComments() {
        Long ownerId = owner.getId();
        List<Item> ownerItems = List.of(item);

        when(itemRepository.findByOwnerId(ownerId)).thenReturn(ownerItems);
        when(bookingRepository.findAllBookingsByItemByUserId(ownerId))
                .thenReturn(List.of(pastBooking, futureBooking));
        when(commentRepository.findByAuthorId(ownerId)).thenReturn(List.of(comment));

        Collection<ItemWithCommentsDto> result = itemService.getOwnerItems(ownerId);

        assertThat(result).hasSize(1);
        ItemWithCommentsDto dto = result.iterator().next();
        assertThat(dto.getId()).isEqualTo(item.getId());
        assertThat(dto.getComments()).hasSize(1);
        assertThat(dto.getLastBooking()).isNotNull();
        assertThat(dto.getNextBooking()).isNotNull();
    }

    @Test
    void searchItem_whenTextIsValid_thenReturnMatchingItems() {
        String searchText = "test";
        List<Item> foundItems = List.of(item);

        when(itemRepository.searchItem(searchText)).thenReturn(foundItems);

        Collection<ItemDto> result = itemService.searchItem(searchText);

        assertThat(result).hasSize(1);
        assertThat(result.iterator().next().getName()).isEqualTo(item.getName());
    }

    @Test
    void searchItem_WhenTextIsNull_ShouldReturnEmptyList() {
        Collection<ItemDto> result = itemService.searchItem(null);

        assertThat(result).isEmpty();
        verify(itemRepository, never()).searchItem(anyString());
    }

    @Test
    void createComment_whenUserHasCompletedBooking_thenSaveComment() {
        Long itemId = item.getId();
        Long authorId = booker.getId();

        when(itemRepository.findById(itemId)).thenReturn(Optional.of(item));
        when(userRepository.findById(authorId)).thenReturn(Optional.of(booker));

        List<Booking> completedBookings = List.of(pastBooking);
        when(bookingRepository.findByBookerIdAndItemIdAndStatusApproved(authorId, itemId))
                .thenReturn(completedBookings);

        when(commentRepository.save(any(Comment.class))).thenReturn(comment);

        CommentDto result = itemService.createComment(itemId, newCommentRequest, authorId);

        assertThat(result).isNotNull();
        assertThat(result.getText()).isEqualTo("Great item!");

        verify(commentRepository).save(commentArgumentCaptor.capture());
        Comment savedComment = commentArgumentCaptor.getValue();
        assertThat(savedComment.getText()).isEqualTo("Great item!");
        assertThat(savedComment.getAuthor()).isEqualTo(booker);
        assertThat(savedComment.getItem()).isEqualTo(item);
    }

    @Test
    void createComment_whenUserHasNoBookings_thenThrowValidationException() {
        Long itemId = item.getId();
        Long authorId = booker.getId();

        when(itemRepository.findById(itemId)).thenReturn(Optional.of(item));
        when(userRepository.findById(authorId)).thenReturn(Optional.of(booker));
        when(bookingRepository.findByBookerIdAndItemIdAndStatusApproved(authorId, itemId))
                .thenReturn(Collections.emptyList());

        ValidationException exception = assertThrows(ValidationException.class,
                () -> itemService.createComment(itemId, newCommentRequest, authorId));

        assertThat(exception.getMessage()).contains("не брали эту вещь в аренду");
        verify(commentRepository, never()).save(any());
    }

    @Test
    void createComment_whenUserHasOnlyFutureBookings_thenThrowValidationException() {
        Long itemId = item.getId();
        Long authorId = booker.getId();

        Booking futureNotCompleted = Booking.builder()
                .id(302L)
                .booker(booker)
                .item(item)
                .start(LocalDateTime.now().plusDays(1))
                .end(LocalDateTime.now().plusDays(5))
                .status(BookingStatus.APPROVED)
                .build();

        when(itemRepository.findById(itemId)).thenReturn(Optional.of(item));
        when(userRepository.findById(authorId)).thenReturn(Optional.of(booker));
        when(bookingRepository.findByBookerIdAndItemIdAndStatusApproved(authorId, itemId))
                .thenReturn(List.of(futureNotCompleted));

        ValidationException exception = assertThrows(ValidationException.class,
                () -> itemService.createComment(itemId, newCommentRequest, authorId));

        assertThat(exception.getMessage()).contains("нет завершенных бронирований");
        verify(commentRepository, never()).save(any());
    }

    @Test
    void createComment_whenItemNotFound_thenThrowNotFoundException() {
        Long itemId = item.getId();
        Long authorId = booker.getId();

        when(itemRepository.findById(itemId)).thenReturn(Optional.empty());

        NotFoundException exception = assertThrows(NotFoundException.class,
                () -> itemService.createComment(itemId, newCommentRequest, authorId));

        assertThat(exception.getMessage()).contains("Item", "не найдена");
        verify(commentRepository, never()).save(any());
    }

    @Test
    void createComment_whenUserNotFound_thenThrowNotFoundException() {
        Long itemId = item.getId();
        Long authorId = booker.getId();

        when(itemRepository.findById(itemId)).thenReturn(Optional.of(item));
        when(userRepository.findById(authorId)).thenReturn(Optional.empty());

        NotFoundException exception = assertThrows(NotFoundException.class,
                () -> itemService.createComment(itemId, newCommentRequest, authorId));

        assertThat(exception.getMessage()).contains("Пользователь", "не найден");
        verify(commentRepository, never()).save(any());
    }
}