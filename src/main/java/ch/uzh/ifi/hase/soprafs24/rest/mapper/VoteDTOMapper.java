package ch.uzh.ifi.hase.soprafs24.rest.mapper;

import ch.uzh.ifi.hase.soprafs24.entity.Vote;
import ch.uzh.ifi.hase.soprafs24.rest.dto.VoteDTO;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface VoteDTOMapper {
    VoteDTOMapper INSTANCE = Mappers.getMapper(VoteDTOMapper.class);

    Vote convertVoteDTOtoEntity(VoteDTO voteDTO);

    <R> R convertEntityToVoteDTO(Vote vote);
}
