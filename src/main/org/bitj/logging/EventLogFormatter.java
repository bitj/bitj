package org.bitj.logging;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.logging.Formatter;
import java.util.logging.LogRecord;

public class EventLogFormatter extends Formatter {

  private String lineSeparator = (String) System.getProperty("line.separator");

  public String format(LogRecord record) {
    String message = record.getMessage();
    StringBuffer sb = new StringBuffer();
    appendMessage(sb, message);
    appendLineSeparator(sb);
    appendExceptionIfPresent(record, sb);
    return sb.toString();
  }

  private void appendMessage(StringBuffer sb, String message) {
    sb.append(message);
  }

  private void appendLineSeparator(StringBuffer sb) {
    sb.append(lineSeparator);
  }

  private void appendExceptionIfPresent(LogRecord record, StringBuffer sb) {
    if (record.getThrown() != null) {
      StringWriter sw = new StringWriter();
      PrintWriter pw = new PrintWriter(sw);
      record.getThrown().printStackTrace(pw);
      pw.close();
      sb.append(sw.toString());
    }
  }

}
