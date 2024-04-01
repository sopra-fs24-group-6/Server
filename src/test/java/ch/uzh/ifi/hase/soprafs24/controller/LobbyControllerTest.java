package ch.uzh.ifi.hase.soprafs24.controller;

import ch.uzh.ifi.hase.soprafs24.entity.Lobby;
import ch.uzh.ifi.hase.soprafs24.rest.dto.*;
import ch.uzh.ifi.hase.soprafs24.service.LobbyService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.web.server.ResponseStatusException;

import java.util.Collections;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doNothing;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * UserControllerTest
 * This is a WebMvcTest which allows to test the UserController i.e. GET/POST
 * request without actually sending them over the network.
 * This tests if the UserController works.
 */
@WebMvcTest(LobbyController.class)
public class LobbyControllerTest {

  @Autowired
  private MockMvc mockMvc;

  @MockBean
  private LobbyService lobbyService;

  private Lobby testLobby;

  @BeforeEach
  public void setup() {
    testLobby = new Lobby();
    testLobby.setId(1L);
    testLobby.setName("lobbyName");
  }

  @Test
  public void createLobby_validInput_thenLobbyCreated() throws Exception {
    // given
    LobbyPostDTO lobbyPostDTO = new LobbyPostDTO();
    lobbyPostDTO.setLobbyAdmin(1L);
    lobbyPostDTO.setName("lobbyName");

    given(lobbyService.createLobby(Mockito.any())).willReturn(testLobby);

    // when/then -> do the request + validate the result
    MockHttpServletRequestBuilder postRequest = post("/lobbies")
      .contentType(MediaType.APPLICATION_JSON)
      .content(asJsonString(lobbyPostDTO));

    // then
    mockMvc.perform(postRequest).andExpect(status().isCreated())
      .andExpect(jsonPath("$.id", is(testLobby.getId().intValue())))
      .andExpect(jsonPath("$.name", is(testLobby.getName())));
  }

  @Test
  public void createLobby_invalidInput_thenReturnConflict() throws Exception {
    // given
    LobbyPostDTO lobbyPostDTO = new LobbyPostDTO();
    lobbyPostDTO.setLobbyAdmin(1L);
    lobbyPostDTO.setName("lobbyName");

    given(lobbyService.createLobby(Mockito.any()))
      .willThrow(new ResponseStatusException(HttpStatus.CONFLICT, "lobby name exists already"));

    // when/then -> do the request + validate the result
    MockHttpServletRequestBuilder postRequest = post("/lobbies")
      .contentType(MediaType.APPLICATION_JSON)
      .content(asJsonString(lobbyPostDTO));

    // then
    mockMvc.perform(postRequest).andExpect(status().isConflict());
  }

  @Test
  public void getAllLobbies_thenReturnJsonArray() throws Exception {
    // given
    List<Lobby> allLobbies = Collections.singletonList(testLobby);
    given(lobbyService.getLobbies()).willReturn(allLobbies);

    // when/then -> do the request + validate the result
    MockHttpServletRequestBuilder getRequest = get("/lobbies")
      .contentType(MediaType.APPLICATION_JSON);

    // then
    mockMvc.perform(getRequest).andExpect(status().isOk())
      .andExpect(jsonPath("$", hasSize(1)))
      .andExpect(jsonPath("$[0].id", is(testLobby.getId().intValue())))
      .andExpect(jsonPath("$[0].name", is(testLobby.getName())));
  }

  @Test
  public void getLobby_validInput_thenReturnLobby() throws Exception {
    // given
    given(lobbyService.getLobbyById(Mockito.any())).willReturn(testLobby);

    // when/then -> do the request + validate the result
    MockHttpServletRequestBuilder getRequest = get("/lobbies/{lobbyId}", testLobby.getId())
      .contentType(MediaType.APPLICATION_JSON);

    // then
    mockMvc.perform(getRequest).andExpect(status().isOk())
      .andExpect(jsonPath("$.id", is(testLobby.getId().intValue())))
      .andExpect(jsonPath("$.name", is(testLobby.getName())));
  }

