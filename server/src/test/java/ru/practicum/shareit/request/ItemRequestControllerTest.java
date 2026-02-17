package ru.practicum.shareit.request;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.NewItemRequestDto;
import ru.practicum.shareit.request.service.ItemRequestService;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ItemRequestController.class)
class ItemRequestControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private ItemRequestService itemRequestService;

    @SneakyThrows
    @Test
    void createItemRequest_whenItemRequestIsValid_thenReturnOk() {
        long requesterId = 1L;
        NewItemRequestDto itemRequestToCreate = new NewItemRequestDto();
        itemRequestToCreate.setDescription("Description");

        ItemRequestDto expectedItemRequest = ItemRequestDto.builder()
                .id(0L)
                .description("Description")
                .created(itemRequestToCreate.getCreated())
                .build();
        when(itemRequestService.createItemRequest(itemRequestToCreate, requesterId))
                .thenReturn(expectedItemRequest);

        String result = mockMvc.perform(post("/requests")
                        .header("X-Sharer-User-Id", requesterId)
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(itemRequestToCreate)))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        assertEquals(objectMapper.writeValueAsString(expectedItemRequest), result);
    }

    @SneakyThrows
    @Test
    void getItemRequestsByRequester() {
        long requesterId = 1L;

        mockMvc.perform(get("/requests")
                .header("X-Sharer-User-Id", requesterId))
                .andDo(print())
                .andExpect(status().isOk());

        verify(itemRequestService, times(1)).getItemRequestsByRequester(requesterId);
    }

    @SneakyThrows
    @Test
    void getAllItemRequests() {
        long requesterId = 1L;

        mockMvc.perform(get("/requests/all")
                        .header("X-Sharer-User-Id", requesterId))
                .andDo(print())
                .andExpect(status().isOk());

        verify(itemRequestService, times(1)).getAllItemRequests(requesterId);
    }

    @SneakyThrows
    @Test
    void getItemRequestById() {
        long requestId = 1L;

        mockMvc.perform(get("/requests/{requestId}", requestId))
                .andDo(print())
                .andExpect(status().isOk());

        verify(itemRequestService, times(1)).getItemRequestById(requestId);
    }
}