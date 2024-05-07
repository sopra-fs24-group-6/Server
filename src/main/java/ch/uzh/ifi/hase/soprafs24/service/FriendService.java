package ch.uzh.ifi.hase.soprafs24.service;

import ch.uzh.ifi.hase.soprafs24.entity.Friend;
import ch.uzh.ifi.hase.soprafs24.repository.FriendRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;

@Service
public class FriendService {
    @Autowired
    private FriendRepository friendRepository;

    // Send a friend request
    public void sendFriendRequest(Long senderUserId, Long receiverUserId) {
        Optional<Friend> existingRequest = friendRepository.findBySenderUserIdAndReceiverUserId(senderUserId, receiverUserId);
        if (existingRequest.isPresent()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Request already sent");
        }
        Friend newFriendRequest = new Friend();
        newFriendRequest.setSenderUserId(senderUserId);
        newFriendRequest.setReceiverUserId(receiverUserId);
        newFriendRequest.setIsApproved(false);
        friendRepository.save(newFriendRequest);
    }

    // Accept a friend request (add friends mutually)
    public void acceptFriendRequest(Long userId, Long requesterId) {
        // Retrieve the friend request to ensure it exists and is not yet approved
        Optional<Friend> friendRequestOpt = friendRepository.findBySenderUserIdAndReceiverUserId(requesterId, userId);
        if (!friendRequestOpt.isPresent()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Friend request not found.");
        }

        Friend friendRequest = friendRequestOpt.get();
        if (friendRequest.getIsApproved()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Friend request already accepted.");
        }

        // Approve the friend request
        friendRequest.setIsApproved(true);
        friendRepository.save(friendRequest);

        // Check if the reverse friendship exists; if not, create it
        Optional<Friend> reverseFriendOpt = friendRepository.findBySenderUserIdAndReceiverUserId(userId, requesterId);
        if (!reverseFriendOpt.isPresent() || !reverseFriendOpt.get().getIsApproved()) {
            Friend newFriend = new Friend();
            newFriend.setSenderUserId(userId);
            newFriend.setReceiverUserId(requesterId);
            newFriend.setIsApproved(true);
            friendRepository.save(newFriend);
        }
    }


    public void deleteFriendRequest(Long userId, Long requesterId) {
        Optional<Friend> friendRequest = friendRepository.findBySenderUserIdAndReceiverUserId(requesterId, userId);
        if (friendRequest.isPresent() && !friendRequest.get().getIsApproved()) {
            friendRepository.delete(friendRequest.get());
        } else {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Cannot delete an approved friendship or non-existing request.");
        }
    }

}


