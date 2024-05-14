package ch.uzh.ifi.hase.soprafs24.controller;

import ch.uzh.ifi.hase.soprafs24.entity.User;
import ch.uzh.ifi.hase.soprafs24.rest.dto.UserGetDTO;
import ch.uzh.ifi.hase.soprafs24.service.GameRecordService;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.test.web.servlet.MockMvc;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(GameRecordController.class)
class GameRecordControllerTest {

  @Autowired
  private MockMvc mockMvc;

  @MockBean
  private GameRecordService gameRecordService;

  @Test
  void testGetTopPlayers() throws Exception {
    // Mock data
    List<User> users = new ArrayList<>();
    users.add(new User());
    users.add(new User());

    List<UserGetDTO> userGetDTOs = new ArrayList<>();
    userGetDTOs.add(new UserGetDTO());
    userGetDTOs.add(new UserGetDTO());

    // Mock behavior for page 1
    when(gameRecordService.getTopUsers(1)).thenReturn(users);

    // Perform GET request for page 1 and verify status
    mockMvc.perform(get("/leaderboard/page=1"))
            .andExpect(status().isOk());

    // Mock behavior for page 2
    when(gameRecordService.getTopUsers(2)).thenReturn(users);

    // Perform GET request for page 2 and verify status
    mockMvc.perform(get("/leaderboard/page=2"))
            .andExpect(status().isOk());
  }
}