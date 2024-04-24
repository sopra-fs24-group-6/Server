package ch.uzh.ifi.hase.soprafs24.service;

import ch.uzh.ifi.hase.soprafs24.constant.LobbyStatus;
import ch.uzh.ifi.hase.soprafs24.constant.LobbyType;
import ch.uzh.ifi.hase.soprafs24.entity.User;
import ch.uzh.ifi.hase.soprafs24.entity.Lobby;
import ch.uzh.ifi.hase.soprafs24.entity.Theme;
import ch.uzh.ifi.hase.soprafs24.entity.Player;
import ch.uzh.ifi.hase.soprafs24.repository.UserRepository;
import ch.uzh.ifi.hase.soprafs24.repository.PlayerRepository;
import ch.uzh.ifi.hase.soprafs24.repository.LobbyRepository;
import ch.uzh.ifi.hase.soprafs24.repository.ThemeRepository;
import ch.uzh.ifi.hase.soprafs24.rest.dto.LobbyGetDTO;
import ch.uzh.ifi.hase.soprafs24.rest.dto.PlayerDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


@Service
@Transactional
public class LobbyService {

  private final Logger log = LoggerFactory.getLogger(LobbyService.class);

  private final UserRepository userRepository;
  private final PlayerRepository playerRepository;
  private final LobbyRepository lobbyRepository;
  private final ThemeRepository themeRepository;

  private final SimpMessagingTemplate messagingTemplate;

  private Lobby defaultLobby = new Lobby();

  @Autowired
  public LobbyService(SimpMessagingTemplate messagingTemplate,
                      UserRepository userRepository,
                      PlayerRepository playerRepository,
                      @Qualifier("lobbyRepository") LobbyRepository lobbyRepository,
                      ThemeRepository themeRepository) {
    this.messagingTemplate = messagingTemplate;
    this.userRepository = userRepository;
    this.playerRepository = playerRepository;
    this.lobbyRepository = lobbyRepository;
    this.themeRepository = themeRepository;
  }

    @PostConstruct
    private void initializeDefaultLobby() {
        defaultLobby = new Lobby();
        defaultLobby.setName("Default Lobby");
        defaultLobby.setPassword(null); // Assuming public by default
        defaultLobby.setType(LobbyType.PUBLIC);
        defaultLobby.setStatus(LobbyStatus.WAITING);
        defaultLobby.setPlayerLimit(10);
        defaultLobby.setPlayerCount(0);
        defaultLobby.setRounds(3);
        defaultLobby.setRoundTimer(60);
        defaultLobby.setClueTimer(10);
        defaultLobby.setDiscussionTimer(30);
        defaultLobby.setThemes(themeRepository.findAll());
        defaultLobby.setId(999L);
        // You might want to set a default theme or leave it empty
    }


  public List<Lobby> getLobbies(String username, Long userId) {
    // if username is specified
    if (username != null) {
      Lobby lobbyByUsername = getLobbyByUsername(username);
      return Collections.singletonList(lobbyByUsername);
    }
    // if userId is specified
    if (userId != null) {
      Lobby lobbyByUserId = getLobbyByUserId(userId);
      return Collections.singletonList(lobbyByUserId);
    }

      //For Testing purposes
      List<Lobby> lobbies = this.lobbyRepository.findAll();
      for (Lobby lobby : lobbies) {
          System.out.println("Current Lobby: " + lobby.getId());
      }

    // if no parameters, then return all lobbies
    return getAllLobbies();
  }

  public List<Lobby> getAllLobbies() {
      //For Testing purposes
      List<Lobby> lobbies = this.lobbyRepository.findAll();
      for (Lobby lobby : lobbies) {
          System.out.println("Current Lobby: " + lobby.getId());
      }
      return this.lobbyRepository.findAll();

  }

  public Lobby getLobbyByUsername(String username) {
    // Find the player by username
    Player player = playerRepository.findByUsername(username)
      .orElseThrow(() -> new ResponseStatusException(
        HttpStatus.NOT_FOUND, "Player with username " + username + " could not be found."));

    // Check if the player is associated with a lobby
    if (player.getLobby() == null) {
      throw new ResponseStatusException(
        HttpStatus.NOT_FOUND, "Player is not in any lobby.");
    }
    return player.getLobby();
  }

  public Lobby getLobbyByUserId(Long userId) {
    // Find the player by userId
    Player player = playerRepository.findById(userId)
      .orElseThrow(() -> new ResponseStatusException(
        HttpStatus.NOT_FOUND, "Player with userId " + userId + " could not be found."));

    // Check if the player is associated with a lobby
    if (player.getLobby() == null) {
      throw new ResponseStatusException(
        HttpStatus.NOT_FOUND, "Player is not in any lobby.");
    }
    return player.getLobby();
  }

