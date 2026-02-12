package ru.practicum.shareit.request;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.NewItemRequestDto;
import ru.practicum.shareit.request.service.ItemRequestService;

import java.util.List;

/**
 * TODO Sprint add-item-requests.
 */
@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/requests")
public class ItemRequestController {
    private final ItemRequestService itemRequestService;

    @PostMapping
    public ItemRequestDto createItemRequest(@Valid @RequestBody NewItemRequestDto newItemRequestDto,
                                            @RequestHeader("X-Sharer-User-Id") Long requesterId) {
        return itemRequestService.createItemRequest(newItemRequestDto, requesterId);
    }

    @GetMapping
    public List<ItemRequestDto> getItemRequestsByRequester(@RequestHeader("X-Sharer-User-Id") Long requesterId) {
        return itemRequestService.getItemRequestsByRequester(requesterId);
    }

    @GetMapping("/all")
    public List<ItemRequestDto> getAllItemRequests(@RequestHeader("X-Sharer-User-Id") Long requesterId) {
        return itemRequestService.getAllItemRequests(requesterId);
    }

    @GetMapping("/{requestId}")
    public ItemRequestDto getItemRequestById(@PathVariable Long requestId) {
        return itemRequestService.getItemRequestById(requestId);
    }
}
