package pl.wit.projekt;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Set;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.stream.Stream;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

/**
 * Klasa służąca do porządkowania plików. Klasa umożliwia przekopiowanie podanych plików do katalogu docelowego. 
 * Pliki w katalogu docelowym są porządkowane zgodnie z datą ich utworzenia w taki sposób, że wszystkie pliki z tą samą 
 * datą utworzenia trafiają do jednego podkatalogu o takiej nazwie jak ta data utworzenia.
 * @author Artur
 *
 */
public class FileOrganizer {
	// maksymalna ilość wykorzystywanych wątków
	private byte numOfThreads;
	// pula wątków
	private ExecutorService threadPool;
	// schemat formatu daty
	private SimpleDateFormat dateFormater;
	// logger
	private Logger logger = null;
	
	/**
	 * Konstruktor 1-argumentowy określajacy ilość wątków w puli i inicjalizujący pulę wątków oraz format daty używany w nazwach folderów
	 * @param numOfThreads ilość wątków w puli wykorzystywanych do kopiowania plików
	 * @throws IllegalArgumentException jeśli parametr numOfThreads jest mniejszy lub równy zero
	 */
	public FileOrganizer(byte numOfThreads) {
		if(numOfThreads <= 0) 
			throw new IllegalArgumentException("Number of threads cannot be lesser than or equal 0"); 
		this.numOfThreads = numOfThreads;
		this.dateFormater = new SimpleDateFormat("yyyy-MM-dd");
	}
	
	/**
	 * Konstruktor 2-argumentowy przyjmujący jako parametr ilość wątków w puli oraz klasę na rzecz której zostanie pozyskany logger. 
	 * Logger dla przekazanej klasy powinien być uprzednio przygotowany do działania, w przeciwnym przypadku logowanie może 
	 * nie działać poprawnie i powodować błędy.
	 * @param numOfThreads ilość wątków w puli wykorzystywanych do kopiowania plików
	 * @param loggerClass klasa dla które zostanie pozyskany logger używany przy kopiowaniu plików
	 * @throws IllegalArgumentException jeśli parametr numOfThreads jest mniejszy lub równy zero
	 */
	public FileOrganizer(byte numOfThreads, Class<?> loggerClass) {
		this(numOfThreads);
		this.logger = LogManager.getLogger(loggerClass.getName());
	}
	
	/**
	 * Metoda zmieniająca parametr dotyczący maksymalnej ilości wykorzystywanych wątków
	 * @param numOfThreads ilość wątków w puli wykorzystywanych do kopiowania plików
	 * @throws IllegalArgumentException jeśli parametr numOfThreads jest mniejszy lub równy zero
	 */
	public void setNumberOfThread(byte numOfThreads) {
		if(numOfThreads <= 0) 
			throw new IllegalArgumentException("Number of threads cannot be lesser than or equal 0"); 
		this.numOfThreads = numOfThreads;
	}
	
	/**
	 * Metoda ustawiająca nową wartość dla parametru dotyczącego loggera
	 * @param loggerClass klasa dla które zostanie pozyskany logger używany przy kopiowaniu plików
	 */
	public void setLoggerForClass(Class<?> loggerClass) {
		if(loggerClass == null) return;
		this.logger = LogManager.getLogger(loggerClass.getName());
	}
	
	/**
	 * Metoda organizująca wybrane pliki poprzez przekopiowanie ich do katalogu docelowego. Metoda porządkuje przekopiowywane pliki 
	 * w taki sposób, że umieszcza je w podkatalogach katalogu docelowego o nazwach takich jak data utworzenia pliku. 
	 * Pliki nazywane są kolejnymi numerami. Przekopiowywanie realizowane jest za pomocą puli wątków. Aby wykonanie porządkowania 
	 * plików zostało wykonane to katalog docelowy musi być pusty. Jeśli katalog docelowy nie istnieje to zostanie utworzony.
	 * @param dst ścieżka do katalogu docelowego
	 * @param files zbiór plików do przekopiowania i uporządkowania
	 * @throws IOException jeśli ścieżka docelowa nie prowadzi do katalogu lub nie można uzyskać dostępu do katalogu
	 * @throws BadParameterException jeśli katalog docelowy nie jest pusty
	 * @throws FileCopyingException jeśli wątek zostanie przerwany podczas oczekiwania na zakończenie zadań kopiowania
	 * @throws NullPointerException jeśli którykolwiek z argumentów ma wartość null
	 */
	public void organize(Path dst, Set<Path> files) throws IOException, BadParameterException, FileCopyingException {
		if(Files.notExists(dst)) {
			Files.createDirectories(dst);
		} else {
			Stream<Path> st = Files.list(dst);
			long numFiles = st.count();
			st.close();
			if(numFiles > 0) 
				throw new BadParameterException("Source directory is not empty. File organizing cannot be performed");
		}
		
		this.threadPool = Executors.newFixedThreadPool(numOfThreads);
		CountDownLatch finishedTasksCounter = new CountDownLatch(files.size());
		FileCopyingContext fileCopyingContext = new FileCopyingContext(new HashMap<>(), dateFormater, finishedTasksCounter, logger);
		
		for(Path file : files)
			this.threadPool.execute(new FileCopyingTask(file, dst, fileCopyingContext));
		
		try {
			finishedTasksCounter.await((int) Math.ceil(files.size() / 4), TimeUnit.MINUTES);
		} catch (InterruptedException e) {
			throw new FileCopyingException("Main thread was interrupted during waiting for finishing of copying tasks.", e);
		}
		threadPool.shutdown();
	}
}
