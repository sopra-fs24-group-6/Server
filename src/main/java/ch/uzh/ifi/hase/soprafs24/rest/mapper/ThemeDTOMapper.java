package ch.uzh.ifi.hase.soprafs24.rest.mapper;

import ch.uzh.ifi.hase.soprafs24.entity.Theme;
import ch.uzh.ifi.hase.soprafs24.rest.dto.ThemeDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;


@Mapper
public interface ThemeDTOMapper {

  ThemeDTOMapper INSTANCE = Mappers.getMapper(ThemeDTOMapper.class);

  @Mapping(source = "id", target = "id")
  @Mapping(source = "name", target = "name")
  Theme convertThemeDTOtoEntity(ThemeDTO themeDTO);
}
