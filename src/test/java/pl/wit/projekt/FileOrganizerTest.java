package pl.wit.projekt;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Stream;

import org.apache.log4j.FileAppender;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.apache.log4j.PatternLayout;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * Klasa testowa dla klasy pl.wit.projekt.FileOrganizerTest
 * @author Artur
 *
 */
public class FileOrganizerTest {
	// ścieżka do katalogu docelowego
	private Path dstPath;
	// zbiór plików do przekopiowania
	private Set<Path> files;
	// logger
	private static final Logger logger = LogManager.getLogger(FileOrganizerTest.class.getName());
	// appender dla raportu z ostatnich testów
	private static FileAppender fileReportAppender;
	
	@BeforeClass
	public static void setUpBeforeClass() {
		try {
			fileReportAppender = new FileAppender(
					new PatternLayout("%-5p [%t]: %m%n"), "src/test/resources/FileOrganizerTest-last-report.log", false);
		} catch (IOException e) {
			e.printStackTrace();
		}
		logger.addAppender(fileReportAppender);
	}
	
	@AfterClass
	public static void tearDownAfterClass() {
		logger.removeAppender(fileReportAppender);
		fileReportAppender.close();
	}
	
	@Before
	public void setUp() {
		dstPath = Path.of("src/test/resources/target-test-folder");
		
		try {
			Files.createDirectories(Path.of("src/test/resources/target-test-folder"));
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		files = new HashSet<>();
		files.add(Path.of("src/test/resources/source-test-folder/img-1800x2500.jpg"));
		files.add(Path.of("src/test/resources/source-test-folder/img-2900x1880.jpg"));
		files.add(Path.of("src/test/resources/source-test-folder/img-640x400.jpg"));
		files.add(Path.of("src/test/resources/source-test-folder/img-850x850.jpg"));
		files.add(Path.of("src/test/resources/source-test-folder/f1/f1-img-1800x2500.jpeg"));
		files.add(Path.of("src/test/resources/source-test-folder/f1/f1-img-640x400.jpg"));
		files.add(Path.of("src/test/resources/source-test-folder/f1/f1-img-850x850.jpg"));
		files.add(Path.of("src/test/resources/source-test-folder/f5/img-2900x1880.JPEG"));
	}
	
	@After
	public void tearDown() {
		if(Files.notExists(Path.of(dstPath.toString()))) return;
		try {
			Files.walkFileTree(Path.of(dstPath.toString()), new SimpleFileVisitor<Path>() {
				@Override
				public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException{
					Files.delete(file);
					return FileVisitResult.CONTINUE;
				}
				
				@Override
				public FileVisitResult postVisitDirectory(Path dir, IOException e) throws IOException {
					if(e == null) {
						Files.delete(dir);
						return FileVisitResult.CONTINUE;
					} throw e;
				}
			});
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Test poprawności porządkowania plików
	 */
	@Test
	public void orgaznizeCorrectExecutionTest() {
		logger.debug("-------------------------------------------------");
		logger.debug("Test orgaznizeCorrectExecutionTest execution");
		FileOrganizer fileOrganizer = new FileOrganizer((byte)8, getClass());
		
		try {
			fileOrganizer.organize(dstPath, files);
		} catch (Exception e) {
			e.printStackTrace();
			fail(e.getMessage());
		}
		
		int numberOfFiles = 0;
		try(Stream<Path> st = Files.walk(dstPath)){
			numberOfFiles = (int) st.filter(p -> {
				String strPath = p.toString().toLowerCase();
				return strPath.endsWith(".jpg") || strPath.endsWith(".jpeg");
			}).count();
		} catch (IOException e) {
			e.printStackTrace();
			fail(e.getMessage());
		}
		
		assertEquals(8, numberOfFiles);
	}
	
	/**
	 * Test porządkowania plików w przypadku przekazania wartości null w parametrze ścieżki docelowej
	 */
	@Test(expected = NullPointerException.class)
	public void organizeDstPathIsNullTest() {
		FileOrganizer fileOrganizer = new FileOrganizer((byte)8, getClass());
		Path dstPath = null;
		
		try {
			fileOrganizer.organize(dstPath, files);
		} catch (IOException | BadParameterException | FileCopyingException e) {
			e.printStackTrace();
			fail(e.getMessage());
		}
		
	}
	
	/**
	 * Test porządkowania plików w przypadku przekazania wartości null w parametrze zbioru plików
	 */
	@Test(expected = NullPointerException.class)
	public void organizeFilesSetIsNullTest() {
		FileOrganizer fileOrganizer = new FileOrganizer((byte)8, getClass());
		Set<Path> files = null;
		
		try {
			fileOrganizer.organize(dstPath, files);
		} catch (IOException | BadParameterException | FileCopyingException e) {
			e.printStackTrace();
			fail(e.getMessage());
		}
		
	}
	
	/**
	 * Test porządkowania plików w przypadku przekazania pustego zbioru plików
	 */
	@Test
	public void organizeFilesSetIsEmptyTest() {
		logger.debug("-------------------------------------------------");
		logger.debug("Test organizeFilesSetIsEmptyTest execution");
		FileOrganizer fileOrganizer = new FileOrganizer((byte)8, getClass());
		Set<Path> files = new HashSet<>();
		
		try {
			fileOrganizer.organize(dstPath, files);
		} catch (Exception e) {
			e.printStackTrace();
			fail(e.getMessage());
		}
		
		try(Stream<Path> st = Files.list(dstPath)){
			assertEquals(0, st.count());
		} catch (IOException e) {
			e.printStackTrace();
			fail(e.getMessage());
		}
	}
	
	/**
	 * Test porządkowania plików w przypadku gdy ścieżka docelowa prowadzi do nieistniejącego folderu
	 */
	@Test
	public void organizeDstPathDoesNotExistTest() {
		logger.debug("-------------------------------------------------");
		logger.debug("Test organizeDstPathDoesNotExistTest execution");
		FileOrganizer fileOrganizer = new FileOrganizer((byte)8, getClass());
		Path dstPath = Path.of(this.dstPath.toString(), "nonexistent-folder-test");
		
		try {
			fileOrganizer.organize(dstPath, files);
		} catch (Exception e) {
			e.printStackTrace();
			fail(e.getMessage());
		}
		
		int numberOfFiles = 0;
		try(Stream<Path> st = Files.walk(dstPath)){
			numberOfFiles = (int) st.filter(p -> {
				String strPath = p.toString().toLowerCase();
				return strPath.endsWith(".jpg") || strPath.endsWith(".jpeg");
			}).count();
		} catch (IOException e) {
			e.printStackTrace();
			fail(e.getMessage());
		}
		
		assertEquals(8, numberOfFiles);
	}
	
	/**
	 * Test porządkowania plików w przypadku gdy ścieżka docelowa nie prowadzi do folderu
	 * @throws IOException
	 */
	@Test(expected = IOException.class)
	public void organizeDstPathIsNotFolderTest() throws IOException {
		FileOrganizer fileOrganizer = new FileOrganizer((byte)8, getClass());
		Path dstPath = Path.of("src/test/resources/source-test-folder/img-640x400.jpg");
		
		try {
			fileOrganizer.organize(dstPath, files);
		} catch (BadParameterException | FileCopyingException e) {
			e.printStackTrace();
			fail(e.getMessage());
		}
	}
	
	/**
	 * Test porządkowania plików w przypadku gdy katalog docelowy nie jest pusty
	 * @throws BadParameterException
	 */
	@Test(expected = BadParameterException.class)
	public void organizeDstPathFolderIsNoNEmptyTest() throws BadParameterException {
		Path auxFile = Path.of(dstPath.toString(), "tmpFile.txt");
		try {
			Files.createFile(auxFile);
		} catch (IOException e) {
			e.printStackTrace();
			fail(e.getMessage());
		}
		
		FileOrganizer fileOrganizer = new FileOrganizer((byte)8, getClass());
		
		try {
			fileOrganizer.organize(dstPath, files);
		} catch (IOException | FileCopyingException e) {
			e.printStackTrace();
			fail(e.getMessage());
		}
		
		try {
			Files.delete(auxFile);
		} catch (IOException e) {
			e.printStackTrace();
			fail(e.getMessage());
		}
	}
	
	/**
	 * Test wydajności porządkowania plików
	 */
	@Test
	public void organizePerormanceTest() {
		try {
			Files.createDirectories(Path.of("src/test/resources/target-test-folder/test-8-thrads"));
			Files.createDirectories(Path.of("src/test/resources/target-test-folder/test-2-thrads"));
		} catch (IOException e) {
			e.printStackTrace();
			fail(e.getMessage());
		}
		
		StringBuilder logMessage = new StringBuilder("Test organizePerormanceTest execution\n");
		FileOrganizer fileOrganizer1 = new FileOrganizer((byte)8);
		FileOrganizer fileOrganizer2 = new FileOrganizer((byte)2);
		
		long timeStart;
		long timeEnd;
		try {
			timeStart = System.currentTimeMillis();
			fileOrganizer1.organize(Path.of(dstPath.toString(), "test-8-thrads"), files);
			timeEnd = System.currentTimeMillis();
			logMessage.append("8 threads in pool - time : " + (timeEnd - timeStart) + "\n");
			
			
			timeStart = System.currentTimeMillis();
			fileOrganizer2.organize(Path.of(dstPath.toString(), "test-2-thrads"), files);
			timeEnd = System.currentTimeMillis();
			logMessage.append("2 threads in pool - time : " + (timeEnd - timeStart) + "\n");
		} catch (Exception e) {
			e.printStackTrace();
			fail(e.getMessage());
		}
		
		logger.debug(logMessage.toString());
	}

}
