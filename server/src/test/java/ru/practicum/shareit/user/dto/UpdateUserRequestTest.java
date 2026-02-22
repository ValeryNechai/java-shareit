package ru.practicum.shareit.user.dto;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class UpdateUserRequestTest {

    @Test
    void hasName_WhenNameIsValid_ShouldReturnTrue() {
        UpdateUserRequest request = new UpdateUserRequest();
        request.setName("John Doe");

        boolean result = request.hasName();

        assertTrue(result);
    }

    @Test
    void hasEmail_WhenEmailNotSet_ShouldReturnFalse() {
        UpdateUserRequest request = new UpdateUserRequest();

        boolean result = request.hasEmail();

        assertFalse(result);
    }
}