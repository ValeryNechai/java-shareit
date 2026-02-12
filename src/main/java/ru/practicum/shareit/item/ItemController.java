package ru.practicum.shareit.item;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.*;
import ru.practicum.shareit.item.service.ItemService;

import java.util.Collection;

/**
 * TODO Sprint add-controllers.
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/items")
public class ItemController {
    private final ItemService itemService;

    @PostMapping
    public ItemDto createItem(@Valid @RequestBody NewItemDto item,
                              @RequestHeader("X-Sharer-User-Id") Long ownerId) {
        return itemService.createItem(item, ownerId);
    }

    @PatchMapping("/{itemId}")
    public ItemDto updateItem(@RequestHeader("X-Sharer-User-Id") Long userId,
                              @Valid @RequestBody UpdateItemRequest request,
                              @PathVariable Long itemId) {
        return itemService.updateItem(userId, request, itemId);
    }

    @GetMapping("/{itemId}")
    public ItemWithCommentsDto getItem(@PathVariable Long itemId) {
        return itemService.getItem(itemId);
    }

    @GetMapping
    public Collection<ItemWithCommentsDto> getOwnerItems(@RequestHeader("X-Sharer-User-Id") Long ownerId) {
        return itemService.getOwnerItems(ownerId);
    }

    @GetMapping("/search")
    public Collection<ItemDto> searchItem(@RequestParam String text) {
        return itemService.searchItem(text);
    }

    @PostMapping("{itemId}/comment")
    public CommentDto createComment(@PathVariable Long itemId,
                                    @Valid @RequestBody NewCommentRequest newComment,
                                    @RequestHeader("X-Sharer-User-Id") Long authorId) {
        return itemService.createComment(itemId, newComment, authorId);
    }
}
