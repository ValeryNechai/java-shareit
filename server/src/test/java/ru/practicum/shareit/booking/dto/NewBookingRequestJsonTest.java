package ru.practicum.shareit.booking.dto;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
class NewBookingRequestJsonTest {

    @Autowired
    private JacksonTester<NewBookingRequest> json;

    @Test
    void serialize_NewBookingRequest_ToJson() throws Exception {
        NewBookingRequest request = new NewBookingRequest();
        request.setItemId(1L);
        request.setStart(LocalDateTime.now().plusDays(1));
        request.setEnd(LocalDateTime.now().plusDays(2));
        request.setStatus(BookingStatus.WAITING);

        JsonContent<NewBookingRequest> result = json.write(request);

        assertThat(result).hasJsonPathNumberValue("$.itemId");
        assertThat(result).hasJsonPathValue("$.start");
        assertThat(result).hasJsonPathValue("$.end");
        assertThat(result).hasJsonPathValue("$.status");

        assertThat(result).extractingJsonPathNumberValue("$.itemId").isEqualTo(1);
        assertThat(result).extractingJsonPathStringValue("$.status").isEqualTo("WAITING");
    }
}