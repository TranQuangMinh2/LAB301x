package com.trnqngmnh.library.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.trnqngmnh.library.entity.Document;

public interface DocumentRepository extends JpaRepository<Document, Long> {
}
