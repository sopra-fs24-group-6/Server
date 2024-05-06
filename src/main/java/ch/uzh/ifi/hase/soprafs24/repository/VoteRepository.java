package ch.uzh.ifi.hase.soprafs24.repository;

import ch.uzh.ifi.hase.soprafs24.entity.User;
import ch.uzh.ifi.hase.soprafs24.entity.Vote;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository("voteRepository")
public interface VoteRepository extends JpaRepository<Vote, Long> {

    Optional<Vote> findByVoterUserId(Long voterUserId);
    Optional<Vote> findByVotedUserId(Long votedUserId);

    List<Vote> findByLobbyId(Long lobbyId);

    List<Vote> findByLobbyIdAndRound(Long lobbyId, Integer round);

    Optional<Object> findByVoterUserIdAndLobbyId(Long voterUserId, Long lobbyId);

    Optional<Object> findByVoterUserIdAndLobbyIdAndRound(Long voterUserId, Long lobbyId, Integer round);

    void deleteByLobbyId(Long lobbyId);
}