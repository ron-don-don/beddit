package app.rondondon.beddit.service;

import app.rondondon.beddit.dto.request.CommentCreateRequest;
import app.rondondon.beddit.dto.response.CommentResponse;
import app.rondondon.beddit.dto.response.ListObjectResponse;
import app.rondondon.beddit.entity.Comment;
import app.rondondon.beddit.exception.CommentException;
import app.rondondon.beddit.exception.ErrorCode;
import app.rondondon.beddit.repo.CommentRepository;
import app.rondondon.beddit.repo.PostRepository;
import app.rondondon.beddit.repo.UserRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CommentService {
    private final CommentRepository commentRepository;
    private final UserRepository userRepository;
    private final PostRepository postRepository;

    private static final Logger log = LoggerFactory.getLogger(CommentService.class);

    public void createComment(CommentCreateRequest req, Long id){
        log.debug("Creating comment with content: {}", req.text());
        var author = userRepository.findById(id).orElseThrow(() -> {
            log.debug("Cannot create comment(author not found)");
            return new CommentException(ErrorCode.USER_NOT_FOUND);
        });
        Optional<Comment> repliedComment = Optional.empty();
        if (req.repliedCommentId() != null) {
            repliedComment = commentRepository.findById(req.repliedCommentId());
        }
        var post = postRepository.findById(req.postId()).orElseThrow(() -> {
            log.debug("Cannot create comment(post not found)");
            return new CommentException(ErrorCode.POST_NOT_FOUND);
        });
        var comment = new Comment();
        comment.setAuthor(author);
        comment.setPost(post);
        repliedComment.ifPresent(comment::setRepliedComment);
        comment.setText(req.text());
        commentRepository.save(comment);
        log.debug("Comment created");
    }

    public void deleteComment(Long commentId, Long id){
        commentRepository.deleteByIdAndAuthorId(commentId, id);
        log.debug("Deleted comment with id: {}", id);
    }

    public ListObjectResponse<CommentResponse> getCommentsByPostId(Long postId, Integer pageNumber, Integer pageSize) {
        postRepository.findById(postId).orElseThrow(() -> {
            log.debug("Cannot find comments(post not found)");
            return new CommentException(ErrorCode.POST_NOT_FOUND);
        });
        var comments = commentRepository.findAllByPostId(postId, PageRequest.of(pageNumber, pageSize,  Sort.by(Sort.Direction.DESC, "createdAt")));
        return new ListObjectResponse<>(comments.stream().map(comment -> new CommentResponse(
                comment.getId(), comment.getText(), postId, comment.getRepliedComment().getId(), comment.getAuthor().getId()))
                .toList(),
                comments.hasNext()
        );
    }

    public ListObjectResponse<CommentResponse> getCommentsByAuthorId(Long authorId, Integer pageNumber, Integer pageSize) {
        userRepository.findById(authorId).orElseThrow(() -> {
            log.debug("Cannot find comments(author not found)");
            return new CommentException(ErrorCode.USER_NOT_FOUND);
        });
        var comments = commentRepository.findAllByAuthorId(authorId, PageRequest.of(pageNumber, pageSize,  Sort.by(Sort.Direction.DESC, "createdAt")));
        return new ListObjectResponse<>(comments.stream().map(comment -> new CommentResponse(
                        comment.getId(), comment.getText(), comment.getPost().getId(), comment.getRepliedComment().getId(), authorId))
                .toList(),
                comments.hasNext()
        );
    }

    public ListObjectResponse<CommentResponse> getCommentsByRepliedCommentId(Long repliedCommentId, Integer pageNumber, Integer pageSize) {
        commentRepository.findById(repliedCommentId).orElseThrow(() -> {
            log.debug("Cannot find comments(replied not found)");
            return new CommentException(ErrorCode.COMMENT_NOT_FOUND);
        });
        var comments = commentRepository.findAllByRepliedCommentId(repliedCommentId, PageRequest.of(pageNumber, pageSize,  Sort.by(Sort.Direction.DESC, "createdAt")));
        return new ListObjectResponse<>(comments.stream().map(comment ->new CommentResponse(
                        comment.getId(), comment.getText(), comment.getPost().getId(), repliedCommentId, comment.getAuthor().getId()))
                .toList(),
                comments.hasNext()
        );
    }
}
