package app.rondondon.beddit.dto.response;

public record CommentResponse(Long id, String text, Long postId, Long repliedId, Long authorId) {
}
