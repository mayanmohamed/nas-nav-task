package nas.nav.task.repository;

import nas.nav.task.models.Picture;
import nas.nav.task.models.Status;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PictureRepository extends JpaRepository<Picture, Integer> {

    Optional<Picture> findByIdAndStatus(int id , Status status);
    List<Picture> findAllByStatus(Status status);
}
