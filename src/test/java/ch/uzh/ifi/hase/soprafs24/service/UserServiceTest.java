package ch.uzh.ifi.hase.soprafs24.service;

import ch.uzh.ifi.hase.soprafs24.constant.UserStatus;
import ch.uzh.ifi.hase.soprafs24.entity.User;
import ch.uzh.ifi.hase.soprafs24.repository.UserRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInfo;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

public class UserServiceTest {

  @Mock
  private UserRepository userRepository;

  @InjectMocks
  private UserService userService;

  @BeforeEach
  public void setup() {
    MockitoAnnotations.openMocks(this);
  }

//    @AfterEach
//    public void afterEachTest(TestInfo testInfo) {
//        System.out.println("AfterUserServiceTest: " + testInfo.getDisplayName());
//        System.out.println("Current Environment Variables:");
//        String googleCredentials = System.getenv("GOOGLE_APPLICATION_CREDENTIALS");
//        if (googleCredentials != null) {
//            System.out.println("GOOGLE_APPLICATION_CREDENTIALS = " + googleCredentials);
//        } else {
//            System.out.println("GOOGLE_APPLICATION_CREDENTIALS is not set.");
//        }
//    }

  @Test
  public void createUser_validInputs_success() {
    // given
    User newUser = new User();
    newUser.setUsername("newUsername");
    newUser.setPassword("password");

    Mockito.when(userRepository.save(Mockito.any())).thenReturn(newUser);
    Mockito.when(userRepository.findByUsername(Mockito.any())).thenReturn(Optional.empty());

    // when
    User createdUser = userService.createUser(newUser);

    // then
    Mockito.verify(userRepository, Mockito.times(1)).save(Mockito.any());
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
    Mockito.when(userRepository.findByUsername(Mockito.any())).thenReturn(Optional.of(newUser));

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
    Mockito.when(userRepository.findAll()).thenReturn(userList);

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
    Mockito.when(userRepository.findById(Mockito.any())).thenReturn(Optional.of(user));

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
    Mockito.when(userRepository.findById(Mockito.any())).thenReturn(Optional.empty());

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

    Mockito.when(userRepository.findById(Mockito.any())).thenReturn(Optional.of(existingUser));
    Mockito.when(userRepository.findByUsername(Mockito.any())).thenReturn(Optional.empty());
    Mockito.when(userRepository.save(Mockito.any(User.class))).thenReturn(userInput);

    // when
    userService.updateUser(userId, userInput);

    // then
    Mockito.verify(userRepository, Mockito.times(1)).save(Mockito.any());
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

    Mockito.when(userRepository.findById(Mockito.any())).thenReturn(Optional.empty());

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

    Mockito.when(userRepository.findById(Mockito.any())).thenReturn(Optional.of(existingUser));
    Mockito.when(userRepository.findByUsername(Mockito.any())).thenReturn(Optional.of(userInput));

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

    Mockito.when(userRepository.findByUsername(Mockito.any())).thenReturn(Optional.of(existingUser));
    Mockito.when(userRepository.save(Mockito.any(User.class))).thenReturn(existingUser);

    // when
    User loggedInUser = userService.loginUser(userInput);

    // then
    Mockito.verify(userRepository, Mockito.times(1)).save(Mockito.any());
    assertEquals(userInput.getUsername(), loggedInUser.getUsername());
    assertEquals(UserStatus.ONLINE, loggedInUser.getStatus());
  }

  @Test
  public void login_invalidUsername_throwsException() {
    // given
    User userInput = new User();
    userInput.setUsername("username");
    userInput.setPassword("password");

    Mockito.when(userRepository.findByUsername(Mockito.any())).thenReturn(Optional.empty());

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

    Mockito.when(userRepository.findByUsername(Mockito.any())).thenReturn(Optional.of(existingUser));

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

    Mockito.when(userRepository.findById(Mockito.any())).thenReturn(Optional.of(existingUser));
    Mockito.when(userRepository.save(Mockito.any(User.class))).thenReturn(existingUser);

    // when
    userService.logoutUser(existingUser.getId());

    // then
    Mockito.verify(userRepository, Mockito.times(1)).save(Mockito.any());
    assertEquals(UserStatus.OFFLINE, existingUser.getStatus());
  }

  @Test
  public void logout_invalidUserId_throwsException() {
    // given
    User existingUser = new User();
    existingUser.setUsername("username");
    existingUser.setPassword("password");

    Mockito.when(userRepository.findById(Mockito.any())).thenReturn(Optional.empty());

    // when/then
    ResponseStatusException exception = assertThrows(
      ResponseStatusException.class, () -> userService.logoutUser(existingUser.getId()));
    assertEquals(HttpStatus.NOT_FOUND, exception.getStatus());
  }

}
