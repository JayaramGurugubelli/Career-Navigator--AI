package career_Navigator_parent.interviewexperience.dto.response;

import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PageResponse<T> {

    private List<T> content;

    private Integer page;

    private Integer size;

    private Long totalElements;

    private Integer totalPages;

    private Boolean first;

    private Boolean last;

    private Boolean hasNext;

    private Boolean hasPrevious;
}