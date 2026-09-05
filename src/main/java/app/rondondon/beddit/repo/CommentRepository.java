package app.rondondon.beddit.repo;

import app.rondondon.beddit.entity.Comment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CommentRepository extends JpaRepository<Comment, Long> {
    void deleteByIdAndAuthorId(Long id, Long authorId);
    Page<Comment> findAllByAuthorId(Long authorId, Pageable pageable);
    Page<Comment> findAllByPostId(Long postId, Pageable pageable);
    Page<Comment> findAllByRepliedCommentId(Long repliedCommentId, Pageable pageable);
}
