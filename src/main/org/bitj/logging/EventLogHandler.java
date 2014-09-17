package org.bitj.logging;

import java.util.logging.ConsoleHandler;
import java.util.logging.Formatter;
import java.util.logging.Level;

public class EventLogHandler extends ConsoleHandler {

  public EventLogHandler(Level level) {
    Formatter formatter = new EventLogFormatter();
    setOutputStream(System.out);
    setFormatter(formatter);
    setLevel(level);
  }

}
