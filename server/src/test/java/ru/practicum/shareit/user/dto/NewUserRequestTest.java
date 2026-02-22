package ru.practicum.shareit.user.dto;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
class NewUserRequestTest {

    @Autowired
    private JacksonTester<NewUserRequest> json;

    @Test
    void serialize_NewUserRequest_ToJson() throws Exception {
        NewUserRequest request = new NewUserRequest();
        request.setName("Иван Петров");
        request.setEmail("ivan@mail.ru");

        JsonContent<NewUserRequest> result = json.write(request);

        assertThat(result)
                .hasJsonPathValue("$.name")
                .hasJsonPathValue("$.email");

        assertThat(result)
                .extractingJsonPathStringValue("$.name")
                .isEqualTo("Иван Петров");

        assertThat(result)
                .extractingJsonPathStringValue("$.email")
                .isEqualTo("ivan@mail.ru");
    }
}