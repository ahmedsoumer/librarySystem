package com.assessment.librarySystem.repository;

import com.assessment.librarySystem.model.Book;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BookRepository extends BaseRepository<Book, Long> {
    
    List<Book> findByIsbn(String isbn);
    
    List<Book> findByTitleContainingIgnoreCase(String title);
    
    List<Book> findByAuthorContainingIgnoreCase(String author);
    
    @Query("SELECT b FROM Book b WHERE b.isbn = :isbn AND b.title = :title AND b.author = :author")
    List<Book> findByIsbnAndTitleAndAuthor(@Param("isbn") String isbn, 
                                          @Param("title") String title, 
                                          @Param("author") String author);
}