  @Test
  public void updateLobby_validInput_thenReturnNoContent() throws Exception {
    // given
    LobbyPostDTO lobbyPostDTO = new LobbyPostDTO();
    lobbyPostDTO.setLobbyAdmin(1L);
    lobbyPostDTO.setName("lobbyName");

    doNothing().when(lobbyService).updateLobby(Mockito.any(), Mockito.any());

    // when/then -> do the request + validate the result
    MockHttpServletRequestBuilder putRequest = put("/lobbies/{lobbyId}", testLobby.getId())
      .contentType(MediaType.APPLICATION_JSON)
      .content(asJsonString(lobbyPostDTO));

    // then
    mockMvc.perform(putRequest).andExpect(status().isNoContent());
  }

  @Test
  public void joinLobby_validInput_thenPlayerCreated() throws Exception {
    // given
    UserIdDTO userIdDTO = new UserIdDTO();
    userIdDTO.setUserId(1L);

    given(lobbyService.addPlayerToLobby(Mockito.any(), Mockito.any())).willReturn(testLobby);

    // when/then -> do the request + validate the result
    MockHttpServletRequestBuilder postRequest = post("/lobbies/{lobbyId}/players", testLobby.getId())
      .contentType(MediaType.APPLICATION_JSON)
      .content(asJsonString(userIdDTO));

    // then
    mockMvc.perform(postRequest).andExpect(status().isCreated())
      .andExpect(jsonPath("$.id", is(testLobby.getId().intValue())))
      .andExpect(jsonPath("$.name", is(testLobby.getName())));
  }

  @Test
  public void kickPlayer_validInput_thenPlayerDeleted() throws Exception {
    // given
    Long targetUserId = 2L;
    UserIdDTO userIdDTO = new UserIdDTO();
    userIdDTO.setUserId(1L);

    doNothing().when(lobbyService).kickPlayerFromLobby(Mockito.any(), Mockito.any(), Mockito.any());

    // when/then -> do the request + validate the result
    MockHttpServletRequestBuilder deleteRequest = delete("/lobbies/{lobbyId}/players/{userId}", testLobby.getId(), targetUserId)
      .contentType(MediaType.APPLICATION_JSON)
      .content(asJsonString(userIdDTO));

    // then
    mockMvc.perform(deleteRequest).andExpect(status().isNoContent());
  }

  @Test
  public void joinLobbyWithPassword_validInput_success() throws Exception {
    //given
    PasswordDTO passwordDTO = new PasswordDTO();
    passwordDTO.setPassword("password");

    doNothing().when(lobbyService).authenticateLobby(Mockito.any(), Mockito.any());

    // when/then -> do the request + validate the result
    MockHttpServletRequestBuilder postRequest = post("/lobbies/{lobbyId}/authentication", testLobby.getId())
      .contentType(MediaType.APPLICATION_JSON)
      .content(asJsonString(passwordDTO));

    // then
    mockMvc.perform(postRequest).andExpect(status().isOk());
  }

  @Test
  public void getAllThemes_success() throws Exception {
    // given
    String themeName = "Theme";
    List<String> allThemes = Collections.singletonList(themeName);
    given(lobbyService.getThemes()).willReturn(allThemes);

    // when/then -> do the request + validate the result
    MockHttpServletRequestBuilder getRequest = get("/themes")
      .contentType(MediaType.APPLICATION_JSON);

    // then
    mockMvc.perform(getRequest).andExpect(status().isOk())
      .andExpect(jsonPath("$", hasSize(1)))
      .andExpect(jsonPath("$[0]", is(themeName)));
  }



  /**
   * Helper Method to convert userPostDTO into a JSON string such that the input
   * can be processed
   * Input will look like this: {"name": "Test User", "username": "testUsername"}
   * 
   * @param object
   * @return string
   */
  private String asJsonString(final Object object) {
    try {
      return new ObjectMapper().writeValueAsString(object);
    } catch (JsonProcessingException e) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
          String.format("The request body could not be created.%s", e.toString()));
    }
  }
}