package com.daicham.mybatis3sample;

import org.apache.derby.jdbc.EmbeddedDriver;
import org.apache.ibatis.datasource.*;
import org.apache.ibatis.transaction.jdbc.*;
import org.apache.ibatis.transaction.*;
import org.apache.ibatis.io.*;
import org.apache.ibatis.mapping.*;
import org.apache.ibatis.session.*;

import java.io.*;
import java.sql.*;
import javax.sql.*;

public class Main {
    public static void main(String... args) throws Exception {
        Connection conn = null;
        SqlSession session = null;
        try {
            conn = initDB();
            SqlSessionFactory sqlSessionFactory = createSqlSessionFactoryWithXml();
            session = sqlSessionFactory.openSession();
            BlogMapper mapper = session.getMapper(BlogMapper.class);
            Blog blog = mapper.selectBlog(1);
            System.out.println(blog);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (conn != null) conn.close();
            session.close();
        }
    }
    
    private static Connection initDB() throws SQLException, ClassNotFoundException {
      // ドライバ名
      final String driverName = org.apache.derby.jdbc.EmbeddedDriver.class.getCanonicalName();
      // DB名（カレントディレクトリにこの名前でDBインスタンス＝ディレクトリが作成される）
      final String dbName = "mybatis3sample";
      // DB接続文字列（create=trueでDBが存在しない場合新規作成するように指定している）
      final String connectionURL = "jdbc:derby:" + dbName + ";create=true";
      
      try {
        // ドライバをロードする
        Class.forName(driverName);
        // DB接続オブジェクトを参照する変数
        Connection conn = null;
        Statement s = null;
        try {
            // DB接続オブジェクトを取得
            conn = DriverManager.getConnection(connectionURL);
          
          // DBが初期化されているかどうかを確認
          if (!wasDbInitialized(conn)) {
            // 初期化されていない場合は初期化処理が必要
            // 最後に、初期化判断に使用しているBLOGテーブルを作成
            s = conn.createStatement();
            s.execute("create table BLOG (" +
            " ID        BIGINT    generated always as identity constraint LOG_PK primary key" +
            ",TIMESTAMP TIMESTAMP with default CURRENT_TIMESTAMP" +
            ",TITLE     VARCHAR(100)" +
            ",CONTENT   VARCHAR(2000)" +
            ")");
            s.execute("insert into BLOG(TIMESTAMP, TITLE, CONTENT) values ('2015-12-11 14:37:10', 'Hello Mybatis3', 'This is sample entry.')");
          }
          
        } catch (SQLException e) {
            throw e;
        } finally {
            if (s != null) s.close();
        }
        return conn;
      } catch (ClassNotFoundException e) {
        // ドライバのロードに失敗
        throw e;
      }
    }

    public static boolean wasDbInitialized(Connection conn) throws SQLException {
      // チェック用クエリが返す結果セットを参照するための変数
      ResultSet rs = null;
      try {
        // データベースが初期化されているかどうかを検証するためUPDATE文を発行
        final Statement s = conn.createStatement();
        // BLOGテーブルの件数をカウント
        // ※ここではこれ以上のチェックをしないことにする（テーブルのあるなしのみで判断することにする）
        rs = s.executeQuery("select count(1) from BLOG");
        
      } catch (SQLException e) {
        // 例外がスローされた。エラーコードを確認して処理を分岐
        
        // SQLステートコードを取得。
        final String state = e.getSQLState();
        
        // ステートコードで処理を分岐。
        if (state.equals("42X05")) {
          // テーブルが存在しない。データベースはまだ初期化されていない。
          return false;
        } else if (state.equals("42X14") || state.equals("42821")) {
          // テーブル定義が不正。テーブルは存在するも想定した定義ではない...
          throw e;
        } else {
          // その他の予期せぬ例外が発生...
          throw e;
        }
        
      } finally {
        // ともかくもResultSetをクローズ
        if (rs != null) rs.close();
      }
      // テーブルが存在した。（＝すでに初期化されている。）
      return true;
    }

    // private static SqlSessionFactory createSqlSessionFactory() {
    //     DataSource dataSource = BlogDataSourceFactory.getBlogDataSource();
    //     TransactionFactory transactionFactory = new JdbcTransactionFactory();
    //     Environment environment = new Environment("development", transactionFactory, dataSource);
    //     Configuration configuration = new Configuration(environment);
    //     configuration.addMapper(BlogMapper.class);
    //     SqlSessionFactory sqlSessionFactory = new SqlSessionFactoryBuilder().build(configuration);
    // }
    
    private static SqlSessionFactory createSqlSessionFactoryWithXml() throws IOException {
      String resource = "com/daicham/mybatis3sample/mybatis-config.xml";
      InputStream inputStream = Resources.getResourceAsStream(resource);
      return new SqlSessionFactoryBuilder().build(inputStream);
    }
}
