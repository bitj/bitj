package org.bitj.logging;

import java.util.logging.Level;
import java.util.logging.Logger;

public class LoggersRegistrar {

  private static Logger eventLogger;

  public static void registerEventLogger(Level level) {
    eventLogger = Logger.getLogger("org.bitj.eventlogger");
    eventLogger.setUseParentHandlers(false);
    eventLogger.setLevel(level);
    if (eventLogger.getHandlers().length == 0)
      eventLogger.addHandler(new EventLogHandler(level));
    eventLogger.info("Registered org.bitj.eventlogger with level " + level.getName());
  }

}
