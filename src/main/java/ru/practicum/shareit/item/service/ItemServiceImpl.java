package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.dao.ItemDao;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.NewItemRequest;
import ru.practicum.shareit.item.dto.UpdateItemRequest;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.dao.UserDao;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {
    private final ItemDao itemDao;
    private final UserDao userDao;

    @Override
    public ItemDto createItem(NewItemRequest newItem, Long ownerId) {
        validateUser(ownerId);
        Item item = itemDao.createItem(newItem, ownerId);
        log.debug("Вещь {} успешно добавлена.", item.getName());

        return ItemMapper.mapToItemDto(item);
    }

    @Override
    public ItemDto updateItem(Long userId, UpdateItemRequest request, Long itemId) {
        validateUser(userId);
        Item item = validateItem(itemId);
        Item updateItem;
        if (!userId.equals(item.getOwnerId())) {
            log.warn("Вносить изменения в параметры item может только владелец!");
            throw new ValidationException("Вносить изменения в параметры item может только владелец!");
        } else {
            updateItem = itemDao.updateItem(userId, request, itemId);
            log.debug("Данные item {} успешно обновлены.", item.getName());
        }

        return ItemMapper.mapToItemDto(updateItem);
    }

    @Override
    public ItemDto getItem(Long itemId) {
        Item item = validateItem(itemId);
        return ItemMapper.mapToItemDto(item);
    }

    @Override
    public Collection<ItemDto> getOwnerItems(Long ownerId) {
        return itemDao.getOwnerItems(ownerId).stream()
                .map(ItemMapper::mapToItemDto)
                .collect(Collectors.toList());
    }

    @Override
    public Collection<ItemDto> searchItem(String text) {
        if (text.isBlank()) {
            return List.of();
        }
        return itemDao.searchItem(text).stream()
                .map(ItemMapper::mapToItemDto)
                .collect(Collectors.toList());
    }

    private void validateUser(Long userId) {
        userDao.getUser(userId)
                .orElseThrow(() -> {
                    log.warn("Пользователь с id = {} не найден", userId);
                    return new NotFoundException("Пользователь с id = " + userId + " не найден.");
                });
    }

    private Item validateItem(Long itemId) {
        return itemDao.getItem(itemId)
                .orElseThrow(() -> {
                    log.warn("Item с id = {} не найден", itemId);
                    return new NotFoundException("Item с id = " + itemId + " не найдена.");
                });
    }
}
