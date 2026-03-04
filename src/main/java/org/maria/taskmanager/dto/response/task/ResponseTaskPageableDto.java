package org.maria.taskmanager.dto.response.task;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ResponseTaskPageableDto {
    private int page;
    private int pageSize;
    private long totalElements;
    private List<ResponseTaskDto> tasks;
}
