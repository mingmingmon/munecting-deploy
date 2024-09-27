package com.munecting.api.domain.comment.service;

import com.munecting.api.domain.comment.dao.CommentRepository;
import com.munecting.api.domain.comment.dto.request.CommentRequestDto;
import com.munecting.api.domain.comment.dto.response.CommentIdResponseDto;
import com.munecting.api.domain.comment.dto.response.CommentResponseDto;
import com.munecting.api.domain.comment.entity.Comment;
import com.munecting.api.domain.spotify.service.SpotifyService;
import com.munecting.api.global.common.dto.response.PagedResponseDto;
import com.munecting.api.global.common.dto.response.Status;
import com.munecting.api.global.error.exception.EntityNotFoundException;
import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class CommentService {

    private final CommentRepository commentRepository;
    private final SpotifyService spotifyService;

    @Transactional
    public CommentIdResponseDto createComment(CommentRequestDto commentRequestDto) {
        String trackId = commentRequestDto.trackId();
        spotifyService.validateTrackExists(trackId);
        Comment comment = Comment.toEntity(commentRequestDto);
        Long id = saveComment(comment);
        return CommentIdResponseDto.of(id);
    }

    private Long saveComment(Comment comment) {
        return commentRepository.save(comment).getId();
    }

    @Transactional(readOnly = true)
    public PagedResponseDto<CommentResponseDto> getCommentsByTrackId(String trackId, LocalDateTime cursor, int limit) {
        Pageable pageable = PageRequest.of(0, limit);
        //Timestamp cursorTimestamp = LocalDateTimeUtil.toTimestamp(cursor);
        Page<Comment> pagedComment = getCommentsByTrackIdWithCursor(trackId, cursor, pageable);
        Page<CommentResponseDto> pagedCommentResponseDto = pagedComment.map(CommentResponseDto::of);
        return new PagedResponseDto<>(pagedCommentResponseDto);
    }

    @Transactional(readOnly = true)
    public Page<Comment> getCommentsByTrackIdWithCursor(String trackId, LocalDateTime cursor, Pageable pageable) {
        log.info(String.valueOf(cursor));
        return commentRepository.findCommentsByTrackIdWithCursor(trackId, cursor.toString(), pageable);
    }

    @Transactional
    public CommentIdResponseDto updateComment(Long commentId, CommentRequestDto commentRequestDto) {
        Comment comment = getCommentById(commentId);
        comment.updateContent(commentRequestDto.content());
        return CommentIdResponseDto.of(commentId);
    }

    @Transactional(readOnly = true)
    public Comment getCommentById(Long id) {
        return commentRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(Status.COMMENT_NOT_FOUND));
    }

    @Transactional
    public CommentIdResponseDto deleteCommentById(Long commentId) {
        Comment comment = getCommentById(commentId);
        deleteComment(comment);
        return CommentIdResponseDto.of(commentId);
    }

    private void deleteComment(Comment comment) {
        commentRepository.delete(comment);
    }
}