  public Lobby getLobbyById(Long lobbyId) {
      Lobby lobby = findLobbyById(lobbyId);
      if (lobby == null) {
          // Return a copy of the default lobby to avoid modification of the original
          return defaultLobby;
      }
      return lobby;
  }

    public List<Player> getPlayersById(Long lobbyId){
      Lobby lobby = lobbyRepository.findById(lobbyId)
              .orElseThrow(() -> new ResponseStatusException(
                      HttpStatus.NOT_FOUND, "Lobby with id " + lobbyId + " could not be found."));
      return lobby.getPlayers();
    }

    public void sendPlayerListToLobby(List<PlayerDTO> playerDTOS, long lobbyId) {
        String destination = "/lobbies/" + lobbyId + "/players";
        System.out.println("Hello");
        for(PlayerDTO playerDTO : playerDTOS) {
            System.out.println(playerDTO);
        }
        System.out.println("KL");
        messagingTemplate.convertAndSend(destination, playerDTOS);
    }

    public void sendLobbyInfoToLobby(long lobbyId, LobbyGetDTO lobbyGetDTO) {
        String destination = "/lobbies/" + lobbyId + "/lobby_info";
        System.out.println("Hello");
        messagingTemplate.convertAndSend(destination, lobbyGetDTO);
    }

  public Lobby createLobby(Lobby newLobby) {
    // check if input name already exists
    // if so, then trow exception
    checkIfLobbyNameExists(newLobby.getName());

    // set lobby type and isPrivate
    // if password is set, then PRIVATE, else PUBLIC
    LobbyType type = determineLobbyType(newLobby.getPassword());
    newLobby.setType(type);
    newLobby.setIsPrivate(type == LobbyType.PRIVATE);

    // set status
    newLobby.setStatus(LobbyStatus.WAITING);

    // set themes by names
    // if name not found, then throw exception
    List<Theme> themes = findThemesByNames(newLobby.getThemeNames());
    newLobby.setThemes(themes);

    // create host player
    // if userId not found, then throw exception
    User hostUser = findUserById(newLobby.getHost().getUserId());
    Player hostPlayer = createPlayerFromUser(hostUser, true);
    hostUser.setPlayer(hostPlayer);

    // add host to lobby
    newLobby.setHost(hostPlayer);
    newLobby.addPlayer(hostPlayer);

    // saves the given entity but data is only persisted in the database once
    // flush() is called
    newLobby = lobbyRepository.save(newLobby);
    lobbyRepository.flush();

      System.out.println(newLobby);

    log.debug("Created Information for User: {}", newLobby);
    return newLobby;
  }

  public Lobby updateLobby (Long lobbyId, Lobby newLobby) {
    // find lobby by id
    // if not found, then throw exception
    Lobby targetLobby = findLobbyById(lobbyId);

    // update properties
    targetLobby.setName(newLobby.getName());
    targetLobby.setPassword(newLobby.getPassword());
    targetLobby.setRoundTimer(newLobby.getRoundTimer());
    targetLobby.setClueTimer(newLobby.getClueTimer());
    targetLobby.setDiscussionTimer(newLobby.getDiscussionTimer());
    LobbyType type = determineLobbyType(targetLobby.getPassword());
    targetLobby.setType(type);

    // update playerLimit
    // if new playerLimit < current playerCount, then throw except
    if (newLobby.getPlayerLimit() >= targetLobby.getPlayerCount()) {
      targetLobby.setPlayerLimit(newLobby.getPlayerLimit());
    } else {
      throw new ResponseStatusException(
        HttpStatus.CONFLICT, "Player limit should be less than current player counts");
    }

    // update themes
    List<Theme> newThemes = findThemesByNames(newLobby.getThemeNames());
    targetLobby.setThemes(newThemes);

    // save
    lobbyRepository.save(targetLobby);
    lobbyRepository.flush();

    return targetLobby;
  }

