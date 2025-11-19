package com.example.postgresdemo.controller;

import com.example.postgresdemo.exception.ResourceNotFoundException;
import com.example.postgresdemo.model.Question;
import com.example.postgresdemo.repository.QuestionRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import javax.validation.Valid;

/**
 * REST controller for managing questions.
 * Provides endpoints for CRUD operations on Question entities with pagination support.
 */
@RestController
@Tag(name = "Questions", description = "API endpoints for managing questions")
public class QuestionController {

    @Autowired
    private QuestionRepository questionRepository;

    /**
     * PUBLIC_INTERFACE
     * Retrieves a paginated list of all questions.
     * 
     * @param pageable pagination information (page number, size, sort)
     * @return Page of Question objects
     */
    // PUBLIC_INTERFACE
    @Operation(summary = "Get all questions", description = "Retrieves a paginated list of all questions")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successfully retrieved questions")
    })
    @GetMapping("/questions")
    public Page<Question> getQuestions(@Parameter(description = "Pagination parameters") Pageable pageable) {
        return questionRepository.findAll(pageable);
    }


    /**
     * PUBLIC_INTERFACE
     * Creates a new question.
     * 
     * @param question the question object to create
     * @return the created Question object
     */
    // PUBLIC_INTERFACE
    @Operation(summary = "Create a new question", description = "Creates a new question with title and description")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Question created successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid input")
    })
    @PostMapping("/questions")
    public Question createQuestion(@Parameter(description = "Question to create") @Valid @RequestBody Question question) {
        return questionRepository.save(question);
    }

    /**
     * PUBLIC_INTERFACE
     * Updates an existing question.
     * 
     * @param questionId the ID of the question to update
     * @param questionRequest the updated question data
     * @return the updated Question object
     * @throws ResourceNotFoundException if question is not found
     */
    // PUBLIC_INTERFACE
    @Operation(summary = "Update a question", description = "Updates an existing question by ID")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Question updated successfully"),
        @ApiResponse(responseCode = "404", description = "Question not found"),
        @ApiResponse(responseCode = "400", description = "Invalid input")
    })
    @PutMapping("/questions/{questionId}")
    public Question updateQuestion(@Parameter(description = "ID of the question to update") @PathVariable Long questionId,
                                   @Parameter(description = "Updated question data") @Valid @RequestBody Question questionRequest) {
        return questionRepository.findById(questionId)
                .map(question -> {
                    question.setTitle(questionRequest.getTitle());
                    question.setDescription(questionRequest.getDescription());
                    return questionRepository.save(question);
                }).orElseThrow(() -> new ResourceNotFoundException("Question not found with id " + questionId));
    }


    /**
     * PUBLIC_INTERFACE
     * Deletes a question by ID.
     * 
     * @param questionId the ID of the question to delete
     * @return ResponseEntity with no content
     * @throws ResourceNotFoundException if question is not found
     */
    // PUBLIC_INTERFACE
    @Operation(summary = "Delete a question", description = "Deletes a question by ID")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Question deleted successfully"),
        @ApiResponse(responseCode = "404", description = "Question not found")
    })
    @DeleteMapping("/questions/{questionId}")
    public ResponseEntity<?> deleteQuestion(@Parameter(description = "ID of the question to delete") @PathVariable Long questionId) {
        return questionRepository.findById(questionId)
                .map(question -> {
                    questionRepository.delete(question);
                    return ResponseEntity.ok().build();
                }).orElseThrow(() -> new ResourceNotFoundException("Question not found with id " + questionId));
    }
}
