package ch.uzh.ifi.hase.soprafs24.service;

import ch.uzh.ifi.hase.soprafs24.constant.LobbyStatus;
import ch.uzh.ifi.hase.soprafs24.entity.Lobby;
import ch.uzh.ifi.hase.soprafs24.repository.LobbyRepository;
import ch.uzh.ifi.hase.soprafs24.entity.Theme;
import ch.uzh.ifi.hase.soprafs24.repository.ThemeRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;


@Service
@Transactional
public class LobbyService {

  private final Logger log = LoggerFactory.getLogger(LobbyService.class);

  private final LobbyRepository lobbyRepository;
  private final ThemeRepository themeRepository;

  @Autowired
  public LobbyService(@Qualifier("lobbyRepository") LobbyRepository lobbyRepository, ThemeRepository themeRepository) {
    this.lobbyRepository = lobbyRepository;
    this.themeRepository = themeRepository;
  }


  public List<Lobby> getLobbies() {
    return this.lobbyRepository.findAll();
  }

  public Lobby getLobbyById(Long lobbyId) {
    return lobbyRepository.findById(lobbyId)
      .orElseThrow(() -> new ResponseStatusException(
        HttpStatus.NOT_FOUND, "Lobby with id " + lobbyId + " could not be found."));
  }

  public Lobby createLobby(Lobby newLobby) {
    newLobby.setStatus(LobbyStatus.WAITING);

    // set theme by name
    // if name not found, then throw exception
    String inputThemeName = newLobby.getTheme().getName();
    Theme theme = themeRepository.findByName(inputThemeName)
      .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Theme not found with name: " + inputThemeName));
    newLobby.setTheme(theme);

    // check if input name already exists
    // if exists, then throw exception
    String inputLobbyName = newLobby.getName();
    lobbyRepository.findByName(inputLobbyName).ifPresent(existingLobby -> {
      throw new ResponseStatusException(HttpStatus.CONFLICT, "Lobby with name: " + inputLobbyName + " already exists.");
    });

    // saves the given entity but data is only persisted in the database once
    // flush() is called
    newLobby = lobbyRepository.save(newLobby);
    lobbyRepository.flush();

    log.debug("Created Information for User: {}", newLobby);
    return newLobby;
  }

}
