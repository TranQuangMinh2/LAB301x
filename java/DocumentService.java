package com.trnqngmnh.library;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class DocumentService {

	@Autowired
	private DocumentRepository documentRepository;

	public DocumentService(DocumentRepository documentRepository) {
		this.documentRepository = documentRepository;
	}

	public Document saveDocument(Document document) {
		return documentRepository.save(document);
	}

	public List<Document> getAllProducts() {
		return documentRepository.findAll();
	}

	public Document getDocumentById(Long id) {
		return documentRepository.findById(id).orElse(null);
	}

	public Document updateDocument(Document document) {
		Document existingDocument = documentRepository.findById(document.getId()).orElse(null);
		existingDocument.setTitle(document.getTitle());
		existingDocument.setContent(document.getContent());
		return documentRepository.save(existingDocument);
	}

	public void deleteById(Long id) {
		documentRepository.deleteById(id);
	}

	public List<Document> getAllDocuments() {
		// TODO Auto-generated method stub
		return documentRepository.findAll();
	}
}
