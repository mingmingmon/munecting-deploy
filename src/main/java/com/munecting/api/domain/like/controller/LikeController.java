package com.munecting.api.domain.like.controller;

import com.munecting.api.domain.like.dto.response.AddTrackLikeResponseDto;
import com.munecting.api.domain.like.dto.response.DeleteTrackLikeResponseDto;
import com.munecting.api.domain.like.dto.response.GetLikedTrackListResponseDto;
import com.munecting.api.domain.like.service.LikeService;
import com.munecting.api.global.auth.user.UserId;
import com.munecting.api.global.common.dto.response.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/tracks")
@Tag(name = "like", description = "Like 관련 api </br> <i> 담당자 : 김송은 </i>")
public class LikeController {

    private final LikeService likeService;

    @PostMapping("/{trackId}/likes")
    @Operation(summary = "좋아요 누르기")
    public ApiResponse<?> addTrackLike(
            @PathVariable(name = "trackId") String trackId,
            @UserId Long userId
    ) {
        AddTrackLikeResponseDto dto = likeService.addTrackLike(trackId, userId);
        return ApiResponse.created(dto);
    }

    @GetMapping("/liked")
    @Operation(summary = "좋아요한 음악 조회")
    public ApiResponse<?> getLikedTracks (
            @UserId Long userId,
            @RequestParam(required = false) Long cursor,
            @RequestParam(required = false, defaultValue = "20") int size
    ) {
        GetLikedTrackListResponseDto dto = likeService.getLikedTracks(userId, cursor, size);
        return ApiResponse.ok(dto);
    }

    @DeleteMapping("/{trackId}/likes")
    @Operation(summary = "좋아요 취소")
    public ApiResponse<?> deleteTrackLike (
            @PathVariable(name = "trackId") String trackId,
            @UserId Long userId
    ) {
        DeleteTrackLikeResponseDto dto = likeService.deleteTrackLike(trackId, userId);
        return ApiResponse.ok(dto);
    }
}
