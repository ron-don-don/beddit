package app.rondondon.beddit.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;

@Entity
@Table(name = "beds")
@Getter
public class Bed {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Setter
    @Column(nullable = false, unique = true)
    private String name;

    @Setter
    @Column(nullable = false)
    private String description;

    @Column(updatable = false)
    private Instant createdAt = Instant.now();
}
