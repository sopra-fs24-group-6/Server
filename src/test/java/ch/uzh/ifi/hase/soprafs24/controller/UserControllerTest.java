package ch.uzh.ifi.hase.soprafs24.controller;

import ch.uzh.ifi.hase.soprafs24.constant.UserStatus;
import ch.uzh.ifi.hase.soprafs24.entity.User;
import ch.uzh.ifi.hase.soprafs24.rest.dto.UserAvatarDTO;
import ch.uzh.ifi.hase.soprafs24.rest.dto.UserIdDTO;
import ch.uzh.ifi.hase.soprafs24.rest.dto.UserPostDTO;
import ch.uzh.ifi.hase.soprafs24.rest.dto.UserPutDTO;
import ch.uzh.ifi.hase.soprafs24.service.UserService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.TestInfo;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.time.Instant;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * UserControllerTest
 * This is a WebMvcTest which allows to test the UserController i.e. GET/POST
 * request without actually sending them over the network.
 * This tests if the UserController works.
 */
@WebMvcTest(UserController.class)
public class UserControllerTest {

  @Autowired
  private MockMvc mockMvc;

  @MockBean
  private UserService userService;

  private User testUser;
  private String dateString;

  @BeforeEach
  public void setup() {
    testUser = new User();
    testUser.setId(1L);
    testUser.setUsername("username");
    testUser.setToken("token");
    testUser.setStatus(UserStatus.OFFLINE);
    testUser.setPassword("password");
    dateString = "2024-01-01T00:00:00.000+00:00";
    Date date = Date.from(Instant.parse(dateString));
    testUser.setCreationDate(date);
    testUser.setBirthDate(date);
    testUser.setLanguage("en");
    testUser.setWins(20);
    testUser.setLosses(13);
    testUser.setWinlossratio(1.335);
    testUser.setAvatarUrl("images/avatar/default");
  }

//    @AfterEach
//    public void afterEachTest(TestInfo testInfo) {
//        System.out.println("After UserControllerTest: " + testInfo.getDisplayName());
//        System.out.println("Current Environment Variables:");
//        String googleCredentials = System.getenv("GOOGLE_APPLICATION_CREDENTIALS");
//        if (googleCredentials != null) {
//            System.out.println("GOOGLE_APPLICATION_CREDENTIALS = " + googleCredentials);
//        } else {
//            System.out.println("GOOGLE_APPLICATION_CREDENTIALS is not set.");
//        }
//    }

  @Test
  public void givenUsers_whenGetUsers_thenReturnJsonArray() throws Exception {
    // given
    List<User> allUsers = Collections.singletonList(testUser);

    // this mocks the UserService -> we define above what the userService should
    // return when getUsers() is called
    given(userService.getUsers()).willReturn(allUsers);

    // when
    MockHttpServletRequestBuilder getRequest = get("/users").contentType(MediaType.APPLICATION_JSON);

    // then
    mockMvc.perform(getRequest).andExpect(status().isOk())
      .andExpect(jsonPath("$", hasSize(1)))
      .andExpect(jsonPath("$[0].id", is(testUser.getId().intValue())))
      .andExpect(jsonPath("$[0].username", is(testUser.getUsername())))
      .andExpect(jsonPath("$[0].token", is(testUser.getToken())))
      .andExpect(jsonPath("$[0].status", is(testUser.getStatus().toString())))
      .andExpect(jsonPath("$[0].creationDate", is(dateString)))
      .andExpect(jsonPath("$[0].birthDate", is(dateString)))
      .andExpect(jsonPath("$[0].language", is(testUser.getLanguage())))
            .andExpect(jsonPath("$[0].wins", is(testUser.getWins())))
            .andExpect(jsonPath("$[0].losses", is(testUser.getLosses())))
            .andExpect(jsonPath("$[0].winlossratio", is(testUser.getWinlossratio())))
            .andExpect(jsonPath("$[0].avatarUrl", is(testUser.getAvatarUrl())));
  }

  @Test
  public void createUser_validInput_thenUserCreated() throws Exception {
    // given
    UserPostDTO userPostDTO = new UserPostDTO();
    userPostDTO.setUsername("username");
    userPostDTO.setPassword("password");
    testUser.setStatus(UserStatus.ONLINE);

    given(userService.createUser(Mockito.any())).willReturn(testUser);

    // when/then -> do the request + validate the result
    MockHttpServletRequestBuilder postRequest = post("/users")
      .contentType(MediaType.APPLICATION_JSON)
      .content(asJsonString(userPostDTO));

    // then
    mockMvc.perform(postRequest).andExpect(status().isCreated())
      .andExpect(jsonPath("$.id").exists())
      .andExpect(jsonPath("$.username", is(testUser.getUsername())))
      .andExpect(jsonPath("$.status", is(UserStatus.ONLINE.toString())))
      .andExpect(jsonPath("$.token").exists())
      .andExpect(jsonPath("$.creationDate").exists());
  }

