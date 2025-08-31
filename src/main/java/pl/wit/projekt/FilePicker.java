package pl.wit.projekt;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Klasa służąca do wybierania z określonego katalogu plików o wybranych foramtach. 
 * Przeszukuje katalog źródłowy rekursywanie wybierając wszystkie pliki o żądanym formacie i 
 * tworzy zbiór ścieżek do tych plików.
 * @author Artur
 *
 */
public class FilePicker {
	// zbiór ścieżek do wybranych plików 
	private Set<Path> selectedFiles = null;
	// tablica wyszukiwanych formatów plików
	private String[] formatsArray = null;
	// ścieżka do przeszukiwanego katalogu
	private Path path = null;
	
	/**
	 * Konstruktor bezparametrowy inicjalizujący pusty obiekt
	 */
	public FilePicker() {
		this.selectedFiles = new HashSet<>();
	}
	
	/**
	 * Konsturktor 2-argumentowy przyjmujący listę żądanych formatów plików w postaci tablicy Stringów 
	 * oraz ścieżkę do przeszukiwanego katalogu
	 * @param formatsArray tablica formatów plików podanych w postaci łańcuchów znaków
	 * @param path ścieżka do katalogu w którym ma być przeprowadzone wyszukiwanie
	 */
	public FilePicker(String[] formatsArray, Path path) {
		this();
		this.formatsArray = formatsArray;
		this.path = path;
	}
	
	/**
	 * Metoda wyszukująca w katalogu pliki o żądanym formacie. Zwraca zbiór ścieżek do znalezionych plików. 
	 * Jeśli katalog docelowy do przeszukiwania lub lista formatów plików nie zostały wcześniej ustawione to metoda zwróci pusty zbiór.
	 * @return zbiór ścieżek do plików o żądanym formacie
	 * @throws IOException jeśli nie można uzyskać dostępu do podanej w ścieżce lokalizacji
	 */
	public Set<Path> selectFiles() throws IOException{
		Set<Path> tmpRes = new HashSet<>();
		if(formatsArray != null && formatsArray.length != 0 && path != null) {
			try(Stream<Path> filesStream = Files.walk(path)){
				tmpRes.addAll(filesStream.filter((p) -> {
					for(String format : formatsArray)
						if(p.toString().endsWith("." + format.toLowerCase()) || p.toString().endsWith("." + format.toUpperCase())) 
							return true;
					return false;
				}).collect(Collectors.toSet()));
			}
		}
		this.selectedFiles = tmpRes;
		return getSelectedFiles();
	}

	/////////////////////////////////
	//  Gettery i Settery
	/////////////////////////////////
	/**
	 * Metoda zwracająca uniemodyfikowalny zbiór ścieżek do znalezionych plików 
	 * @return zbiór ścieżek do wybranych plików
	 */
	public Set<Path> getSelectedFiles() {
		return Collections.unmodifiableSet(selectedFiles);
	}
	/**
	 * Metoda zwracająca tablicę aktualnie ustawionych formatów plików wykorzystywanych przy wyszukiwaniu plików
	 * @return tablicę formatów plików
	 */
	public String[] getFormatsArray() {
		return formatsArray == null ? null : Arrays.copyOf(formatsArray, formatsArray.length);
	}
	/**
	 * Metoda zwracająca aktualnie ustawioną ścieżkę do przeszukiwania
	 * @return ścieżkę do katalogu w którym są wyszukiwane pliki
	 */
	public Path getPath() {
		return path;
	}
	/**
	 * Metoda ustawiająca nową wartość dla tablicy wyszukiwanych formatów
	 * @param formatsArray nowa tablica formatów
	 */
	public void setFormatsArray(String[] formatsArray) {
		this.formatsArray = formatsArray;
		this.selectedFiles = new HashSet<>();
	}
	/**
	 * Metoda ustawiająca nową wartość ścieżki do katalogu dla wyszukiwania
	 * @param path nowa ścieżka do wyszukiwania plików
	 */
	public void setPath(Path path) {
		this.path = path;
		this.selectedFiles = new HashSet<>();
	}

}
