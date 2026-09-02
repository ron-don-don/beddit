package app.rondondon.beddit.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "description")
public class Desc {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Getter
    private Long id;

    @Getter
    @Setter
    private String text;

    @Getter
    @Setter
    @JoinColumn(name = "owner_id", referencedColumnName = "id")
    @OneToOne
    private User owner;
}
