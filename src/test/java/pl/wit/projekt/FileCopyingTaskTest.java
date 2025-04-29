package pl.wit.projekt;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.CharArrayWriter;
import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.concurrent.CountDownLatch;

import org.apache.log4j.Level;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.apache.log4j.PatternLayout;
import org.apache.log4j.WriterAppender;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 * Klasa testowa dla klasy pl.wit.projekt.FileCopyingTask
 * @author Artur
 *
 */
public class FileCopyingTaskTest {
	// ścieżka do pliku źródłowego dla testów
	private Path srcPath = Path.of("src/test/resources/source-test-folder/img-640x400.jpg");
	// ścieżka docelowa dla testów
	private Path dstPath = Path.of("src/test/resources");
	// nazwa podkatalogu testowego
	private String directoryName;
	// kontekst kopiowania
	private FileCopyingContext fileCopyingContext;
	// appender do przechwytywania logów
	private WriterAppender writerAppender;
	// strumień wyjściowy do przechwytywania logów
	private CharArrayWriter outCharArray;
	//logger
	private static final Logger logger = LogManager.getLogger(FileCopyingContextTest.class.getName());
	
	@Before
	public void setUp() {
		try {
			fileCopyingContext = new FileCopyingContext(new HashMap<String, Integer>(), new SimpleDateFormat("yyyy-MM-dd"), 
					new CountDownLatch(1), logger);
		} catch (FileCopyingException e) {
			e.printStackTrace();
		}
		
		BasicFileAttributes fileAttr = null;
		try {
			fileAttr = Files.readAttributes(srcPath, BasicFileAttributes.class);
		} catch (IOException e) {
			e.printStackTrace();
		}
		directoryName = fileCopyingContext.getDateFormater().format(new Date(fileAttr.creationTime().toMillis()));
		
		outCharArray = new CharArrayWriter();
		writerAppender = new WriterAppender(new PatternLayout("%-5p [%t]: %m%n"), outCharArray);
		logger.addAppender(writerAppender);
		logger.setAdditivity(false);
	}
	
	@After
	public void tearDown() {
		logger.removeAppender(writerAppender);
		logger.setAdditivity(true);
		outCharArray.close();
		
		if(Files.notExists(Path.of(dstPath.toString(), directoryName))) return;
		try {
			Files.walkFileTree(Path.of(dstPath.toString(), directoryName), new SimpleFileVisitor<Path>() {
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
	 * Test poprawności kopiowania pliku z loggerem
	 */
	@Test
	public void runCorrectWithLoggerTest() {
		if(!Files.exists(srcPath)) 
			fail("Problem with source file has been occurred");
		
		writerAppender.setThreshold(Level.INFO);

		FileCopyingTask fileCopyingTask = new FileCopyingTask(srcPath, dstPath, fileCopyingContext);
		assertNotNull(fileCopyingTask);
		
		fileCopyingTask.run();
		
		String expectedMessage = "File " + srcPath.toString() + " was copied successfully to folder " 
								+ directoryName + " with name: 1.jpg";
		assertTrue(outCharArray.toString().strip().endsWith(expectedMessage));
		assertTrue(Files.exists(Path.of(dstPath.toString(), directoryName, "1.jpg")));
	}
	
	/**
	 * Test poprawności kopiowania pliku bez loggera
	 */
	@Test
	public void runCorrectWithoutLoggerTest() {
		if(!Files.exists(srcPath)) 
			fail("Problem with source file has been occurred");
		
		FileCopyingContext fileCopyingContextWithoutLogger = null;
		try {
			fileCopyingContextWithoutLogger = new FileCopyingContext(new HashMap<String, Integer>(), new SimpleDateFormat("yyyy-MM-dd"), 
					new CountDownLatch(1), null);
		} catch (FileCopyingException e) {
			fail(e.getMessage());
		}
		
		FileCopyingTask fileCopyingTask = new FileCopyingTask(srcPath, dstPath, fileCopyingContextWithoutLogger);
		assertNotNull(fileCopyingTask);
		
		fileCopyingTask.run();
		
		assertTrue(Files.exists(Path.of(dstPath.toString(), directoryName)));
		assertTrue(Files.exists(Path.of(dstPath.toString(), directoryName, "1.jpg")));	
		assertEquals(0, fileCopyingContextWithoutLogger.getFinishedTasksCounter().getCount());
		assertEquals(Integer.valueOf(2), fileCopyingContextWithoutLogger.getFilesMap().get(directoryName));
	}
	
	/**
	 * Test poprawności kopiowania pliku z niepustą mapą plików w kontekście kopiowania
	 */
	@Test
	public void runCorrectWithNonEmptyMapTest() {
		if(!Files.exists(srcPath)) 
			fail("Problem with source file has been occurred");
		
		writerAppender.setThreshold(Level.INFO);
		
		FileCopyingTask fileCopyingTask = new FileCopyingTask(srcPath, dstPath, fileCopyingContext);
		assertNotNull(fileCopyingTask);
		
		fileCopyingTask.run();
		fileCopyingTask.run();
		fileCopyingTask.run();
		
		String expectedMessage = "File " + srcPath.toString() + " was copied successfully to folder " 
								+ directoryName + " with name: 3.jpg";
		assertTrue(outCharArray.toString().strip().endsWith(expectedMessage));
		assertTrue(Files.exists(Path.of(dstPath.toString(), directoryName)));
		assertTrue(Files.exists(Path.of(dstPath.toString(), directoryName, "3.jpg")));
		assertEquals(Integer.valueOf(4), fileCopyingContext.getFilesMap().get(directoryName));
	}
	
	/**
	 * Test kopiowania pliku w przypadku podania błędnej ścieżki
	 */
	@Test
	public void runWrongTest() {
		Path srcPath = Path.of("src/test/resources/source-test-folder/nonexistent-file.jpg");
		if(Files.exists(srcPath)) 
			fail("Problem with source file have been occurred");
		
		writerAppender.setThreshold(Level.ERROR);
		
		FileCopyingTask fileCopyingTask = new FileCopyingTask(srcPath, dstPath, fileCopyingContext);
		assertNotNull(fileCopyingTask);
		
		fileCopyingTask.run();
		
		assertTrue(outCharArray.toString().strip().endsWith("Cannot read  file attributes"));
	}

}
