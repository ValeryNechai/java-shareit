package ru.practicum.shareit.request.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.dao.ItemRepository;
import ru.practicum.shareit.item.dto.ItemForItemRequestDto;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.request.dao.ItemRequestRepository;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.NewItemRequestDto;
import ru.practicum.shareit.request.mapper.ItemRequestMapper;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.dao.UserRepository;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class ItemRequestServiceImpl implements ItemRequestService {
    private final ItemRequestRepository itemRequestRepository;
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;

    @Transactional
    @Override
    public ItemRequestDto createItemRequest(NewItemRequestDto newItemRequestDto, Long requesterId) {
        User requester = validateUser(requesterId);
        ItemRequest itemRequest = ItemRequest.builder()
                .description(newItemRequestDto.getDescription())
                .requester(requester)
                .created(newItemRequestDto.getCreated())
                .build();

        ItemRequest createdItemRequest = itemRequestRepository.save(itemRequest);
        log.debug("Добавлен запрос на вещь со следующим описанием: {}.", newItemRequestDto.getDescription());

        return ItemRequestMapper.mapToItemRequestDto(createdItemRequest, List.of());
    }

    @Override
    public List<ItemRequestDto> getItemRequestsByRequester(Long requesterId) {
        validateUser(requesterId);

        List<ItemRequest> itemRequests = itemRequestRepository.findByRequesterIdOrderByCreatedDesc(requesterId);

        if (itemRequests.isEmpty()) {
            return Collections.emptyList();
        }

        List<Long> itemRequestsIds = itemRequests.stream()
                .map(ItemRequest::getId)
                .toList();

        List<Item> items = itemRepository.findByRequestIdIn(itemRequestsIds);

        return createAndSortedListItemRequestDto(itemRequests, items);
    }

    @Override
    public List<ItemRequestDto> getOtherUserRequests(Long requesterId) {
        validateUser(requesterId);
        
        List<ItemRequest> otherUserRequests =
                itemRequestRepository.findByRequesterIdNotOrderByCreatedDesc(requesterId);

        if (otherUserRequests.isEmpty()) {
            return Collections.emptyList();
        }

        List<Long> requestIds = otherUserRequests.stream()
                .map(ItemRequest::getId)
                .toList();
        List<Item> items = itemRepository.findByRequestIdIn(requestIds);

        return createAndSortedListItemRequestDto(otherUserRequests, items);
    }

    @Override
    public ItemRequestDto getItemRequestById(Long requestId) {
        ItemRequest itemRequest = validateItemRequest(requestId);
        List<ItemForItemRequestDto> itemsByItemRequest =
                itemRepository.findByRequestId(requestId).stream()
                        .map(ItemMapper::mapToItemForItemRequestDto)
                        .toList();

        return ItemRequestMapper.mapToItemRequestDto(itemRequest, itemsByItemRequest);
    }

    private User validateUser(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> {
                    log.warn("Пользователь с id = {} не найден", userId);
                    return new NotFoundException("Пользователь с id = " + userId + " не найден.");
                });
    }

    private ItemRequest validateItemRequest(Long requestId) {
        return itemRequestRepository.findById(requestId)
                .orElseThrow(() -> {
                    log.warn("Запрос вещи с id = {} не найден", requestId);
                    return new NotFoundException("Запрос вещи с id = " + requestId + " не найден.");
                });
    }

    private List<ItemRequestDto> createAndSortedListItemRequestDto(List<ItemRequest> itemRequests,
                                                                   List<Item> items) {
        Map<Long, List<ItemForItemRequestDto>> itemsMap = groupItemsByRequestId(items);

        return itemRequests.stream()
                .map(itemRequest -> ItemRequestMapper.mapToItemRequestDto(
                        itemRequest,
                        itemsMap.getOrDefault(itemRequest.getId(), Collections.emptyList())
                ))
                .collect(Collectors.toList());
    }

    private Map<Long, List<ItemForItemRequestDto>> groupItemsByRequestId(List<Item> items) {
        return items.stream()
                .filter(item -> item.getRequest() != null)
                .collect(Collectors.groupingBy(
                        item -> item.getRequest().getId(),
                        Collectors.mapping(ItemMapper::mapToItemForItemRequestDto,
                                Collectors.toList()
                        )
                ));
    }
}
