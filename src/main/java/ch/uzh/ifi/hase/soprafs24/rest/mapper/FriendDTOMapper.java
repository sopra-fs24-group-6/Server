package ch.uzh.ifi.hase.soprafs24.rest.mapper;

import ch.uzh.ifi.hase.soprafs24.entity.Friend;
import ch.uzh.ifi.hase.soprafs24.rest.dto.FriendPostDTO;
import ch.uzh.ifi.hase.soprafs24.rest.dto.FriendGetDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface FriendDTOMapper {
    FriendDTOMapper INSTANCE = Mappers.getMapper(FriendDTOMapper.class);

    @Mapping(source = "senderUserId", target = "senderUserId")
    @Mapping(source = "receiverUserId", target = "receiverUserId")
    Friend friendPostDTOToEntity(FriendPostDTO friendPostDTO);

    @Mapping(source = "id", target = "id")
    @Mapping(source = "senderUserId", target = "senderUserId")
    @Mapping(source = "receiverUserId", target = "receiverUserId")
    @Mapping(source = "isApproved", target = "isApproved")
    FriendGetDTO friendToFriendGetDTO(Friend friend);
}
