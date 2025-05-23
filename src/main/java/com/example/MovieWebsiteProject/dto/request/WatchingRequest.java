package com.example.MovieWebsiteProject.dto.request;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WatchingRequest {
    @NotNull
    @NotEmpty(message = "Film Id cannot be empty!")
    private String filmId;

    private String ownerFilm;
    private String tmdbId;
}
