package hu.ypg.tapse;

import static org.junit.Assert.*;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;

import org.junit.Test;

public class TapseCamTest {
	
	@Test
	public void testParseProcessList(){
		try {
			String s = "abcdefgTESTsdadf";
			BufferedReader input = new BufferedReader(new StringReader(s));
			assertEquals(true, TapseCam.parseProcessList(input,"TEST"));
			assertEquals(false, TapseCam.parseProcessList(input,"NOTTEST"));
			input.close();
		} catch (IOException e) {
			e.printStackTrace();
			fail("Error!");
		}
		
	}

}
