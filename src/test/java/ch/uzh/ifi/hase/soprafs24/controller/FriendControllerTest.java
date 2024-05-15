package ch.uzh.ifi.hase.soprafs24.controller;

import ch.uzh.ifi.hase.soprafs24.entity.Friend;
import ch.uzh.ifi.hase.soprafs24.rest.dto.FriendPostDTO;
import ch.uzh.ifi.hase.soprafs24.service.FriendService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Arrays;
import java.util.List;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.hasSize;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.mockito.BDDMockito.given;

@WebMvcTest(FriendController.class)
public class FriendControllerTest {

  @Autowired
  private MockMvc mockMvc;

  @MockBean
  private FriendService friendService;

  @Test
  public void testGetFriends() throws Exception {
    Long userId = 1L;
    Friend friend = new Friend();
    friend.setId(1L);
    friend.setSenderUserId(1L);
    friend.setReceiverUserId(2L);
    friend.setIsApproved(true);

    List<Friend> friends = Arrays.asList(friend);

    given(friendService.getFriendsOf(userId)).willReturn(friends);

    mockMvc.perform(get("/friends")
                    .param("userId", userId.toString()))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$[0].id").value(friend.getId()))
            .andExpect(jsonPath("$[0].senderUserId").value(friend.getSenderUserId()))
            .andExpect(jsonPath("$[0].receiverUserId").value(friend.getReceiverUserId()))
            .andExpect(jsonPath("$[0].isApproved").value(true));
  }

  @Test
  public void testGetFriendRequests() throws Exception {
    Long userId = 1L;
    Friend friend = new Friend();
    friend.setId(1L);
    friend.setSenderUserId(1L);
    friend.setReceiverUserId(2L);
    friend.setIsApproved(false);

    List<Friend> friendRequests = Arrays.asList(friend);

    given(friendService.getFriendRequestsOf(userId)).willReturn(friendRequests);

    mockMvc.perform(get("/friends/friendRequests")
                    .param("userId", userId.toString()))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$", hasSize(1)))
            .andExpect(jsonPath("$[0].isApproved").value(false));
  }

  @Test
  public void testSendFriendRequest() throws Exception {
    FriendPostDTO dto = new FriendPostDTO();
    dto.setSenderUserId(1L);
    dto.setReceiverUserId(2L);

    mockMvc.perform(post("/friends/friendRequests")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content("{\"senderUserId\":1,\"receiverUserId\":2}"))
            .andExpect(status().isOk())
            .andExpect(content().string(containsString("Friend request sent successfully.")));

    verify(friendService).sendFriendRequest(1L, 2L);
  }

  @Test
  public void testAcceptFriendRequest() throws Exception {
    Long requesterId = 1L;

    mockMvc.perform(put("/friends/friendRequests/{requesterId}", requesterId))
            .andExpect(status().isOk())
            .andExpect(content().string(containsString("Friend request accepted successfully.")));

    verify(friendService).acceptFriendRequest(requesterId);
  }

  @Test
  public void testDenyFriendRequest() throws Exception {
    Long requesterId = 1L;

    mockMvc.perform(delete("/friends/friendRequests/{requesterId}", requesterId))
            .andExpect(status().isOk())
            .andExpect(content().string(containsString("Friend request denied successfully.")));

    verify(friendService).deleteFriendRequest(requesterId);
  }
}
