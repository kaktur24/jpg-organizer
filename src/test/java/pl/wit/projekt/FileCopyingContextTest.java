package pl.wit.projekt;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.fail;

import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.concurrent.CountDownLatch;

import org.apache.log4j.LogManager;
import org.junit.Test;

/**
 * Klasa testowa dla klasy pl.wit.projekt.FileCopyingContext
 * @author Artur
 *
 */
public class FileCopyingContextTest {
	
	/**
	 * Test konstruktora
	 */
	@Test
	public void creatingFileCopyingContextTest() {
		FileCopyingContext fileCopyingContext = null;
		try {
			fileCopyingContext = new FileCopyingContext(new HashMap<String, Integer>(), new SimpleDateFormat(), 
					new CountDownLatch(1), LogManager.getLogger(getClass()));
		} catch (FileCopyingException e) {
			fail(e.getMessage());
		}
		
		assertNotNull(fileCopyingContext);
		assertEquals(new HashMap<String, Integer>(), fileCopyingContext.getFilesMap());
		assertEquals(new SimpleDateFormat(), fileCopyingContext.getDateFormater());
		assertEquals(new CountDownLatch(1).getCount(), fileCopyingContext.getFinishedTasksCounter().getCount());
		assertSame(LogManager.getLogger(getClass()), fileCopyingContext.getLogger());
	}
	
	/**
	 * Test konstruktora w przypadku gdy parametr mapy ma wartość null
	 */
	@Test(expected = FileCopyingException.class)
	public void creatingFileCopyingContextWithNullMapTest() throws FileCopyingException {
		@SuppressWarnings("unused")
		FileCopyingContext fileCopyingContext = new FileCopyingContext(null, new SimpleDateFormat(), new CountDownLatch(1), 
				LogManager.getLogger(getClass()));
	}
	
	/**
	 * Test konstruktora w przypadku gdy parametr formatera daty ma wartość null
	 */
	@Test(expected = FileCopyingException.class)
	public void creatingFileCopyingContextWithNullDateFormaterTest() throws FileCopyingException {
		@SuppressWarnings("unused")
		FileCopyingContext fileCopyingContext = new FileCopyingContext(new HashMap<String, Integer>(), null, new CountDownLatch(1), 
				LogManager.getLogger(getClass()));
	}
	
	/**
	 * Test konstruktora w przypadku gdy parametr licznika zadań ma wartość null
	 */
	@Test(expected = FileCopyingException.class)
	public void creatingFileCopyingContextWithNullCountdownLatchTest() throws FileCopyingException {
		@SuppressWarnings("unused")
		FileCopyingContext fileCopyingContext = new FileCopyingContext(new HashMap<String, Integer>(), new SimpleDateFormat(), null, 
				LogManager.getLogger(getClass()));
	}

	/**
	 * Test konstruktora w przypadku gdy parametr loggera ma wartość null
	 */
	@Test
	public void creatingFileCopyingContextWithNullLoggerTest() {
		try {
			FileCopyingContext fileCopyingContext = 
				new FileCopyingContext(new HashMap<String, Integer>(), new SimpleDateFormat(), new CountDownLatch(1), null);
			assertNull(fileCopyingContext.getLogger());
		} catch (FileCopyingException e) {
			fail(e.getMessage());
		}
	}
}
