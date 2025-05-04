package com.example.MovieWebsiteProject.Controller;

import com.example.MovieWebsiteProject.Common.SuccessCode;
import com.example.MovieWebsiteProject.Repository.FilmRepository;
import com.example.MovieWebsiteProject.Repository.WatchingRepository;
import com.example.MovieWebsiteProject.Service.AuthenticationService;
import com.example.MovieWebsiteProject.Service.WatchingService;
import com.example.MovieWebsiteProject.dto.request.WatchingRequest;
import com.example.MovieWebsiteProject.dto.response.ApiResponse;
import com.example.MovieWebsiteProject.dto.response.FilmWatchingHistoryResponse;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/watching")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class WatchingController {
    WatchingRepository watchingRepository;
    AuthenticationService authenticationService;
    WatchingService watchingService;
    private final FilmRepository filmRepository;

    @PostMapping("/save-watching-history")
    public ApiResponse<Void> saveFilmWatchingHistory(@RequestBody WatchingRequest request) {
        watchingService.saveFilmWatchingHistory(request);
        return ApiResponse.<Void>builder()
                .code(SuccessCode.SUCCESS.getCode())
                .message(SuccessCode.SUCCESS.getMessage())
                .build();
    }

    @GetMapping("/get-watching-history/system-film")
    public ApiResponse<List<FilmWatchingHistoryResponse>> getSystemFilmWatchingHistory() {
        return ApiResponse.<List<FilmWatchingHistoryResponse>>builder()
                .code(SuccessCode.SUCCESS.getCode())
                .message(SuccessCode.SUCCESS.getMessage())
                .results(watchingService.getSystemFilmWatchingHistory())
                .build();
    }

    @GetMapping("/get-watching-history/tmdb-film")
    public ApiResponse<List<FilmWatchingHistoryResponse>> getTmdbFilmWatchingHistory() {
        return ApiResponse.<List<FilmWatchingHistoryResponse>>builder()
                .code(SuccessCode.SUCCESS.getCode())
                .message(SuccessCode.SUCCESS.getMessage())
                .results(watchingService.getTmdbFilmWatchingHistory())
                .build();
    }

    @GetMapping("/save-watched-duration/{filmId}")
    public ApiResponse<Void> saveWatchedDuration(@PathVariable("filmId") String filmId, @RequestParam("watchedDuration") long duration) {
        watchingService.saveWatchedDuration(filmId, duration);
        return ApiResponse.<Void>builder()
                .code(SuccessCode.SUCCESS.getCode())
                .message(SuccessCode.SUCCESS.getMessage())
                .build();
    }
}
