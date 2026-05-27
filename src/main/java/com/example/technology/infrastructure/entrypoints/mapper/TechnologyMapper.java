package com.example.technology.infrastructure.entrypoints.mapper;

import com.example.technology.domain.model.Technology;
import com.example.technology.infrastructure.entrypoints.dto.TechnologyDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface TechnologyMapper {
    @Mapping(target = "id", ignore = true)
    @Mapping(source = "name", target = "name")
    @Mapping(source = "description", target = "description")
    Technology toTechnology(TechnologyDTO technologyDTO);
    TechnologyDTO toTechnologyDTO(Technology technology);
}
