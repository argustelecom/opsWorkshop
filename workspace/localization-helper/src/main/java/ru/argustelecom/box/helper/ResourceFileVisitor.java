package ru.argustelecom.box.helper;

import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Path;
import java.nio.file.PathMatcher;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.List;

public class ResourceFileVisitor extends SimpleFileVisitor<Path> {

	private List<Path> resourceFiles = new ArrayList<>();
	private PathMatcher matcher;

	private ResourceFileVisitor() {
		super();
	}

	public ResourceFileVisitor(PathMatcher matcher) {
		this();
		this.matcher = matcher;
	}

	@Override
	public FileVisitResult visitFile(Path file, BasicFileAttributes attribs) {
		Path name = file.getFileName();
		if (matcher.matches(name)) {
			resourceFiles.add(file);
			try {
				System.out.println(String.format("Найден ресурс: '%s'", file.toRealPath()));
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return FileVisitResult.CONTINUE;
	}

	@Override
	public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) {
		if (dir.getFileName().toString().equals("target") || dir.getFileName().toString().equals(".metadata")) {
			return FileVisitResult.SKIP_SUBTREE;
		}

		return FileVisitResult.CONTINUE;
	}

	public List<Path> getResourceFiles() {
		return resourceFiles;
	}

}