  @Test
  void testUpdateAvatar_validInput() throws Exception {
    Long userId = 1L;
    // Mock MultipartFile
    MockMultipartFile mockFile = new MockMultipartFile(
            "avatar", // name of the file parameter in the request
            "avatar.png", // original filename
            MediaType.IMAGE_PNG_VALUE, // content type
            "test image content".getBytes() // file content
    );

    // Mock the behavior of userService.updateUserAvatar
    when(userService.updateUserAvatar(anyLong(), any(MultipartFile.class))).thenReturn(testUser);

    // Perform the POST request
    mockMvc.perform(multipart("/{userId}/avatar", testUser.getId())
                    .file(mockFile)
                    .contentType(MediaType.MULTIPART_FORM_DATA))
            .andExpect(status().isNoContent());
  }

  @Test
  public void createUser_invalidInput_thenReturnConflict() throws Exception {
    // given
    UserPostDTO userPostDTO = new UserPostDTO();
    userPostDTO.setUsername("username");
    userPostDTO.setPassword("password");

    given(userService.createUser(Mockito.any()))
      .willThrow(new ResponseStatusException(HttpStatus.CONFLICT, "username exists already"));

    // when/then -> do the request + validate the result
    MockHttpServletRequestBuilder postRequest = post("/users")
      .contentType(MediaType.APPLICATION_JSON)
      .content(asJsonString(userPostDTO));

    // then
    mockMvc.perform(postRequest).andExpect(status().isConflict());
  }

  @Test
  public void getUser_validId_thenReturnUser() throws Exception {
    // given
    given(userService.getUser(testUser.getId())).willReturn(testUser);

    // when/then -> do the request + validate the result
    MockHttpServletRequestBuilder getRequest = get("/users/{userId}", testUser.getId())
      .contentType(MediaType.APPLICATION_JSON);

    // then
    mockMvc.perform(getRequest)
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id", is(testUser.getId().intValue())))
            .andExpect(jsonPath("$.username", is(testUser.getUsername())))
            .andExpect(jsonPath("$.token", is(testUser.getToken())))
            .andExpect(jsonPath("$.status", is(testUser.getStatus().toString())))
            .andExpect(jsonPath("$.creationDate", is(dateString)))
            .andExpect(jsonPath("$.birthDate", is(dateString)))
            .andExpect(jsonPath("$.language", is(testUser.getLanguage())))
            .andExpect(jsonPath("$.wins", is(testUser.getWins())))
            .andExpect(jsonPath("$.losses", is(testUser.getLosses())))
            .andExpect(jsonPath("$.winlossratio", is(testUser.getWinlossratio())))
            .andExpect(jsonPath("$.avatarUrl", is(testUser.getAvatarUrl())));
  }

  @Test
  public void getUser_invalidId_thenReturnNotFound() throws Exception {
    // given
    Long invalidUserId = 2L;
    given(userService.getUser(invalidUserId))
      .willThrow(new ResponseStatusException(HttpStatus.NOT_FOUND, "user with userId not found"));;

    // when/then -> do the request + validate the result
    MockHttpServletRequestBuilder getRequest = get("/users/{userId}", invalidUserId)
      .contentType(MediaType.APPLICATION_JSON);

    // then
    mockMvc.perform(getRequest).andExpect(status().isNotFound());
  }

  @Test
  public void updateUser_validInput_thenReturnNoContent() throws Exception {
    // given
    UserPutDTO userPutDTO = new UserPutDTO();
    userPutDTO.setUsername("newUsername");
    userPutDTO.setBirthDate(new Date());

    doNothing().when(userService).updateUser(Mockito.any(), Mockito.any());

    // when/then -> do the request + validate the result
    MockHttpServletRequestBuilder putRequest = put("/users/{userId}", testUser.getId())
      .contentType(MediaType.APPLICATION_JSON)
      .content(asJsonString(userPutDTO));

    // then
    mockMvc.perform(putRequest).andExpect(status().isNoContent());
  }

