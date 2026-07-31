package career_Navigator_parent.coding.service.impl;

import career_Navigator_parent.coding.dto.request.ExecutionRequests.Review;
import career_Navigator_parent.coding.dto.response.CodingResponses;
import career_Navigator_parent.coding.dto.response.StudentCodingResponses;
import career_Navigator_parent.coding.entity.*;
import career_Navigator_parent.coding.enums.*;
import career_Navigator_parent.coding.repository.*;
import career_Navigator_parent.coding.service.ProblemReviewService;
import career_Navigator_parent.security.util.SecurityUtils;
import career_Navigator_parent.student.entity.Student;
import career_Navigator_parent.student.repository.StudentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.*;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
@RequiredArgsConstructor
@Transactional
public class ProblemReviewServiceImpl implements ProblemReviewService {

 private static final int MAX_REVIEW_PAGE_SIZE = 100;

 private final ProblemReviewRepository problemReviewRepository;
 private final ProblemReviewHelpfulVoteRepository helpfulVoteRepository;
 private final CodingProblemRepository codingProblemRepository;
 private final ProblemAttemptRepository problemAttemptRepository;
 private final StudentRepository studentRepository;
 private final SecurityUtils securityUtils;

 @Override
 public CodingResponses.Review save(
         Long problemId,
         Review request
 ) {
  validateReviewRequest(request);

  Student student = getCurrentStudent();

  CodingProblem problem =
          getPublishedProblem(problemId);

  verifyStudentSolvedProblem(
          student.getId(),
          problemId
  );

  ProblemReview review =
          problemReviewRepository
                  .findByStudentIdAndProblemId(
                          student.getId(),
                          problemId
                  )
                  .orElseGet(() ->
                          ProblemReview.builder()
                                  .student(student)
                                  .problem(problem)
                                  .helpfulCount(0L)
                                  .build()
                  );

  applyReviewRequest(
          review,
          request
  );

  try {
   ProblemReview saved =
           problemReviewRepository.save(
                   review
           );

   return mapToResponse(saved);

  } catch (DataIntegrityViolationException exception) {
   throw new ResponseStatusException(
           HttpStatus.CONFLICT,
           "A review already exists for this student and problem.",
           exception
   );
  }
 }

 @Override
 @Transactional(readOnly = true)
 public Page<CodingResponses.Review> list(
         Long problemId,
         Pageable pageable
 ) {
  validatePositiveId(
          problemId,
          "A valid problem ID is required."
  );

  if (
          !codingProblemRepository
                  .existsById(problemId)
  ) {
   throw new ResponseStatusException(
           HttpStatus.NOT_FOUND,
           "Problem not found."
   );
  }

  Pageable normalizedPageable =
          normalizePageable(pageable);

  return problemReviewRepository
          .findByProblemIdOrderByHelpfulCountDescCreatedAtDesc(
                  problemId,
                  normalizedPageable
          )
          .map(this::mapToResponse);
 }

 @Override
 public CodingResponses.Review update(
         Long problemId,
         Long reviewId,
         Review request
 ) {
  validateReviewRequest(request);

  Student student = getCurrentStudent();

  validatePositiveId(
          problemId,
          "A valid problem ID is required."
  );

  validatePositiveId(
          reviewId,
          "A valid review ID is required."
  );

  ProblemReview review =
          problemReviewRepository
                  .findByIdAndProblemIdAndStudentId(
                          reviewId,
                          problemId,
                          student.getId()
                  )
                  .orElseThrow(() ->
                          new ResponseStatusException(
                                  HttpStatus.NOT_FOUND,
                                  "Review not found or you do not own this review."
                          )
                  );

  /*
   * The solved check is repeated because a review must remain
   * associated with a verified solver.
   */
  verifyStudentSolvedProblem(
          student.getId(),
          problemId
  );

  applyReviewRequest(
          review,
          request
  );

  ProblemReview saved =
          problemReviewRepository.save(review);

  return mapToResponse(saved);
 }

 @Override
 public void delete(
         Long problemId,
         Long reviewId
 ) {
  Student student = getCurrentStudent();

  validatePositiveId(
          problemId,
          "A valid problem ID is required."
  );

  validatePositiveId(
          reviewId,
          "A valid review ID is required."
  );

  ProblemReview review =
          problemReviewRepository
                  .findByIdAndProblemIdAndStudentId(
                          reviewId,
                          problemId,
                          student.getId()
                  )
                  .orElseThrow(() ->
                          new ResponseStatusException(
                                  HttpStatus.NOT_FOUND,
                                  "Review not found or you do not own this review."
                          )
                  );

  helpfulVoteRepository.deleteByReviewId(
          reviewId
  );

  problemReviewRepository.delete(review);
 }

 @Override
 public StudentCodingResponses.ReviewHelpful
 toggleHelpful(
         Long problemId,
         Long reviewId
 ) {
  Student student = getCurrentStudent();

  validatePositiveId(
          problemId,
          "A valid problem ID is required."
  );

  validatePositiveId(
          reviewId,
          "A valid review ID is required."
  );

  ProblemReview review =
          problemReviewRepository
                  .findByIdAndProblemId(
                          reviewId,
                          problemId
                  )
                  .orElseThrow(() ->
                          new ResponseStatusException(
                                  HttpStatus.NOT_FOUND,
                                  "Problem review not found."
                          )
                  );

  if (
          review.getStudent().getId()
                  .equals(student.getId())
  ) {
   throw new ResponseStatusException(
           HttpStatus.BAD_REQUEST,
           "You cannot mark your own review as helpful."
   );
  }

  var existingVote =
          helpfulVoteRepository
                  .findByStudentIdAndReviewId(
                          student.getId(),
                          reviewId
                  );

  boolean helpful;

  if (existingVote.isPresent()) {
   helpfulVoteRepository.delete(
           existingVote.get()
   );

   helpful = false;

  } else {
   ProblemReviewHelpfulVote vote =
           ProblemReviewHelpfulVote.builder()
                   .student(student)
                   .review(review)
                   .build();

   try {
    helpfulVoteRepository.save(vote);
    helpful = true;

   } catch (DataIntegrityViolationException exception) {
    /*
     * Handles two simultaneous requests from the
     * same student without creating duplicate votes.
     */
    helpful = true;
   }
  }

  long helpfulCount =
          helpfulVoteRepository
                  .countByReviewId(reviewId);

  review.setHelpfulCount(helpfulCount);

  problemReviewRepository.save(review);

  return new StudentCodingResponses.ReviewHelpful(
          reviewId,
          helpful,
          helpfulCount
  );
 }

