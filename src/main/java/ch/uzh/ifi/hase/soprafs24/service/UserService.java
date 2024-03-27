package ch.uzh.ifi.hase.soprafs24.service;

import ch.uzh.ifi.hase.soprafs24.constant.UserStatus;
import ch.uzh.ifi.hase.soprafs24.entity.User;
import ch.uzh.ifi.hase.soprafs24.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.Date;

/**
 * User Service
 * This class is the "worker" and responsible for all functionality related to
 * the user
 * (e.g., it creates, modifies, deletes, finds). The result will be passed back
 * to the caller.
 */
@Service
@Transactional
public class UserService {

  private final Logger log = LoggerFactory.getLogger(UserService.class);

  private final UserRepository userRepository;

  @Autowired
  public UserService(@Qualifier("userRepository") UserRepository userRepository) {
    this.userRepository = userRepository;
  }

  public List<User> getUsers() {
    return this.userRepository.findAll();
  }

  public User getUserProfile(Long id) {
    checkIfUserIdExists(id);
    return this.userRepository.findUserById(id);
  }

  public User createUser(User newUser) {
    newUser.setToken(UUID.randomUUID().toString());
    newUser.setStatus(UserStatus.ONLINE);
    newUser.setCreationDate(new Date());
    checkIfUserExists(newUser);
    // saves the given entity but data is only persisted in the database once
    // flush() is called
    newUser = userRepository.save(newUser);
    userRepository.flush();

    log.debug("Created Information for User: {}", newUser);
    return newUser;
  }

  public User Edit(Long id,User user) {
      Optional<User> userOptional = this.userRepository.findById(id);
      String NotFoundMessage = "User with id:%s is not Found. Have you registered yet?";
      String ConflictUserName = "Username: %s has been used, please choose another username";
      if (!userOptional.isPresent()) {
          throw new ResponseStatusException(HttpStatus.NOT_FOUND, String.format(NotFoundMessage , id));
      } else {
          User userByUsername = userRepository.findByUsername(user.getUsername());
          if (userByUsername != null) {
              throw new ResponseStatusException(HttpStatus.CONFLICT, String.format(ConflictUserName , user.getUsername()));
          }
          userOptional.get().setBirthday(user.getBirthday());
          userOptional.get().setUsername(user.getUsername());
          return userOptional.get();
      }
  }

  // Authentication for login
  public User authentication(String username, String password) {
    User user = userRepository.findByUsername(username);
    if (user != null && user.getPassword().equals(password)) {
      user.setStatus(UserStatus.ONLINE);
      return user;
    } else {
      throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Username or password is incorrect");
    }
  }

  // this function handles the user status
  public User updateUserStatus(Long id, UserStatus status) {
    Optional<User> result = userRepository.findById(id);

    if (!result.isPresent()) {
      throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User with this id could not be found.");
    }

    User user = result.get();
    user.setStatus(status);
    return userRepository.save(user);

  }

  /**
   * This is a helper method that will check the uniqueness criteria of the
   * username and the name
   * defined in the User entity. The method will do nothing if the input is unique
   * and throw an error otherwise.
   *
   * @param userToBeCreated
   * @throws org.springframework.web.server.ResponseStatusException
   * @see User
   */
  private void checkIfUserExists(User userToBeCreated) {
    User userByUsername = userRepository.findByUsername(userToBeCreated.getUsername());
    String baseErrorMessage = "The %s provided is not unique. Therefore, the user could not be created!";
    if (userByUsername != null) {
      throw new ResponseStatusException(HttpStatus.CONFLICT, String.format(baseErrorMessage, "username"));
    }
  }

  private void checkIfUserIdExists(Long userId) {
    User userById = userRepository.findUserById(userId);
    if (userById == null) {
      throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Id not found: " + userId);
    }
  }

}
