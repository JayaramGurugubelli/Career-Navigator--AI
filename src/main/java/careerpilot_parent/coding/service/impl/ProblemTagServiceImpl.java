package careerpilot_parent.coding.service.impl;

import careerpilot_parent.coding.dto.request.ProblemTagRequests;
import careerpilot_parent.coding.dto.response.ProblemTagResponse;
import careerpilot_parent.coding.entity.ProblemTag;
import careerpilot_parent.coding.repository.ProblemTagRepository;
import careerpilot_parent.coding.service.ProblemTagService;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Locale;

@Service
@RequiredArgsConstructor
@Transactional
public class ProblemTagServiceImpl
        implements ProblemTagService {

    private final ProblemTagRepository tags;

    @Override
    public ProblemTagResponse create(
            ProblemTagRequests.Create request
    ) {

        String name = normalizeName(request.name());
        String slug = generateSlug(name);

        if (tags.existsByNameIgnoreCase(name)) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "A coding problem tag with this name already exists."
            );
        }

        if (tags.existsBySlug(slug)) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "A coding problem tag with this slug already exists."
            );
        }

        ProblemTag tag = ProblemTag.builder()
                .name(name)
                .slug(slug)
                .description(
                        normalizeNullableText(
                                request.description()
                        )
                )
                .active(true)
                .build();

        try {
            return toResponse(
                    tags.saveAndFlush(tag)
            );
        } catch (DataIntegrityViolationException exception) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "A coding problem tag with the same name or slug already exists."
            );
        }
    }

    @Override
    public ProblemTagResponse update(
            Long tagId,
            ProblemTagRequests.Update request
    ) {

        ProblemTag tag = getRequiredTag(tagId);

        String name = normalizeName(request.name());
        String slug = generateSlug(name);

        if (
                tags.existsByNameIgnoreCaseAndIdNot(
                        name,
                        tagId
                )
        ) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Another coding problem tag already uses this name."
            );
        }

        if (
                tags.existsBySlugAndIdNot(
                        slug,
                        tagId
                )
        ) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Another coding problem tag already uses this slug."
            );
        }

        tag.setName(name);
        tag.setSlug(slug);
        tag.setDescription(
                normalizeNullableText(
                        request.description()
                )
        );

        if (request.active() != null) {
            tag.setActive(request.active());
        }

        try {
            return toResponse(
                    tags.saveAndFlush(tag)
            );
        } catch (DataIntegrityViolationException exception) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "A coding problem tag with the same name or slug already exists."
            );
        }
    }

    @Override
    @Transactional(readOnly = true)
    public ProblemTagResponse get(Long tagId) {

        return toResponse(
                getRequiredTag(tagId)
        );
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProblemTagResponse> list(
            Boolean includeInactive
    ) {

        List<ProblemTag> result;

        if (Boolean.TRUE.equals(includeInactive)) {
            result = tags.findAll();
        } else {
            result =
                    tags.findAllByActiveTrueOrderByNameAsc();
        }

        return result.stream()
                .map(this::toResponse)
                .toList();
    }

    @Override
    public void delete(Long tagId) {

        ProblemTag tag = getRequiredTag(tagId);

        if (
                tag.getProblems() != null
                        && !tag.getProblems().isEmpty()
        ) {
            tag.setActive(false);
            tags.save(tag);
            return;
        }

        tags.delete(tag);
    }

    private ProblemTag getRequiredTag(Long tagId) {

        if (tagId == null || tagId <= 0) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "A valid coding problem tag ID is required."
            );
        }

        return tags.findById(tagId)
                .orElseThrow(
                        () -> new ResponseStatusException(
                                HttpStatus.NOT_FOUND,
                                "Coding problem tag not found."
                        )
                );
    }

    private ProblemTagResponse toResponse(
            ProblemTag tag
    ) {

        return new ProblemTagResponse(
                tag.getId(),
                tag.getName(),
                tag.getSlug(),
                tag.getDescription(),
                tag.getActive(),
                tag.getCreatedAt(),
                tag.getUpdatedAt()
        );
    }

    private String normalizeName(String name) {

        if (name == null || name.isBlank()) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Tag name is required."
            );
        }

        return name.trim()
                .replaceAll("\\s+", " ");
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

    private String generateSlug(String value) {

        String slug = value
                .trim()
                .toLowerCase(Locale.ROOT)
                .replaceAll("[^a-z0-9]+", "-")
                .replaceAll("(^-|-$)", "");

        if (slug.isBlank()) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Tag name cannot generate a valid slug."
            );
        }

        return slug;
    }
}