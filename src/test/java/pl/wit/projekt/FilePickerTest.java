package pl.wit.projekt;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.fail;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Collections;
import java.util.Set;
import java.util.stream.Stream;

import org.junit.Test;

/**
 * Klasa testowa dla klasy pl.wit.projekt.FilePicker
 * @author Artur
 *
 */
public class FilePickerTest {
	// ścieżka do folderu źródłowego dla testów
	private Path testFolderPath = Path.of("src/test/resources/source-test-folder");
	// testowa tablica formatów
	private String[] formatsArray = new String[] {"jpg", "jpeg"};
	
	/**
	 * Test konstuktora bezparametrowego
	 */
	@Test
	public void creatingFilePickerWithoutArgumentsTest() {
		FilePicker filePicker = new FilePicker();
		
		assertNotNull(filePicker);
		assertEquals(Collections.EMPTY_SET, filePicker.getSelectedFiles());
		assertNull(filePicker.getFormatsArray());
		assertNull(filePicker.getPath());
	}
	
	/**
	 * Test konstruktora 2-argumentowego (wyszukiwany format i ścieżka)
	 */
	@Test
	public void creatingFilePickerWith2ArgumentsTest() {
		FilePicker filePicker = new FilePicker(formatsArray, testFolderPath);
		
		assertNotNull(filePicker);
		assertEquals(Collections.EMPTY_SET, filePicker.getSelectedFiles());
		assertArrayEquals(formatsArray, filePicker.getFormatsArray());
		assertEquals(testFolderPath, filePicker.getPath());
	}
	
	/**
	 * Test poprawności wyszukiwania plików (przykład: pliki JPG)
	 */
	@Test
	public void selectFilesCorrectExecutionFindJPGTest() {
		FilePicker filePicker = new FilePicker(formatsArray, testFolderPath);
		Set<Path> filesSet = null;
		int numberOfSelectedProperFiles = 0;
		
		try {
			filesSet = filePicker.selectFiles();
		} catch (IOException e) {
			fail("Cannot access the source directory");
		}
		
		try(Stream<Path> st = filesSet.stream()){
			numberOfSelectedProperFiles = (int) st.filter(p -> {
				String pStr = p.toString().toLowerCase();
				return pStr.endsWith(".jpg") || pStr.endsWith(".jpeg");
			}).count();
		}
		
		assertEquals(numberOfSelectedProperFiles, filesSet.size());
		assertEquals(13, filesSet.size());
	}
	
	/**
	 * Test poprawności wyszukiwania plików (przykład: pliki PNG)
	 */
	@Test
	public void selectFilesCorrectExecutionFindPNGTest() {
		FilePicker filePicker = new FilePicker(new String[] {"png"}, testFolderPath);
		Set<Path> filesSet = null;
		int numberOfSelectedProperFiles = 0;
		
		try {
			filesSet = filePicker.selectFiles();
		} catch (IOException e) {
			fail("Cannot access the source directory");
		}
		
		try(Stream<Path> st = filesSet.stream()){
			numberOfSelectedProperFiles = (int) st.filter(p ->  p.toString().toLowerCase().endsWith(".png")).count();
		}
		
		assertEquals(numberOfSelectedProperFiles, filesSet.size());
		assertEquals(3, filesSet.size());
	}
	
	/**
	 * Test poprawności wyszukiwania plików (przykład: pliki TXT)
	 */
	@Test
	public void selectFilesCorrectExecutionFindTXTTest() {
		FilePicker filePicker = new FilePicker(new String[] {"txt"}, testFolderPath);
		Set<Path> filesSet = null;
		int numberOfSelectedProperFiles = 0;
		
		try {
			filesSet = filePicker.selectFiles();
		} catch (IOException e) {
			fail("Cannot access the source directory");
		}
		
		try(Stream<Path> st = filesSet.stream()){
			numberOfSelectedProperFiles = (int) st.filter(p ->  p.toString().toLowerCase().endsWith(".txt")).count();
		}
		
		assertEquals(numberOfSelectedProperFiles, filesSet.size());
		assertEquals(5, filesSet.size());
	}
	
	/**
	 * Test wyszukiwania plików w przypadku podania błędnych argumentów
	 */
	@Test
	public void selectFilesWrongArgumentsTest() {
		FilePicker filePicker1 = new FilePicker(formatsArray, null);
		FilePicker filePicker2 = new FilePicker(null, testFolderPath);
		FilePicker filePicker3 = new FilePicker(new String[] {}, testFolderPath);
		
		Set<Path> result1 = null;
		Set<Path> result2 = null;
		Set<Path> result3 = null;
		
		try {
			result1 = filePicker1.selectFiles();
			result2 = filePicker2.selectFiles();
			result3 = filePicker3.selectFiles();
		} catch (IOException e) {
			fail("Cannot access the source directory");
		}
		
		assertEquals(Collections.EMPTY_SET, result1);
		assertEquals(Collections.EMPTY_SET, result2);
		assertEquals(Collections.EMPTY_SET, result3);
	}
	
	/**
	 * Test pobierania zbioru znalezionych ścieżek (niemodyfikowalność zwracanego zbioru)
	 */
	@Test(expected = UnsupportedOperationException.class)
	public void getSelectedFilesTest() {
		FilePicker filePicker = new FilePicker();
		
		Set<Path> filesSet = filePicker.getSelectedFiles();
		filesSet.add(Path.of("src/test"));
	}
	
	/**
	 * Test pobierania tablicy formatów (tożsamość tablicy)
	 */
	@Test
	public void getFormatsArrayTest() {
		FilePicker filePicker = new FilePicker(formatsArray, testFolderPath);
		
		String[] formsArr = filePicker.getFormatsArray();
		
		assertArrayEquals(formatsArray, formsArr);
		assertNotSame(formatsArray, formsArr);
	}

}
