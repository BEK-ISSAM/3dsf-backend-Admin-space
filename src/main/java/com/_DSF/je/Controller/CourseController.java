package com._DSF.je.Controller;

import com._DSF.je.Entity.Category;
import com._DSF.je.Entity.Course;
import com._DSF.je.Entity.User;
import com._DSF.je.Service.CourseService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/courses")
public class CourseController {

    private final CourseService courseService;

    public CourseController(CourseService courseService) {
        this.courseService = courseService;
    }

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Course> createCourse(
            @RequestParam("course") String courseJson,
            @RequestParam("pdfFile") MultipartFile pdfFile,
            @RequestParam(value = "thumbnail", required = false) MultipartFile thumbnail) {
        try {
            // Log the received course JSON
            System.out.println("Received course JSON: " + courseJson);

            // Convert the JSON string to a Course object
            ObjectMapper objectMapper = new ObjectMapper();
            Course course = objectMapper.readValue(courseJson, Course.class);
            System.out.println("Course object created: " + course);

            // Check if teacher is provided
            if (course.getTeacher() == null || course.getTeacher().getId() == null) {
                // Log missing teacher
                System.err.println("Teacher is missing or not properly set in the course.");
                return ResponseEntity.badRequest().body(null); // Return bad request if teacher is missing
            }

            // Log information about the files
            if (pdfFile != null) {
                System.out.println("Received PDF file: " + pdfFile.getOriginalFilename() + " (" + pdfFile.getSize() + " bytes)");
            } else {
                System.out.println("No PDF file received");
            }

            if (thumbnail != null) {
                System.out.println("Received thumbnail image: " + thumbnail.getOriginalFilename() + " (" + thumbnail.getSize() + " bytes)");
            } else {
                System.out.println("No thumbnail image received");
            }

            // Save the course with the files
            Course createdCourse = courseService.createCourse(course, pdfFile, thumbnail);
            System.out.println("Course successfully created: " + createdCourse);

            return ResponseEntity.ok(createdCourse);
        } catch (IOException e) {
            System.err.println("Error while parsing course JSON or handling the files: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            System.err.println("Unexpected error occurred: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }




    @GetMapping
    public ResponseEntity<List<Course>> getAllCourses() {
        List<Course> courses = courseService.getAllCourses();
        return ResponseEntity.ok(courses);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Course> getCourseById(@PathVariable Long id) {
        return courseService.getCourseById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}")
    public ResponseEntity<Course> updateCourse(@PathVariable Long id, @RequestBody Course courseDetails) {
        Course updatedCourse = courseService.updateCourse(id, courseDetails);
        return ResponseEntity.ok(updatedCourse);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCourse(@PathVariable Long id) {
        courseService.deleteCourse(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/search")
    public ResponseEntity<List<Course>> searchCourses(@RequestParam String keyword) {
        List<Course> courses = courseService.searchCourses(keyword);
        return ResponseEntity.ok(courses);
    }
    @GetMapping("/grouped-by-category")
    public ResponseEntity<Map<Category, List<Course>>> getCoursesGroupedByCategory() {
        List<Course> courses = courseService.getAllCourses();

        // Group courses by category
        Map<Category, List<Course>> coursesByCategory = courses.stream()
                .collect(Collectors.groupingBy(Course::getCategory));

        return ResponseEntity.ok(coursesByCategory);
    }
    @GetMapping("/filterByPrice")
    public ResponseEntity<List<Course>> filterByPrice(
            @RequestParam(required = false) Double minPrice,
            @RequestParam(required = false) Double maxPrice,
            @RequestParam(required = false, defaultValue = "asc") String sortOrder) {
        List<Course> courses = courseService.filterByPrice(minPrice, maxPrice, sortOrder);
        return ResponseEntity.ok(courses);
    }
    // Add this method to CourseController
    @GetMapping("/teacher/{teacherId}")
    public ResponseEntity<List<Course>> getCoursesByTeacher(@PathVariable Long teacherId) {
        List<Course> courses = courseService.getCoursesByTeacher(teacherId);
        return ResponseEntity.ok(courses);
    }

}
