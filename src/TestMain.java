
import static org.junit.Assert.fail;

import java.io.IOException;

import org.junit.After;
import org.junit.Test;

public class TestMain{
	
	private static final String BASEDIR = "C:\\Users\\abmis\\Documents\\GitHub\\SENGgroupA1";
	
	@After
	public void teardown() {
		System.out.println();
		System.out.println();
	}
	
	@Test
	public void testUnQualifiedName() {
		try {
			Main.main(new String[] {BASEDIR + "\\testFiles1", "String"});
		} catch (IOException e) {
			fail();
		}
	}
	
	
	
	@Test
	public void testQualifiedName() {
		try {
			Main.main(new String[] {BASEDIR + "\\testFiles1", "java.lang.String"});
		} catch (IOException e) {
			fail();
		}
	}
	
	@Test
	public void testUnQualifiedName2() {
		try {
			Main.main(new String[] {BASEDIR + "\\testFiles1", "hi"});
		} catch (IOException e) {
			fail();
		}
	}
	
	@Test
	public void testUnQualifiedName3() {
		try {
			Main.main(new String[] {BASEDIR + "\\testFiles1", "Go"});
		} catch (IOException e) {
			fail();
		}
	}
	
}