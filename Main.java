package com.schwab;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardWatchEventKinds;
import java.nio.file.WatchEvent;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Main {

	public static void main(String[] args) {

		// define a folder root
		Path myDir = Paths.get("D:\\CS\\Forms\\APP");
		HashMap<String, Set<String>> folderStatus;
		Set<String> modifyList, createList, deleteList;
		try {

			while (true) {
				WatchService watcher = myDir.getFileSystem().newWatchService();
				System.out.println("watching");
				myDir.register(watcher, StandardWatchEventKinds.ENTRY_CREATE, StandardWatchEventKinds.ENTRY_DELETE,
						StandardWatchEventKinds.ENTRY_MODIFY);
				WatchKey watckKey = watcher.take();

				folderStatus = new HashMap<>();
				modifyList = new HashSet<String>();
				createList = new HashSet<String>();
				deleteList = new HashSet<String>();
				List<WatchEvent<?>> events = watckKey.pollEvents();
				System.out.println("count "+events.size());
				for (WatchEvent event : events) {
					if (event.kind() == StandardWatchEventKinds.ENTRY_CREATE) {
						System.out.println("Created: " + event.context().toString());
						createList.add(event.context().toString());
					}
					if (event.kind() == StandardWatchEventKinds.ENTRY_DELETE) {
						System.out.println("Delete: " + event.context().toString());
						deleteList.add(event.context().toString());
					}
					if (event.kind() == StandardWatchEventKinds.ENTRY_MODIFY) {
						System.out.println("Modify: " + event.context().toString());
						modifyList.add(event.context().toString());
					}
				}
				folderStatus.put("CREATED", createList);
				folderStatus.put("DELETED", deleteList);
				folderStatus.put("MODIFIED", modifyList);

				System.out.println("FinalEvent list " + folderStatus.toString());
				 //modifyList.clear(); createList.clear(); deleteList.clear(); 

				
			}
		} catch (Exception e) {
			System.out.println("Error: " + e.toString());
		}
		System.out.println("Done");

	}
}


/*
 * WatchService watcher = myDir.getFileSystem().newWatchService();
 * System.out.println("watching"); myDir.register(watcher,
 * StandardWatchEventKinds.ENTRY_CREATE, StandardWatchEventKinds.ENTRY_DELETE,
 * StandardWatchEventKinds.ENTRY_MODIFY); WatchKey watckKey = watcher.take();
 * folderStatus = new HashMap<>(); modifyList = new HashSet<String>();
 * createList = new HashSet<String>(); deleteList = new HashSet<String>(); while
 * (watckKey != null && watckKey.reset()) { // polled events
 * System.out.println("watchkey "+watckKey); for (final WatchEvent<?> event :
 * watckKey.pollEvents()) {
 * System.out.printf("\nGlobal received: %s, event for file: %s\n",
 * event.kind(), event.context()); switch (event.kind().name()) { case
 * "ENTRY_CREATE": System.out.println("event ENTRY_CREATE " +
 * event.context().toString()); createList.add(event.context().toString());
 * 
 * break; case "ENTRY_DELETE": System.out.println("event ENTRY_DELETE " +
 * event.context().toString()); deleteList.add(event.context().toString());
 * 
 * break; case "ENTRY_MODIFY": System.out.println("event ENTRY_MODIFY " +
 * event.context().toString()); modifyList.add(event.context().toString());
 * 
 * break; default: System.out.println("event other [OVERFLOW] " +
 * event.context().toString()); break; } } folderStatus.put("CREATED",
 * createList); folderStatus.put("DELETED", deleteList);
 * folderStatus.put("MODIFIED", modifyList);
 * System.out.println("FinalEvent list "+folderStatus.toString());
 * //modifyList.clear(); createList.clear(); deleteList.clear(); watckKey =
 * watcher.take(); }
 */