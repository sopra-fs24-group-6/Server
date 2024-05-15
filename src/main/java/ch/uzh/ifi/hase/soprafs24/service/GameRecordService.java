package ch.uzh.ifi.hase.soprafs24.service;


import ch.uzh.ifi.hase.soprafs24.entity.User;
import ch.uzh.ifi.hase.soprafs24.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class GameRecordService {
        

  private final UserRepository userRepository;

  @Autowired
  public GameRecordService(@Qualifier("userRepository") UserRepository userRepository) {
    this.userRepository = userRepository;
  }


    public List<User> getTopUsers(int page) {
        // Retrieve all users from the repository
      // Define the page size
      int pageSize = 10;

      // Create a Pageable object with the given page number and page size
        List<User> allUsers = userRepository.findAll();

        // Sort users based on their win-loss ratios
        List<User> topUsers = allUsers.stream().sorted(Comparator.comparingDouble(User::getWinlossratio).reversed())
                .collect(Collectors.toList());

      int start = (page - 1) * pageSize;
      int end = Math.min(start + pageSize, topUsers.size());

      return topUsers.subList(start, end);
    }

}
