package ch.uzh.ifi.hase.soprafs24.service;

import ch.uzh.ifi.hase.soprafs24.constant.UserStatus;
import ch.uzh.ifi.hase.soprafs24.entity.User;
import ch.uzh.ifi.hase.soprafs24.repository.UserRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInfo;
import org.junit.jupiter.api.io.TempDir;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.lang.reflect.Field;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Instant;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

public class UserServiceTest {

  @Mock
  private UserRepository userRepository;

  @InjectMocks
  private UserService userService;

  private static final String AVATAR_DIR = "src/main/images/users";

  User testUser;

  @TempDir
  Path tempDir;

  private String dateString;

  @BeforeEach
  public void setup() {

    MockitoAnnotations.openMocks(this);
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
    userService = new UserService(userRepository);
  }

//  @Test
//  void updateUserAvatar_validInput() throws IOException {
//    // Given
//    MockMultipartFile mockFile = new MockMultipartFile(
//            "avatar",
//            "avatar.png",
//            "image/png",
//            "test image content".getBytes()
//    );
//
//    // Ensure the initial state: neither file exists
//    Path userDir = Paths.get(tempDir.toString(), "user1");
//    Files.createDirectories(userDir);
//    Files.deleteIfExists(userDir.resolve("avatar_pos.png"));
//    Files.deleteIfExists(userDir.resolve("avatar_neg.png"));
//
//    when(userRepository.findById(anyLong())).thenReturn(Optional.of(testUser));
//
//    // When
//    User updatedUser = userService.updateUserAvatar(testUser.getId(), mockFile);
//
//    // Then
//    assertNotNull(updatedUser);
//    assertEquals("/images/users/user1/avatar_pos.png", updatedUser.getAvatarUrl());
//    verify(userRepository, times(1)).save(testUser);
//  }
//
//  @Test
//  void updateUserAvatar_validInput_switchToNeg() throws IOException {
//    // Given
//    MockMultipartFile mockFile = new MockMultipartFile(
//            "avatar",
//            "avatar.png",
//            "image/png",
//            "test image content".getBytes()
//    );
//
//    // Ensure the initial state: avatar_pos.png exists
//    Path userDir = Paths.get(tempDir.toString(), "user1");
//    Files.createDirectories(userDir);
//    Files.deleteIfExists(userDir.resolve("avatar_pos.png"));
//    Files.deleteIfExists(userDir.resolve("avatar_neg.png"));
//    Files.createFile(userDir.resolve("avatar_pos.png"));
//
//    when(userRepository.findById(anyLong())).thenReturn(Optional.of(testUser));
//
//    // When
//    User updatedUser = userService.updateUserAvatar(testUser.getId(), mockFile);
//
//    // Then
//    assertNotNull(updatedUser);
//    assertEquals("/images/users/user1/avatar_neg.png", updatedUser.getAvatarUrl());
//    verify(userRepository, times(1)).save(testUser);
//  }
//
//  @Test
//  void updateUserAvatar_validInput_switchToPos() throws IOException {
//    // Given
//    MockMultipartFile mockFile = new MockMultipartFile(
//            "avatar",
//            "avatar.png",
//            "image/png",
//            "test image content".getBytes()
//    );
//
//    // Ensure the initial state: avatar_pos.png exists
//    Path userDir = Paths.get(tempDir.toString(), "user1");
//    Files.createDirectories(userDir);
//    Files.deleteIfExists(userDir.resolve("avatar_pos.png"));
//    Files.deleteIfExists(userDir.resolve("avatar_neg.png"));
//    Files.createFile(userDir.resolve("avatar_neg.png"));
//
//    when(userRepository.findById(anyLong())).thenReturn(Optional.of(testUser));
//
//    // When
//    User updatedUser = userService.updateUserAvatar(testUser.getId(), mockFile);
//
//    // Then
//    assertNotNull(updatedUser);
//    assertEquals("/images/users/user1/avatar_pos.png", updatedUser.getAvatarUrl());
//    verify(userRepository, times(1)).save(testUser);
//  }

