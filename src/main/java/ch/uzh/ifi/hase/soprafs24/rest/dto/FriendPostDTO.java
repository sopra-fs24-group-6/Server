package ch.uzh.ifi.hase.soprafs24.rest.dto;

public class FriendPostDTO {
    private Long senderUserId;
    private Long receiverUserId;

    public Long getSenderUserId() {
        return senderUserId;
    }

    public void setSenderUserId(Long senderUserId) {
        this.senderUserId = senderUserId;
    }

    public Long getReceiverUserId() {
        return receiverUserId;
    }

    public void setReceiverUserId(Long receiverUserId) {
        this.receiverUserId = receiverUserId;
    }
}
