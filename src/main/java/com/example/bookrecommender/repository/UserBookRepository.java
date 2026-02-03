package com.example.bookrecommender.repository;

import com.example.bookrecommender.entity.UserBook;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserBookRepository extends JpaRepository<UserBook, Long> {
    
    List<UserBook> findByUserId(Long userId);
    
    Optional<UserBook> findByUserIdAndBookId(Long userId, Long bookId);
    
    @Query("SELECT DISTINCT ub.book.genre FROM UserBook ub WHERE ub.user.id = :userId AND ub.rating >= 4")
    List<String> findFavoriteGenresByUserId(@Param("userId") Long userId);
    
    @Query("SELECT DISTINCT ub.book.author FROM UserBook ub WHERE ub.user.id = :userId AND ub.rating >= 4")
    List<String> findFavoriteAuthorsByUserId(@Param("userId") Long userId);
    
    List<UserBook> findByBookId(Long bookId);
    
    boolean existsByUserIdAndBookId(Long userId, Long bookId);
    
    @Query("SELECT COUNT(ub) FROM UserBook ub WHERE ub.book.id = :bookId AND ub.isFavorite = true")
    Integer countFavoritesByBookId(@Param("bookId") Long bookId);
    
    @Query("SELECT ub FROM UserBook ub WHERE ub.book.id = :bookId AND ub.rating IS NOT NULL")
    List<UserBook> findReviewsByBookId(@Param("bookId") Long bookId);
}
