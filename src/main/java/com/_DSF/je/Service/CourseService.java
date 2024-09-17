package com._DSF.je.Service;

import com._DSF.je.Entity.Course;
import com._DSF.je.Repository.CourseRepository;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

@Service
public class CourseService {

    private final CourseRepository courseRepository;

    public CourseService(CourseRepository courseRepository) {
        this.courseRepository = courseRepository;
    }

    public Course createCourse(Course course, MultipartFile pdfFile, MultipartFile thumbnail) throws IOException {
        if (pdfFile != null && !pdfFile.isEmpty()) {
            course.setPdfName(pdfFile.getOriginalFilename());
            course.setPdfType(pdfFile.getContentType());
            course.setPdfData(pdfFile.getBytes());
        }

        if (thumbnail != null && !thumbnail.isEmpty()) {
            course.setThumbnail(thumbnail.getBytes());
        }

        return courseRepository.save(course);
    }

    public List<Course> getAllCourses() {
        return courseRepository.findAll();
    }

    public Optional<Course> getCourseById(Long id) {
        return courseRepository.findById(id);
    }

    public Course updateCourse(Long id, Course courseDetails) {
        return courseRepository.findById(id)
                .map(course -> {
                    course.setTitle(courseDetails.getTitle());
                    course.setTeacher(courseDetails.getTeacher());
                    course.setCategory(courseDetails.getCategory());
                    course.setAssignments(courseDetails.getAssignments());
                    course.setQuizzes(courseDetails.getQuizzes());
                    course.setVideos(courseDetails.getVideos());
                    course.setDescription(courseDetails.getDescription());
                    course.setPrice(courseDetails.getPrice());
                    return courseRepository.save(course);
                })
                .orElseThrow(() -> new RuntimeException("Course not found with id " + id));
    }

    public void deleteCourse(Long id) {
        Optional<Course> courseOpt = courseRepository.findById(id);
        if (courseOpt.isPresent()) {
            Course course = courseOpt.get();

            // Remove related entities
            course.getAssignments().clear();
            course.getQuizzes().clear();
            course.getVideos().clear();

            // Save changes
            courseRepository.save(course);

            // Now delete the course
            courseRepository.deleteById(id);
        } else {
            throw new RuntimeException("Course not found with id " + id);
        }
    }

    public List<Course> searchCourses(String keyword) {
        return courseRepository.searchCourses(keyword);
    }

    public List<Course> filterByPrice(Double minPrice, Double maxPrice, String sortOrder) {
        Sort sort = Sort.by("price");
        if ("desc".equalsIgnoreCase(sortOrder)) {
            sort = sort.descending();
        } else {
            sort = sort.ascending();
        }
        return courseRepository.filterByPrice(minPrice, maxPrice, sort);

    }
    public List<Course> getCoursesByTeacher(Long teacherId) {
        return courseRepository.findByTeacherId(teacherId);
    }

}