package ch.uzh.ifi.hase.soprafs24.rest.mapper;

import ch.uzh.ifi.hase.soprafs24.constant.UserStatus;
import ch.uzh.ifi.hase.soprafs24.entity.User;
import ch.uzh.ifi.hase.soprafs24.rest.dto.UserGetDTO;
import ch.uzh.ifi.hase.soprafs24.rest.dto.UserPostDTO;
import ch.uzh.ifi.hase.soprafs24.rest.dto.UserPutDTO;
import org.junit.jupiter.api.Test;

import java.util.Date;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * DTOMapperTest
 * Tests if the mapping between the internal and the external/API representation
 * works.
 */
public class UserDTOMapperTest {
  @Test
  public void testCreateUser_fromUserPostDTO_toUser_success() {
    // create UserPostDTO
    UserPostDTO userPostDTO = new UserPostDTO();
    userPostDTO.setUsername("username");
    userPostDTO.setPassword("password");

    // MAP -> Create user
    User user = UserDTOMapper.INSTANCE.convertUserPostDTOtoEntity(userPostDTO);

    // check content
    assertEquals(userPostDTO.getUsername(), user.getUsername());
    assertEquals(userPostDTO.getPassword(), user.getPassword());
  }

  @Test
  public void testGetUser_fromUser_toUserGetDTO_success() {
    // create User
    User user = new User();
    user.setId(1L);
    user.setUsername("username");
    user.setStatus(UserStatus.OFFLINE);
    user.setCreationDate(new Date());
    user.setBirthDate(new Date());
    user.setToken("token");

    // MAP -> Create UserGetDTO
    UserGetDTO userGetDTO = UserDTOMapper.INSTANCE.convertEntityToUserGetDTO(user);

    // check content
    assertEquals(user.getId(), userGetDTO.getId());
    assertEquals(user.getUsername(), userGetDTO.getUsername());
    assertEquals(user.getStatus(), userGetDTO.getStatus());
    assertEquals(user.getCreationDate(), userGetDTO.getCreationDate());
    assertEquals(user.getBirthDate(), userGetDTO.getBirthDate());
    assertEquals(user.getToken(), userGetDTO.getToken());
  }

  @Test
  public void testUpdateUser_fromUserPutDTO_toUser_success() {
    // create User
    UserPutDTO userPutDTO = new UserPutDTO();
    userPutDTO.setUsername("newUsername");
    userPutDTO.setBirthDate(new Date());

    // MAP -> Create UserGetDTO
    User user = UserDTOMapper.INSTANCE.convertUserPutDTOtoEntity(userPutDTO);

    // check content
    assertEquals(userPutDTO.getUsername(), user.getUsername());
    assertEquals(userPutDTO.getBirthDate(), user.getBirthDate());
  }

}
