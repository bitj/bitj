import org.bitj.Db;
import org.testng.annotations.Test;

import java.io.File;
import java.io.IOException;

public class DbTest {

  @Test
  public void doesNotThrowOnConnectDisconnect() throws Exception {
    String dbFilePath = tmpFilePath("bitj_test_tmp_db");

    // First connection creates db
    Db db = new Db(dbFilePath);
    db.connect();
    assert new File(dbFilePath).exists();

    // Second connection does not throw
    Db db2 = new Db(dbFilePath);
    db2.connect();

    // Disconnection does not throw
    db.disconnect();
    db2.disconnect();
  }

  @Test
  public void doesNotThrowOnDisconnectBeforeConnect() throws Exception {
    String dbFilePath = tmpFilePath("bitj_test_db_");
    Db db = new Db(dbFilePath);
    db.disconnect();
  }

  private String tmpFilePath(String prefix) throws IOException {
    File tmpFile = File.createTempFile(prefix, "");
    String path = tmpFile.getAbsolutePath();
    assert tmpFile.delete();
    return path;
  }

}
