package ch.uzh.ifi.hase.soprafs24.service;

import ch.uzh.ifi.hase.soprafs24.constant.Role;
import ch.uzh.ifi.hase.soprafs24.entity.Player;
import ch.uzh.ifi.hase.soprafs24.entity.Result;
import ch.uzh.ifi.hase.soprafs24.entity.Vote;
import ch.uzh.ifi.hase.soprafs24.repository.VoteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.*;
import java.util.stream.Collectors;

@Service
@Transactional
public class VoteService {

    private final VoteRepository voteRepository;

    @Autowired
    public VoteService(VoteRepository voteRepository) {
        this.voteRepository = voteRepository;
    }


    public void saveVote(Vote vote) {
        // Check if the voter has already voted in this lobby
        if (!hasVoted(vote.getVoterUserId(), vote.getLobbyId())) {
            voteRepository.save(vote);
            voteRepository.flush();
        } else {
            throw new IllegalStateException("Voter has already voted in this lobby.");
        }
    }

    private boolean hasVoted(Long voterUserId, Long lobbyId) {
        return voteRepository.findByVoterUserIdAndLobbyId(voterUserId, lobbyId).isPresent();
    }

    public Optional<Result> calculateResults(Long lobbyId, List<Player> players) {
        // get all votes by lobbyId
        List<Vote> votes = voteRepository.findByLobbyId(lobbyId);

        // if all players voted, then calculate and return result
        if (votes.size() == players.size()) {
            // determine winner's role
            Role winnerRole = determineWinnerRole(votes, players);

            // get result based on winnerRole
            Result result = getResultFromWinnerRole(winnerRole, players);
            return Optional.of(result);

        } else {
            return Optional.empty();
        }
    }

    private Role determineWinnerRole(List<Vote> votes, List<Player> players) {
        Map<Long, Integer> voteCount = new HashMap<>();
        votes.forEach(vote -> voteCount.merge(vote.getVotedUserId(), 1, Integer::sum));

        // find max number of voting
        Integer maxVotes = voteCount.values().stream().max(Integer::compare).orElse(0);

        // find max voted player(s)
        List<Long> maxVotedPlayerUserIds = voteCount.entrySet().stream()
          .filter(entry -> Objects.equals(entry.getValue(), maxVotes))
          .map(Map.Entry::getKey)
          .toList();

        // determine winner's role. if tied voting, then wolf wins
        Role winnerRole;
        if (maxVotedPlayerUserIds.size() >= 2) {
            winnerRole = Role.WOLF;
        } else  {
            Player maxVotedPlayer = players.stream()
              .filter(player -> player.getUserId().equals(maxVotedPlayerUserIds.get(0)))
              .findFirst()
              .orElseThrow(() -> new IllegalStateException("No player found with the given userId"));
            winnerRole = (maxVotedPlayer.getRole() == Role.WOLF) ? Role.VILLAGER : Role.WOLF;
        }

        return winnerRole;
    }

    private Result getResultFromWinnerRole(Role winnerRole, List<Player> players) {
        // classify players into winners and losers
        Map<Boolean, List<Player>> partitionedPlayers = players.stream()
          .collect(Collectors.partitioningBy(player -> player.getRole() == winnerRole));
        List<Player> winnerPlayers = partitionedPlayers.get(true);
        List<Player> loserPlayers = partitionedPlayers.get(false);

        // crate and return Result entity
        Result result = new Result();
        result.setWinnerRole(winnerRole);
        result.setWinnerPlayers(winnerPlayers);
        result.setLoserPlayers(loserPlayers);
        return result;
    }
}

