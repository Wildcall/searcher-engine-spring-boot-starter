package ru.malygin.helper.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.malygin.helper.enums.TaskState;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TaskCallback {
    private Long taskId;
    private TaskState state;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
}
