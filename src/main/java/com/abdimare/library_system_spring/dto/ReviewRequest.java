package com.abdimare.library_system_spring.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record ReviewRequest(
        @NotNull Long bookId,
        @NotNull Long memberId,
        @NotNull @Min(1) @Max(5) Integer rating,
        String comment
) {}