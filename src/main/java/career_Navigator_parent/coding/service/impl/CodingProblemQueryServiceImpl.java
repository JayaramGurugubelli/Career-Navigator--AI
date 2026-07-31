package career_Navigator_parent.coding.service.impl;

import career_Navigator_parent.coding.dto.response.CodingResponses;
import career_Navigator_parent.coding.dto.response.CodingResponses.Detail;
import career_Navigator_parent.coding.dto.response.CodingResponses.Solution;
import career_Navigator_parent.coding.dto.response.CodingResponses.Summary;
import career_Navigator_parent.coding.entity.CodingProblem;
import career_Navigator_parent.coding.entity.ProblemAttempt;
import career_Navigator_parent.coding.entity.ProblemStarterCode;
import career_Navigator_parent.coding.enums.ProblemAttemptStatus;
import career_Navigator_parent.coding.enums.ProblemDifficulty;
import career_Navigator_parent.coding.enums.ProblemStatus;
import career_Navigator_parent.coding.enums.ProgrammingLanguage;
import career_Navigator_parent.coding.mapper.CodingProblemMapper;
import career_Navigator_parent.coding.repository.*;
import career_Navigator_parent.coding.service.CodingProblemQueryService;
import career_Navigator_parent.security.util.SecurityUtils;
import career_Navigator_parent.student.entity.Student;
import career_Navigator_parent.student.repository.StudentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CodingProblemQueryServiceImpl implements CodingProblemQueryService {

 private final CodingProblemRepository problems;
 private final ProblemAttemptRepository attempts;
 private final ProblemBookmarkRepository bookmarks;
 private final ProblemReviewRepository reviews;
 private final ProblemSolutionRepository solutions;
 private final StudentRepository students;
 private final SecurityUtils security;
 private final CodingProblemMapper mapper;
 private final CodingProblemRepository codingProblemRepository;
 private final ProblemStarterCodeRepository starterCodeRepository;

 @Override
 public Page<Summary> search(
         String keyword,
         ProblemDifficulty difficulty,
         String tag,
         Pageable pageable
 ) {

  Student student = currentStudent();

  String normalizedKeyword = normalize(keyword);
  String normalizedTag = normalize(tag);

  return problems
          .searchPublished(
                  ProblemStatus.PUBLISHED,
                  normalizedKeyword,
                  difficulty,
                  normalizedTag,
                  pageable
          )
          .map(problem -> {

           ProblemAttempt attempt = attempts
                   .findByStudentIdAndProblemId(
                           student.getId(),
                           problem.getId()
                   )
                   .orElse(null);

           boolean attempted = attempt != null;

           boolean solved =
                   attempt != null
                           && attempt.getStatus()
                           == ProblemAttemptStatus.SOLVED;

           boolean bookmarked = bookmarks
                   .existsByStudentIdAndProblemId(
                           student.getId(),
                           problem.getId()
                   );

           Double averageRating =
                   reviews.averageRating(
                           problem.getId()
                   );

           return mapper.summary(
                   problem,
                   solved,
                   attempted,
                   bookmarked,
                   averageRating
           );
          });
 }

 @Override
 public Detail get(String slug) {

  Student student = currentStudent();

  CodingProblem problem = problems
          .findBySlugAndStatusAndActiveTrue(
                  slug,
                  ProblemStatus.PUBLISHED
          )
          .orElseThrow(
                  () -> new ResponseStatusException(
                          HttpStatus.NOT_FOUND,
                          "Published coding problem not found."
                  )
          );

  ProblemAttempt attempt = attempts
          .findByStudentIdAndProblemId(
                  student.getId(),
                  problem.getId()
          )
          .orElse(null);

  boolean attempted = attempt != null;

  boolean solved =
          attempt != null
                  && attempt.getStatus()
                  == ProblemAttemptStatus.SOLVED;

  boolean bookmarked = bookmarks
          .existsByStudentIdAndProblemId(
                  student.getId(),
                  problem.getId()
          );

  Double averageRating =
          reviews.averageRating(
                  problem.getId()
          );

  return mapper.detail(
          problem,
          solved,
          attempted,
          bookmarked,
          averageRating
  );
 }

 @Override
 public List<Solution> solutions(Long problemId) {

  Student student = currentStudent();

  boolean solved = attempts
          .findByStudentIdAndProblemId(
                  student.getId(),
                  problemId
          )
          .map(
                  attempt ->
                          attempt.getStatus()
                                  == ProblemAttemptStatus.SOLVED
          )
          .orElse(false);

  if (!solved) {
   throw new ResponseStatusException(
           HttpStatus.FORBIDDEN,
           "Solve the problem before viewing the official solution."
   );
  }

  boolean problemExists = problems
          .findByIdAndStatusAndActiveTrue(
                  problemId,
                  ProblemStatus.PUBLISHED
          )
          .isPresent();

  if (!problemExists) {
   throw new ResponseStatusException(
           HttpStatus.NOT_FOUND,
           "Published coding problem not found."
   );
  }

  return solutions
          .findByProblemIdAndActiveTrueOrderByOfficialDescIdAsc(
                  problemId
          )
          .stream()
          .map(solution -> new Solution(
                  solution.getId(),
                  solution.getProgrammingLanguage(),
                  solution.getApproach(),
                  solution.getTitle(),
                  solution.getExplanation(),
                  solution.getSourceCode(),
                  solution.getTimeComplexity(),
                  solution.getSpaceComplexity(),
                  Boolean.TRUE.equals(
                          solution.getOfficial()
                  )
          ))
          .toList();
 }

 private Student currentStudent() {

  Long userId =
          security.getCurrentUserId();

  return students
          .findByUserId(userId)
          .orElseThrow(
                  () -> new ResponseStatusException(
                          HttpStatus.NOT_FOUND,
                          "Student profile not found."
                  )
          );
 }

 private String normalize(String value) {

  if (value == null || value.isBlank()) {
   return null;
  }

  return value.trim();
 }
    @Override
    @Transactional(readOnly = true)
    public CodingResponses.Starter starterCode(
            Long problemId,
            ProgrammingLanguage language
    ) {
        if (problemId == null || problemId <= 0) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "A valid problem ID is required."
            );
        }

        if (language == null) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Programming language is required."
            );
        }

        CodingProblem problem =
                codingProblemRepository.findById(problemId)
                        .orElseThrow(() ->
                                new ResponseStatusException(
                                        HttpStatus.NOT_FOUND,
                                        "Coding problem not found."
                                )
                        );

        if (
                problem.getStatus() != ProblemStatus.PUBLISHED
                        || !Boolean.TRUE.equals(problem.getActive())
        ) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND,
                    "Published coding problem not found."
            );
        }

        ProblemStarterCode starterCode =
                starterCodeRepository
                        .findByProblemIdAndProgrammingLanguageAndActiveTrue(
                                problemId,
                                language
                        )
                        .orElseThrow(() ->
                                new ResponseStatusException(
                                        HttpStatus.NOT_FOUND,
                                        "Active starter code not found for language "
                                                + language
                                                + "."
                                )
                        );

        return new CodingResponses.Starter(
                starterCode.getId(),
                starterCode.getProgrammingLanguage(),
                starterCode.getStarterCode(),
                starterCode.getMethodSignature()
        );
    }
}