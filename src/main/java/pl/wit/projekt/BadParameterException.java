package pl.wit.projekt;

/**
 * Klasa wyjątku reprezentująca wyjątki powstałe przy podaniu niepoprawnych parametrów wywołania aplikacji.
 * @author Artur
 *
 */
public class BadParameterException extends Exception {
	// id
	private static final long serialVersionUID = -4177724871124996778L;

	/**
	 * Konsturktor 1-argumentowy ustawiający wiadomość wyjątku
	 * @param msg komunikat
	 */
	public BadParameterException(String msg) {
		super(msg);
	}
	
	/**
	 * Konstruktor 2-argumentowy ustawiający wiadomość wyjątku i wyjątek będący przyczyną powstania tego wyjątku
	 * @param msg komunikat
	 * @param e wyjątek będący powodem dla rzucenia tego wyjątku
	 */
	public BadParameterException(String msg, Exception e) {
		super(msg, e);
	}

}
