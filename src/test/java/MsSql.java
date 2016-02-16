import junit.framework.TestCase;
import org.junit.Test;

import java.sql.*;

/**
 * Created by slaviann on 16.02.16.
 */
public class MsSql extends TestCase {

    private Connection connection;
    private final String username = "sa";
    private final String password = "1234";
    private final String URL = "jdbc:jtds:sqlserver://192.168.56.102:1433";

    @Test
    public void testMsSql() throws SQLException {
        DriverManager.registerDriver(new net.sourceforge.jtds.jdbc.Driver());
        connection = DriverManager.getConnection(URL, username, password);
        if(connection!=null) System.out.println("Connection Successful !\n");

        Statement statement = connection.createStatement();

        ResultSet resultSet = statement.executeQuery("SELECT 2, 3");

        Integer columnCount = resultSet.getMetaData().getColumnCount();
        while (resultSet.next()) {
            for (Integer i=1; i <= columnCount; i++){
                System.out.println(resultSet.getString(i));
            }
        }


    }


}
