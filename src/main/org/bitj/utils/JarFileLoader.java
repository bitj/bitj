package org.bitj.utils;

import java.io.*;

// TODO: add tests (this is imported from the other project where no tests were present)
public class JarFileLoader {

  public static String readTextFileFromJar(String path) {
    StringBuilder content = new StringBuilder();
    try {
      return tryToReadTextFileFromJar(path, content);
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  private static String tryToReadTextFileFromJar(String path, StringBuilder content) throws IOException {
    InputStream inputStream = JarFileLoader.class.getResourceAsStream(path);
    if (inputStream == null)
      throw new IOException("Invalid resource path " + path);
    InputStreamReader inputStreamReader = new InputStreamReader(inputStream, "UTF-8");
    BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
    String line;
    while ((line = bufferedReader.readLine()) != null)
      content.append(line).append("\n");
    return content.toString();
  }

  public static byte[] readBinaryFileFromJar(String path) {
    try {
      return tryToReadBinaryFileFromJar(path);
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  private static byte[] tryToReadBinaryFileFromJar(String path) throws IOException {
    InputStream inputStream = JarFileLoader.class.getResourceAsStream(path);
    if (inputStream == null)
      throw new IOException("Invalid resource path " + path);
    ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
    try {
      while (true) {
        int b = inputStream.read();
        if (b == -1)
          return outputStream.toByteArray();
        else
          outputStream.write(b);
      }
    } finally {
      inputStream.close();
      outputStream.close();
    }
  }

}
