package ch.uzh.ifi.hase.soprafs24.controller;

import ch.uzh.ifi.hase.soprafs24.entity.Friend;
import ch.uzh.ifi.hase.soprafs24.entity.User;
import ch.uzh.ifi.hase.soprafs24.rest.dto.*;
import ch.uzh.ifi.hase.soprafs24.service.FriendService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/friends")
public class FriendController {

    @Autowired
    private FriendService friendService;

    @PostMapping("/{userId}/friendRequests")
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public ResponseEntity<?> sendFriendRequest(@PathVariable Long userId, @RequestBody FriendPostDTO friendPostDTO) {

        friendPostDTO.setReceiverUserId(userId);
        try {
            friendService.sendFriendRequest(friendPostDTO.getSenderUserId(), friendPostDTO.getReceiverUserId());
            return ResponseEntity.ok("Friend request sent successfully.");
        } catch (IllegalStateException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    @PutMapping("/{userId}/friendRequests/{requesterId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public ResponseEntity<?> acceptFriendRequest(@PathVariable Long userId, @PathVariable Long requesterId) {
        try {
            friendService.acceptFriendRequest(userId, requesterId);
            return ResponseEntity.ok("Friend request accepted successfully.");
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Error accepting friend request: " + e.getMessage());
        }
    }


    // Endpoint to delete a friend request
    @DeleteMapping("/{userId}/friendRequests/{requesterId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public ResponseEntity<?> denyFriendRequest(@PathVariable Long userId, @PathVariable Long requesterId) {
        try {
            friendService.deleteFriendRequest(userId, requesterId);
            return ResponseEntity.ok("Friend request denied successfully.");
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Error denying friend request: " + e.getMessage());
        }
    }

}

