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
  void testGetTopUsers_10() {
    List<User> testUsers = new ArrayList<>();
    User user1 = new User();
    User user2 = new User();
    User user3 = new User();
    user1.setUsername("user1");
    user1.setWins(10);
    user1.setLosses(5);
    user2.setUsername("user2");
    user2.setWins(20);
    user2.setLosses(15);
    user3.setUsername("user3");
    user3.setWins(5);
    user3.setLosses(10);
    testUsers.add(user1);
    testUsers.add(user2);
    testUsers.add(user3);

    when(userRepository.findAll()).thenReturn(testUsers);

    List<User> topUsers = gameRecordService.getTopUsers_10();

    assertEquals(3, topUsers.size());
    assertEquals("user2", topUsers.get(0).getUsername());
    assertEquals("user1", topUsers.get(1).getUsername());
  }
}
