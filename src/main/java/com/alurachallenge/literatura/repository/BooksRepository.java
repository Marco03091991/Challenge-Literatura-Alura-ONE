package com.alurachallenge.literatura.repository;

import com.alurachallenge.literatura.model.Book;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface BooksRepository  extends JpaRepository<Book, Long> {

    @Query("SELECT l FROM Book l WHERE l.language ILIKE %:language%")
    List<Book> searchByLanguage(String language);

    @Query("SELECT l FROM Book l ORDER BY l.download_count DESC")
    List<Book> searchTopDownloads(Pageable pageable);

    boolean existsByTitle(String title);
}
