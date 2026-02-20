package ru.practicum.shareit.request.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.dao.ItemRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.request.dao.ItemRequestRepository;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.NewItemRequestDto;
import ru.practicum.shareit.request.mapper.ItemRequestMapper;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.dao.UserRepository;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ItemRequestServiceImplTest {

    @InjectMocks
    private ItemRequestServiceImpl itemRequestService;

    @Mock
    private ItemRequestRepository itemRequestRepository;

    @Mock
    private ItemRepository itemRepository;

    @Mock
    private UserRepository userRepository;

    @Captor
    private ArgumentCaptor<ItemRequest> itemRequestArgumentCaptor;

    private User requester;
    private User anotherUser;
    private ItemRequest itemRequest;
    private ItemRequest anotherItemRequest;
    private NewItemRequestDto newItemRequestDto;
    private Item item;
    private Item anotherItem;

    @BeforeEach
    void setUp() {
        // Пользователи
        requester = User.builder()
                .id(1L)
                .name("Requester")
                .email("requester@test.com")
                .build();

        anotherUser = User.builder()
                .id(2L)
                .name("Another User")
                .email("another@test.com")
                .build();

        // Запросы на вещи
        itemRequest = ItemRequest.builder()
                .id(10L)
                .description("Need a drill")
                .requester(requester)
                .created(LocalDateTime.now().minusDays(5))
                .build();

        anotherItemRequest = ItemRequest.builder()
                .id(11L)
                .description("Need a ladder")
                .requester(anotherUser)
                .created(LocalDateTime.now().minusDays(3))
                .build();

        // DTO для создания запроса
        newItemRequestDto = new NewItemRequestDto();
        newItemRequestDto.setDescription("Need a new drill");
        newItemRequestDto.setCreated(LocalDateTime.now());

        // Вещи, созданные по запросам
        item = Item.builder()
                .id(100L)
                .name("Drill")
                .description("Powerful drill")
                .available(true)
                .owner(anotherUser)
                .request(itemRequest)
                .build();

        anotherItem = Item.builder()
                .id(101L)
                .name("Ladder")
                .description("Tall ladder")
                .available(true)
                .owner(requester)
                .request(anotherItemRequest)
                .build();
    }

    @Test
    void createItemRequest_whenValid_thenSaveAndReturnRequest() {
        when(userRepository.findById(requester.getId())).thenReturn(Optional.of(requester));
        when(itemRequestRepository.save(any(ItemRequest.class))).thenReturn(itemRequest);

        ItemRequestDto result = itemRequestService.createItemRequest(newItemRequestDto, requester.getId());

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(10L);
        assertThat(result.getDescription()).isEqualTo("Need a drill");
        assertThat(result.getItems()).isEmpty();

        verify(itemRequestRepository).save(itemRequestArgumentCaptor.capture());
        ItemRequest savedRequest = itemRequestArgumentCaptor.getValue();
        assertThat(savedRequest.getDescription()).isEqualTo(newItemRequestDto.getDescription());
        assertThat(savedRequest.getRequester()).isEqualTo(requester);
        assertThat(savedRequest.getCreated()).isEqualTo(newItemRequestDto.getCreated());
    }

    @Test
    void createItemRequest_whenRequesterNotFound_thenThrowNotFoundException() {
        when(userRepository.findById(requester.getId())).thenReturn(Optional.empty());

        NotFoundException exception = assertThrows(NotFoundException.class,
                () -> itemRequestService.createItemRequest(newItemRequestDto, requester.getId()));

        assertThat(exception.getMessage()).contains("Пользователь", "не найден");
        verify(itemRequestRepository, never()).save(any());
    }

    @Test
    void getItemRequestById_whenItemRequestFound_thenReturnedItemRequest() {
        long userId = 111L;
        long itemRequestId = 1L;
        User user = User.builder()
                .id(userId)
                .name("UserName")
                .email("qwert@mail.ru")
                .build();
        ItemRequest expectedItemRequest = ItemRequest.builder()
                .id(itemRequestId)
                .description("description")
                .created(LocalDateTime.now())
                .requester(user)
                .build();

        when(itemRequestRepository.findById(itemRequestId)).thenReturn(Optional.of(expectedItemRequest));

        ItemRequestDto expectedItemRequestDto = ItemRequestMapper.mapToItemRequestDto(expectedItemRequest, List.of());
        ItemRequestDto actualItemRequestDto = itemRequestService.getItemRequestById(itemRequestId);

        assertEquals(expectedItemRequestDto, actualItemRequestDto);
    }

    @Test
    void getItemRequestById_WhenItemRequestNotFound_ThenNotFoundExceptionThrown() {
        long itemRequestId = 1L;
        when(itemRequestRepository.findById(itemRequestId)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> itemRequestService.getItemRequestById(itemRequestId));
    }

    @Test
    void getItemRequestsByRequester_whenRequestsExist_thenReturnRequestsWithItems() {
        List<ItemRequest> requests = List.of(itemRequest);
        List<Item> items = List.of(item);

        when(userRepository.findById(requester.getId())).thenReturn(Optional.of(requester));
        when(itemRequestRepository.findByRequesterIdOrderByCreatedDesc(requester.getId())).thenReturn(requests);
        when(itemRepository.findByRequestIdIn(List.of(itemRequest.getId()))).thenReturn(items);

        List<ItemRequestDto> result = itemRequestService.getItemRequestsByRequester(requester.getId());

        assertThat(result).hasSize(1);
        ItemRequestDto dto = result.get(0);
        assertThat(dto.getId()).isEqualTo(itemRequest.getId());
        assertThat(dto.getDescription()).isEqualTo(itemRequest.getDescription());
        assertThat(dto.getItems()).hasSize(1);
        assertThat(dto.getItems().get(0).getId()).isEqualTo(item.getId());
    }

    @Test
    void getItemRequestsByRequester_whenRequestsExistButNoItems_thenReturnRequestsWithEmptyItems() {
        List<ItemRequest> requests = List.of(itemRequest);

        when(userRepository.findById(requester.getId())).thenReturn(Optional.of(requester));
        when(itemRequestRepository.findByRequesterIdOrderByCreatedDesc(requester.getId())).thenReturn(requests);
        when(itemRepository.findByRequestIdIn(List.of(itemRequest.getId()))).thenReturn(Collections.emptyList());

        List<ItemRequestDto> result = itemRequestService.getItemRequestsByRequester(requester.getId());

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getItems()).isEmpty();
    }

    @Test
    void getItemRequestsByRequester_whenRequesterNotFound_thenThrowNotFoundException() {
        when(userRepository.findById(requester.getId())).thenReturn(Optional.empty());

        NotFoundException exception = assertThrows(NotFoundException.class,
                () -> itemRequestService.getItemRequestsByRequester(requester.getId()));

        assertThat(exception.getMessage()).contains("Пользователь", "не найден");
        verify(itemRequestRepository, never()).findByRequesterIdOrderByCreatedDesc(anyLong());
    }

    @Test
    void getOtherUserRequests_whenRequestsExist_thenReturnOnlyOtherUsersRequests() {
        List<ItemRequest> otherUserRequests = List.of(anotherItemRequest);
        List<Long> requestIds = otherUserRequests.stream()
                .map(ItemRequest::getId)
                .toList();
        List<Item> items = List.of(anotherItem);

        when(userRepository.findById(requester.getId())).thenReturn(Optional.of(requester));
        when(itemRequestRepository.findByRequesterIdNotOrderByCreatedDesc(requester.getId()))
                .thenReturn(otherUserRequests);
        when(itemRepository.findByRequestIdIn(requestIds)).thenReturn(items);

        List<ItemRequestDto> result = itemRequestService.getOtherUserRequests(requester.getId());

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getId()).isEqualTo(anotherItemRequest.getId());
        assertThat(result.get(0).getItems()).hasSize(1);
        assertThat(result.get(0).getItems().get(0).getId()).isEqualTo(anotherItem.getId());
    }

    @Test
    void getOtherUserRequests_whenUserNotFound_thenThrowNotFoundException() {
        when(userRepository.findById(requester.getId())).thenReturn(Optional.empty());

        NotFoundException exception = assertThrows(NotFoundException.class,
                () -> itemRequestService.getOtherUserRequests(requester.getId()));

        assertThat(exception.getMessage()).contains("Пользователь", "не найден");
        verify(itemRequestRepository, never()).findAll();
    }


}