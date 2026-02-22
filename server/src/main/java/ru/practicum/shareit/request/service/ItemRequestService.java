package ru.practicum.shareit.request.service;

import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.NewItemRequestDto;

import java.util.List;

public interface ItemRequestService {
    ItemRequestDto createItemRequest(NewItemRequestDto newItemRequestDto, Long requesterId);

    List<ItemRequestDto> getItemRequestsByRequester(Long requesterId);

    List<ItemRequestDto> getOtherUserRequests(Long requesterId);

    ItemRequestDto getItemRequestById(Long requestId);
}
