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

  public User getUser(Long userId) {
    return findUserById(userId);
  }

  public User createUser(User newUser) {
    // set properties to newUser
    newUser.setToken(UUID.randomUUID().toString());
    newUser.setStatus(UserStatus.ONLINE);
    newUser.setCreationDate(new Date());
    // check username conflict
    checkIfUsernameExists(newUser.getUsername());
    // saves the given entity but data is only persisted in the database once
    // flush() is called
    newUser = userRepository.save(newUser);
    userRepository.flush();

    log.debug("Created Information for User: {}", newUser);
    return newUser;
  }

  public void updateUser(Long userId, User userInput) {
    // find user by userId
    User userToBeUpdated = findUserById(userId);
    // check username conflict
    checkIfUsernameExists(userInput.getUsername());
    // update user properties
    userToBeUpdated.setUsername(userInput.getUsername());
    userToBeUpdated.setBirthDate(userInput.getBirthDate());
    userToBeUpdated.setLanguage(userInput.getLanguage());
    // save to database
    userToBeUpdated = userRepository.save(userToBeUpdated);
    userRepository.flush();
  }

  // Authentication for login
  public User loginUser(User userInput) {
    // find user by username
    User userByUsername = findUserByUsername(userInput.getUsername());
    // check if correct password
    if (userByUsername.getPassword().equals(userInput.getPassword())) {
      // update user status
      userByUsername.setStatus(UserStatus.ONLINE);
      userByUsername = userRepository.save(userByUsername);
      userRepository.flush();
      return userByUsername;
    } else {
      throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Username or password is incorrect");
    }
  }

  // this function handles the user status
  public void logoutUser(Long userId) {
    // find user by userId
    // if not found, throw exception
    User userById = findUserById(userId);
    // update status and save
    userById.setStatus(UserStatus.OFFLINE);
    userById = userRepository.save(userById);
    userRepository.flush();
  }

  /**
   * Helper methods
   */
  public void checkIfUsernameExists (String username) {
    userRepository.findByUsername(username).ifPresent(existingUsername -> {
      throw new ResponseStatusException(
        HttpStatus.CONFLICT, "User with username " + username + " already exists.");
    });
  }

  public User findUserById (Long userId) {
    return userRepository.findById(userId)
      .orElseThrow(() -> new ResponseStatusException(
        HttpStatus.NOT_FOUND, "User with id " + userId + " could not be found."));
  }

  public User findUserByUsername (String username) {
    return userRepository.findByUsername(username)
      .orElseThrow(() -> new ResponseStatusException(
        HttpStatus.NOT_FOUND, "User with username " + username + " could not be found."));
  }

}
