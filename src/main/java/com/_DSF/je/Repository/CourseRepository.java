package com._DSF.je.Repository;

import com._DSF.je.Entity.Category;
import com._DSF.je.Entity.Course;
import org.springframework.data.domain.Sort;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface CourseRepository extends JpaRepository<Course, Long> {

    @Query("SELECT c FROM Course c WHERE " +
            "LOWER(c.title) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            "LOWER(c.teacher.username) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            "LOWER(c.category.name) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    List<Course> searchCourses(@Param("keyword") String keyword);
    List<Course> findByCategory(Category category);
    @Query("SELECT c FROM Course c WHERE " +
            "(:minPrice IS NULL OR c.price >= :minPrice) AND " +
            "(:maxPrice IS NULL OR c.price <= :maxPrice)")
    List<Course> filterByPrice(@Param("minPrice") Double minPrice,
                               @Param("maxPrice") Double maxPrice,
                               Sort sort);

    List<Course> findByTeacherId(Long teacherId);
}

