package com.example.iamservice.dto.response.common;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.domain.Page;

import java.util.List;

@Getter
@Setter
public class PageableObject<T> {
    private long totalPages;
    private long totalElements;
    private int currentPage;
    private List<T> data;

    public PageableObject(Page<T> page) {
        this.totalPages = page.getTotalPages();
        this.totalElements = page.getTotalElements();
        this.currentPage = page.getNumber();
        this.data = page.getContent();
    }
}
