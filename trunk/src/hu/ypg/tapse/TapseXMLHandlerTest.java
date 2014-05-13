package hu.ypg.tapse;

import static org.junit.Assert.*;

import org.junit.Test;

public class TapseXMLHandlerTest {

	@Test
	public void testBoolToString(){
		assertEquals("true",TapseXMLHandler.boolToString(true));
		assertEquals("false",TapseXMLHandler.boolToString(false));
	}

	@Test
	public void testStringToBool(){
		assertEquals(true, TapseXMLHandler.stringToBool("true"));
		assertEquals(true, TapseXMLHandler.stringToBool(""));
		assertEquals(false, TapseXMLHandler.stringToBool("false"));
	}

	@Test
	public void testTranslatePath(){
		assertEquals("/asd/asder", TapseXMLHandler.translatePath("\\asd\\asder"));
		assertEquals("/asd/asder", TapseXMLHandler.translatePath("\\\\asd\\\\asder"));
		assertEquals("/asd/asder", TapseXMLHandler.translatePath("\\asd\\\\asder"));
		assertEquals("//////asder", TapseXMLHandler.translatePath("//\\//\\asder") );
	}

}
