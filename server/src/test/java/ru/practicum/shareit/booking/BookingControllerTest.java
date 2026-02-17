package ru.practicum.shareit.booking;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingStatus;
import ru.practicum.shareit.booking.dto.NewBookingRequest;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.user.service.UserService;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(BookingController.class)
class BookingControllerTest {

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private BookingService bookingService;

    @MockBean
    private ItemService itemService;

    @MockBean
    private UserService userService;

    @SneakyThrows
    @Test
    void createBooking_whenBookingNotValid_ThenReturnedBadRequest() {
        long bookerId = 0L;
        NewBookingRequest bookingToCreate = new NewBookingRequest();
        bookingToCreate.setItemId(null);
        bookingToCreate.setStart(LocalDateTime.now().plusDays(1));
        bookingToCreate.setEnd(LocalDateTime.now().plusDays(3));

        mockMvc.perform(post("/bookings")
                        .header("X-Sharer-User-Id", bookerId)
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(bookingToCreate)))
                .andExpect(status().isBadRequest());

        verify(bookingService, never()).createBooking(bookingToCreate, bookerId);
    }

    @SneakyThrows
    @Test
    void updateBookingStatus_whenUpdateApproved_ThenReturnedOk() {
        long bookingId = 0L;
        boolean approved = true;
        long ownerId = 1L;
        BookingDto expectedBooking = BookingDto.builder()
                .id(bookingId)
                .status(approved ? BookingStatus.APPROVED : BookingStatus.REJECTED)
                .start(LocalDateTime.now())
                .end(LocalDateTime.now().plusDays(1))
                .build();

        when(bookingService.updateBookingStatus(bookingId, approved, ownerId))
                .thenReturn(expectedBooking);

        String result = mockMvc.perform(patch("/bookings/{bookingId}", bookingId)
                        .param("approved", String.valueOf(approved))
                        .header("X-Sharer-User-Id", ownerId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        assertEquals(objectMapper.writeValueAsString(expectedBooking), result);
        verify(bookingService, times(1)).updateBookingStatus(bookingId, approved, ownerId);
    }

    @SneakyThrows
    @Test
    void getBookingById() {
        long bookingId = 0L;
        long userId = 1L;

        mockMvc.perform(get("/bookings/{bookingId}", bookingId)
                .header("X-Sharer-User-Id", userId))
                .andDo(print())
                .andExpect(status().isOk());

        verify(bookingService).getBooking(bookingId, userId);
    }

    @SneakyThrows
    @Test
    void getAllBookingsByUser() {
        long userId = 1L;

        mockMvc.perform(get("/bookings")
                        .header("X-Sharer-User-Id", userId))
                .andDo(print())
                .andExpect(status().isOk());

        verify(bookingService).getAllBookingsByUser(null, userId);
    }

    @SneakyThrows
    @Test
    void getAllBookingsByItemByUserId() {
        long userId = 1L;

        mockMvc.perform(get("/bookings/owner")
                        .header("X-Sharer-User-Id", userId))
                .andDo(print())
                .andExpect(status().isOk());

        verify(bookingService).getAllBookingsByItemByUserId(null, userId);
    }
}