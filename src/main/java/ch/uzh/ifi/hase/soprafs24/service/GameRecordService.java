package ch.uzh.ifi.hase.soprafs24.service;


import ch.uzh.ifi.hase.soprafs24.entity.User;
import ch.uzh.ifi.hase.soprafs24.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
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

    public List<User> getTopUsers_10() {
        // Retrieve all users from the repository
        List<User> allUsers = userRepository.findAll();

        // Calculate win-loss ratio for each user
        allUsers.forEach(user -> {
            int wins = user.getWins();
            int losses = user.getLosses();
            double winLossRatio = weightedWinLossRatio(wins, losses);
            user.setWinlossratio(winLossRatio);
            userRepository.save(user);
        });

        userRepository.flush();

        // Sort users based on their win-loss ratios
        List<User> topUsers = allUsers.stream()
                .sorted(Comparator.comparingDouble(User::getWinlossratio).reversed())
                .limit(10) // Limit to top 10 users
                .collect(Collectors.toList());

        return topUsers;
    }

    public static double calculateWinLossRatio(int wins, int losses) {
        if (wins + losses == 0) {
            return 0; // Avoid division by zero
        }
        return (double) wins / (wins + losses);
    }

    public static double weightedWinLossRatio(int wins, int losses) {
        int gamesPlayed = wins + losses;
        if (gamesPlayed == 0) {
            return 0; // Avoid division by zero
        }
        // Adjusted with a weighting factor based on the number of games played
        double weight = 1 + Math.log10(gamesPlayed); // You can adjust this weighting factor as needed
        return calculateWinLossRatio(wins, losses) * weight;
    }

}
