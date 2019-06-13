package com.asaraff.sns.model;

import lombok.Data;
import org.hibernate.validator.constraints.NotEmpty;

import javax.validation.constraints.NotNull;

@Data
public class Topic {
    @NotNull @NotEmpty
    private String name;
    // maybe null or empty
    private String notification;
    // maybe null or empty
    private String audit;
}
