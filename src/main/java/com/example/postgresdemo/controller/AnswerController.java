package com.example.postgresdemo.controller;

import com.example.postgresdemo.exception.ResourceNotFoundException;
import com.example.postgresdemo.model.Answer;
import com.example.postgresdemo.repository.AnswerRepository;
import com.example.postgresdemo.repository.QuestionRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import javax.validation.Valid;
import java.util.List;

/**
 * REST controller for managing answers.
 * Provides endpoints for CRUD operations on Answer entities associated with questions.
 */
@RestController
@Tag(name = "Answers", description = "API endpoints for managing answers to questions")
public class AnswerController {

    @Autowired
    private AnswerRepository answerRepository;

    @Autowired
    private QuestionRepository questionRepository;

    /**
     * PUBLIC_INTERFACE
     * Retrieves all answers for a specific question.
     * 
     * @param questionId the ID of the question
     * @return List of Answer objects for the specified question
     */
    // PUBLIC_INTERFACE
    @Operation(summary = "Get answers by question ID", description = "Retrieves all answers for a specific question")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successfully retrieved answers")
    })
    @GetMapping("/questions/{questionId}/answers")
    public List<Answer> getAnswersByQuestionId(@Parameter(description = "ID of the question") @PathVariable Long questionId) {
        return answerRepository.findByQuestionId(questionId);
    }

    /**
     * PUBLIC_INTERFACE
     * Creates a new answer for a specific question.
     * 
     * @param questionId the ID of the question to answer
     * @param answer the answer object to create
     * @return the created Answer object
     * @throws ResourceNotFoundException if question is not found
     */
    // PUBLIC_INTERFACE
    @Operation(summary = "Add an answer to a question", description = "Creates a new answer for a specific question")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Answer created successfully"),
        @ApiResponse(responseCode = "404", description = "Question not found"),
        @ApiResponse(responseCode = "400", description = "Invalid input")
    })
    @PostMapping("/questions/{questionId}/answers")
    public Answer addAnswer(@Parameter(description = "ID of the question") @PathVariable Long questionId,
                            @Parameter(description = "Answer to create") @Valid @RequestBody Answer answer) {
        return questionRepository.findById(questionId)
                .map(question -> {
                    answer.setQuestion(question);
                    return answerRepository.save(answer);
                }).orElseThrow(() -> new ResourceNotFoundException("Question not found with id " + questionId));
    }

    /**
     * PUBLIC_INTERFACE
     * Updates an existing answer.
     * 
     * @param questionId the ID of the question
     * @param answerId the ID of the answer to update
     * @param answerRequest the updated answer data
     * @return the updated Answer object
     * @throws ResourceNotFoundException if question or answer is not found
     */
    // PUBLIC_INTERFACE
    @Operation(summary = "Update an answer", description = "Updates an existing answer for a question")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Answer updated successfully"),
        @ApiResponse(responseCode = "404", description = "Question or answer not found"),
        @ApiResponse(responseCode = "400", description = "Invalid input")
    })
    @PutMapping("/questions/{questionId}/answers/{answerId}")
    public Answer updateAnswer(@Parameter(description = "ID of the question") @PathVariable Long questionId,
                               @Parameter(description = "ID of the answer to update") @PathVariable Long answerId,
                               @Parameter(description = "Updated answer data") @Valid @RequestBody Answer answerRequest) {
        if(!questionRepository.existsById(questionId)) {
            throw new ResourceNotFoundException("Question not found with id " + questionId);
        }

        return answerRepository.findById(answerId)
                .map(answer -> {
                    answer.setText(answerRequest.getText());
                    return answerRepository.save(answer);
                }).orElseThrow(() -> new ResourceNotFoundException("Answer not found with id " + answerId));
    }

    /**
     * PUBLIC_INTERFACE
     * Deletes an answer by ID.
     * 
     * @param questionId the ID of the question
     * @param answerId the ID of the answer to delete
     * @return ResponseEntity with no content
     * @throws ResourceNotFoundException if question or answer is not found
     */
    // PUBLIC_INTERFACE
    @Operation(summary = "Delete an answer", description = "Deletes an answer for a specific question")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Answer deleted successfully"),
        @ApiResponse(responseCode = "404", description = "Question or answer not found")
    })
    @DeleteMapping("/questions/{questionId}/answers/{answerId}")
    public ResponseEntity<?> deleteAnswer(@Parameter(description = "ID of the question") @PathVariable Long questionId,
                                          @Parameter(description = "ID of the answer to delete") @PathVariable Long answerId) {
        if(!questionRepository.existsById(questionId)) {
            throw new ResourceNotFoundException("Question not found with id " + questionId);
        }

        return answerRepository.findById(answerId)
                .map(answer -> {
                    answerRepository.delete(answer);
                    return ResponseEntity.ok().build();
                }).orElseThrow(() -> new ResourceNotFoundException("Answer not found with id " + answerId));

    }
}
