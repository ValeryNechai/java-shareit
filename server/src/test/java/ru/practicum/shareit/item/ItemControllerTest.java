package ru.practicum.shareit.item;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.NewCommentRequest;
import ru.practicum.shareit.item.dto.NewItemDto;
import ru.practicum.shareit.item.dto.UpdateItemRequest;
import ru.practicum.shareit.item.service.ItemService;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ItemController.class)
class ItemControllerTest {

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ItemService itemService;

    @SneakyThrows
    @Test
    void createItem_whenItemIsValid_ThenReturnedOk() {
        NewItemDto itemToCreate = NewItemDto.builder()
                .name("Name")
                .description("Description")
                .available(true)
                .build();
        long ownerId = 1L;
        ItemDto expectedItem = ItemDto.builder()
                .id(0L)
                .name("Name")
                .description("Description")
                .available(true)
                .build();
        when(itemService.createItem(itemToCreate, ownerId)).thenReturn(expectedItem);

        String result = mockMvc.perform(post("/items")
                        .header("X-Sharer-User-Id", ownerId)
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(itemToCreate)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.available").value(true))
                .andReturn()
                .getResponse()
                .getContentAsString();

        assertEquals(objectMapper.writeValueAsString(expectedItem), result);
    }

    @SneakyThrows
    @Test
    void updateItem_whenItemIsValid_ThenReturnedOk() {
        long userId = 1L;
        long itemId = 2L;
        UpdateItemRequest itemToUpdate = new UpdateItemRequest();
        itemToUpdate.setName("NewName");
        ItemDto expectedItem = ItemDto.builder()
                .id(itemId)
                .name("NewName")
                .description("Description")
                .available(true)
                .build();

        when(itemService.updateItem(userId, itemToUpdate, itemId))
                .thenReturn(expectedItem);

        String result = mockMvc.perform(patch("/items/{itemId}", itemId)
                        .header("X-Sharer-User-Id", userId)
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(itemToUpdate)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.available").value(true))
                .andReturn()
                .getResponse()
                .getContentAsString();

        assertEquals(objectMapper.writeValueAsString(expectedItem), result);
    }

    @SneakyThrows
    @Test
    void getItem() {
        long itemId = 1L;

        mockMvc.perform(get("/items/{itemId}", itemId))
                .andDo(print())
                .andExpect(status().isOk());

        verify(itemService).getItem(itemId);
    }

    @SneakyThrows
    @Test
    void getOwnerItems() {
        long ownerId = 1L;

        mockMvc.perform(get("/items")
                        .header("X-Sharer-User-Id", ownerId))
                .andDo(print())
                .andExpect(status().isOk());

        verify(itemService).getOwnerItems(ownerId);
    }

    @SneakyThrows
    @Test
    void searchItem() {
        String text = "Text";

        mockMvc.perform(get("/items/search")
                        .param("text", text))
                .andDo(print())
                .andExpect(status().isOk());

        verify(itemService).searchItem(text);
    }

    @SneakyThrows
    @Test
    void createComment_whenCommentIsNotValid_ThenReturnedBadRequest() {
        long itemId = 1L;
        long authorId = 0L;
        NewCommentRequest commentToCreate = new NewCommentRequest();
        commentToCreate.setText(null);

        mockMvc.perform(post("/items/{itemId}/comment", itemId)
                .header("X-Sharer-User-Id", authorId)
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(commentToCreate)))
                .andExpect(status().isBadRequest());

        verify(itemService, never()).createComment(itemId, commentToCreate, authorId);
    }
}