
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.io.File;
import java.io.IOException;

import org.junit.After;
import org.junit.Test;

public class TestMain{
	
	private static final String BASEDIR = "C:\\Users\\abmis\\Documents\\GitHub\\SENGgroupA1";
	
	@Test
	public void testFileFinding() {
		try {
			File[] files = Main.readFilesInDir(BASEDIR + "\\testFiles1");
			assertEquals(files.length, 2);
		} catch (IOException e) {
			e.printStackTrace();
			fail();
		}
	}
	
	
	
	@After
	public void teardown() {
		System.out.println();
		System.out.println();
	}
	
	
	
	@Test
	public void testUnQualifiedName() {
		Main.main(new String[] {BASEDIR + "\\testFiles1", "String"});
	}
	
	
	
	@Test
	public void testQualifiedName() {
		Main.main(new String[] {BASEDIR + "\\testFiles1", "java.lang.String"});
	}
	
	@Test
	public void testUnQualifiedName2() {
		Main.main(new String[] {BASEDIR + "\\testFiles1", "hi"});
	}
	
	@Test
	public void testUnQualifiedName3() {
		Main.main(new String[] {BASEDIR + "\\testFiles1", "Go"});
	}
	
}