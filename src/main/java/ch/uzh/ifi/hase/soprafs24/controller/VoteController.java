package ch.uzh.ifi.hase.soprafs24.controller;

import ch.uzh.ifi.hase.soprafs24.entity.Vote;
import ch.uzh.ifi.hase.soprafs24.rest.dto.VoteDTO;
import ch.uzh.ifi.hase.soprafs24.rest.mapper.VoteDTOMapper;
import ch.uzh.ifi.hase.soprafs24.service.VoteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
public class VoteController {

    private final VoteService voteService;

    @Autowired
    public VoteController(VoteService voteService) {
        this.voteService = voteService;
    }

    @PostMapping("/votes/{lobbyId}")
    @ResponseStatus(HttpStatus.CREATED)
    public void sendVote(@PathVariable("lobbyId") Long lobbyId, @RequestBody VoteDTO voteDTO) {
        Vote vote = VoteDTOMapper.INSTANCE.convertVoteDTOtoEntity(voteDTO);
        vote.setLobbyId(lobbyId);
        voteService.saveVote(vote);
    }

    @GetMapping("/votes/{lobbyId}")
    @ResponseStatus(HttpStatus.OK)
    public List<Object> getVotesByLobby(@PathVariable("lobbyId") Long lobbyId) {
        List<Vote> votes = voteService.getVotesByLobbyId(lobbyId);
        return votes.stream()
                .map(VoteDTOMapper.INSTANCE::convertEntityToVoteDTO)
                .collect(Collectors.toList());
    }

    @GetMapping("/votes/{lobbyId}/results")
    @ResponseStatus(HttpStatus.OK)
    public Map<String, Object> getVotingResults(@PathVariable("lobbyId") Long lobbyId) {
        return voteService.calculateResults(lobbyId);
    }
}
