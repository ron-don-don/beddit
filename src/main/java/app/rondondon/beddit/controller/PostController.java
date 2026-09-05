package app.rondondon.beddit.controller;

import app.rondondon.beddit.dto.request.PostCreateRequest;
import app.rondondon.beddit.dto.response.ListObjectResponse;
import app.rondondon.beddit.dto.response.PostResponse;
import app.rondondon.beddit.service.PostService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/posts")
@RequiredArgsConstructor
public class PostController {
    private final PostService postService;

    @PostMapping
    public ResponseEntity<Void> createPost(@Valid @RequestBody PostCreateRequest req, @AuthenticationPrincipal Long id) {
        postService.createPost(req, id);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @GetMapping("/{bed_id}")
    public ResponseEntity<ListObjectResponse<PostResponse>> getPosts(
            @RequestParam(name = "page_number") Integer pageNumber,
            @RequestParam(name = "page_size") Integer pageSize, @PathVariable(name = "bed_id") Long bed_id) {
        return ResponseEntity.ok(postService.getPosts(bed_id, pageNumber, pageSize));
    }

    @DeleteMapping
    public ResponseEntity<Void> deletePost(@RequestParam(name = "post_id") Long postId, @AuthenticationPrincipal Long id) {
        postService.deletePost(postId, id);
        return ResponseEntity.noContent().build();
    }
}
