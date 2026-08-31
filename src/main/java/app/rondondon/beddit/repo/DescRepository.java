package app.rondondon.beddit.repo;

import app.rondondon.beddit.entity.Desc;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface DescRepository extends JpaRepository<Desc, Long> {
    public List<Desc> findByOwnerId(Long id);
}
