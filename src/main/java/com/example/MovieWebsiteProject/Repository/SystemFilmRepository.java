package com.example.MovieWebsiteProject.Repository;

import com.example.MovieWebsiteProject.Entity.SystemFilm;
import lombok.NonNull;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Repository
public interface SystemFilmRepository extends JpaRepository<SystemFilm, String>, JpaSpecificationExecutor<SystemFilm> {

    @Query(value = """
            SELECT sf.system_film_id, sf.title, sf.release_date, sf.backdrop_path, sf.poster_path, genre.genre_name
            FROM system_film AS sf
            JOIN system_film_genres AS sfg ON sf.system_film_id = sfg.system_film_id
            JOIN genre ON sfg.genre_id = genre.genre_id
            ORDER BY sf.release_date DESC
            """, nativeQuery = true)
    List<Map<String, Object>> getAllSystemFilmSummary();

    @Query(value = """
            SELECT
                sf.*, 
                film.*, 
                w.watched_duration, 
                g.genre_name
            FROM system_film AS sf
            JOIN film ON film.film_id = sf.system_film_id
            LEFT JOIN (
                SELECT * 
                FROM watching 
                WHERE user_id = :userId AND film_id = :filmId
                ORDER BY watching_time DESC
                LIMIT 1
            ) AS w ON film.film_id = w.film_id
            JOIN system_film_genres AS sfg ON sf.system_film_id = sfg.system_film_id
            JOIN genre AS g ON sfg.genre_id = g.genre_id
            WHERE sf.system_film_id = :filmId
            """, nativeQuery = true)
    List<Map<String, Object>> getSystemFilmDetail(@Param("filmId") String filmId, @Param("userId") String userId);

    @Override
    @EntityGraph(attributePaths = {"genres"})
    Page<SystemFilm> findAll(@NonNull Pageable pageable);
}
