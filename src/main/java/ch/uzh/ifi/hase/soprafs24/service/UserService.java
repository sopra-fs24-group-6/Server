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
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Random;
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

  // Path where we store the avatar images
  private static final String AVATAR_DIR = "src/main/images/users";

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

  public User updateUserAvatar(Long userId, MultipartFile file) throws IOException {
    // Validate the file
    if (file == null || file.isEmpty()) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "file is empty");
    }
    String user_file = AVATAR_DIR + "/user" + userId;
    // Ensure the directory exists
    Path uploadDir = Paths.get(user_file);
    if (!Files.exists(uploadDir)) {
      Files.createDirectories(uploadDir);
    }

    // Set up the two possible avatar file paths
    Path posPath = uploadDir.resolve("avatar_pos.png");
    Path negPath = uploadDir.resolve("avatar_neg.png");
    Path targetPath;

    // Determine which file to use
    if (Files.exists(posPath)) {
      // `avatar_pos.png` exists, switch to `avatar_neg.png`
      Files.delete(posPath);
      targetPath = negPath;
    } else if (Files.exists(negPath)) {
      // `avatar_neg.png` exists, switch to `avatar_pos.png`
      Files.delete(negPath);
      targetPath = posPath;
    } else {
      // Neither file exists, start with `avatar_pos.png`
      targetPath = posPath;
    }

    Files.write(targetPath, file.getBytes());
    String avatarUrl = "/images/users/user" + userId + "/" + targetPath.getFileName().toString();

    // Update the user's avatar field in the database
    User user = userRepository.findById(userId).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User with ID: " + userId + " is not found"));
    user.setAvatarUrl(avatarUrl);
    userRepository.save(user);
    userRepository.flush();
    // Return the URL or path to the stored avatar (adjust as needed)
    return user;
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
    // Load images as byte arrays
    String villagerAvatar = "/images/avatar/villager.png";
    String wolfAvatar = "/images/avatar/wolf.png";

    // Randomly select one of the avatars
    Random random = new Random();
    String selectedAvatar = random.nextBoolean() ? villagerAvatar : wolfAvatar;
    System.out.println(selectedAvatar);

    // Assign selected avatar to the newUser
    newUser.setAvatarUrl(selectedAvatar);

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
