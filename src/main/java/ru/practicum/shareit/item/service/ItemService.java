package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.NewItemRequest;
import ru.practicum.shareit.item.dto.OwnerItemsDto;
import ru.practicum.shareit.item.dto.UpdateItemRequest;

import java.util.Collection;

public interface ItemService {
    ItemDto createItem(NewItemRequest item, Long ownerId);

    ItemDto updateItem(Long userId, UpdateItemRequest request, Long itemId);

    ItemDto getItem(Long itemId);

    Collection<OwnerItemsDto> getOwnerItems(Long ownerId);

    Collection<ItemDto> searchItem(String text);
}
