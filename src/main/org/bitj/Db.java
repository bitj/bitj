package org.bitj;

import org.bitj.utils.JarFileLoader;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;

public class Db {

  private String path;
  private Connection conn;

  public Db(String path) {
    this.path = path;
  }

  public void connect() throws ClassNotFoundException, SQLException {
    openConnection();
    migrate();
    if (isEmpty()) populate();
  }

  private void openConnection() throws ClassNotFoundException, SQLException {
    Class.forName("org.h2.Driver");
    conn = DriverManager.getConnection("jdbc:h2:" + path, "sa", "");
  }

  private void migrate() throws SQLException {
    String migrateSql = new JarFileLoader().readTextFileFromJar("/migrate.sql");
    exec(migrateSql);
  }

  private boolean isEmpty() throws SQLException {
    String sql = "SELECT COUNT(*) FROM blocks LIMIT 1";
    ResultSet resultSet = conn.createStatement().executeQuery(sql);
    resultSet.next();
    return resultSet.getLong(1) == 0;
  }

  private void populate() throws SQLException {
    String populateSql = new JarFileLoader().readTextFileFromJar("/populate.sql");
    exec(populateSql);
  }

  private void exec(String sql) throws SQLException {
    conn.createStatement().execute(sql);
  }

  public void disconnect() throws SQLException {
    if (conn != null) conn.close();
  }

  public Connection getConnection() {
    return conn;
  }
}
