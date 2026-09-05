package app.rondondon.beddit.repo;

import app.rondondon.beddit.entity.Post;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PostRepository extends JpaRepository<Post, Long> {
    Page<Post> findByBedId(Long bedId, Pageable pageable);

    Optional<Post> findByTitleAndBedId(String title, Long bedId);

    void deleteByIdAndAuthorId(Long postId, Long authorId);
}
