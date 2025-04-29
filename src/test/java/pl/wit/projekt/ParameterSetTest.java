package pl.wit.projekt;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

/**
 * Klasa testowa dla klasy pl.wit.projekt.ParameterSet
 * @author Artur
 *
 */
public class ParameterSetTest {
	
	@Rule
	public ExpectedException expectedExceptionRule = ExpectedException.none();
	
	@BeforeClass
	public static void setUpBeforeClass() {
		try {
			Files.createDirectories(Path.of("src/test/resources/target-test-folder"));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	@AfterClass
	public static void tearDownAfterClass() {
		try {
			Files.delete(Path.of("src/test/resources/target-test-folder"));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Test konsturktora 3-argumentowego
	 */
	@Test
	public void creatingParameterSetTest() {
		String srcPath = "src/test/resources/source-test-folder";
		String dstPath = "src/test/resources/target-test-folder";
		byte numOfThreads = (byte)10;
		ParameterSet parameterSet = null;
		
		try {
			parameterSet = new ParameterSet(srcPath, dstPath, numOfThreads);
		} catch (BadParameterException e) {
			fail(e.getMessage());
		}
		
		assertNotNull(parameterSet);
		assertEquals(Path.of(srcPath), parameterSet.getSourcePath());
		assertEquals(Path.of(dstPath), parameterSet.getDestinationPath());
		assertEquals(numOfThreads, parameterSet.getNumOfThreads());
	}
	
	/**
	 * Test konstruktora w przypadku podania nieistniejącej ścieżki źródłowej
	 */
	@Test
	public void creatingParameterSetWithNonexistentSrcPathTest() throws BadParameterException {
		String srcPath = "src/test/resources/source-test-folder-nonexistent";
		String dstPath = "src/test/resources/target-test-folder";
		byte numOfThreads = (byte)10;
		
		@SuppressWarnings("unused")
		ParameterSet parameterSet = null;
		
		expectedExceptionRule.expect(BadParameterException.class);
		expectedExceptionRule.expectMessage("Path parameter cannot be resolved");
		parameterSet = new ParameterSet(srcPath, dstPath, numOfThreads);
	}
	
	/**
	 * Test konstruktora w przypadku podania nieistniejącej ścieżki docelowej
	 */
	@Test
	public void creatingParameterSetWithNonexistentDstPathTest() {
		String srcPath = "src/test/resources/source-test-folder";
		String dstPath = "src/test/resources/target-test-folder-nonexistent";
		byte numOfThreads = (byte)10;
		ParameterSet parameterSet = null;
		
		try {
			parameterSet = new ParameterSet(srcPath, dstPath, numOfThreads);
		} catch (BadParameterException e) {
			fail(e.getMessage());
		}
		
		assertNotNull(parameterSet);
		assertEquals(Path.of(srcPath), parameterSet.getSourcePath());
		assertEquals(Path.of(dstPath), parameterSet.getDestinationPath());
		assertEquals(numOfThreads, parameterSet.getNumOfThreads());
	}
	
	/**
	 * Test konstruktora w przypadku podania niewłaściwej ścieżki
	 */
	@Test
	public void creatingParameterSetWithWrongPathTest() throws BadParameterException {
		String srcPath = "src/test/resources/source-test-folder/img-640x400.jpg";
		String dstPath = "src/test/resources/target-test-folder";
		byte numOfThreads = (byte)10;
		
		@SuppressWarnings("unused")
		ParameterSet parameterSet = null;
		
		expectedExceptionRule.expect(BadParameterException.class);
		expectedExceptionRule.expectMessage("Path parameter is not a directory");
		parameterSet = new ParameterSet(srcPath, dstPath, numOfThreads);
	}
	
	/**
	 * Test konstruktora w przypadku podania ścieżki o wartości null
	 */
	@Test
	public void creatingParameterSetWithNullPathTest() throws BadParameterException {
		String srcPath = "src/test/resources/source-test-folder";
		String dstPath = null;
		byte numOfThreads = (byte)10;
		
		@SuppressWarnings("unused")
		ParameterSet parameterSet = null;
		
		expectedExceptionRule.expect(BadParameterException.class);
		expectedExceptionRule.expectMessage("Path parameter cannot be resolved");
		parameterSet = new ParameterSet(srcPath, dstPath, numOfThreads);
	}
	
	/**
	 * Test konstruktora w przypadku podania niepoprawnej liczby wątków
	 */
	@Test
	public void creatingParameterSetWithWrongNumOfThreadsTest() throws BadParameterException {
		String srcPath = "src/test/resources/source-test-folder";
		String dstPath = "src/test/resources/target-test-folder";
		byte numOfThreads = (byte)0;
		
		@SuppressWarnings("unused")
		ParameterSet parameterSet = null;
		
		expectedExceptionRule.expect(BadParameterException.class);
		expectedExceptionRule.expectMessage("Wrong number of threads");
		parameterSet = new ParameterSet(srcPath, dstPath, numOfThreads);
	}
}
