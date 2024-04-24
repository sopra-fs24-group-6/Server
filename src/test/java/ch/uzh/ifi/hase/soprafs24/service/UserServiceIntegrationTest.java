package ch.uzh.ifi.hase.soprafs24.service;

import ch.uzh.ifi.hase.soprafs24.constant.UserStatus;
import ch.uzh.ifi.hase.soprafs24.entity.User;
import ch.uzh.ifi.hase.soprafs24.repository.UserRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.web.server.ResponseStatusException;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for the UserResource REST resource.
 *
 * @see UserService
 */
@WebAppConfiguration
@SpringBootTest
public class UserServiceIntegrationTest {

  @Qualifier("userRepository")
  @Autowired
  private UserRepository userRepository;

  @Autowired
  private UserService userService;

  @BeforeEach
  public void setup() {
    userRepository.deleteAll();
  }

//    @AfterEach
//    public void afterEachTest(TestInfo testInfo) {
//        System.out.println("After UserServiceIntegrationTest: " + testInfo.getDisplayName());
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
    assertTrue(userRepository.findByUsername("username").isEmpty());

    User userInput = new User();
    userInput.setUsername("username");
    userInput.setPassword("password");
    userInput.setLanguage("en");

    // when
    User createdUser = userService.createUser(userInput);

    // then
    assertEquals(userInput.getId(), createdUser.getId());
    assertEquals(userInput.getUsername(), createdUser.getUsername());
    assertEquals(userInput.getPassword(), createdUser.getPassword());
    assertNotNull(createdUser.getToken());
    assertNotNull(createdUser.getCreationDate());
    assertEquals(UserStatus.ONLINE, createdUser.getStatus());
  }

  @Test
  public void createUser_duplicateUsername_throwsException() {
    assertTrue(userRepository.findByUsername("testUsername").isEmpty());

    User testUser = new User();
    testUser.setUsername("testUsername");
    testUser.setPassword("password");
    testUser.setLanguage("en");
    User createdUser = userService.createUser(testUser);

    // attempt to create second user with same username
    User testUser2 = new User();

    // change the name but forget about the username
    testUser2.setUsername("testUsername");
    testUser2.setPassword("password");

    // check that an error is thrown
    assertThrows(ResponseStatusException.class, () -> userService.createUser(testUser2));
  }
}
