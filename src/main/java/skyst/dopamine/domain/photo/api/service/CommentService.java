package skyst.dopamine.domain.photo.api.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import skyst.dopamine.domain.photo.core.comment.Comment;
import skyst.dopamine.domain.photo.core.repository.CommentRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CommentService {

    private final CommentRepository commentRepository;

    public Comment save(Comment comment) {
        return commentRepository.save(comment);
    }

    public List<Comment> getCommentsByPhotoId(Long photoId) {
        return commentRepository.findByPhotoboothId(photoId);
    }
}
