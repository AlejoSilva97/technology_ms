package com.example.technology.domain.model;

import com.example.technology.domain.constants.Constants;
import com.example.technology.domain.exceptions.InvalidFieldException;

public record Technology(
        Long id,
        String name,
        String description
) {
    public Technology {
        if (name == null || name.trim().isEmpty()) {
            throw new InvalidFieldException(Constants.TECHNOLOGY_NAME_REQUIRED);
        }
        if (name.length() > 50) {
            throw new InvalidFieldException(Constants.TECHNOLOGY_NAME_TOOLONG);
        }
        if (description == null || description.trim().isEmpty()) {
            throw new InvalidFieldException(Constants.TECHNOLOGY_DESCRIPTION_REQUIRED);
        }
        if (description.length() > 90) {
            throw new InvalidFieldException(Constants.TECHNOLOGY_DESCRIPTION_TOOLONG);
        }
    }
}
