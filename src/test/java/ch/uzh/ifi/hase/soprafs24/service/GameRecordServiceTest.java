package ch.uzh.ifi.hase.soprafs24.service;

import ch.uzh.ifi.hase.soprafs24.entity.User;
import ch.uzh.ifi.hase.soprafs24.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import java.util.ArrayList;
import java.util.List;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@SpringBootTest
public class GameRecordServiceTest {
  @Autowired
  private GameRecordService gameRecordService;

  @MockBean
  private UserRepository userRepository;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);
  }

  @Test
  void testGetTopUsers() {
    List<User> testUsers = new ArrayList<>();
    for (int i = 1; i <= 15; i++) {
      User user = new User();
      user.setUsername("user" + i);
      user.setWins(i * 10);
      user.setLosses(i);
      user.updateWeightedWinLossRatio();
      testUsers.add(user);
    }

    when(userRepository.findAll()).thenReturn(testUsers);

    List<User> topUsersPage1 = gameRecordService.getTopUsers(1);
    List<User> topUsersPage2 = gameRecordService.getTopUsers(2);

    assertEquals(10, topUsersPage1.size());
    assertEquals("user15", topUsersPage1.get(0).getUsername());
    assertEquals("user6", topUsersPage1.get(9).getUsername());

    assertEquals(5, topUsersPage2.size());
    assertEquals("user5", topUsersPage2.get(0).getUsername());
    assertEquals("user1", topUsersPage2.get(4).getUsername());
  }
}
