package ch.uzh.ifi.hase.soprafs24.controller;

import ch.uzh.ifi.hase.soprafs24.entity.Game;
import ch.uzh.ifi.hase.soprafs24.entity.Player;
import ch.uzh.ifi.hase.soprafs24.entity.Result;
import ch.uzh.ifi.hase.soprafs24.entity.Vote;
import ch.uzh.ifi.hase.soprafs24.rest.dto.VoteDTO;
import ch.uzh.ifi.hase.soprafs24.rest.mapper.VoteDTOMapper;
import ch.uzh.ifi.hase.soprafs24.service.GameService;
import ch.uzh.ifi.hase.soprafs24.service.VoteService;
import ch.uzh.ifi.hase.soprafs24.websocket.dto.ChatMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
public class VoteController {

    private final VoteService voteService;
    private final GameService gameService;

    @Autowired
    public VoteController(VoteService voteService, GameService gameService) {
        this.voteService = voteService;
        this.gameService = gameService;
    }

    @MessageMapping("/vote/{lobbyId}/sendVote")
    public void saveVoteAndNotifyResult(@DestinationVariable Long lobbyId, @Payload VoteDTO voteDTO) {
        // get game
        Game game = gameService.getActiveGameByLobbyId(lobbyId);

        // save vote
        Vote vote = VoteDTOMapper.INSTANCE.convertVoteDTOtoEntity(voteDTO);
        vote.setLobbyId(lobbyId);
        vote.setRound(game.getCurrentRound());
        voteService.saveVote(vote);

        // calculate result. if not all players have voted, then return Optional.empty()
        Optional<Result> result = voteService.calculateResults(game);

        // if result is calculated, then notify players
        result.ifPresent(value -> gameService.notifyResults(lobbyId, value));
    }

//    @PostMapping("/vote/{lobbyId}/sendVote")
//    @ResponseStatus(HttpStatus.CREATED)
//    public void sendVote(@PathVariable("lobbyId") Long lobbyId, @RequestBody VoteDTO voteDTO) {
//        Vote vote = VoteDTOMapper.INSTANCE.convertVoteDTOtoEntity(voteDTO);
//        vote.setLobbyId(lobbyId);
//        voteService.saveVote(vote);
//    }
//
//    @GetMapping("/vote/{lobbyId}/getVotes")
//    @ResponseStatus(HttpStatus.OK)
//    public List<Object> getVotesByLobby(@PathVariable("lobbyId") Long lobbyId) {
//        List<Vote> votes = voteService.getVotesByLobbyId(lobbyId);
//        return votes.stream()
//                .map(VoteDTOMapper.INSTANCE::convertEntityToVoteDTO)
//                .collect(Collectors.toList());
//    }
//
//    @GetMapping("/vote/{lobbyId}/results")
//    @ResponseStatus(HttpStatus.OK)
//    public Map<String, Object> getVotingResults(@PathVariable("lobbyId") Long lobbyId) {
//        return voteService.calculateResults(lobbyId);
//    }
}
