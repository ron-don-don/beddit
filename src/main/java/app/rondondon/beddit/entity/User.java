package app.rondondon.beddit.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "users")
@Getter
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Setter
    @Column(unique = true, nullable = false)
    private String username;

    @Setter
    @Column
    private String passwordHash;

    @Setter
    @Column(unique = true)
    private String email;

    @Setter
    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, mappedBy = "author", fetch = FetchType.LAZY)
    private List<Post> posts;

    @Setter
    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, mappedBy = "author", fetch = FetchType.LAZY)
    private List<Comment> comments;

    @Setter
    @ElementCollection(fetch = FetchType.EAGER)
    private List<String> roles = new ArrayList<>();

    @Column(updatable = false)
    private Instant createdAt = Instant.now();
}
