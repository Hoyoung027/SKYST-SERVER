package skyst.dopamine.domain.photo.core.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import skyst.dopamine.domain.photo.core.comment.Comment;

import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Long> {
    List<Comment> findByPhotoboothId(Long photoboothId);
}
