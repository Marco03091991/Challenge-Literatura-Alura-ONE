package com.alurachallenge.literatura.repository;

import com.alurachallenge.literatura.model.Author;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface AuthorsRepository extends JpaRepository<Author, Long> {

    @Query("SELECT a FROM Author a WHERE a.dayDeath = :year")
    List<Author> searchByDeathYear(Integer year);

    @Query("SELECT a FROM Author a WHERE a.birthDay = :year")
    List<Author> searchByBirthYear(Integer year);

    @Query("SELECT a FROM Author a WHERE a.birthDay <= :year AND (a.dayDeath IS NULL OR a.dayDeath >= :year) ORDER BY a.birthDay ASC")
    List<Author> searchAuthorsAliveInYear(Integer year);

    @Query("SELECT a FROM Author a WHERE LOWER(a.name) LIKE LOWER(CONCAT('%', :name, '%'))")
    List<Author> searchByName(String name);

    boolean existsByName(String name);
}
