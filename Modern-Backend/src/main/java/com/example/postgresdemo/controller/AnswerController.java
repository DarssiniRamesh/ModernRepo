package com.example.postgresdemo.controller;

import com.example.postgresdemo.exception.ResourceNotFoundException;
import com.example.postgresdemo.model.Answer;
import com.example.postgresdemo.repository.AnswerRepository;
import com.example.postgresdemo.repository.QuestionRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import javax.validation.Valid;
import java.util.List;

/**
 * PUBLIC_INTERFACE
 * REST controller for managing answers to questions.
 * Provides endpoints for CRUD operations on Answer entities associated with specific questions.
 */
@RestController
@Tag(name = "Answers", description = "Answer management APIs - Create, read, update, and delete answers for questions")
public class AnswerController {

    @Autowired
    private AnswerRepository answerRepository;

    @Autowired
    private QuestionRepository questionRepository;

    /**
     * PUBLIC_INTERFACE
     * Retrieves all answers for a specific question.
     * 
     * @param questionId ID of the question
     * @return List of answers for the question
     */
    @Operation(
        summary = "Get all answers for a question",
        description = "Retrieves all answers associated with a specific question ID."
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Successfully retrieved answers",
            content = @Content(schema = @Schema(implementation = List.class))
        )
    })
    @GetMapping("/questions/{questionId}/answers")
    public List<Answer> getAnswersByQuestionId(
            @Parameter(description = "ID of the question", required = true, example = "1000")
            @PathVariable Long questionId) {
        return answerRepository.findByQuestionId(questionId);
    }

    /**
     * PUBLIC_INTERFACE
     * Creates a new answer for a specific question.
     * 
     * @param questionId ID of the question to answer
     * @param answer Answer object to create
     * @return Created answer with generated ID and timestamps
     * @throws ResourceNotFoundException if question not found
     */
    @Operation(
        summary = "Add an answer to a question",
        description = "Creates a new answer for a specific question. The question must exist."
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Answer created successfully",
            content = @Content(schema = @Schema(implementation = Answer.class))
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
    @PostMapping("/questions/{questionId}/answers")
    public Answer addAnswer(
            @Parameter(description = "ID of the question to answer", required = true, example = "1000")
            @PathVariable Long questionId,
            @Parameter(description = "Answer to create", required = true)
            @Valid @RequestBody Answer answer) {
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
     * @param questionId ID of the question
     * @param answerId ID of the answer to update
     * @param answerRequest Updated answer data
     * @return Updated answer
     * @throws ResourceNotFoundException if question or answer not found
     */
    @Operation(
        summary = "Update an answer",
        description = "Updates an existing answer's text by answer ID and question ID."
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Answer updated successfully",
            content = @Content(schema = @Schema(implementation = Answer.class))
        ),
        @ApiResponse(
            responseCode = "404",
            description = "Question or answer not found",
            content = @Content
        ),
        @ApiResponse(
            responseCode = "400",
            description = "Invalid input - validation failed",
            content = @Content
        )
    })
    @PutMapping("/questions/{questionId}/answers/{answerId}")
    public Answer updateAnswer(
            @Parameter(description = "ID of the question", required = true, example = "1000")
            @PathVariable Long questionId,
            @Parameter(description = "ID of the answer to update", required = true, example = "1000")
            @PathVariable Long answerId,
            @Parameter(description = "Updated answer data", required = true)
            @Valid @RequestBody Answer answerRequest) {
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
     * @param questionId ID of the question
     * @param answerId ID of the answer to delete
     * @return Empty response with 200 status
     * @throws ResourceNotFoundException if question or answer not found
     */
    @Operation(
        summary = "Delete an answer",
        description = "Deletes an answer by answer ID and question ID."
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Answer deleted successfully"
        ),
        @ApiResponse(
            responseCode = "404",
            description = "Question or answer not found",
            content = @Content
        )
    })
    @DeleteMapping("/questions/{questionId}/answers/{answerId}")
    public ResponseEntity<?> deleteAnswer(
            @Parameter(description = "ID of the question", required = true, example = "1000")
            @PathVariable Long questionId,
            @Parameter(description = "ID of the answer to delete", required = true, example = "1000")
            @PathVariable Long answerId) {
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
