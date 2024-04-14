package ch.uzh.ifi.hase.soprafs24.rest.mapper;

import ch.uzh.ifi.hase.soprafs24.entity.User;
import ch.uzh.ifi.hase.soprafs24.rest.dto.*;
import org.mapstruct.*;
import org.mapstruct.factory.Mappers;

/**
 * DTOMapper
 * This class is responsible for generating classes that will automatically
 * transform/map the internal representation
 * of an entity (e.g., the User) to the external/API representation (e.g.,
 * UserGetDTO for getting, UserPostDTO for creating)
 * and vice versa.
 * Additional mappers can be defined for new entities.
 * Always created one mapper for getting information (GET) and one mapper for
 * creating information (POST).
 */
@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface UserDTOMapper {

  UserDTOMapper INSTANCE = Mappers.getMapper(UserDTOMapper.class);

  @Mapping(source = "username", target = "username")
  @Mapping(source = "password", target = "password")
  User convertUserPostDTOtoEntity(UserPostDTO userPostDTO);

  @Mapping(source = "username", target = "username")
  @Mapping(source = "birthDate", target = "birthDate")
  @Mapping(source = "language", target = "language")
  User convertUserPutDTOtoEntity(UserPutDTO userPutDTO);


  @Mapping(source = "username", target = "username")
  @Mapping(source = "password", target = "password")
  @Mapping(source = "language", target = "language")
  User convertUserRegisterDTOtoEntity(UserRegisterDTO userRegisterDTO);

  @Mapping(source = "id", target = "id")
  @Mapping(source = "username", target = "username")
  @Mapping(source = "status", target = "status")
  @Mapping(source = "creationDate", target = "creationDate")
  @Mapping(source = "birthDate", target = "birthDate")
  @Mapping(source = "token", target = "token")
  @Mapping(source = "language", target = "language")
  UserGetDTO convertEntityToUserGetDTO(User user);
}
