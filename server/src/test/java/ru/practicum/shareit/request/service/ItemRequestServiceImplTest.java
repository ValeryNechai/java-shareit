package ru.practicum.shareit.request.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.dao.ItemRepository;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.request.dao.ItemRequestRepository;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.mapper.ItemRequestMapper;
import ru.practicum.shareit.user.User;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ItemRequestServiceImplTest {

    @InjectMocks
    private ItemRequestServiceImpl itemRequestService;

    @Mock
    private ItemRequestRepository itemRequestRepository;

    @Mock
    private ItemRepository itemRepository;

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
}