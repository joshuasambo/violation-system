package com.school.violationsystem.model;

public record HandbookSection(int id, String name) {
    @Override
    public String toString() {
        return name;
    }
}
