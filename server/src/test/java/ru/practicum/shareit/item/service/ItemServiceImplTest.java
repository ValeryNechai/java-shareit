package ru.practicum.shareit.item.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.item.dao.ItemRepository;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.UpdateItemRequest;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.dao.UserRepository;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ItemServiceImplTest {

    @Mock
    private ItemRepository itemRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private ItemServiceImpl itemService;

    @Captor
    private ArgumentCaptor<Item> itemArgumentCaptor;

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
}