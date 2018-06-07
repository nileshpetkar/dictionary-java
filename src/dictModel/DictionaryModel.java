package dictModel;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;

/**
 *
 * @author nilesh petkar
 */
public class DictionaryModel {

    private List list;
    private final Date date = new Date();
    private SimpleDateFormat fm;
    private Connection con;
    private Statement stmt;
    private ResultSet rs;
    private String sql;
    //private final String mySqlurl = "jdbc:mysql://localhost:3306/dictionary";
    //private final String url = "jdbc:sqlite:C:/sqlite/db/dicttable.sqlite";   to connect at C: location

    private Connection getConnection() {
        try {
            //Class.forName("com.mysql.jdbc.Driver"); //for mysql
            final String path = System.getProperty("user.dir").replace("\\", "/") + "/db/mydictionary.sqlite";
            final String url = "jdbc:sqlite:" + path;
            con = DriverManager.getConnection(url);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return con;
    }

    //return complete word info
    public List getInfo(String word) {
        try {
            list = new ArrayList();
            sql = "select * from dicttable where word='" + word + "'";
            con = getConnection();  //method defined in this class
            stmt = con.createStatement();
            rs = stmt.executeQuery(sql);

            if (rs != null) {
                if (rs.next()) {
                    list.add("" + rs.getString(2));
                    list.add("" + rs.getString(3));
                    list.add("" + rs.getString(4));
                    list.add("" + rs.getString(5));
                    list.add("" + rs.getString(6));
                } else {
                    list.add("");
                }
            }
        } catch (SQLException s) {
            s.printStackTrace();
        } finally {
            try {
                con.close();
                stmt.close();
            } catch (Exception e) {
            }
        }
        return list;
    }

    public List getWordsList(String category) {
        try {
            list = new ArrayList();
            sql = "select word from dicttable where category='" + category + "'";
            con = getConnection();
            stmt = con.createStatement();
            rs = stmt.executeQuery(sql);
            if (rs != null) {
                while (rs.next()) {
                    list.add(rs.getString("word"));
                }
            }
        } catch (Exception e) {
        } finally {
            try {
                con.close();
                stmt.close();
            } catch (Exception e) {
            }
        }
        return list;
    }

    //return suggested item in the drop down list
    public List getSuggestList(String keyWord) {
        try {

            list = new ArrayList();
            sql = "select * from dicttable where word LIKE '" + keyWord + "%'";
            con = getConnection();
            stmt = con.createStatement();
            rs = stmt.executeQuery(sql);

            if (rs != null) {
                while (rs.next()) {
                    list.add(rs.getString("word"));
                }
            }

        } catch (SQLException sqle) {
            sqle.printStackTrace();
        } finally {
            try {
                con.close();
                stmt.close();
            } catch (Exception e) {
            }
        }
        return list;
    }

    private Integer getWordId() {
        List<Integer> idList = new ArrayList();
        int wordId = 0;
        fm = new SimpleDateFormat("d");
        int dateNum = Integer.parseInt(fm.format(date));
        try {
            sql = "select wordid from dicttable";
            con = getConnection();
            stmt = con.createStatement();
            rs = stmt.executeQuery(sql);
            if (rs != null) {
                while (rs.next()) {
                    int num = Integer.parseInt(rs.getString("wordid"));
                    if (dateNum % 2 == 0 && num % 2 == 0) {
                        idList.add(num);
                    }
                    if (dateNum % 2 != 0 && num % 2 != 0) {
                        idList.add(num);
                    }
                }
            }
            for (Integer id : idList) {
                Random random = new Random();
                int randomNum = random.nextInt(idList.size()); //get random number
                wordId = idList.get(randomNum);
                sql = "select isdayword from dicttable where wordid='" + wordId + "'";
                rs = stmt.executeQuery(sql);
                if (rs != null) {
                    if (rs.next()) {
                        if (rs.getString("isdayword") == null || !rs.getString("isdayword").equals("Y")) {
                            sql = "update dicttable set isdayword='Y' where wordid='" + wordId + "'";
                            stmt.executeUpdate(sql);
                            return wordId;
                        }
                    }
                }
            }
        } catch (SQLException s) {
            s.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                con.close();
                stmt.close();
            } catch (Exception e) {
            }
        }
        return wordId;
    }

