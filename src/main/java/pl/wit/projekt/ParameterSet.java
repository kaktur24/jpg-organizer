package pl.wit.projekt;

import java.nio.file.Files;
import java.nio.file.Path;
/**
 * Klasa reprezentuje zbiór parametrów potrzebnych do wykonania porządkowania plików, takich jak 
 * katalog źródłowy, katalog docelowy i maksymalna liczba wątków jaka może być użyta w procesie porządkowania.
 * @author Artur
 *
 */
public class ParameterSet {
	// ścieżka źródłowa
	private final Path sourcePath;
	// ścieżka docelowa
	private final Path destinationPath;
	// maksymalna liczba wątków
	private final byte numOfThreads;
	
	/**
	 * Konstruktor 3-argumentowy ustawiający ścieżkę do katalogu źródłowego, ścieżkę do katalogu docelowego oraz liczbę wątków
	 * @param sourcePath ścieżka do katalogu źródłowego
	 * @param destinationPath ścieżka do katalogu docelowego
	 * @param numOfThreads ilość wątków w puli
	 * @throws BadParameterException jeśli podane ścieżki są niepoprawne, nie prowadzą do katalogu, ścieżka  źródłowa nie istnieje lub 
	 * jeśli podana liczba wątków jest mniejsza od 0
	 */
	public ParameterSet(String sourcePath, String destinationPath, byte numOfThreads) throws BadParameterException {
		try {
			if(sourcePath == null || destinationPath == null) 
				throw new IllegalArgumentException();
			
			this.sourcePath = Path.of(sourcePath);
			this.destinationPath = Path.of(destinationPath);
			
			if(Files.notExists(this.sourcePath)) 
				throw new IllegalArgumentException();
		}catch(IllegalArgumentException e) {
			throw new BadParameterException("Path parameter cannot be resolved");
		}
		
		if(!Files.isDirectory(this.sourcePath) || (Files.exists(this.destinationPath) && !Files.isDirectory(this.destinationPath)))
			throw new BadParameterException("Path parameter is not a directory");
		
		if(numOfThreads <= 0)
			throw new BadParameterException("Wrong number of threads");

		this.numOfThreads = numOfThreads;
	}

	/////////////////////////////////
	//  Gettery
	/////////////////////////////////
	/**
	 * Metoda zwracająca parametr ścieżki źródłowej
	 * @return ścieżkę źródłową
	 */
	public Path getSourcePath() {
		return sourcePath;
	}
	/**
	 * Metoda zwracająca parametr ścieżki docelowej
	 * @return ścieżkę docelową
	 */
	public Path getDestinationPath() {
		return destinationPath;
	}
	/**
	 * Metoda zwracająca parametr liczby wątków
	 * @return liczbę wątków
	 */
	public byte getNumOfThreads() {
		return numOfThreads;
	}	

}
