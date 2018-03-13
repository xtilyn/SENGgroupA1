
import static org.junit.Assert.fail;

import java.io.IOException;

import org.junit.Test;

public class TestMain{
	
	private static final String BASEDIR = "C:\\Users\\abmis\\Documents\\GitHub\\SENGgroupA1";
	
	@Test
	public void testQualifiedName() {
		try {
			Main.main(new String[] {BASEDIR + "\\testFiles1", "String"});
		} catch (IOException e) {
			fail();
		}
	}
	
	
}