    //return id stored in the "todayword.txt" file
    private Integer getTodayId() {

        int todayId = 0;
        BufferedWriter writer;
        BufferedReader reader;
        String currentDate, fileModifiedDate;
        File file;
        int wordId = 0;
        System.out.println("we are here" + wordId);
        Object wordIdLine = null;

        try {

            String path = System.getProperty("user.dir").replace("\\", "/");
            file = new File(path, "todayword.txt");

            //if file is not created
            if (file.createNewFile()) {
                writer = new BufferedWriter(new FileWriter(file));
//                    writer.write(getWordId().toString());
                wordId = getWordId();
                if (wordId == 0) {
                    System.out.println("word id " + wordId);
                    con = getConnection();
                    stmt = con.createStatement();
                    sql = "UPDATE dicttable set isdayword ='N'";
                    stmt.executeUpdate(sql);
                    wordIdLine = getWordId();
                } else {
                    wordIdLine = wordId;
                }
                //writing to file
                writer.write(wordIdLine.toString());
                writer.close();
            } else { // file already exist
                fm = new SimpleDateFormat("MM/dd/yyyy");
                currentDate = fm.format(date);
                fileModifiedDate = fm.format(file.lastModified());

                if (!currentDate.equals(fileModifiedDate)) {
                    writer = new BufferedWriter(new FileWriter(file));
                    wordId = getWordId();
                    if (wordId == 0) {
                        System.out.println("word id " + wordId);
                        con = getConnection();
                        stmt = con.createStatement();
                        sql = "UPDATE dicttable set isdayword ='N'";
                        stmt.executeUpdate(sql);
                        wordIdLine = getWordId();
                    } else {
                        wordIdLine = wordId;
                    }
                    //writing to file
                    writer.write(wordIdLine.toString());
                    writer.close();
                }
            }
            reader = new BufferedReader(new FileReader(file));
            String line;
            if ((line = reader.readLine()) != null) {
                todayId = Integer.parseInt(line);
            }
        } catch (IOException io) {
        } catch (Exception e) {
        }
        return todayId;
    }

    public List<String> getTodayWord() {
        List<String> wList = new ArrayList();
        try {
            int id = getTodayId();  //getSelectedNum called here
            sql = "select word, meaning from dicttable where wordid='" + id + "'";
            con = getConnection();
            stmt = con.createStatement();
            rs = stmt.executeQuery(sql);
            if (rs != null) {
                while (rs.next()) {
                    wList.add(rs.getString("word"));
                    wList.add(rs.getString("meaning"));
                }
            }
        } catch (SQLException s) {
        } finally {
            try {
                con.close();
                stmt.close();
            } catch (Exception e) {
            }
        }

        return wList;
    }

    public void setRecentCol(String word) {

        try {
            sql = "select recent from dicttable where word='" + word + "'";
            con = getConnection();
            stmt = con.createStatement();
            rs = stmt.executeQuery(sql);
            if (rs != null) {
                if (rs.next()) {
                    System.out.println(rs.getString("recent"));
                    if (rs.getString("recent") == null || rs.getString("recent").equals("N")) {
                        sql = "UPDATE dicttable SET recent='Y', searchdate=DATETIME('now','localtime') WHERE word='" + word + "'";
                        stmt.executeUpdate(sql);
                        System.out.println("updated");
                    } else {
                        //sql ="UPDATE dicttable SET searchdate=NOW() WHERE word='"+word+"'";  for mysql
                        sql = "UPDATE dicttable SET searchdate=DATETIME('now','localtime') WHERE word='" + word + "'";    //for sqlite
                        stmt.executeUpdate(sql);
                    }
                }
            }
        } catch (SQLException s) {
            s.printStackTrace();
        } finally {
            try {
                con.close();
                stmt.close();
            } catch (Exception e) {
            }
        }
    }

    public List getRecentCol() {
        try {
            list = new ArrayList();
            sql = "select word from dicttable where recent='Y' ORDER BY searchdate DESC";
            con = getConnection();
            stmt = con.createStatement();
            rs = stmt.executeQuery(sql);
            if (rs != null) {
                while (rs.next()) {
                    list.add(rs.getString("word"));
                }
            }
        } catch (SQLException s) {
        } finally {
            try {
                con.close();
                stmt.close();
            } catch (Exception e) {
            }
        }
        return list;
    }

