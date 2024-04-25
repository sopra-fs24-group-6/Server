package ch.uzh.ifi.hase.soprafs24.service;

import ch.uzh.ifi.hase.soprafs24.entity.Vote;
import ch.uzh.ifi.hase.soprafs24.entity.Player;
import ch.uzh.ifi.hase.soprafs24.repository.*;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class VoteServiceTest {

  @Mock
  private VoteRepository voteRepository;

  @InjectMocks
  private VoteService voteService;

  @BeforeEach
  public void setup() {
    MockitoAnnotations.openMocks(this);
  }


  @Test
  public void saveVote_success() {
    // given
    Vote vote = new Vote();
    vote.setLobbyId(1L);
    vote.setVoterUserId(1L);
    vote.setVotedUserId(2L);

    Mockito.when(voteRepository.findByVoterUserIdAndLobbyId(Mockito.any(), Mockito.any()))
      .thenReturn(Optional.empty());

    // when
    voteService.saveVote(vote);

    // then
    Mockito.verify(voteRepository).save(vote);
    Mockito.verify(voteRepository).flush();
  }

}
