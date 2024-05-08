package ch.uzh.ifi.hase.soprafs24.controller;

import ch.uzh.ifi.hase.soprafs24.entity.User;
import ch.uzh.ifi.hase.soprafs24.rest.dto.*;
import ch.uzh.ifi.hase.soprafs24.rest.mapper.UserDTOMapper;
import ch.uzh.ifi.hase.soprafs24.service.UserService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * User Controller
 * This class is responsible for handling all REST request that are related to
 * the user.
 * The controller will receive the request and delegate the execution to the
 * UserService and finally return the result.
 */
@RestController
public class UserController {

  private final UserService userService;

  UserController(UserService userService) {
    this.userService = userService;
  }

  @GetMapping("/users")
  @ResponseStatus(HttpStatus.OK)
  @ResponseBody
  public List<UserGetDTO> getAllUsers() {
    // fetch all users in the internal representation
    List<User> users = userService.getUsers();
    List<UserGetDTO> userGetDTOs = new ArrayList<>();
    // convert each user to the API representation
    for (User user : users) {
      userGetDTOs.add(UserDTOMapper.INSTANCE.convertEntityToUserGetDTO(user));
    }
    return userGetDTOs;
  }

  @PostMapping("/{userId}/avatar")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  public UserAvatarDTO UpdateAvatar(@PathVariable Long userId,@RequestParam("avatar") MultipartFile file) throws IOException {

    User user = userService.updateUserAvatar(userId, file);
    return UserDTOMapper.INSTANCE.convertEntityToUserAvatarDTO(user);
  }

  @PutMapping("/users/{userId}")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  public void updateUser(@PathVariable("userId") Long userId, @RequestBody UserPutDTO userPutDTO) {
      User userInput = UserDTOMapper.INSTANCE.convertUserPutDTOtoEntity(userPutDTO);
      userService.updateUser(userId, userInput);
  }

  @GetMapping("/users/{userId}")
  @ResponseStatus(HttpStatus.OK)
  @ResponseBody
  public UserGetDTO getUser(@PathVariable("userId") Long userId) {
    // fetch user in the internal representation
    User user = userService.getUser(userId);
    return UserDTOMapper.INSTANCE.convertEntityToUserGetDTO(user);
  }

  @PostMapping("/users")
  @ResponseStatus(HttpStatus.CREATED)
  @ResponseBody
  public UserGetDTO createUser(@RequestBody UserRegisterDTO userRegisterDTO) {
    // convert API user to internal representation
    User userInput = UserDTOMapper.INSTANCE.convertUserRegisterDTOtoEntity(userRegisterDTO);
    // create user
      System.out.println(userInput.getUsername());
      System.out.println(userInput.getPassword());
      System.out.println(userInput.getLanguage());
    User createdUser = userService.createUser(userInput);
    // convert internal representation of user back to API
    return UserDTOMapper.INSTANCE.convertEntityToUserGetDTO(createdUser);
  }

  @PostMapping("/login")
  @ResponseStatus(HttpStatus.OK)
  @ResponseBody
  public UserGetDTO loginUser(@RequestBody UserPostDTO userPostDTO) {
    User userInput = UserDTOMapper.INSTANCE.convertUserPostDTOtoEntity(userPostDTO);
    User authenticatedUser = userService.loginUser(userInput);
    return UserDTOMapper.INSTANCE.convertEntityToUserGetDTO(authenticatedUser);
  }

  @PutMapping("/logout")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  public void logoutUser(@RequestBody UserIdDTO userIdDTO) {
    userService.logoutUser(userIdDTO.getUserId());
  }
}
