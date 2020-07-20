package com.schwab;

import static java.nio.file.StandardWatchEventKinds.ENTRY_CREATE;
import static java.nio.file.StandardWatchEventKinds.ENTRY_DELETE;
import static java.nio.file.StandardWatchEventKinds.ENTRY_MODIFY;

import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.WatchEvent;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Java8WatchServiceExample {

	private final WatchService watcher;
	private final Map<WatchKey, Path> keys;
	private static List<String> forms = null;

	/**
	 * Creates a WatchService and registers the given directory
	 */
	Java8WatchServiceExample(Path dir) throws IOException {
		this.watcher = FileSystems.getDefault().newWatchService();
		this.keys = new HashMap<WatchKey, Path>();
		forms = new ArrayList<String>();
		walkAndRegisterDirectories(dir);
	}

	/**
	 * Register the given directory with the WatchService; This function will be
	 * called by FileVisitor
	 */
	private void registerDirectory(Path dir) throws IOException {
		WatchKey key = dir.register(watcher, ENTRY_CREATE, ENTRY_DELETE, ENTRY_MODIFY);
		keys.put(key, dir);
	}

	/**
	 * Register the given directory, and all its sub-directories, with the
	 * WatchService.
	 */
	private void walkAndRegisterDirectories(final Path start) throws IOException {
		// register directory and sub-directories
		Files.walkFileTree(start, new SimpleFileVisitor<Path>() {
			@Override
			public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
				registerDirectory(dir);
				return FileVisitResult.CONTINUE;
			}
		});
	}

	/**
	 * Process all events for keys queued to the watcher
	 * 
	 * @throws InterruptedException
	 */
	void processEvents() {
		for (;;) {
			// new Thread().sleep(500);
			// wait for key to be signalled
			WatchKey key;
			try {
				key = watcher.take();
			} catch (InterruptedException x) {
				return;
			}

			Path dir = keys.get(key);
			if (dir == null) {
				System.err.println("WatchKey not recognized!!");
				continue;
			}

			for (WatchEvent<?> event : key.pollEvents()) {
				@SuppressWarnings("rawtypes")
				WatchEvent.Kind kind = event.kind();

				// Context for directory entry event is the file name of entry
				@SuppressWarnings("unchecked")
				Path name = ((WatchEvent<Path>) event).context();
				Path child = dir.resolve(name);
				forms.add(child.getFileName().toString());

				// print out event
				System.out.format("%s: %s\n", event.kind().name(), child);

				// if directory is created, and watching recursively, then register it and its
				// sub-directories
				if (kind == ENTRY_CREATE) {
					try {
						if (Files.isDirectory(child)) {
							walkAndRegisterDirectories(child);
						}
					} catch (IOException x) {
						// do something useful
					}
				}
			}

			// reset key and remove from set if directory no longer accessible
			boolean valid = key.reset();
			if (!valid) {
				keys.remove(key);

				// all directories are inaccessible
				if (keys.isEmpty()) {
					break;
				}
			}
		}
		System.out.println("forms " + forms);
	}

	public static void main(String[] args) throws IOException {
		Path dir = Paths.get("D:\\CS\\Forms\\APP");
		new Java8WatchServiceExample(dir).processEvents();
		
	}
}