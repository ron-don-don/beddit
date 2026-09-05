package app.rondondon.beddit.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.time.Instant;
import java.util.List;

@Entity
@Table(name = "posts")
@Getter
public class Post {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    @Setter
    private String title;

    @Column(nullable = false)
    @Setter
    private String text;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(nullable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @Setter
    private Bed bed;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(nullable = false)
    @Setter
    private User author;

    @Column(updatable = false)
    private Instant publishedAt =  Instant.now();
}
