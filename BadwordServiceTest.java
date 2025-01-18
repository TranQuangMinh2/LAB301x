package com.trnqngmnh.library;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.trnqngmnh.library.entity.Badword;
import com.trnqngmnh.library.entity.ResourceNotFoundException;
import com.trnqngmnh.library.repository.BadwordRepository;
import com.trnqngmnh.library.service.BadwordService;

class BadwordServiceTest {

	@InjectMocks
	private BadwordService badwordService;

	@Mock
	private BadwordRepository badwordRepository;

	@BeforeEach
	void setUp() {
		MockitoAnnotations.openMocks(this);
	}

	@Test
	void testGetSensitiveWords() {
		// Arrange
		List<String> mockWords = Arrays.asList("badword1", "badword2");
		when(badwordRepository.findAllNames()).thenReturn(mockWords);

		// Act
		List<String> result = badwordService.getSensitiveWords();

		// Assert
		assertEquals(2, result.size());
		assertEquals("badword1", result.get(0));
		verify(badwordRepository, times(1)).findAllNames();
	}

	@Test
	void testFindBadwordById_WhenFound() {
		// Arrange
		Badword mockBadword = new Badword(1L, "badword1");
		when(badwordRepository.findById(1L)).thenReturn(Optional.of(mockBadword));

		// Act
		Badword result = badwordService.findBadwordById(1L);

		// Assert
		assertNotNull(result);
		assertEquals("badword1", result.getName());
		verify(badwordRepository, times(1)).findById(1L);
	}

	@Test
	void testFindBadwordById_WhenNotFound() {
		// Arrange
		when(badwordRepository.findById(1L)).thenReturn(Optional.empty());

		// Act & Assert
		assertThrows(ResourceNotFoundException.class, () -> badwordService.findBadwordById(1L));
		verify(badwordRepository, times(1)).findById(1L);
	}

	@Test
	void testFindAllBadwords() {
		// Arrange
		List<Badword> mockBadwords = Arrays.asList(new Badword(1L, "badword1"), new Badword(2L, "badword2"));
		when(badwordRepository.findAll()).thenReturn(mockBadwords);

		// Act
		List<Badword> result = badwordService.findAllBadwords();

		// Assert
		assertEquals(2, result.size());
		verify(badwordRepository, times(1)).findAll();
	}

	@Test
	void testSaveBadword() {
		// Arrange
		Badword mockBadword = new Badword(1L, "badword1");
		when(badwordRepository.save(mockBadword)).thenReturn(mockBadword);

		// Act
		Badword result = badwordService.saveBadword(mockBadword);

		// Assert
		assertNotNull(result);
		assertEquals("badword1", result.getName());
		verify(badwordRepository, times(1)).save(mockBadword);
	}

	@Test
	void testUpdateBadword() {
		// Arrange
		Badword existingBadword = new Badword(1L, "oldword");
		Badword updatedBadword = new Badword(1L, "newword");
		when(badwordRepository.findById(1L)).thenReturn(Optional.of(existingBadword));
		when(badwordRepository.save(existingBadword)).thenReturn(existingBadword);

		// Act
		Badword result = badwordService.updateBadword(updatedBadword);

		// Assert
		assertNotNull(result);
		assertEquals("newword", result.getName());
		verify(badwordRepository, times(1)).findById(1L);
		verify(badwordRepository, times(1)).save(existingBadword);
	}

	@Test
	void testDeleteById() {
		// Act
		badwordService.deleteById(1L);

		// Assert
		verify(badwordRepository, times(1)).deleteById(1L);
	}
}
