package com.munecting.api.domain.like.dto.response;

import com.wrapper.spotify.model_objects.specification.Image;
import java.util.List;
import lombok.Builder;

@Builder
public record GetLikedTrackResponseDto(
        Long likeId,
        String trackId,
        String trackTitle,
        List<LikedTrackArtistResponseDto> artists,
        String trackPreview,
        Image[] images
) {}
