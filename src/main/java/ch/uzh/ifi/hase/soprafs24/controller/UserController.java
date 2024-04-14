package ch.uzh.ifi.hase.soprafs24.controller;

import ch.uzh.ifi.hase.soprafs24.entity.User;
import ch.uzh.ifi.hase.soprafs24.rest.dto.*;
import ch.uzh.ifi.hase.soprafs24.rest.mapper.UserDTOMapper;
import ch.uzh.ifi.hase.soprafs24.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

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

  @PostMapping("/register")
  @ResponseStatus(HttpStatus.CREATED)
  @ResponseBody
  public UserGetDTO createUser(@RequestBody UserRegisterDTO userRegisterDTO) {
    // convert API user to internal representation
    User userInput = UserDTOMapper.INSTANCE.convertUserRegisterDTOtoEntity(userRegisterDTO);
    // create user
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
