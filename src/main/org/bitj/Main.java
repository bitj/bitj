package org.bitj;

import org.bitj.logging.LoggersRegistrar;

import java.io.IOException;
import java.sql.SQLException;
import java.util.logging.Level;

public class Main {

  public static void main(String[] args) throws IOException, InterruptedException, SQLException, ClassNotFoundException {
    LoggersRegistrar.registerEventLogger(Level.FINE);
    App.getInstance().run();
    System.out.println("Main.main() end");
  }

}