  public Lobby addPlayerToLobby(Long lobbyId, Long userId){
    // find lobby by id
    Lobby lobby = findLobbyById(lobbyId);

    // find user by id
    // if not found, then throw exception
    User user = findUserById(userId);

    // add player if current count < limit
    // if lobby is full, then throw exception
    if (lobby.getPlayerCount() < lobby.getPlayerLimit()) {
      // create new player
      Player newPlayer = createPlayerFromUser(user, false);
      playerRepository.save(newPlayer);
      playerRepository.flush();

      user.setPlayer(newPlayer);
      userRepository.save(user);
      userRepository.flush();

      lobby.addPlayer(newPlayer);
      lobby = lobbyRepository.save(lobby);
      lobbyRepository.flush();
        System.out.println(lobby);

      return lobby;

    } else {
      throw new ResponseStatusException(
        HttpStatus.CONFLICT, "Lobby with id " + lobbyId + " is full.");
    }
  }

  public Lobby kickPlayerFromLobby(Long lobbyId, Long targetId, Long requesterId) {
    // find lobby by id
    // if not found, then throw exception
    Lobby lobby = findLobbyById(lobbyId);

    // check if target player is in lobby
    Player targetPlayer = findUserById(targetId).getPlayer();
    if (targetPlayer != null && targetPlayer.getLobby().getId().equals(lobbyId)) {
      // check if requester is host player
      if (lobby.getHost().getUserId().equals(requesterId)) {
        // remove target player from lobby
        lobby.removePlayer(targetPlayer);
        // delete target player from database
        playerRepository.delete(targetPlayer);
        playerRepository.flush();
        lobbyRepository.save(lobby);
        lobbyRepository.flush();
          System.out.println(lobby);
          return lobby;

      } else {
        throw new ResponseStatusException(
          HttpStatus.UNAUTHORIZED, "Kicking player is only allowed by the host.");
      }
    } else {
      throw new ResponseStatusException(
        HttpStatus.NOT_FOUND, "Target player with id" + targetId + " could not be found.");
    }
  }


  public void authenticateLobby (Long lobbyId, String password) {
    // find lobby by id
    // if not found, then throw exception
    Lobby lobby = findLobbyById(lobbyId);

    // check password
    // if incorrect password, then throw exception
    if (!lobby.getPassword().equals(password)) {
      throw new ResponseStatusException(
        HttpStatus.UNAUTHORIZED, "Incorrect password for lobby with id " + lobbyId + ".");
    }
  }

  public Lobby startGame (Long lobbyId) {
    // find lobby by id
    // if not found, then throw exception
    Lobby lobby = findLobbyById(lobbyId);

    // change status to IN_PROGRESS
    lobby.setStatus(LobbyStatus.IN_PROGRESS);

    return lobby;
  }

  public List<String> getThemes () {
    List<Theme> themes = themeRepository.findAll();
    List<String> themeNames = new ArrayList<>();
    for (Theme theme: themes) {
      themeNames.add(theme.getName());
    }
    return themeNames;
  }


  /**
   * Helper Methods
   */
  public void checkIfLobbyNameExists (String lobbyName) {
    lobbyRepository.findByName(lobbyName).ifPresent(existingLobby -> {
      throw new ResponseStatusException(
        HttpStatus.CONFLICT, "Lobby with name: " + lobbyName + " already exists.");
    });
  }

  public Lobby findLobbyById (Long lobbyId) {
    return lobbyRepository.findById(lobbyId)
      .orElseThrow(() -> new ResponseStatusException(
        HttpStatus.NOT_FOUND, "Lobby with id " + lobbyId + " could not be found."));
  }

  public User findUserById (Long userId) {
    return userRepository.findById(userId)
      .orElseThrow(() -> new ResponseStatusException(
        HttpStatus.NOT_FOUND, "User with id " + userId + " could not be found."));
  }

  public LobbyType determineLobbyType (String password) {
    return password != null ? LobbyType.PRIVATE : LobbyType.PUBLIC;
  }

  public List<Theme> findThemesByNames(List<String> themeNames) {
    List<Theme> themes = new ArrayList<>();
    for (String themeName : themeNames) {
      Theme theme = themeRepository.findByName(themeName)
        .orElseThrow(() -> new ResponseStatusException(
          HttpStatus.NOT_FOUND, "Theme not found with name: " + themeName));
      themes.add(theme);
    }
    return themes;
  }

  public Player createPlayerFromUser (User user, Boolean isHost) {
    // check if user already join lobby/game
    // if not, then create new player
    // else, throw exception
    if (user.getPlayer() != null) {
      throw new ResponseStatusException(
        HttpStatus.CONFLICT, "User with id " + user.getId() + " already joins a lobby.");
    } else {
      // create new player
      Player newPlayer = new Player();
      newPlayer.setUserId(user.getId());
      newPlayer.setUsername(user.getUsername());
      newPlayer.setLanguage(user.getLanguage());
      newPlayer.setHost(isHost);

      return newPlayer;
    }
  }

}
