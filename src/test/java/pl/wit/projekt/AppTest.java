package pl.wit.projekt;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.CharArrayWriter;
import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.stream.Stream;

import org.apache.log4j.FileAppender;
import org.apache.log4j.Level;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.apache.log4j.PatternLayout;
import org.apache.log4j.WriterAppender;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * Klasa testowa dla klasy pl.wit.projekt.App
 */
public class AppTest {
	// ścieżka do katalogu źródłowego
	private Path srcPath;
	// ścieżka do katalogu docelowego
	private Path dstPath;
	// logger
	private static final Logger appLogger = LogManager.getLogger(App.class.getName());
	// appender dla raportu z ostatnich testów
	private static FileAppender fileReportAppender;
	// appender do przechwytywania logów
	private WriterAppender writerAppender;
	// strumień wyjściowy do przechwytywania logów
	private CharArrayWriter outCharArray;
	
	@BeforeClass
	public static void setUpBeforeClass() {
		try {
			fileReportAppender = new FileAppender(
					new PatternLayout("%-5p [%t]: %m%n"), "src/test/resources/AppTest-last-report.log", false);
		} catch (IOException e) {
			e.printStackTrace();
		}
		appLogger.addAppender(fileReportAppender);
		appLogger.setAdditivity(false);
	}
	
	@AfterClass
	public static void tearDownAfterClass() {
		appLogger.removeAppender(fileReportAppender);
		fileReportAppender.close();
		appLogger.setAdditivity(true);
	}
	
