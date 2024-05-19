package ch.uzh.ifi.hase.soprafs24.rest.mapper;

import ch.uzh.ifi.hase.soprafs24.entity.Lobby;
import ch.uzh.ifi.hase.soprafs24.entity.Player;
import ch.uzh.ifi.hase.soprafs24.entity.Theme;
import ch.uzh.ifi.hase.soprafs24.rest.dto.LobbyGetDTO;
import ch.uzh.ifi.hase.soprafs24.rest.dto.LobbyPostDTO;
import ch.uzh.ifi.hase.soprafs24.rest.dto.PlayerDTO;
import org.mapstruct.*;
import org.mapstruct.factory.Mappers;

import java.util.ArrayList;
import java.util.List;


@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface LobbyDTOMapper {

  LobbyDTOMapper INSTANCE = Mappers.getMapper(LobbyDTOMapper.class);

  @Mapping(source = "name", target = "name")
  @Mapping(source = "password", target = "password")
  @Mapping(source = "playerLimit", target = "playerLimit")
  @Mapping(source = "themes", target = "themes", qualifiedByName = "namesToThemeList")
  @Mapping(source = "rounds", target = "rounds")
  @Mapping(source = "roundTimer", target = "roundTimer")
  @Mapping(source = "clueTimer", target = "clueTimer")
  @Mapping(source = "discussionTimer", target = "discussionTimer")
  @Mapping(source = "lobbyAdmin", target = "host.userId")
  @Mapping(source = "isPrivate", target = "isPrivate")
  Lobby convertLobbyPostDTOtoEntity(LobbyPostDTO lobbyPostDTO);

  // Custom mapping for themes (List<String> to List<Theme>)
  @Named("namesToThemeList")
  static List<Theme> namesToThemeList(List<String> names) {
    if (names == null) {
      return new ArrayList<>();
    }

    List<Theme> themes = new ArrayList<>();
    for (String name : names) {
      Theme theme = new Theme();
      theme.setName(name);
      themes.add(theme);
    }
    return themes;
  }

  @Mapping(source = "id", target = "id")
  @Mapping(source = "name", target = "name")
  @Mapping(source = "host.userId", target = "lobbyAdmin")
  @Mapping(source = "isPrivate", target = "isPrivate")
  @Mapping(source = "status", target = "status")
  @Mapping(source = "players", target = "players", qualifiedByName = "playersToDTOs")
  @Mapping(source = "playerLimit", target = "playerLimit")
  @Mapping(source = "playerCount", target = "playerCount")
  @Mapping(source = "themes", target = "themes", qualifiedByName = "themesToNames")
  @Mapping(source = "rounds", target = "rounds")
  @Mapping(source = "roundTimer", target = "roundTimer")
  @Mapping(source = "clueTimer", target = "clueTimer")
  @Mapping(source = "discussionTimer", target = "discussionTimer")
  LobbyGetDTO convertEntityToLobbyGetDTO(Lobby lobby);

  // custom mapping for themes
  @Named("themesToNames")
  static List<String> themesToNames(List<Theme> themes) {
    if (themes == null) {
      return new ArrayList<>();
    }

    List<String> themeNames = new ArrayList<>();
    for (Theme theme : themes) {
      String themeName = theme.getName();
      themeNames.add(themeName);
    }
    return themeNames;
  }

  // custom mapping for players
  @Named("playersToDTOs")
  static List<PlayerDTO> playersToDTOs(List<Player> players) {
    if (players == null) {
      return new ArrayList<>();
    }

    List<PlayerDTO> playerDTOS = new ArrayList<>();
    for (Player player : players) {
      playerDTOS.add(PlayerDTOMapper.INSTANCE.convertEntityToPlayerDTO(player));
    }
    return playerDTOS;
  }

}
