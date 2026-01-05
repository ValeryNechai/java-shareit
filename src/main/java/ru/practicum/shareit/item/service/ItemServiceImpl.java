package ru.practicum.shareit.item.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.dao.ItemDao;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.NewItemRequest;
import ru.practicum.shareit.item.dto.OwnerItemsDto;
import ru.practicum.shareit.item.dto.UpdateItemRequest;
import ru.practicum.shareit.user.dao.UserDao;

import java.util.Collection;

@Service
@Slf4j
public class ItemServiceImpl implements ItemService {
    private final ItemDao itemDao;
    private final UserDao userDao;

    @Autowired
    public ItemServiceImpl(ItemDao itemDao, UserDao userDao) {
        this.itemDao = itemDao;
        this.userDao = userDao;
    }

    @Override
    public ItemDto createItem(NewItemRequest item, Long ownerId) {
        try {
            userDao.getUser(ownerId);
        } catch (NotFoundException exception) {
            log.warn("Пользователь с id = {} не найден, невозможно создать предмет", ownerId);
            throw exception;
        }

        return itemDao.createItem(item, ownerId);
    }

    @Override
    public ItemDto updateItem(Long userId, UpdateItemRequest request, Long itemId) {
        try {
            userDao.getUser(userId);
        } catch (NotFoundException exception) {
            log.warn("Пользователь с id = {} не найден, невозможно создать предмет", userId);
            throw exception;
        }

        return itemDao.updateItem(userId, request, itemId);
    }

    @Override
    public ItemDto getItem(Long itemId) {
        return itemDao.getItem(itemId);
    }

    @Override
    public Collection<OwnerItemsDto> getOwnerItems(Long ownerId) {
        return itemDao.getOwnerItems(ownerId);
    }

    @Override
    public Collection<ItemDto> searchItem(String text) {
        return itemDao.searchItem(text);
    }
}
