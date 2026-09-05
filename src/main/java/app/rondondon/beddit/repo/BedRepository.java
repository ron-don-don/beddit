package app.rondondon.beddit.repo;

import app.rondondon.beddit.entity.Bed;
import org.jspecify.annotations.NullMarked;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface BedRepository extends JpaRepository<Bed, Long> {
    Optional<Bed> findByName(String name);

    @NullMarked
    Page<Bed> findAll(Pageable pageable);
}