  @Test
  public void updateUser_invalidUserId_thenReturnNotFound() throws Exception {
    // given
    Long invalidUserId = 2L;
    UserPutDTO userPutDTO = new UserPutDTO();
    userPutDTO.setUsername("newUsername");
    userPutDTO.setBirthDate(new Date());

    doThrow(new ResponseStatusException(HttpStatus.NOT_FOUND))
      .when(userService).updateUser(Mockito.any(), Mockito.any());

    // when/then -> do the request + validate the result
    MockHttpServletRequestBuilder putRequest = put("/users/{userId}", invalidUserId)
      .contentType(MediaType.APPLICATION_JSON)
      .content(asJsonString(userPutDTO));

    // then
    mockMvc.perform(putRequest).andExpect(status().isNotFound());
  }

  @Test
  public void updateUser_invalidUsername_thenReturnConflict() throws Exception {
    // given
    UserPutDTO userPutDTO = new UserPutDTO();
    userPutDTO.setUsername("username");
    userPutDTO.setBirthDate(new Date());

    doThrow(new ResponseStatusException(HttpStatus.CONFLICT))
      .when(userService).updateUser(Mockito.any(), Mockito.any());

    // when/then -> do the request + validate the result
    MockHttpServletRequestBuilder putRequest = put("/users/{userId}", testUser.getId())
      .contentType(MediaType.APPLICATION_JSON)
      .content(asJsonString(userPutDTO));

    // then
    mockMvc.perform(putRequest).andExpect(status().isConflict());
  }

  @Test
  public void login_validInput_thenReturnUser() throws Exception {
    // given
    UserPostDTO userPostDTO = new UserPostDTO();
    userPostDTO.setUsername("username");
    userPostDTO.setPassword("password");
    testUser.setStatus(UserStatus.ONLINE);

    given(userService.loginUser(Mockito.any())).willReturn(testUser);

    // when/then -> do the request + validate the result
    MockHttpServletRequestBuilder postRequest = post("/login")
      .contentType(MediaType.APPLICATION_JSON)
      .content(asJsonString(userPostDTO));

    // then
    mockMvc.perform(postRequest).andExpect(status().isOk())
      .andExpect(jsonPath("$.username", is(testUser.getUsername())))
      .andExpect(jsonPath("$.status", is(UserStatus.ONLINE.toString())));
  }

  @Test
  public void login_invalidUsername_thenReturnNotFound() throws Exception {
    // given
    UserPostDTO userPostDTO = new UserPostDTO();
    userPostDTO.setUsername("invalidUsername");
    userPostDTO.setPassword("password");

    given(userService.loginUser(Mockito.any()))
      .willThrow(new ResponseStatusException(HttpStatus.NOT_FOUND, "user with username not found"));;

    // when/then -> do the request + validate the result
    MockHttpServletRequestBuilder postRequest = post("/login")
      .contentType(MediaType.APPLICATION_JSON)
      .content(asJsonString(userPostDTO));

    // then
    mockMvc.perform(postRequest).andExpect(status().isNotFound());
  }

  @Test
  public void login_invalidCredentials_thenReturnUnauthorized() throws Exception {
    // given
    UserPostDTO userPostDTO = new UserPostDTO();
    userPostDTO.setUsername("username");
    userPostDTO.setPassword("invalidPassword");

    given(userService.loginUser(Mockito.any()))
      .willThrow(new ResponseStatusException(HttpStatus.UNAUTHORIZED, "wrong credentials"));;

    // when/then -> do the request + validate the result
    MockHttpServletRequestBuilder postRequest = post("/login")
      .contentType(MediaType.APPLICATION_JSON)
      .content(asJsonString(userPostDTO));

    // then
    mockMvc.perform(postRequest).andExpect(status().isUnauthorized());
  }

  @Test
  public void logout_validInput_thenReturnNoContent() throws Exception {
    // given
    UserIdDTO userIdDTO = new UserIdDTO();
    userIdDTO.setUserId(1L);

    doNothing().when(userService).logoutUser(Mockito.any());

    // when/then -> do the request + validate the result
    MockHttpServletRequestBuilder putRequest = put("/logout")
      .contentType(MediaType.APPLICATION_JSON)
      .content(asJsonString(userIdDTO));

    // then
    mockMvc.perform(putRequest).andExpect(status().isNoContent());
  }

  @Test
  public void logout_invalidUserId_thenReturnNotFound() throws Exception {
    // given
    UserIdDTO userIdDTO = new UserIdDTO();
    userIdDTO.setUserId(2L);

    doNothing().when(userService).logoutUser(Mockito.any());
    doThrow(new ResponseStatusException(HttpStatus.NOT_FOUND))
      .when(userService).logoutUser(Mockito.any());

    // when/then -> do the request + validate the result
    MockHttpServletRequestBuilder putRequest = put("/logout")
      .contentType(MediaType.APPLICATION_JSON)
      .content(asJsonString(userIdDTO));

    // then
    mockMvc.perform(putRequest).andExpect(status().isNotFound());
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