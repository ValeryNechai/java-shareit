package ru.practicum.shareit.item.dao;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.item.dto.NewItemRequest;
import ru.practicum.shareit.item.dto.UpdateItemRequest;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;

import java.util.*;
import java.util.stream.Collectors;

@Component
public class InMemoryItemDao implements ItemDao {
    private final Map<Long, Item> items = new HashMap<>();
    private long id = 0;

    @Override
    public Item createItem(NewItemRequest newItem, Long ownerId) {
        Item item = Item.builder()
                .id(++id)
                .name(newItem.getName())
                .description(newItem.getDescription())
                .ownerId(ownerId)
                .available(newItem.getAvailable())
                .build();
        items.put(item.getId(), item);
        return item;
    }

    @Override
    public Item updateItem(Long userId, UpdateItemRequest request, Long itemId) {
        return ItemMapper.updateItemFields(items.get(itemId), request);
    }

    @Override
    public Optional<Item> getItem(Long itemId) {
        return Optional.ofNullable(items.get(itemId));
    }

    @Override
    public Collection<Item> getOwnerItems(Long ownerId) {
        return items.values().stream()
                .filter(item -> ownerId.equals(item.getOwnerId()))
                .collect(Collectors.toList());
    }

    @Override
    public Collection<Item> searchItem(String text) {
        String searchText = text.toLowerCase();

        return items.values().stream()
                .filter(Item::getAvailable)
                .filter(item -> item.getName().toLowerCase().contains(searchText) ||
                            item.getDescription().toLowerCase().contains(searchText))
                .collect(Collectors.toList());
    }
}
