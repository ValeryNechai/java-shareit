package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.*;

import java.util.Collection;

public interface ItemService {
    ItemDto createItem(NewItemDto item, Long ownerId);

    ItemDto updateItem(Long userId, UpdateItemRequest request, Long itemId);

    ItemWithCommentsDto getItem(Long itemId);

    Collection<ItemWithCommentsDto> getOwnerItems(Long ownerId);

    Collection<ItemDto> searchItem(String text);

    CommentDto createComment(Long itemId, NewCommentRequest newComment, Long authorId);
}
