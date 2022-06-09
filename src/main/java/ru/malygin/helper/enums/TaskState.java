package ru.malygin.helper.enums;

import lombok.AllArgsConstructor;

import java.util.Arrays;

@AllArgsConstructor
public enum TaskState {
    CREATE(0, ""),
    SENDING(1, ""),
    START(2, "task_start"),
    COMPLETE(3, "task_end"),
    INTERRUPT(4, "task_interrupt"),
    ERROR(5, "task_error");

    private final int state;
    private final String template;

    public static TaskState getFromState(int state) {
        return Arrays
                .stream(TaskState.values())
                .filter(taskState -> taskState.getState() == state)
                .findFirst()
                .orElseThrow(RuntimeException::new);
    }

    public int getState() {
        return this.state;
    }

    public String getTemplate() {
        return this.template;
    }
}
