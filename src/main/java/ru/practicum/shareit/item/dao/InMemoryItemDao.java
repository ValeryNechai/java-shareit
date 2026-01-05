package ru.practicum.shareit.item.dao;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.NewItemRequest;
import ru.practicum.shareit.item.dto.OwnerItemsDto;
import ru.practicum.shareit.item.dto.UpdateItemRequest;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
@Slf4j
public class InMemoryItemDao implements ItemDao {
    private final Map<Long, Item> items = new HashMap<>();
    private static long id = 0;

    @Override
    public ItemDto createItem(NewItemRequest newItem, Long ownerId) {
        Item item = Item.builder()
                .id(++id)
                .name(newItem.getName())
                .description(newItem.getDescription())
                .ownerId(ownerId)
                .available(newItem.getAvailable())
                .build();
        items.put(item.getId(), item);
        log.debug("Вещь {} успешно добавлена.", item.getName());
        return ItemMapper.mapToItemDto(item);
    }

    @Override
    public ItemDto updateItem(Long userId, UpdateItemRequest request, Long itemId) {
        try {
            Item item = items.get(itemId);
            if (userId.equals(item.getOwnerId())) {
                Item updateItem = ItemMapper.updateItemFields(item, request);
                log.debug("Данные item {} успешно обновлены.", item.getName());

                return ItemMapper.mapToItemDto(updateItem);
            } else {
                log.warn("Вносить изменения в параметры item может только владелец!");
                throw new ValidationException("Вносить изменения в параметры item может только владелец!");
            }

        } catch (NotFoundException exc) {
            log.warn("Item с id = {} не найден", itemId);
            throw new NotFoundException("Item с id = " + itemId + " не найдена.");
        }
    }

    @Override
    public ItemDto getItem(Long itemId) {
        return ItemMapper.mapToItemDto(items.get(itemId));
    }

    @Override
    public Collection<OwnerItemsDto> getOwnerItems(Long ownerId) {
        return items.values().stream()
                .filter(item -> ownerId.equals(item.getOwnerId()))
                .map(ItemMapper::mapToOwnerItems)
                .collect(Collectors.toList());
    }

    @Override
    public Collection<ItemDto> searchItem(String text) {
        if (text.isBlank()) {
            return List.of();
        }
        String searchText = text.toLowerCase();

        return items.values().stream()
                .filter(Item::getAvailable)
                .filter(item -> item.getName().toLowerCase().contains(searchText) ||
                            item.getDescription().toLowerCase().contains(searchText))
                .map(ItemMapper::mapToItemDto)
                .collect(Collectors.toList());
    }
}