 private CodingProblem getPublishedProblem(
         Long problemId
 ) {
  validatePositiveId(
          problemId,
          "A valid problem ID is required."
  );

  return codingProblemRepository
          .findByIdAndStatusAndActiveTrue(
                  problemId,
                  ProblemStatus.PUBLISHED
          )
          .orElseThrow(() ->
                  new ResponseStatusException(
                          HttpStatus.NOT_FOUND,
                          "Published coding problem not found."
                  )
          );
 }

 private void verifyStudentSolvedProblem(
         Long studentId,
         Long problemId
 ) {
  boolean solved =
          problemAttemptRepository
                  .existsByStudentIdAndProblemIdAndStatus(
                          studentId,
                          problemId,
                          ProblemAttemptStatus.SOLVED
                  );

  if (!solved) {
   throw new ResponseStatusException(
           HttpStatus.FORBIDDEN,
           "Only verified solvers can review this problem."
   );
  }
 }

 private void applyReviewRequest(
         ProblemReview review,
         Review request
 ) {
  review.setRating(request.rating());

  review.setTitle(
          normalizeNullableText(
                  request.title()
          )
  );

  review.setReview(
          normalizeRequiredText(
                  request.review(),
                  "Review content is required."
          )
  );

  review.setContainsSpoiler(
          Boolean.TRUE.equals(
                  request.containsSpoiler()
          )
  );
 }

 private void validateReviewRequest(
         Review request
 ) {
  if (request == null) {
   throw new ResponseStatusException(
           HttpStatus.BAD_REQUEST,
           "Review request is required."
   );
  }

  if (
          request.rating() == null
                  || request.rating() < 1
                  || request.rating() > 5
  ) {
   throw new ResponseStatusException(
           HttpStatus.BAD_REQUEST,
           "Rating must be between 1 and 5."
   );
  }

  String reviewText =
          request.review();

  if (
          reviewText == null
                  || reviewText.isBlank()
  ) {
   throw new ResponseStatusException(
           HttpStatus.BAD_REQUEST,
           "Review content is required."
   );
  }

  if (reviewText.trim().length() > 5000) {
   throw new ResponseStatusException(
           HttpStatus.BAD_REQUEST,
           "Review content cannot exceed 5000 characters."
   );
  }

  if (
          request.title() != null
                  && request.title()
                  .trim()
                  .length() > 200
  ) {
   throw new ResponseStatusException(
           HttpStatus.BAD_REQUEST,
           "Review title cannot exceed 200 characters."
   );
  }
 }

 private CodingResponses.Review mapToResponse(
         ProblemReview review
 ) {
  return new CodingResponses.Review(
          review.getId(),
          review.getStudent().getId(),
          review.getRating(),
          review.getTitle(),
          review.getReview(),
          Boolean.TRUE.equals(
                  review.getContainsSpoiler()
          ),
          review.getHelpfulCount() == null
                  ? 0L
                  : review.getHelpfulCount(),
          review.getCreatedAt()
  );
 }

 private Student getCurrentStudent() {
  Long currentUserId =
          securityUtils.getCurrentUserId();

  return studentRepository
          .findByUserId(currentUserId)
          .orElseThrow(() ->
                  new ResponseStatusException(
                          HttpStatus.NOT_FOUND,
                          "Student profile not found."
                  )
          );
 }

 private Pageable normalizePageable(
         Pageable pageable
 ) {
  if (pageable == null) {
   return PageRequest.of(
           0,
           10,
           Sort.by(
                   Sort.Direction.DESC,
                   "helpfulCount"
           ).and(
                   Sort.by(
                           Sort.Direction.DESC,
                           "createdAt"
                   )
           )
   );
  }

  int size =
          Math.min(
                  Math.max(
                          pageable.getPageSize(),
                          1
                  ),
                  MAX_REVIEW_PAGE_SIZE
          );

  /*
   * Repository method already declares the review order.
   * The supplied pageable controls only page and size.
   */
  return PageRequest.of(
          Math.max(
                  pageable.getPageNumber(),
                  0
          ),
          size
  );
 }

 private String normalizeRequiredText(
         String value,
         String message
 ) {
  if (
          value == null
                  || value.isBlank()
  ) {
   throw new ResponseStatusException(
           HttpStatus.BAD_REQUEST,
           message
   );
  }

  return value.trim();
 }

 private String normalizeNullableText(
         String value
 ) {
  if (value == null) {
   return null;
  }

  String normalized = value.trim();

  return normalized.isEmpty()
          ? null
          : normalized;
 }

 private void validatePositiveId(
         Long id,
         String message
 ) {
  if (id == null || id <= 0) {
   throw new ResponseStatusException(
           HttpStatus.BAD_REQUEST,
           message
   );
  }
 }

}