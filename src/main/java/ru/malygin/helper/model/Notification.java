package ru.malygin.helper.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Notification implements Serializable {
    private String type;
    private String sendTo;
    private String subject;
    private String template;
    private Map<String, String> payload;
}
