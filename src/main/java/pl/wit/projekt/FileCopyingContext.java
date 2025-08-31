package pl.wit.projekt;

import java.text.SimpleDateFormat;
import java.util.Map;
import java.util.concurrent.CountDownLatch;

import org.apache.log4j.Logger;

/**
 * Klasa będąca kontenerem dla parametrów potrzebnych podczas wykonywania kopiowania plików za pomocą klasy FileCopyingTask
 * @author Artur
 *
 */
public class FileCopyingContext {
	// mapa ilości plików w podkatalogach katalogu docelowego
	private final Map<String, Integer> filesMap;
	// schemat formatu daty
	private final SimpleDateFormat dateFormater;
	// synchronizowany licznik zadań, które jeszcze nie zostały wykonane
	private final CountDownLatch finishedTasksCounter;
	// logger
	private final Logger logger;
	
	/**
	 * Konstruktor 4-argumentowy ustawiajacy mapę katalogów z ilością plików, formater daty, licznik zadań oraz logger. 
	 * Wszystkie parametry poza parametrem loggera nie mogą mieć wartości null.
	 * @param filesMap mapa, której kluczami są podkatalogi a wartościami ilość plików w nich zawartych
	 * @param dateFormater formater daty używany przy określaniu nazw podkatalogów
	 * @param finishedTasksCounter synchornizowany licznik zadań pozostałych do wykonania
	 * @param logger logger wykorzystywany do logowania komunikatów podczas kopiowania plików
	 * @throws FileCopyingException jeśli mapa, formater lub licznik mają wartość null
	 */
	public FileCopyingContext(Map<String, Integer> filesMap, SimpleDateFormat dateFormater, CountDownLatch finishedTasksCounter, 
			Logger logger) throws FileCopyingException{
		if(filesMap == null || dateFormater == null || finishedTasksCounter == null)
			throw new FileCopyingException("One of the required FileCopyingContext's parameter is null");
		this.filesMap = filesMap;
		this.dateFormater = dateFormater;
		this.finishedTasksCounter = finishedTasksCounter;
		this.logger = logger;
	}

	////////////////////////////////
	// Getters and Setters
	////////////////////////////////
	/**
	 * Metoda zwracająca przechowywaną mapę ilości plików w podkatalogach katalogu docelowego
	 * @return mapę ilości plików w podkatalogach
	 */
	public Map<String, Integer> getFilesMap() {
		return filesMap;
	}
	/**
	 * Metoda zwracająca przechowywany schemat formatu daty
	 * @return formater daty
	 */
	public SimpleDateFormat getDateFormater() {
		return dateFormater;
	}
	/**
	 * Metoda zwracająca przechowywany synchronizowany licznik zadań do wykonania
	 * @return licznik zadań do wykonania
	 */
	public CountDownLatch getFinishedTasksCounter() {
		return finishedTasksCounter;
	}
	/**
	 * Metoda zwracająca przechowywany logger
	 * @return logger
	 */
	public Logger getLogger() {
		return logger;
	}

}
