
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.fail;

import java.io.File;
import java.io.IOException;

import org.junit.Test;

public class TestMain{
	
	private static final String BASEDIR = "C:\\Users\\abmis\\Documents\\GitHub\\SENGgroupA1";
	
	
	//Passing
	@Test
	public void testFileFinding() {
		String path = BASEDIR + "\\testFiles1";
		try {
			File[] files = Main.readFilesInDir(path);
			assertEquals(files.length, 2);
		} catch (IOException e) {
			e.printStackTrace();
			fail();
		}
	}
	
	
	//Passing
	@Test
	public void testFileFinding2() {
		String path = BASEDIR + "\\testFiles1";
		try {
			File[] files = Main.readFilesInDir(path);
			assertEquals(files[0], new File(path + "\\bye.java"));
			assertEquals(files[1], new File(path + "\\hi.java"));
		} catch (IOException e) {
			e.printStackTrace();
			fail();
		}
	}
	
	
	//Passing
	@Test(expected = ArrayIndexOutOfBoundsException.class)
	public void testFileFinding3() {
		String path = BASEDIR + "\\testFileEmp";
		
		try {
			File[] files = Main.readFilesInDir(path);
			files[0] = null;
		} catch (IOException e) {
			e.printStackTrace();
			fail();
		}
		
	}
	
	
	//Passing
	@Test
	public void testReadFile() {
		String path = BASEDIR + "\\testRead\\uReadBro.txt";
		String test = "\r\n" + 
				"hi\r\n" + 
				"\r\n" + 
				"k";
		try {
			String src = Main.readFileToString(path);
			assertEquals(src, test);
		} catch (IOException e) {
			System.err.println("Bad Test Setup");
			e.printStackTrace();
		}
	}

	
	//Passing
	@Test
	public void testReadFile2() {
		String path = BASEDIR + "\\testRead\\nofingHere.txt";
		String test = "";
		try {
			String src = Main.readFileToString(path);
			assertEquals(src, test);
		} catch (IOException e) {
			System.err.println("Bad Test Setup");
			e.printStackTrace();
		}
	}
	
	
	//Passing
	@Test
	public void testReadFile3() {
		String path = BASEDIR + "\\testRead\\lastOneIPromise.txt";
		String test = "AzA9val2as\r\n" + 
				"ba6b32SoWR\r\n" + 
				"56yylg1SMY\r\n" + 
				"2ns4WUYYl5\r\n" + 
				"cyypIqFP3Z\r\n" + 
				"mB8qeZdefH\r\n" + 
				"kC4ITOgOqG\r\n" + 
				"KwhQutszWT\r\n" + 
				"mZ5WHMUeWP\r\n" + 
				"wZaRb7MZS5";
		try {
			String src = Main.readFileToString(path);
			assertEquals(src, test);
		} catch (IOException e) {
			System.err.println("Bad Test Setup");
			e.printStackTrace();
		}
	}
	
	
	//Unable to recognize references as of this type
	@Test
	public void testParseOne1QualifiedName() {
		String path = BASEDIR + "\\testFiles1\\hi.java";
		Main.setTarget("java.lang.String");
		try {
			ASTVisitor result = Main.parseAll(new File[] {new File(path)});
			assertEquals(0, result.getDeclarationsCount());
			assertEquals(6, result.getReferencesCount());
		} catch (IOException e) {
			System.err.println("Bad Test Setup");
			e.printStackTrace();
		}
	}
	
	
	//Passing
	@Test
	public void testParseOne1UnQualifiedName() {
		String path = BASEDIR + "\\testFiles1\\hi.java";
		Main.setTarget("String");
		try {
			ASTVisitor result = Main.parseAll(new File[] {new File(path)});
			assertEquals(6, result.getReferencesCount());
			assertEquals(0, result.getDeclarationsCount());
		} catch (IOException e) {
			System.err.println("Bad Test Setup");
			e.printStackTrace();
		}
	}
	
	
	//Unable to recognize "new Go()" as reference
	@Test
	public void testParseOne2QualifiedName() {
		String path = BASEDIR + "\\testFiles1\\bye.java";
		Main.setTarget("bye.Go");
		try {
			ASTVisitor result = Main.parseAll(new File[] {new File(path)});
			assertEquals(1, result.getDeclarationsCount());
			assertEquals(2, result.getReferencesCount());
		} catch (IOException e) {
			System.err.println("Bad Test Setup");
			e.printStackTrace();
		}
	}
	
	
	//Not expected to pass due to partial working of qualified name recognition
	@Deprecated
	@Test
	public void testParseOne2UnQualifiedName() {
		String path = BASEDIR + "\\testFiles1\\bye.java";
		Main.setTarget("Go");
		try {
			ASTVisitor result = Main.parseAll(new File[] {new File(path)});
			assertEquals(1, result.getDeclarationsCount());
			assertEquals(2, result.getReferencesCount());
		} catch (IOException e) {
			System.err.println("Bad Test Setup");
			e.printStackTrace();
		}
	}
	
	
	//Unable to recognize references as of this type
	@Test
	public void testParseAll1QualifiedName() {
		String path = BASEDIR + "\\testFiles2";
		Main.setTarget("java.lang.String");
		try {
			File[] files = Main.readFilesInDir(path);
			ASTVisitor result = Main.parseAll(files);
			assertEquals(0, result.getDeclarationsCount());
			assertEquals(7, result.getReferencesCount());		//1 in Bye, 6 in Hi
		} catch (IOException e) {
			System.err.println("Bad Test Setup");
			e.printStackTrace();
		} catch (NullPointerException e) {
			System.err.println("Bad Test Setup");
			e.printStackTrace();
		}
	}
	
	
	//Passing, should not pass upon full implementation of qualified name recognition
	@Test
	public void testParseAll1UnQualifiedName() {
		String path = BASEDIR + "\\testFiles2";
		Main.setTarget("String");
		try {
			File[] files = Main.readFilesInDir(path);
			ASTVisitor result = Main.parseAll(files);
			assertEquals(0, result.getDeclarationsCount());
			assertEquals(7, result.getReferencesCount());		//1 in Bye, 6 in Hi
		} catch (IOException e) {
			System.err.println("Bad Test Setup");
			e.printStackTrace();
		} catch (NullPointerException e) {
			System.err.println("Bad Test Setup");
			e.printStackTrace();
		}
	}
	
	
	//Passing
	@Test
	public void testParseAll2NameBye() {
		String path = BASEDIR + "\\testFiles2";
		Main.setTarget("Bye");
		try {
			File[] files = Main.readFilesInDir(path);
			ASTVisitor result = Main.parseAll(files);
			assertEquals(1, result.getDeclarationsCount());
			assertEquals(2, result.getReferencesCount());		//2 in Hi
		} catch (IOException e) {
			System.err.println("Bad Test Setup");
			e.printStackTrace();
		} catch (NullPointerException e) {
			System.err.println("Bad Test Setup");
			e.printStackTrace();
		}
	}
	
	
	//should be recognizing 3 references at this point but only 2
	@Test
	public void testParseAll2QualifiedNameGo() {
		String path = BASEDIR + "\\testFiles2";
		Main.setTarget("Bye.Go");
		try {
			File[] files = Main.readFilesInDir(path);
			ASTVisitor result = Main.parseAll(files);
			assertEquals(1, result.getDeclarationsCount());
			assertEquals(5, result.getReferencesCount());		//2 in Bye, 3 in Hi
		} catch (IOException e) {
			System.err.println("Bad Test Setup");
			e.printStackTrace();
		} catch (NullPointerException e) {
			System.err.println("Bad Test Setup");
			e.printStackTrace();
		}
	}
	
	
	//only recognizing 2 again
	@Test
	public void testParseAll2UnQualifiedNameGo() {
		String path = BASEDIR + "\\testFiles2";
		Main.setTarget("Go");
		try {
			File[] files = Main.readFilesInDir(path);
			ASTVisitor result = Main.parseAll(files);
			assertEquals(1, result.getDeclarationsCount());
			assertEquals(5, result.getReferencesCount());		//2 in Bye, 3 in Hi
		} catch (IOException e) {
			System.err.println("Bad Test Setup");
			e.printStackTrace();
		} catch (NullPointerException e) {
			System.err.println("Bad Test Setup");
			e.printStackTrace();
		}
	}
	
	//Passing
	@Test
	public void testParseAllDirectories() {
		String path = BASEDIR + "\\testFileWut";
		Main.setTarget("Irrelevent");
		try {
			File[] files = Main.readFilesInDir(path);
			ASTVisitor result = Main.parseAll(files);
		} catch (IOException e) {
			System.err.println("Bad Test Setup");
			e.printStackTrace();
		} catch (NullPointerException e) {
			System.err.println("Bad Test Setup");
			e.printStackTrace();
		}
	}
	
	
}