    public boolean isFav(String word) {
        try {
            sql = "select fav from dicttable where word='" + word + "'";
            con = getConnection();
            stmt = con.createStatement();
            rs = stmt.executeQuery(sql);
            if (rs.next()) {
                if (rs.getString("fav").equals("Y")) {
                    return true;
                } else {
                    return false;
                }
            }
        } catch (Exception e) {
        } finally {
            try {
                con.close();
                stmt.close();
            } catch (Exception e) {
            }
        }
        return false;
    }

    public void setFav(String word) {
        try {
            sql = "select fav from dicttable where word='" + word + "'";
            con = getConnection();
            stmt = con.createStatement();
            rs = stmt.executeQuery(sql);
            if (rs != null) {
                if (rs.next()) {
                    if (rs.getString("fav") == null || rs.getString("fav").equals("N")) {
                        sql = "UPDATE dicttable SET fav='Y' WHERE word='" + word + "'";
                        stmt.executeUpdate(sql);
                        System.out.println("updated");
                    }
                }
            }
        } catch (SQLException s) {
        } finally {
            try {
                con.close();
                stmt.close();
            } catch (Exception e) {
            }
        }
    }

    public void unSetFav(String word) {
        try {
            sql = "select fav from dicttable where word='" + word + "'";
            con = getConnection();
            stmt = con.createStatement();
            rs = stmt.executeQuery(sql);
            if (rs != null) {
                if (rs.next()) {
                    if (rs.getString("fav").equals("Y")) {
                        sql = "UPDATE dicttable SET fav='N' WHERE word='" + word + "'";
                        stmt.executeUpdate(sql);
                        System.out.println("updated");
                    }
                }
            }
        } catch (SQLException s) {
        } finally {
            try {
                con.close();
                stmt.close();
            } catch (Exception e) {
            }
        }
    }

    public List getFavCol() {
        try {
            list = new ArrayList();
            sql = "select word from dicttable where fav='Y'";
            con = getConnection();
            stmt = con.createStatement();
            rs = stmt.executeQuery(sql);
            if (rs != null) {
                while (rs.next()) {
                    list.add(rs.getString("word"));
                }
            }
        } catch (SQLException s) {
        } finally {
            try {
                con.close();
                stmt.close();
            } catch (Exception e) {
            }
        }
        return list;
    }

    public boolean isFavList() {
        try {
            list = new ArrayList();
            sql = "select fav from dicttable where fav='Y'";
            con = getConnection();
            stmt = con.createStatement();
            rs = stmt.executeQuery(sql);
            if (rs != null) {
                while (rs.next()) {
                    list.add(rs.getString("fav"));
                }
            }
            if (!list.isEmpty()) {
                return true;
            }
        } catch (Exception e) {
        } finally {
            try {
                con.close();
                stmt.close();
            } catch (Exception e) {
            }
        }
        return false;
    }

    public boolean isRecList() {
        try {
            list = new ArrayList();
            sql = "select recent from dicttable where recent='Y'";
            con = getConnection();
            stmt = con.createStatement();
            rs = stmt.executeQuery(sql);
            if (rs != null) {
                while (rs.next()) {
                    list.add(rs.getString("recent"));
                }
            }
            if (!list.isEmpty()) {
                return true;
            }
        } catch (Exception e) {
        } finally {
            try {
                con.close();
                stmt.close();
            } catch (Exception e) {
            }
        }
        return false;
    }

    //to clear single recent word
    public void clearRecWord(String word) {
        try {
            sql = "update dicttable set recent='N' where word='" + word + "'";
            con = getConnection();
            stmt = con.createStatement();
            stmt.executeUpdate(sql);
        } catch (Exception e) {
        }
    }

    //to clear single fav word
    public void clearFavWord(String word) {
        try {
            sql = "update dicttable set fav='N' where word='" + word + "'";
            con = getConnection();
            stmt = con.createStatement();
            stmt.executeUpdate(sql);
        } catch (Exception e) {
        }
    }

    public void clearAllRecent() {
        try {
            sql = "update dicttable set recent='N'";
            con = getConnection();
            stmt = con.createStatement();
            stmt.executeUpdate(sql);
        } catch (Exception e) {
        }
    }

    public void clearAllFav() {
        try {
            sql = "update dicttable set fav='N'";
            con = getConnection();
            stmt = con.createStatement();
            stmt.executeUpdate(sql);
        } catch (Exception e) {
        } finally {
            try {
                con.close();
                stmt.close();
            } catch (Exception e) {
            }
        }
    }

    public static void main(String[] args) {
        DictionaryModel model = new DictionaryModel();
    }
}
