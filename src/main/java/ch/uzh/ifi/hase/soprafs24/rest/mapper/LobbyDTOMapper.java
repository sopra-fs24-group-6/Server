package ch.uzh.ifi.hase.soprafs24.rest.mapper;

import ch.uzh.ifi.hase.soprafs24.entity.Lobby;
import ch.uzh.ifi.hase.soprafs24.entity.Player;
import ch.uzh.ifi.hase.soprafs24.rest.dto.LobbyGetDTO;
import ch.uzh.ifi.hase.soprafs24.rest.dto.LobbyPostDTO;
import ch.uzh.ifi.hase.soprafs24.rest.dto.PlayerDTO;
import org.mapstruct.*;
import org.mapstruct.factory.Mappers;


@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface LobbyDTOMapper {

  LobbyDTOMapper INSTANCE = Mappers.getMapper(LobbyDTOMapper.class);

  @Mapping(source = "name", target = "name")
  @Mapping(source = "type", target = "type")
  @Mapping(source = "password", target = "password")
  @Mapping(source = "numPlayers", target = "numPlayers")
  @Mapping(source = "themeName", target = "theme.name")
  @Mapping(source = "gameDuration", target = "gameDuration")
  @Mapping(source = "hostUserId", target = "hostUserId")
  Lobby convertLobbyPostDTOtoEntity(LobbyPostDTO lobbyPostDTO);

  @Mapping(source = "id", target = "id")
  @Mapping(source = "name", target = "name")
  @Mapping(source = "type", target = "type")
  @Mapping(source = "status", target = "status")
  @Mapping(source = "players", target = "players")
  @Mapping(source = "numPlayers", target = "numPlayers")
  @Mapping(source = "theme.name", target = "themeName")
  @Mapping(source = "gameDuration", target = "gameDuration")
  @Mapping(source = "hostUserId", target = "hostUserId")
  LobbyGetDTO convertEntityToLobbyGetDTO(Lobby lobby);

  PlayerDTO EntityToPlayerDTO(Player player);

}
