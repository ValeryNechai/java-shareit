package ru.practicum.shareit.item.dao;

import ru.practicum.shareit.item.dto.NewItemRequest;
import ru.practicum.shareit.item.dto.UpdateItemRequest;
import ru.practicum.shareit.item.model.Item;

import java.util.Collection;
import java.util.Optional;

public interface ItemDao {
    Item createItem(NewItemRequest item, Long ownerId);

    Item updateItem(Long userId, UpdateItemRequest request, Long itemId);

    Optional<Item> getItem(Long itemId);

    Collection<Item> getOwnerItems(Long ownerId);

    Collection<Item> searchItem(String text);
}
