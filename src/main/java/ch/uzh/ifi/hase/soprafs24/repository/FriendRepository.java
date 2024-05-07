package ch.uzh.ifi.hase.soprafs24.repository;

import ch.uzh.ifi.hase.soprafs24.entity.Friend;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository("friendRepository")
public interface FriendRepository extends JpaRepository<Friend, Long> {
    Optional<Friend> findBySenderUserId(Long senderUserId);
    Optional<Friend> findByReceiverUserId(Long receiverUserId);
    Optional<Friend> findBySenderUserIdAndReceiverUserId(Long senderUserId, Long receiverUserId);

}