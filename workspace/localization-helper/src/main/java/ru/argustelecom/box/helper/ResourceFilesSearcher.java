package ru.argustelecom.box.helper;

import java.io.IOException;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.PathMatcher;
import java.util.ArrayList;
import java.util.List;

public class ResourceFilesSearcher {

	public List<Path> find(Path startDir) {
		List<Path> result = new ArrayList<>();
		result.addAll(find(startDir, "*Bundle*.{properties}"));
		result.addAll(find(startDir, "Messages*.{properties}"));
		return result;
	}

	private List<Path> find(Path startDir, String pattern) {
		FileSystem fs = FileSystems.getDefault();
		final PathMatcher matcher = fs.getPathMatcher("glob:" + pattern);
		ResourceFileVisitor visitor = new ResourceFileVisitor(matcher);
		try {
			Files.walkFileTree(startDir, visitor);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}

		return visitor.getResourceFiles();
	}
}
