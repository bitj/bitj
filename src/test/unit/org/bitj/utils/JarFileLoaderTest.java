package org.bitj.utils;

import org.bitj.BaseTest;
import org.testng.annotations.Test;

import static org.testng.Assert.*;

public class JarFileLoaderTest extends BaseTest {

  @Test
  public void readTextFileFromJar() throws Exception {
    assertEquals(JarFileLoader.readTextFileFromJar("/jarfileloader/example.txt"), "ĄĆĘŁŃÓŚŻŹ\nąćęłńóśźż\n");
  }

  @Test(expectedExceptions = RuntimeException.class, expectedExceptionsMessageRegExp = ".*IOException.*")
  public void readTextFileFromJar_invalidPath() throws Exception {
    JarFileLoader.readTextFileFromJar("jarfileloader/example.txt");
  }

  @Test
  public void readBinaryFileFromJar() throws Exception {
    assertEquals(JarFileLoader.readBinaryFileFromJar("/jarfileloader/example.bin"), bytes("FF 00 10 DD"));
  }

  @Test(expectedExceptions = RuntimeException.class, expectedExceptionsMessageRegExp = ".*IOException.*")
  public void readBinaryFileFromJar_invalidPath() throws Exception {
    JarFileLoader.readBinaryFileFromJar("jarfileloader/example.bin");
  }

}
