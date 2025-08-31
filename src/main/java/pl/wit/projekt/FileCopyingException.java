package pl.wit.projekt;

/**
 * Klasa wyjątku reprezentująca wyjątki powstałe podczas kopiowania plików.
 * @author Artur
 *
 */
public class FileCopyingException extends Exception {
	// id
	private static final long serialVersionUID = 3584594048149339596L;

	/**
	 * Konsturktor 1-argumentowy ustawiający wiadomość wyjątku
	 * @param msg komunikat
	 */
	public FileCopyingException(String msg) {
		super(msg);
	}
	
	/**
	 * Konstruktor 2-argumentowy ustawiający wiadomość wyjątku i wyjątek będący przyczyną powstania tego wyjątku
	 * @param msg komunikat
	 * @param e wyjątek będący powodem dla rzucenia tego wyjątku
	 */
	public FileCopyingException(String msg, Exception e) {
		super(msg, e);
	}

}
