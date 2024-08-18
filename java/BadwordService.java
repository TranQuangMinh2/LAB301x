package com.trnqngmnh.library;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class BadwordService {
	@Autowired
	private BadwordRepository badwordRepository;
//	private final BadwordRepository badwordRepository;

	public List<String> getSensitiveWords() {
		return badwordRepository.findAllNames();
	}

	public BadwordService(BadwordRepository badwordRepository) {
		this.badwordRepository = badwordRepository;
	}

	public Badword findBadwordById(Long id) {
		return badwordRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Badword not found"));
	}

	public List<Badword> findAllBadwords() {
		// TODO Auto-generated method stub
		return badwordRepository.findAll();
	}

	public Badword saveBadword(Badword badword) {
		// TODO Auto-generated method stub
		return badwordRepository.save(badword);

	}

	public Badword updateBadword(Badword badword) {
		Badword existingBadword = badwordRepository.findById(badword.getId()).orElse(null);
		existingBadword.setName(badword.getName());
		return badwordRepository.save(existingBadword);
	}

	public void deleteById(Long id) {
		badwordRepository.deleteById(id);
	}
}