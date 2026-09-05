package app.rondondon.beddit.service;

import app.rondondon.beddit.dto.request.PostCreateRequest;
import app.rondondon.beddit.dto.response.ListObjectResponse;
import app.rondondon.beddit.dto.response.PostResponse;
import app.rondondon.beddit.entity.Post;
import app.rondondon.beddit.exception.AuthenticationException;
import app.rondondon.beddit.exception.ErrorCode;
import app.rondondon.beddit.exception.PostException;
import app.rondondon.beddit.repo.BedRepository;
import app.rondondon.beddit.repo.PostRepository;
import app.rondondon.beddit.repo.UserRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PostService {
    private final PostRepository postRepository;
    private final UserRepository userRepository;
    private final BedRepository bedRepository;

    private static final Logger log = LoggerFactory.getLogger(PostService.class);

    public void createPost(PostCreateRequest req, Long id){
        log.debug("Creating post with title: {}", req.title());
        Post post = new Post();
        var author = userRepository.findById(id).orElseThrow(() -> {
            log.debug("Cannot create post(author not found)");
            return new AuthenticationException(ErrorCode.USER_NOT_FOUND);
        });
        var bed = bedRepository.findById(req.bedId()).orElseThrow(() -> {
            log.debug("Cannot create post(bed not found)");
            return new AuthenticationException(ErrorCode.BED_NOT_FOUND);
        });
        postRepository.findByTitleAndBedId(req.title(), req.bedId()).ifPresent(b -> {
            log.debug("Cannot create post(post at this bed already exists)");
            throw new PostException(ErrorCode.POST_ALREADY_EXISTS);
        });
        post.setAuthor(author);
        post.setBed(bed);
        post.setTitle(req.title());
        post.setText(req.text());
        postRepository.save(post);
        log.debug("Post created");
    }

    public void deletePost(Long postId, Long id){
        postRepository.deleteByIdAndAuthorId(postId, id);
        log.debug("Deleted post with id: {}", postId);
    }

    public ListObjectResponse<PostResponse> getPosts(Long bedId, Integer pageSize, Integer pageNumber){
        var posts = postRepository.findByBedId(bedId, PageRequest.of(pageNumber, pageSize,  Sort.by(Sort.Direction.DESC, "publishedAt")));
        var postsResponse = posts.getContent().stream().map(post ->
                        new PostResponse(post.getId(), post.getTitle(), post.getText(), post.getBed().getId(), post.getAuthor().getId(), post.getPublishedAt()))
                .toList();
        log.debug("Got objects, returned: {}", postsResponse.size());
        return new ListObjectResponse<>(postsResponse, posts.hasNext());
    }
}
