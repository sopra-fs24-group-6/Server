package ch.uzh.ifi.hase.soprafs24.repository;

import ch.uzh.ifi.hase.soprafs24.constant.UserStatus;
import ch.uzh.ifi.hase.soprafs24.entity.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.util.Date;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
public class UserRepositoryIntegrationTest {

  @Autowired
  private TestEntityManager entityManager;

  @Autowired
  private UserRepository userRepository;

  @Test
  public void findByUsername_success() {
    // given
    User user = new User();
    user.setUsername("firstname@lastname");
    user.setPassword("password");
    user.setStatus(UserStatus.OFFLINE);
    user.setToken("1");
    user.setCreationDate(new Date());

    entityManager.persist(user);
    entityManager.flush();

    // when
    Optional<User> found = userRepository.findByUsername(user.getUsername());

    // then
    assertTrue(found.isPresent());
    assertNotNull(found.get().getId());
    assertEquals(found.get().getUsername(), user.getUsername());
    assertEquals(found.get().getPassword(), user.getPassword());
    assertEquals(found.get().getToken(), user.getToken());
    assertEquals(found.get().getStatus(), user.getStatus());
  }
}
