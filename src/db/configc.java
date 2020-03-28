package db;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

public class configc {

    private static Properties prop = new Properties();

    static {
        try {
//            System.out.println(new File(".").getAbsolutePath());
            prop.load(new FileInputStream("/Users/yang_sijie/IdeaProjects/Project05/src/config"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static final String SQL_URL = prop.getProperty("SQL_URL");
    public static final String SQL_USR = prop.getProperty("SQL_USR");
    public static final String SQL_PWD = prop.getProperty("SQL_PWD");

}
