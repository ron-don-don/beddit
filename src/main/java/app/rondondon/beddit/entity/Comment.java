package app.rondondon.beddit.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.time.Instant;
import java.util.List;

@Entity
@Table(name = "comments")
@Getter
public class Comment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Setter
    private String text;

    @OneToOne(fetch = FetchType.LAZY)
    @Setter
    @JoinColumn(nullable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Post post;

    @ManyToOne(fetch = FetchType.EAGER)
    @Setter
    private Comment repliedComment;

    @OneToMany(fetch = FetchType.LAZY, cascade = {CascadeType.MERGE, CascadeType.PERSIST})
    private List<Comment> replies;

    @Setter
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(nullable = false)
    private User author;

    @Column(updatable = false)
    private Instant createdAt = Instant.now();

}
