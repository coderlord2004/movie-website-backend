package com.example.IdentityService.Repository;

import com.example.IdentityService.Entity.Comment;
import com.example.IdentityService.dto.projection.FilmComments;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CommentRepository extends JpaRepository<Comment, String> {

    @Query(value = "SELECT count(user_id) FROM comment WHERE user_id = :userId AND film_id = :filmId", nativeQuery = true)
    int countUserCommentFilm(@Param("userId") String userId, @Param("filmId") String filmId);

    @Query(value = "SELECT c.comment_id AS commentId, c.user_id AS userId, c.content, c.comment_time AS commentTime " +
            "FROM comment c WHERE c.film_id = :filmId ORDER BY c.comment_time DESC", nativeQuery = true)
    List<FilmComments> getFilmComments(@Param("filmId") String filmId);

}
