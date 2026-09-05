package app.rondondon.beddit.controller;

import app.rondondon.beddit.dto.request.CommentCreateRequest;
import app.rondondon.beddit.dto.response.CommentResponse;
import app.rondondon.beddit.dto.response.ListObjectResponse;
import app.rondondon.beddit.service.CommentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/comments")
@RequiredArgsConstructor
public class CommentController {
    public final CommentService commentService;

    @PostMapping
    public ResponseEntity<Void> createComment(@RequestBody CommentCreateRequest req,  @AuthenticationPrincipal Long id){
        commentService.createComment(req, id);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{id}")
    public ResponseEntity<ListObjectResponse<CommentResponse>> getComments(
            @RequestParam(name = "page_number") Integer pageNumber,
            @RequestParam(name = "page_size") Integer pageSize,
            @RequestParam(name = "by") String getBy, @PathVariable("id") Long id){
        return switch (getBy) {
            case "author" -> ResponseEntity.ok().body(commentService.getCommentsByAuthorId(id, pageNumber, pageSize));
            case "post" -> ResponseEntity.ok().body(commentService.getCommentsByPostId(id, pageNumber, pageSize));
            case "comment" -> ResponseEntity.ok().body(commentService.getCommentsByRepliedCommentId(id, pageNumber, pageSize));
            default -> ResponseEntity.noContent().build();
        };
    }

    @DeleteMapping
    public ResponseEntity<Void> deleteComment(@RequestParam(name = "comment_id") Long commentId, @AuthenticationPrincipal Long id){
        commentService.deleteComment(commentId, id);
        return ResponseEntity.noContent().build();
    }
}
