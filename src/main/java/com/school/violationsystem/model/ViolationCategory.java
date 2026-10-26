package com.school.violationsystem.model;

public record ViolationCategory(int id, String name) {
    @Override
    public String toString() {
        return name;
    }
}
