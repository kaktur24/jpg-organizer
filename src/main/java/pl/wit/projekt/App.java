package pl.wit.projekt;

import java.io.IOException;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.apache.log4j.xml.DOMConfigurator;

/**
 * Klasa główna aplikacji służącej do organizowania plików zgodnie z ich datą utworzenia.<br>
 * Argumenty: srcPath, dstPath, numberOfThreads
 * @author Artur
 */
public class App {
	// zestaw formatów plików do wyszukiwania
	private static final String[] FORMATS = new String[] {"jpg", "jpeg", "jpe", "jfif"};
	// logger
	private static final Logger logger = LogManager.getLogger(App.class.getName());
	// konfiguracja loggera
	static {
		DOMConfigurator.configure("src/main/resources/log4j.xml");
	}
	
	/**
	 * Główna metoda aplikacji
	 * @param args zestwa argumentów dla aplikacji
	 */
    public static void main(String[] args) {
    	if(args.length < 3) {
    		logger.error("Passed arguments set is wrong. Missing arguments.");
    		return;
    	}
    	
    	ParameterSet paramSet = null;
        try {
			paramSet = new ParameterSet(args[0], args[1], Byte.parseByte(args[2]));
		} catch (BadParameterException e) {
			logger.error(e.getMessage());
			logger.debug(e, e);
			return;
		} catch (NumberFormatException e) {
			logger.error("Third argument must be a number");
			logger.debug(e, e);
			return;
		}
        
        FilePicker filePicker = new FilePicker(FORMATS, paramSet.getSourcePath());
        FileOrganizer fileOrganizer = new FileOrganizer(paramSet.getNumOfThreads(), App.class);
        
        try {
        	fileOrganizer.organize(paramSet.getDestinationPath(), filePicker.selectFiles());
        	
        } catch(IOException | BadParameterException e) {
        	logger.error(e.getMessage());
			logger.debug(e, e);
        	return;
        	
        } catch(FileCopyingException e) {
        	logger.warn(e.getMessage());
			logger.debug(e, e);
        	return;
        }
    }
}
