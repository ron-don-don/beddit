package app.rondondon.beddit.dto.request;

public record CommentCreateRequest(String text, Long postId, Long repliedCommentId) {
}
