package app.rondondon.beddit.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "users")
@NoArgsConstructor
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Getter
    private Long id;

    @Getter
    @Setter
    @Column(unique = true, nullable = false)
    private String username;

    @Getter
    @Setter
    @Column
    private String passwordHash;

    @Getter
    @Setter
    @Column(unique = true)
    private String email;

    @Getter
    @Setter
    @ElementCollection(fetch = FetchType.EAGER)
    private List<String> roles = new ArrayList<>();

    @Getter
    @Column(updatable = false)
    private Instant createdAt = Instant.now();

    @Getter
    @Setter
    @OneToOne(mappedBy = "owner", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private Desc description;
}