	@Before
	public void setUp() {
		srcPath = Path.of("src/test/resources/source-test-folder");
		dstPath = Path.of("src/test/resources/target-test-folder");
		
		try {
			Files.createDirectories(Path.of("src/test/resources/target-test-folder"));
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		outCharArray = new CharArrayWriter();
		writerAppender = new WriterAppender(new PatternLayout("%-5p [%t]: %m%n"), outCharArray);
		writerAppender.setThreshold(Level.ERROR);
		appLogger.addAppender(writerAppender);
	}
	
	@After
	public void tearDown() {
		appLogger.removeAppender(writerAppender);
		outCharArray.close();
		
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
	 * Test poprawności działania aplikacji
	 */
	@Test
	public void correctRunTest() {
		appLogger.debug("-------------------------------------------------");
		appLogger.debug("Test correctRunTest execution");
		
		App.main(new String[] {srcPath.toString(), dstPath.toString(), "8"});
		
		int numberOfFiles = countJPGFiles(dstPath);
		assertEquals(15, numberOfFiles);
	}
	
	/**
	 * Test aplikacji w sytuacji gdy podana ścieżka do katalogu źródłowego nie istnieje
	 */
	@Test
	public void runWhenSrcPathDoesNotExistTest() {
		appLogger.debug("-------------------------------------------------");
		appLogger.debug("Test runWhenSrcPathDoesNotExistTest execution");
		
		App.main(new String[] {Path.of(srcPath.toString(),"none-existent-folder").toString(), dstPath.toString(), "8"});
		
		assertTrue(outCharArray.toString().strip().startsWith("ERROR"));
		assertTrue(outCharArray.toString().strip().endsWith("Path parameter cannot be resolved"));
	}
	
	/**
	 * Test aplikacji w sytuacji gdy  katalog źródłowy nie jest katalogiem
	 */
	@Test
	public void runWhenSrcPathIsNotFolderTest() {
		appLogger.debug("-------------------------------------------------");
		appLogger.debug("Test runWhenSrcPathIsNotFolderTest execution");
		
		App.main(new String[] {Path.of(srcPath.toString(),"img-225x300.png").toString(), dstPath.toString(), "8"});
		
		assertTrue(outCharArray.toString().strip().startsWith("ERROR"));
		assertTrue(outCharArray.toString().strip().endsWith("Path parameter is not a directory"));
	}
	
	/**
	 * Test aplikacji w sytuacji gdy katalog źródłowy jest pusty
	 */
	@Test
	public void runWhenSrcPathIsEmptyTest() {
		Path srcPath = Path.of("src/test/resources/tmp-test-folder");
		try {
			Files.createDirectories(srcPath);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		App.main(new String[] {srcPath.toString(), dstPath.toString(), "8"});
		
		int numberOfFiles = countJPGFiles(dstPath);
		assertEquals(0, numberOfFiles);
		
		try {
			Files.delete(srcPath);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}
	
	/**
	 * Test aplikacji w sytuacji gdy podana ścieżka do katalogu docelowego nie istnieje
	 */
	@Test
	public void runWhenDstPathDoesNotExistTest() {
		appLogger.debug("-------------------------------------------------");
		appLogger.debug("Test runWhenDstPathDoesNotExistTest execution");
		Path dstPath = Path.of(this.dstPath.toString(),"none-existent-folder");
		
		App.main(new String[] {srcPath.toString(), dstPath.toString(), "8"});
		
		int numberOfFiles = countJPGFiles(dstPath);
		assertEquals(15, numberOfFiles);
	}
	
	/**
	 * Test aplikacji w sytuacji gdy  katalog docelowy nie jest katalogiem
	 */
	@Test
	public void runWhenDstPathIsNotFolderTest() {
		try {
			Files.createFile(Path.of(dstPath.toString(), "tmp.txt"));
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		appLogger.debug("-------------------------------------------------");
		appLogger.debug("Test runWhenDstPathIsNotFolderTest execution");
		
		App.main(new String[] {srcPath.toString(), Path.of(dstPath.toString(), "tmp.txt").toString(), "8"});
		
		assertTrue(outCharArray.toString().strip().startsWith("ERROR"));
		assertTrue(outCharArray.toString().strip().endsWith("Path parameter is not a directory"));
	}
	
	/**
	 * Test aplikacji w sytuacji gdy katalog docelowy nie jest pusty
	 */
	@Test
	public void runWhenDstPathIsNotEmptyTest() {
		try {
			Files.createFile(Path.of(dstPath.toString(), "tmp.txt"));
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		appLogger.debug("-------------------------------------------------");
		appLogger.debug("Test runWhenDstPathIsNotEmptyTest execution");
		
		App.main(new String[] {srcPath.toString(), dstPath.toString(), "8"});
		
		assertTrue(outCharArray.toString().strip().startsWith("ERROR"));
		assertTrue(outCharArray.toString().strip().endsWith("Source directory is not empty. File organizing cannot be performed"));
	}
	
	/**
	 * Test aplikacji w sytuacji gdy parametr ilości wątków nie jest liczbą
	 */
	@Test
	public void runWhenNumOfThreadsIsNotNumberTest() {
		appLogger.debug("-------------------------------------------------");
		appLogger.debug("Test runWhenNumOfThreadsIsNotNumber execution");
		
		App.main(new String[] {srcPath.toString(), dstPath.toString(), "a"});
		
		assertTrue(outCharArray.toString().strip().startsWith("ERROR"));
		assertTrue(outCharArray.toString().strip().endsWith("Third argument must be a number"));
	}
	
	/**
	 * Test aplikacji w sytuacji gdy liczba wątków jest mniejsza lub równa 0
	 */
	@Test
	public void runWhenNumOfThreadsIsWrongNumberTest() {
		appLogger.debug("-------------------------------------------------");
		appLogger.debug("Test runWhenNumOfThreadsIsWrongNumberTest execution");
		
		App.main(new String[] {srcPath.toString(), dstPath.toString(), "0"});
		
		assertTrue(outCharArray.toString().strip().startsWith("ERROR"));
		assertTrue(outCharArray.toString().strip().endsWith("Wrong number of threads"));
	}
	
	private int countJPGFiles(Path path) {
		int numberOfFiles = -1;
		try(Stream<Path> st = Files.walk(path)){
			numberOfFiles = (int) st.filter(p -> {
				String strPath = p.toString().toLowerCase();
				for(String format : new String[] {"jpg", "jpeg", "jpe", "jfif"}) {
					if(strPath.endsWith("." + format))
						return true;
				}
				return false;
			}).count();
		} catch (IOException e) {
			e.printStackTrace();
			fail(e.getMessage());
		}
		return numberOfFiles;
	}
}
