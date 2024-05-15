package ch.uzh.ifi.hase.soprafs24.controller;

import ch.uzh.ifi.hase.soprafs24.entity.Friend;
import ch.uzh.ifi.hase.soprafs24.rest.dto.FriendPostDTO;
import ch.uzh.ifi.hase.soprafs24.service.FriendService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/friends")
public class FriendController {

    private final FriendService friendService;

    @Autowired
    public FriendController(FriendService friendService) {
        this.friendService = friendService;
    }

    @GetMapping("")
    public List<Friend> getFriends(@RequestParam("userId") Long userId) {
        // Filter request that has been approved!
        return friendService.getFriendsOf(userId).stream().filter(Friend::getIsApproved).toList();
    }

    @GetMapping("/friendRequests")
    public List<Friend> getFriendRequests(@RequestParam("userId") Long userId) {
        // Filter request that has NOT been approved!
        return friendService.getFriendRequestsOf(userId).stream().filter(request -> !request.getIsApproved()).toList();
    }

    @PostMapping("/friendRequests")
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public ResponseEntity<?> sendFriendRequest(@RequestBody FriendPostDTO friendPostDTO) {
        try {
            System.out.println(friendPostDTO.toString());
            friendService.sendFriendRequest(friendPostDTO.getSenderUserId(), friendPostDTO.getReceiverUserId());
            return ResponseEntity.ok("Friend request sent successfully.");
        }
        catch (IllegalStateException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
        catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    @PutMapping("/friendRequests/{requesterId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public ResponseEntity<?> acceptFriendRequest(@PathVariable Long requesterId) {
        try {
            friendService.acceptFriendRequest(requesterId);
            return ResponseEntity.ok("Friend request accepted successfully.");
        }
        catch (Exception e) {
            return ResponseEntity.internalServerError().body("Error accepting friend request: " + e.getMessage());
        }
    }


    // Endpoint to delete a friend request
    @DeleteMapping("/friendRequests/{requesterId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public ResponseEntity<?> denyFriendRequest(@PathVariable Long requesterId) {
        try {
            friendService.deleteFriendRequest(requesterId);
            return ResponseEntity.ok("Friend request denied successfully.");
        }
        catch (Exception e) {
            return ResponseEntity.internalServerError().body("Error denying friend request: " + e.getMessage());
        }
    }

}