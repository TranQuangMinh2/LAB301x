package com.trnqngmnh.library;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.trnqngmnh.library.entity.Document;
import com.trnqngmnh.library.repository.DocumentRepository;
import com.trnqngmnh.library.service.DocumentService;

class DocumentServiceTest {

	@Mock
	private DocumentRepository documentRepository;

	@InjectMocks
	private DocumentService documentService;

	@BeforeEach
	void setUp() {
		MockitoAnnotations.openMocks(this);
	}

	@Test
	void testSaveDocument_Success() {
		// Arrange
		Document document = new Document();
		document.setId(1L);
		document.setTitle("Test Title");
		document.setContent("Test Content");

		when(documentRepository.save(document)).thenReturn(document);

		// Act
		Document savedDocument = documentService.saveDocument(document);

		// Assert
		assertNotNull(savedDocument);
		assertEquals("Test Title", savedDocument.getTitle());
		verify(documentRepository, times(1)).save(document);
	}

	@Test
	void testGetAllDocuments_Success() {
		// Arrange
		List<Document> documents = new ArrayList<>();
		Document document1 = new Document();
		document1.setId(1L);
		document1.setTitle("Title 1");
		document1.setContent("Content 1");

		Document document2 = new Document();
		document2.setId(2L);
		document2.setTitle("Title 2");
		document2.setContent("Content 2");

		documents.add(document1);
		documents.add(document2);

		when(documentRepository.findAll()).thenReturn(documents);

		// Act
		List<Document> result = documentService.getAllDocuments();

		// Assert
		assertNotNull(result);
		assertEquals(2, result.size());
		verify(documentRepository, times(1)).findAll();
	}

	@Test
	void testGetDocumentById_Found() {
		// Arrange
		Document document = new Document();
		document.setId(1L);
		document.setTitle("Test Title");
		document.setContent("Test Content");

		when(documentRepository.findById(1L)).thenReturn(Optional.of(document));

		// Act
		Document result = documentService.getDocumentById(1L);

		// Assert
		assertNotNull(result);
		assertEquals("Test Title", result.getTitle());
		verify(documentRepository, times(1)).findById(1L);
	}

	@Test
	void testGetDocumentById_NotFound() {
		// Arrange
		when(documentRepository.findById(1L)).thenReturn(Optional.empty());

		// Act
		Document result = documentService.getDocumentById(1L);

		// Assert
		assertNull(result);
		verify(documentRepository, times(1)).findById(1L);
	}

	@Test
	void testUpdateDocument_Success() {
		// Arrange
		Document existingDocument = new Document();
		existingDocument.setId(1L);
		existingDocument.setTitle("Old Title");
		existingDocument.setContent("Old Content");

		Document updatedDocument = new Document();
		updatedDocument.setId(1L);
		updatedDocument.setTitle("Updated Title");
		updatedDocument.setContent("Updated Content");

		when(documentRepository.findById(1L)).thenReturn(Optional.of(existingDocument));
		when(documentRepository.save(any(Document.class))).thenReturn(updatedDocument);

		// Act
		Document result = documentService.updateDocument(updatedDocument);

		// Assert
		assertNotNull(result);
		assertEquals("Updated Title", result.getTitle());
		verify(documentRepository, times(1)).findById(1L);
		verify(documentRepository, times(1)).save(any(Document.class));
	}

	@Test
	void testDeleteById_Success() {
		// Arrange
		Long id = 1L;

		doNothing().when(documentRepository).deleteById(id);

		// Act
		documentService.deleteById(id);

		// Assert
		verify(documentRepository, times(1)).deleteById(id);
	}
}
