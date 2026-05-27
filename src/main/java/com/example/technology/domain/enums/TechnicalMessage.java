package com.example.technology.domain.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum TechnicalMessage {

    INTERNAL_ERROR("500","Something went wrong, please try again", ""),
    INVALID_REQUEST("400", "Bad Request, please verify data", ""),
    INVALID_PARAMETERS(INVALID_REQUEST.getCode(), "Bad Parameters, please verify data", ""),
    TECHNOLOGY_CREATED("201", "Technology created successfully", ""),
    TECHNOLOGY_ALREADY_EXISTS("400","La tecnologia ya esta registrada" ,"" ),
    TECHNOLOGY_NOT_EXISTS("400","La tecnologia no se encuentra registrada" ,"" ),
    TECHNOLOGY_NAME_TOOLONG("400","El nombre de la tecnologia debe ser de 50 caracteres o menos" ,"" ),
    TECHNOLOGY_DESCRIPTION_TOOLONG("400","La descripcion de la tecnologia debe ser de 90 caracteres o menos" ,"" );

    private final String code;
    private final String message;
    private final String param;
}