package com.example.technology.domain.model;

import com.example.technology.domain.constants.Constants;
import com.example.technology.domain.exceptions.InvalidFieldException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class TechnologyTest {

    @Test
    @DisplayName("Should create Technology when all fields are valid")
    void should_CreateTechnology_When_FieldsAreValid() {
        Long id = 1L;
        String name = "Java";
        String description = "Backend programming language";

        Technology technology = new Technology(id, name, description);

        assertNotNull(technology);
        assertEquals(id, technology.id());
        assertEquals(name, technology.name());
        assertEquals(description, technology.description());
    }

    @Test
    @DisplayName("Should throw InvalidFieldException when name is null or empty")
    void should_ThrowException_When_NameIsNullOrEmpty() {
        // Arrange & Act & Assert for null
        InvalidFieldException nullException = assertThrows(InvalidFieldException.class, () ->
                new Technology(1L, null, "Valid Description")
        );
        assertEquals(Constants.TECHNOLOGY_NAME_REQUIRED, nullException.getMessage());

        // Arrange & Act & Assert for empty/blank
        InvalidFieldException emptyException = assertThrows(InvalidFieldException.class, () ->
                new Technology(1L, "   ", "Valid Description")
        );
        assertEquals(Constants.TECHNOLOGY_NAME_REQUIRED, emptyException.getMessage());
    }

    @Test
    @DisplayName("Should throw InvalidFieldException when name exceeds 50 characters")
    void should_ThrowException_When_NameExceedsMaxLength() {
        String longName = "A".repeat(51);

        InvalidFieldException exception = assertThrows(InvalidFieldException.class, () ->
                new Technology(1L, longName, "Valid Description")
        );
        assertEquals(Constants.TECHNOLOGY_NAME_TOOLONG, exception.getMessage());
    }

    @Test
    @DisplayName("Should throw InvalidFieldException when description is null or empty")
    void should_ThrowException_When_DescriptionIsNullOrEmpty() {
        // Arrange & Act & Assert for null
        InvalidFieldException nullException = assertThrows(InvalidFieldException.class, () ->
                new Technology(1L, "Java", null)
        );
        assertEquals(Constants.TECHNOLOGY_DESCRIPTION_REQUIRED, nullException.getMessage());

        // Arrange & Act & Assert for empty/blank
        InvalidFieldException emptyException = assertThrows(InvalidFieldException.class, () ->
                new Technology(1L, "Java", "   ")
        );
        assertEquals(Constants.TECHNOLOGY_DESCRIPTION_REQUIRED, emptyException.getMessage());
    }

    @Test
    @DisplayName("Should throw InvalidFieldException when description exceeds 90 characters")
    void should_ThrowException_When_DescriptionExceedsMaxLength() {
        String longDescription = "B".repeat(91);

        InvalidFieldException exception = assertThrows(InvalidFieldException.class, () ->
                new Technology(1L, "Java", longDescription)
        );
        assertEquals(Constants.TECHNOLOGY_DESCRIPTION_TOOLONG, exception.getMessage());
    }
}