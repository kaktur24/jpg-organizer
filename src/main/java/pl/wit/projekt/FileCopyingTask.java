package pl.wit.projekt;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.Date;
import java.util.Map;

/**
 * Klasa implementująca interfejs Runnable służąca do kopiowania pliku z określonego miejsca źródłowego do określonego katalogu docelowego. 
 * Plik jest przekopiowywany do podkatalog katalogu docelowego, gdzie nazwa tego podkatalogu odpowiada dacie utworzenia pliku. 
 * Klasa samodzielnie w razie potrzeby tworzy podkatalog oraz ustala nazwę nowo utworzonego po skopiowaniu pliku zgodnie 
 * z numeracją przekazaną jej w mapie plików.  
 * @author Artur
 *
 */
public class FileCopyingTask implements Runnable {
	// ścieżka do pliku źródłowego
	private Path sourcePath;
	// ścieżka do katalogu docelowego
	private Path destinationPath;
	// kontekst kopiowania zawierający dane na temat mapy plików, formatera daty i licznika zadań
	private FileCopyingContext fileCopyContext;
	
	/**
	 * Konsturktor 3-argumentowy przyjmujący ścieżkę do kopiowanego pliku i ścieżkę do katalogu docelowego oraz 
	 * obiekt kontekstu kopiowania zawierający potrzebne dane do przeprowadzenia kopiowania plików w sposób uporządkowany.
	 * @param sourcePath ścieżka do kopiowanego pliku
	 * @param destinationPath ścieżka do katalogu docelowego
	 * @param fileCopyContext kontekst kopiowania
	 * @throws NullPointerException jeśli któryś z argumentów ma wartość null
	 */
	public FileCopyingTask(Path  sourcePath, Path  destinationPath, FileCopyingContext fileCopyContext) {
		if(sourcePath == null || destinationPath == null || fileCopyContext == null) 
			throw new NullPointerException("Argument cannot be null");
		this.sourcePath = sourcePath;
		this.destinationPath = destinationPath;
		this.fileCopyContext = fileCopyContext;
	}
	
	/**
	 * Metoda kopiująca plik w sposób buforowany z buforem wynoszącym 1MB. Metoda ustala nazwę nowo utworzonego po skopiowaniu pliku zgodnie 
	 * z licznikiem zawartym w mapie. Plik jest przekopiowywany do odpowiedniego podkatalogu (który w razie potrzeby jest tworzony) 
	 * w katalogu docelowym.
	 */
	@Override
	public void run() {
		boolean isLoggerSetted = fileCopyContext.getLogger() != null;
		Path destinationPath = this.destinationPath; 
		BasicFileAttributes fileAttr = null;
		try {
			fileAttr = Files.readAttributes(sourcePath, BasicFileAttributes.class);
		} catch (IOException e) {
			fileCopyContext.getFinishedTasksCounter().countDown();
			if(isLoggerSetted) {
				fileCopyContext.getLogger().error("Cannot read  file attributes");
				fileCopyContext.getLogger().debug("Cannot read  file attributes", new FileCopyingException("File attribute issue", e));
			}
			return;
		}
		
		Date creationDate = new Date(fileAttr.creationTime().toMillis());
		String directoryName = this.fileCopyContext.getDateFormater().format(creationDate);
		destinationPath = Path.of(destinationPath.toString(), directoryName);
		
		Map<String, Integer> filesMap = this.fileCopyContext.getFilesMap();
		Integer fileNum;
		String dstFileName;
		synchronized(filesMap) {
			if(!filesMap.containsKey(directoryName)) {
				try {
					Files.createDirectories(destinationPath);
				} catch (IOException e) {
					fileCopyContext.getFinishedTasksCounter().countDown();
					if(isLoggerSetted) {
						fileCopyContext.getLogger().error("Cannot create a subdirectory");
						fileCopyContext.getLogger().debug("Cannot create a subdirectory", new FileCopyingException("Creating directory issue", e));
					}	
					return;
				}
				filesMap.put(directoryName, Integer.valueOf(1));
			}
			
			fileNum = filesMap.put(directoryName, filesMap.get(directoryName) + 1);
		}
		
		dstFileName = fileNum.toString() + this.sourcePath.toString().substring(this.sourcePath.toString().lastIndexOf("."));
		destinationPath = Path.of(destinationPath.toString(), dstFileName);
		
		
		try(FileInputStream input = new FileInputStream(sourcePath.toString());
			FileOutputStream output = new FileOutputStream(destinationPath.toString())) {
			byte[] buff = new byte[1024 * 1024];
			int length;
			while((length = input.read(buff)) != -1)
				output.write(buff, 0, length);
			
		} catch (IOException e) {
			fileCopyContext.getFinishedTasksCounter().countDown();
			if(isLoggerSetted) {
				fileCopyContext.getLogger().error("Copying file process faild");
				fileCopyContext.getLogger().debug("Copying file process faild", new FileCopyingException("File copying issue", e));
			}
			return;
		}
		
		fileCopyContext.getFinishedTasksCounter().countDown();
		if(isLoggerSetted)
			fileCopyContext.getLogger().info(
					"File " + sourcePath.toString() + " was copied successfully to folder " + directoryName + " with name: " + dstFileName);
	}

}