  @Test
  void updateUserAvatar_userNotFound() throws IOException {
    // Given
    MockMultipartFile mockFile = new MockMultipartFile(
            "avatar",
            "avatar.png",
            "image/png",
            "test image content".getBytes()
    );

    when(userRepository.findById(anyLong())).thenReturn(Optional.empty());

    // When/Then
    ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> {
      userService.updateUserAvatar(999L, mockFile);
    });

    assertEquals("404 NOT_FOUND \"User with ID: 999 is not found\"", exception.getMessage());
    verify(userRepository, times(0)).save(any(User.class));
  }

  @Test
  void updateUserAvatar_emptyFile() {
    // Given
    MockMultipartFile mockFile = new MockMultipartFile(
            "avatar",
            "avatar.png",
            "image/png",
            new byte[0]
    );

    // When/Then
    ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> {
      userService.updateUserAvatar(testUser.getId(), mockFile);
    });

    assertEquals("400 BAD_REQUEST \"file is empty\"", exception.getMessage());
    verify(userRepository, times(0)).save(any(User.class));
  }


  @Test
  public void createUser_validInputs_success() {
    // given
    User newUser = new User();
    newUser.setUsername("newUsername");
    newUser.setPassword("password");

    when(userRepository.save(Mockito.any())).thenReturn(newUser);
    when(userRepository.findByUsername(Mockito.any())).thenReturn(Optional.empty());

    // when
    User createdUser = userService.createUser(newUser);

    // then
    verify(userRepository, times(1)).save(Mockito.any());
    assertEquals(newUser.getId(), createdUser.getId());
    assertEquals(newUser.getUsername(), createdUser.getUsername());
    assertNotNull(createdUser.getToken());
    assertNotNull(createdUser.getCreationDate());
    assertEquals(UserStatus.ONLINE, createdUser.getStatus());
  }

  @Test
  public void createUser_duplicateInputs_throwsException() {
    // given
    User newUser = new User();
    newUser.setUsername("newUsername");
    newUser.setPassword("password");

    userService.createUser(newUser);
    when(userRepository.findByUsername(Mockito.any())).thenReturn(Optional.of(newUser));

    // when/then
    ResponseStatusException exception = assertThrows(
      ResponseStatusException.class, () -> userService.createUser(newUser));
    assertEquals(HttpStatus.CONFLICT, exception.getStatus());
  }

  @Test
  public void getAllUsers_success() {
    // given
    User user1 = new User();
    user1.setUsername("username1");
    User user2 = new User();
    user2.setUsername("username2");
    List<User> userList = List.of(user1, user2);
    when(userRepository.findAll()).thenReturn(userList);

    // when
    List<User> result = userService.getUsers();

    // then
    assertEquals(2, result.size());
    assertEquals(userList, result);
  }

  @Test
  public void getUser_validUserId_success() {
    // given
    User user = new User();
    user.setUsername("username");
    when(userRepository.findById(Mockito.any())).thenReturn(Optional.of(user));

    // when
    User result = userService.getUser(user.getId());

    // then
    assertEquals(result.getId(), user.getId());
    assertEquals(result.getUsername(), user.getUsername());
  }

  @Test
  public void getUser_invalidUserId_throwsException() {
    // given
    Long userId = 1L;
    when(userRepository.findById(Mockito.any())).thenReturn(Optional.empty());

    // when/ then
    ResponseStatusException exception = assertThrows(
      ResponseStatusException.class, () -> userService.getUser(userId));
    assertEquals(HttpStatus.NOT_FOUND, exception.getStatus());
  }

  @Test
  public void updateUser_validInput_success() {
    // given
    User existingUser = new User();
    existingUser.setUsername("username");
    existingUser.setBirthDate(new Date());

    Long userId = 1L;
    User userInput = new User();
    userInput.setUsername("newUsername");
    userInput.setBirthDate(new Date());

    when(userRepository.findById(Mockito.any())).thenReturn(Optional.of(existingUser));
    when(userRepository.findByUsername(Mockito.any())).thenReturn(Optional.empty());
    when(userRepository.save(Mockito.any(User.class))).thenReturn(userInput);

    // when
    userService.updateUser(userId, userInput);

    // then
    verify(userRepository, times(1)).save(Mockito.any());
  }

  @Test
  public void updateUser_invalidUserId_throwsException() {
    // given
    User existingUser = new User();
    existingUser.setUsername("username");
    existingUser.setBirthDate(new Date());

    Long userId = 1L;
    User userInput = new User();
    userInput.setUsername("newUsername");
    userInput.setBirthDate(new Date());

    when(userRepository.findById(Mockito.any())).thenReturn(Optional.empty());

    // when/ then
    ResponseStatusException exception = assertThrows(
      ResponseStatusException.class, () -> userService.updateUser(userId, userInput));
    assertEquals(HttpStatus.NOT_FOUND, exception.getStatus());
  }

  @Test
  public void updateUser_invalidUsername_throwsException() {
    // given
    User existingUser = new User();
    existingUser.setUsername("username");
    existingUser.setBirthDate(new Date());

    Long userId = 1L;
    User userInput = new User();
    userInput.setUsername("newUsername");
    userInput.setBirthDate(new Date());

    when(userRepository.findById(Mockito.any())).thenReturn(Optional.of(existingUser));
    when(userRepository.findByUsername(Mockito.any())).thenReturn(Optional.of(userInput));

    // when/ then
    ResponseStatusException exception = assertThrows(
      ResponseStatusException.class, () -> userService.updateUser(userId, userInput));
    assertEquals(HttpStatus.CONFLICT, exception.getStatus());
  }

  @Test
  public void login_validInput_success() {
    // given
    User existingUser = new User();
    existingUser.setUsername("username");
    existingUser.setPassword("password");

    User userInput = new User();
    userInput.setUsername("username");
    userInput.setPassword("password");

    when(userRepository.findByUsername(Mockito.any())).thenReturn(Optional.of(existingUser));
    when(userRepository.save(Mockito.any(User.class))).thenReturn(existingUser);

    // when
    User loggedInUser = userService.loginUser(userInput);

    // then
    verify(userRepository, times(1)).save(Mockito.any());
    assertEquals(userInput.getUsername(), loggedInUser.getUsername());
    assertEquals(UserStatus.ONLINE, loggedInUser.getStatus());
  }

  @Test
  public void login_invalidUsername_throwsException() {
    // given
    User userInput = new User();
    userInput.setUsername("username");
    userInput.setPassword("password");

    when(userRepository.findByUsername(Mockito.any())).thenReturn(Optional.empty());

    // when/then
    ResponseStatusException exception = assertThrows(
      ResponseStatusException.class, () -> userService.loginUser(userInput));
    assertEquals(HttpStatus.NOT_FOUND, exception.getStatus());
  }

  @Test
  public void login_invalidCredentials_throwsException() {
    // given
    User existingUser = new User();
    existingUser.setUsername("username");
    existingUser.setPassword("password");

    User userInput = new User();
    userInput.setUsername("username");
    userInput.setPassword("invalidPassword");

    when(userRepository.findByUsername(Mockito.any())).thenReturn(Optional.of(existingUser));

    // when/ then
    ResponseStatusException exception = assertThrows(
      ResponseStatusException.class, () -> userService.loginUser(userInput));
    assertEquals(HttpStatus.UNAUTHORIZED, exception.getStatus());
  }

  @Test
  public void logout_validInput_success() {
    // given
    User existingUser = new User();
    existingUser.setUsername("username");
    existingUser.setPassword("password");

    when(userRepository.findById(Mockito.any())).thenReturn(Optional.of(existingUser));
    when(userRepository.save(Mockito.any(User.class))).thenReturn(existingUser);

    // when
    userService.logoutUser(existingUser.getId());

    // then
    verify(userRepository, times(1)).save(Mockito.any());
    assertEquals(UserStatus.OFFLINE, existingUser.getStatus());
  }

  @Test
  public void logout_invalidUserId_throwsException() {
    // given
    User existingUser = new User();
    existingUser.setUsername("username");
    existingUser.setPassword("password");

    when(userRepository.findById(Mockito.any())).thenReturn(Optional.empty());

    // when/then
    ResponseStatusException exception = assertThrows(
      ResponseStatusException.class, () -> userService.logoutUser(existingUser.getId()));
    assertEquals(HttpStatus.NOT_FOUND, exception.getStatus());
  }

}
