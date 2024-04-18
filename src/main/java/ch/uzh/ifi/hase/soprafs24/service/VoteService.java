package ch.uzh.ifi.hase.soprafs24.service;

import ch.uzh.ifi.hase.soprafs24.entity.Vote;
import ch.uzh.ifi.hase.soprafs24.repository.VoteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Transactional
public class VoteService {

    private final VoteRepository voteRepository;

    @Autowired
    public VoteService(VoteRepository voteRepository) {
        this.voteRepository = voteRepository;
    }

    public Vote saveVote(Vote vote) {
        // Check if the voter has already voted in this lobby
        if (!hasVoted(vote.getVoterUserId(), vote.getLobbyId())) {
            return voteRepository.save(vote);
        } else {
            throw new IllegalStateException("Voter has already voted in this lobby.");
        }
    }

    public List<Vote> getVotesByLobbyId(Long lobbyId) {
        return voteRepository.findByLobbyId(lobbyId);
    }

    private boolean hasVoted(Long voterUserId, Long lobbyId) {
        return voteRepository.findByVoterUserIdAndLobbyId(voterUserId, lobbyId).isPresent();
    }

    public Map<String, Object> calculateResults(Long lobbyId) {
        List<Vote> votes = voteRepository.findByLobbyId(lobbyId);
        Map<Long, Integer> voteCount = new HashMap<>();

        // Count votes for each voted user
        votes.forEach(vote -> voteCount.merge(vote.getVotedUserId(), 1, Integer::sum));

        // Determine majority vote
        boolean majorityIsWolf = false; // This assumes we know the roles somehow (to be implemented)
        int maxVotes = 0;
        for (Map.Entry<Long, Integer> entry : voteCount.entrySet()) {
            if (entry.getValue() > maxVotes) {
                maxVotes = entry.getValue();
                majorityIsWolf = isWolf(entry.getKey()); // isWolf needs to check the user's role
            }
        }

        // Determine winners and losers based on majority vote
        String winningRole = majorityIsWolf ? "Villagers" : "Wolf";
        String losingRole = majorityIsWolf ? "Wolf" : "Villagers";

        // This would need to collect user IDs of winners and losers
        List<Long> winners = findWinners(lobbyId, winningRole);
        List<Long> losers = findWinners(lobbyId, losingRole);

        // Return the results
        Map<String, Object> results = new HashMap<>();
        results.put("winners", winners);
        results.put("losers", losers);
        results.put("winningRole", winningRole);

        return results;
    }

    private boolean isWolf(Long userId) {
        // This method should check if the given userId belongs to a player who is a wolf
        // Placeholder for actual implementation
        return false;
    }

    private List<Long> findWinners(Long lobbyId, String role) {
        // This method should return the list of user IDs who are winners based on the role
        // Placeholder for actual implementation
        return List.of();
    }
}

