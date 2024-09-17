package com._DSF.je.Service;

import com._DSF.je.Entity.Quiz;
import com._DSF.je.Repository.CourseRepository;
import com._DSF.je.Repository.QuizRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class QuizService {

    private final QuizRepository quizRepository;
    private final CourseRepository courseRepository;

    public QuizService(QuizRepository quizRepository, CourseRepository courseRepository) {
        this.quizRepository = quizRepository;
        this.courseRepository = courseRepository;
    }

    public List<Quiz> getAllQuizzes() {
        return quizRepository.findAll();
    }

    public Optional<Quiz> getQuizById(Long id) {
        return quizRepository.findById(id);
    }

    public Quiz createQuiz(Long courseId, Quiz quiz) {
        return courseRepository.findById(courseId).map(course -> {
            quiz.setCourse(course);
            return quizRepository.save(quiz);
        }).orElseThrow(() -> new RuntimeException("Course not found"));
    }

    public Quiz updateQuiz(Long id, Quiz quizDetails) {
        return quizRepository.findById(id).map(quiz -> {
            quiz.setTitle(quizDetails.getTitle());
            quiz.setCourse(quizDetails.getCourse());
            return quizRepository.save(quiz);
        }).orElseThrow(() -> new RuntimeException("Quiz not found"));
    }

    public void deleteQuiz(Long id) {
        quizRepository.deleteById(id);
    }
}
