package org.bitj;

import java.io.IOException;
import java.sql.SQLException;

public class Main {

  public static void main(String[] args) throws IOException, InterruptedException, SQLException, ClassNotFoundException {
    App.getInstance().run();
    System.out.println("Main.main end");
  }

}
