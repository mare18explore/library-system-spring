package com.abdimare.library_system_spring.dto;

import jakarta.validation.constraints.NotNull;

public record BorrowRequest(@NotNull Long bookId, @NotNull Long memberId) {}