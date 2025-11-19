package com.example.postgresdemo.controller;

import com.example.postgresdemo.exception.ResourceNotFoundException;
import com.example.postgresdemo.model.Question;
import com.example.postgresdemo.repository.QuestionRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
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
 * PUBLIC_INTERFACE
 * REST controller for managing questions.
 * Provides endpoints for CRUD operations on Question entities with pagination support.
 */
@RestController
@Tag(name = "Questions", description = "Question management APIs - Create, read, update, and delete questions")
public class QuestionController {

    @Autowired
    private QuestionRepository questionRepository;

    /**
     * PUBLIC_INTERFACE
     * Retrieves all questions with pagination support.
     * 
     * @param pageable Pagination parameters (page, size, sort)
     * @return Page of questions with pagination metadata
     */
    @Operation(
        summary = "Get all questions",
        description = "Retrieves a paginated list of all questions. Supports sorting and filtering via query parameters."
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Successfully retrieved questions",
            content = @Content(schema = @Schema(implementation = Page.class))
        )
    })
    @GetMapping("/questions")
    public Page<Question> getQuestions(
            @Parameter(description = "Pagination parameters (page, size, sort)", example = "page=0&size=20&sort=createdAt,desc")
            Pageable pageable) {
        return questionRepository.findAll(pageable);
    }

    /**
     * PUBLIC_INTERFACE
     * Creates a new question.
     * 
     * @param question Question object to create (title and description required)
     * @return Created question with generated ID and timestamps
     */
    @Operation(
        summary = "Create a new question",
        description = "Creates a new question with the provided title and description. Title must be 3-100 characters."
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Question created successfully",
            content = @Content(schema = @Schema(implementation = Question.class))
        ),
        @ApiResponse(
            responseCode = "400",
            description = "Invalid input - validation failed",
            content = @Content
        )
    })
    @PostMapping("/questions")
    public Question createQuestion(
            @Parameter(description = "Question to create", required = true)
            @Valid @RequestBody Question question) {
        return questionRepository.save(question);
    }

    /**
     * PUBLIC_INTERFACE
     * Updates an existing question.
     * 
     * @param questionId ID of the question to update
     * @param questionRequest Updated question data
     * @return Updated question
     * @throws ResourceNotFoundException if question not found
     */
    @Operation(
        summary = "Update a question",
        description = "Updates an existing question's title and/or description by ID."
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Question updated successfully",
            content = @Content(schema = @Schema(implementation = Question.class))
        ),
        @ApiResponse(
            responseCode = "404",
            description = "Question not found",
            content = @Content
        ),
        @ApiResponse(
            responseCode = "400",
            description = "Invalid input - validation failed",
            content = @Content
        )
    })
    @PutMapping("/questions/{questionId}")
    public Question updateQuestion(
            @Parameter(description = "ID of the question to update", required = true, example = "1000")
            @PathVariable Long questionId,
            @Parameter(description = "Updated question data", required = true)
            @Valid @RequestBody Question questionRequest) {
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
     * @param questionId ID of the question to delete
     * @return Empty response with 200 status
     * @throws ResourceNotFoundException if question not found
     */
    @Operation(
        summary = "Delete a question",
        description = "Deletes a question by ID. All associated answers will also be deleted (cascade delete)."
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Question deleted successfully"
        ),
        @ApiResponse(
            responseCode = "404",
            description = "Question not found",
            content = @Content
        )
    })
    @DeleteMapping("/questions/{questionId}")
    public ResponseEntity<?> deleteQuestion(
            @Parameter(description = "ID of the question to delete", required = true, example = "1000")
            @PathVariable Long questionId) {
        return questionRepository.findById(questionId)
                .map(question -> {
                    questionRepository.delete(question);
                    return ResponseEntity.ok().build();
                }).orElseThrow(() -> new ResourceNotFoundException("Question not found with id " + questionId));
    }
}
