package com._DSF.je.Controller;

import com._DSF.je.Entity.Qcm;
import com._DSF.je.Entity.QcmDTO;
import com._DSF.je.Entity.Quiz;
import com._DSF.je.Repository.QuizRepository;
import com._DSF.je.Service.QcmService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.util.Optional;

@RestController
@RequestMapping("/api/qcm")
public class QcmController {

    private final QcmService qcmService;
    private final QuizRepository quizRepository;

    public QcmController(QcmService qcmService, QuizRepository quizRepository) {
        this.qcmService = qcmService;
        this.quizRepository = quizRepository;
    }

    @PostMapping("/check/{qcmId}")
    public ResponseEntity<String> checkAnswer(@PathVariable Long qcmId, @RequestBody String answer) {
        try {
            boolean isCorrect = qcmService.checkAnswer(qcmId, answer);
            if (isCorrect) {
                return ResponseEntity.ok("Correct answer! Grade increased.");
            } else {
                return ResponseEntity.ok("Incorrect answer.");
            }
        } catch (ResponseStatusException e) {
            return ResponseEntity.status(e.getStatusCode()).body(e.getReason());
        }
    }

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> createQCM(
            @RequestPart("qcm") String qcmJson,  // Handle the JSON part
            @RequestPart(value = "image", required = false) MultipartFile image) {  // Handle the optional image
        try {
            // Convert JSON string into QcmDTO
            ObjectMapper objectMapper = new ObjectMapper();
            QcmDTO qcmDTO = objectMapper.readValue(qcmJson, QcmDTO.class);

            // Map DTO to Entity
            Qcm qcm = new Qcm();
            qcm.setQuestion(qcmDTO.getQuestion());
            qcm.setCorrectAnswer(qcmDTO.getCorrectAnswer());

            // Associate quiz by fetching it from the DB
            Optional<Quiz> quiz = quizRepository.findById(qcmDTO.getQuiz());
            if (quiz.isEmpty()) {
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Quiz not found");
            }
            qcm.setQuiz(quiz.get());

            // Call service with QCM and optional image
            Qcm createdQCM = qcmService.createQCM(qcm, image);
            return ResponseEntity.ok(createdQCM);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error creating QCM: " + e.getMessage());
        }
    }




}
