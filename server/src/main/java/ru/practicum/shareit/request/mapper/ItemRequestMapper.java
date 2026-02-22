package ru.practicum.shareit.request.mapper;

import ru.practicum.shareit.item.dto.ItemForItemRequestDto;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.request.dto.ItemRequestDto;

import java.util.List;

public final class ItemRequestMapper {
    public static ItemRequestDto mapToItemRequestDto(ItemRequest itemRequest,
                                                     List<ItemForItemRequestDto> items) {
        return ItemRequestDto.builder()
                .id(itemRequest.getId())
                .description(itemRequest.getDescription())
                .created(itemRequest.getCreated())
                .items(items)
                .build();
    }
}
