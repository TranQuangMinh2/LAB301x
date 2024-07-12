/*package com.trnqngmnh.library;

import java.io.IOException;
import java.sql.SQLException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class BackupController {

	@Autowired
	private BackupService backupService;
	@Autowired
	private RestoreService restoreService;

	@GetMapping("/backup")
	public String showBackupForm(Model model) {
		return "admin/backup";
	}

	@PostMapping("/backup")
	public String backupDatabase(@RequestParam String username, @RequestParam String password,
			@RequestParam String database, @RequestParam String backupFilePath, Model model) throws IOException {
		try {
			backupService.backupDatabase(username, password, database, backupFilePath);
			model.addAttribute("message", "Backup successful.");
		} catch (SQLException e) {
			e.printStackTrace();
			model.addAttribute("message", "Backup failed: " + e.getMessage());
		}
		return "admin/backup";
	}

	@PostMapping("/restore")
	public String restoreDatabase(@RequestParam String username, @RequestParam String password,
			@RequestParam String database, @RequestParam String backupFilePath, Model model) {
		try {
			restoreService.restoreDatabase(username, password, database, backupFilePath);
			model.addAttribute("message", "Restore successful.");
		} catch (IOException | SQLException e) {
			e.printStackTrace();
			model.addAttribute("message", "Restore failed: " + e.getMessage());
		}
		return "admin/backup";
	}
}*/

package com.trnqngmnh.library;

import java.sql.SQLException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class BackupController {

	@Autowired
	private BackupService backupService;
	@Autowired
	private RestoreService restoreService;

	@GetMapping("/backup")
	public String showBackupForm(Model model) {
		return "admin/backup";
	}

	@PostMapping("/backup")
	public String backupDatabase(@RequestParam String username, @RequestParam String password,
			@RequestParam String database, @RequestParam String tableName, @RequestParam String backupFilePath,
			Model model) {
		try {
			backupService.backupDatabase(username, password, database, tableName, backupFilePath);
			model.addAttribute("message", "Backup completed successfully.");
		} catch (SQLException e) {
			model.addAttribute("message", "Backup failed: " + e.getMessage());
		}
		return "admin/backup";
	}

	@PostMapping("/restore")
	public String restoreDatabase(@RequestParam String username, @RequestParam String password,
			@RequestParam String database, @RequestParam String tableName, @RequestParam String backupFilePath,
			Model model) {
		try {
			restoreService.restoreDatabase(username, password, database, tableName, backupFilePath);
			model.addAttribute("message", "Restore successful.");
		} catch (SQLException e) {
			e.printStackTrace();
			model.addAttribute("message", "Restore failed: " + e.getMessage());
		}
		return "admin/backup";
	}
}
