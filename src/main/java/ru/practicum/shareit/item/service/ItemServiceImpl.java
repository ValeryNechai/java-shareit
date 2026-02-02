package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.dao.BookingRepository;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.mapper.CommentMapper;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.dao.CommentRepository;
import ru.practicum.shareit.item.dao.ItemRepository;
import ru.practicum.shareit.item.dto.*;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.dao.UserRepository;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
@Slf4j
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final BookingRepository bookingRepository;
    private final CommentRepository commentRepository;

    @Override
    @Transactional
    public ItemDto createItem(NewItemRequest newItem, Long ownerId) {
        User owner = validateUser(ownerId);
        Item item = Item.builder()
                .name(newItem.getName())
                .description(newItem.getDescription())
                .available(newItem.getAvailable())
                .owner(owner)
                .build();
        Item createdItem = itemRepository.save(item);
        log.debug("Вещь {} успешно добавлена.", item.getName());

        return ItemMapper.mapToItemDto(createdItem);
    }

    @Override
    @Transactional
    public ItemDto updateItem(Long userId, UpdateItemRequest request, Long itemId) {
        validateUser(userId);
        Item item = validateItem(itemId);
        Item updateItem;
        if (!userId.equals(item.getOwner().getId())) {
            log.warn("Вносить изменения в параметры item может только владелец!");
            throw new ValidationException("Вносить изменения в параметры item может только владелец!");
        } else {
            updateItem = ItemMapper.updateItemFields(item, request);
            itemRepository.save(updateItem);
            log.debug("Данные item {} успешно обновлены.", item.getName());
        }

        return ItemMapper.mapToItemDto(updateItem);
    }

    @Override
    public ItemWithCommentsDto getItem(Long itemId) {
        Item item = validateItem(itemId);
        List<Comment> comments = commentRepository.findByItemId(itemId);
        List<Booking> bookingsItems = bookingRepository.findByItemId(itemId);

        return ItemMapper.mapToItemWithCommentsDto(item, bookingsItems, comments);
    }

    @Override
    public Collection<ItemWithCommentsDto> getOwnerItems(Long ownerId) {
        List<Item> ownerItems = itemRepository.findByOwnerId(ownerId);
        List<Booking> bookingsItems = bookingRepository.findAllBookingsByItemByUserId(ownerId);
        List<Comment> commentsItems = commentRepository.findByAuthorId(ownerId);

        return ownerItems.stream()
                .map(item -> ItemMapper.mapToItemWithCommentsDto(item, bookingsItems, commentsItems))
                .collect(Collectors.toList());
    }

    @Override
    public Collection<ItemDto> searchItem(String text) {
        if (text == null || text.trim().isEmpty()) {
            return List.of();
        }
        return itemRepository.searchItem(text).stream()
                .map(ItemMapper::mapToItemDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public CommentDto createComment(Long itemId, NewCommentRequest newComment, Long authorId) {
        Item item = validateItem(itemId);
        User author = validateUser(authorId);
        List<Booking> bookingsByAuthorAndItem =
                bookingRepository.findByBookerIdAndItemIdAndStatus(authorId, itemId);
        if (bookingsByAuthorAndItem.isEmpty()) {
            log.warn("Комментарий пытается оставить пользователь, который не брал вещь в аренду!");
            throw new ValidationException(
                    "Вы не брали эту вещь в аренду и не можете оставить комментарий!"
            );
        } else {
            Booking booking = bookingsByAuthorAndItem.stream()
                    .filter(b -> b.getEnd().isBefore(LocalDateTime.now()))
                    .findFirst().orElseThrow(() -> {
                        log.warn("Комментарий пытается оставить пользователь, " +
                                "у которого нет завершенных бронирований этого предмета.");
                        return new ValidationException("У вас нет завершенных бронирований этого предмета.");
                    });

        }
        Comment comment = Comment.builder()
                .text(newComment.getText())
                .author(author)
                .item(item)
                .createdDate(newComment.getCreated())
                .build();

        Comment createdComment = commentRepository.save(comment);

        return CommentMapper.mapToCommentDto(createdComment);
    }

    private User validateUser(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> {
                    log.warn("Пользователь с id = {} не найден", userId);
                    return new NotFoundException("Пользователь с id = " + userId + " не найден.");
                });
    }

    private Item validateItem(Long itemId) {
        return itemRepository.findById(itemId)
                .orElseThrow(() -> {
                    log.warn("Item с id = {} не найден", itemId);
                    return new NotFoundException("Item с id = " + itemId + " не найдена.");
                });
    }
}
