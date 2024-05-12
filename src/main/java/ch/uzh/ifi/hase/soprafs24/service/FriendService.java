package ch.uzh.ifi.hase.soprafs24.service;

import ch.uzh.ifi.hase.soprafs24.entity.Friend;
import ch.uzh.ifi.hase.soprafs24.repository.FriendRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class FriendService {
    @Autowired
    private FriendRepository friendRepository;

    public List<Friend> getFriendsOf(Long userId) {
        List<Friend> friends = new ArrayList<>();

        friends.addAll(friendRepository.findByReceiverUserId(userId));
        friends.addAll(friendRepository.findBySenderUserId(userId));

        return friends;
    }

    public List<Friend> getFriendRequestsOf(Long receiverUserId) {
        // Retrieve all friend requests for the user but filter out the ones that are already approved.
        return friendRepository.findByReceiverUserId(receiverUserId).stream()
                .filter(request -> !request.getIsApproved())
                .collect(Collectors.toList());
    }


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
    public void acceptFriendRequest(Long requesterId) {
        // Retrieve the friend request to ensure it exists and is not yet approved
        Optional<Friend> friendRequestOpt = friendRepository.findById(requesterId);
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

    }


    public void deleteFriendRequest(Long requesterId) {
        Optional<Friend> friendRequest = friendRepository.findById(requesterId);
        if (friendRequest.isPresent() && !friendRequest.get().getIsApproved()) {
            friendRepository.delete(friendRequest.get());
        }
        else {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Cannot delete an approved friendship or non-existing request.");
        }
    }

}