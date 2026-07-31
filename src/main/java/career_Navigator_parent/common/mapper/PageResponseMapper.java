package career_Navigator_parent.common.mapper;

import career_Navigator_parent.interviewexperience.dto.response.PageResponse;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;

import java.util.function.Function;

@Component
public class PageResponseMapper {

    public <E, R> PageResponse<R> toResponse(
            Page<E> page,
            Function<E, R> mapper
    ) {

        return PageResponse.<R>builder()
                .content(
                        page.getContent()
                                .stream()
                                .map(mapper)
                                .toList()
                )
                .page(
                        page.getNumber()
                )
                .size(
                        page.getSize()
                )
                .totalElements(
                        page.getTotalElements()
                )
                .totalPages(
                        page.getTotalPages()
                )
                .first(
                        page.isFirst()
                )
                .last(
                        page.isLast()
                )
                .hasNext(
                        page.hasNext()
                )
                .hasPrevious(
                        page.hasPrevious()
                )
                .build();
    }
}