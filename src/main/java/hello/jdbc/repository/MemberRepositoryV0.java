package hello.jdbc.repository;

import hello.jdbc.connection.DBConnectionUtil;
import hello.jdbc.domain.Member;
import lombok.extern.slf4j.Slf4j;

import java.sql.*;

/**
 * JDBC - DriverManager 사용
 */
@Slf4j
public class MemberRepositoryV0 {

    public Member save(Member member) throws SQLException {
        // SQL 정의
        String sql = "insert into member(member_id, money) values(?,?)";

        Connection con = null;
        PreparedStatement pstmt = null;

        try {
            //DB커넥션 획득
            con = getConnection();
            //DB에 전달할 SQL과 파라미터로 전달할 데이터 준비
            pstmt = con.prepareStatement(sql);
            // SQL의 첫번째 ?에 값 저장
            pstmt.setString(1, member.getMemberId());
            // SQL의 두번째 ?에 값 저장
            pstmt.setInt(2, member.getMoney());
            // 준비된 SQL을 커넥션을 통해 실제 DB에 전달
            pstmt.executeUpdate();
            return member;
        } catch (SQLException e) {
            log.error("db error",e);
            throw e;
        }finally {
            // 쿼리 실행 후 리소스 정리, 정리는 역순으로 해야함
            // 리소스 정리는 필수, 정리하지 않으면 리소스 누수가 발생
            close(con,pstmt,null);
        }
    }

    private void close(Connection con, Statement stmt, ResultSet rs){

        if(rs!=null){
            try {
                rs.close();
            } catch (SQLException e) {
                log.info("error",e);
            }
        }

        if(stmt!=null){
            try {
                stmt.close();  //Exception 터지면
            } catch (SQLException e) {
                log.info("error",e);    // 캣치로 잡음
            }
        }

        if(con!=null){              // 위에 예외 터져도 con은 닫을수 있음
            try {
                con.close();
            } catch (SQLException e) {
                log.info("error",e);
            }
        }
    }


    // 데이터베이스 커넥션 획득
    private static Connection getConnection() {
        return DBConnectionUtil.getConnection();
    }
}
