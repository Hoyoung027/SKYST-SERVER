package skyst.dopamine.domain.photo.api.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import skyst.dopamine.domain.photo.api.service.CommentService;
import skyst.dopamine.domain.photo.core.comment.Comment;

import java.util.List;

@RestController
@RequestMapping("/showing")
@RequiredArgsConstructor
public class CommentController {

    private final CommentService commentService;

    @PostMapping("/{photoId}")
    public Comment createComment(@RequestBody Comment comment) {
        return commentService.save(comment);
    }

    @GetMapping("/{photoId}/comment")
    public List<Comment> getCommentsByPhotoId(@PathVariable Long photoId) {
        return commentService.getCommentsByPhotoId(photoId);
    }
}
