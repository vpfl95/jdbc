package hello.jdbc.repository;

import hello.jdbc.domain.Member;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.datasource.DataSourceUtils;
import org.springframework.jdbc.support.JdbcUtils;

import javax.sql.DataSource;
import java.sql.*;
import java.util.NoSuchElementException;

/**
 * 트랜잭션 - 트랜잭션 매니저
 * DataSourceUtils.getConnection()
 * DataSourceUtils.releaseConnection()
 */
@Slf4j
public class MemberRepositoryV3 {
    private final DataSource dataSource;

    public MemberRepositoryV3(DataSource dataSource) {
        this.dataSource = dataSource;
    }

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

    public Member findById(String memberId) throws SQLException {
        String sql = "select * from member where member_id=?";

        Connection con =null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            con = getConnection();
            pstmt = con.prepareStatement(sql);
            pstmt.setString(1,memberId);

            rs = pstmt.executeQuery();
            if(rs.next()){
                Member member = new Member();
                member.setMemberId(rs.getString("member_id"));
                member.setMoney(rs.getInt("money"));
                return member;
            }else{
                throw new NoSuchElementException("member not found memberId=" + memberId);
            }
        } catch (SQLException e) {
            log.error("error",e);
            throw e;
        }finally {
            close(con,pstmt,rs);
        }
    }


    public void update(String memberId, int money) throws SQLException {
        String sql = "update member set money=? where member_Id=?";

        Connection con = null;
        PreparedStatement pstmt = null;

        try {
            con = getConnection();
            pstmt = con.prepareStatement(sql);
            pstmt.setInt(1,money);
            pstmt.setString(2,memberId);
            //쿼리 실행하고 영향받은 row수 반환
            int resultSize = pstmt.executeUpdate();
            log.info("resultSize={}",resultSize);
        } catch (SQLException e) {
            log.error("error",e);
            throw e;
        }finally {
            close(con,pstmt,null);
        }
    }



    public void delete(String memberId) throws SQLException {
        String sql = "delete from member where member_id=?";

        Connection con = null;
        PreparedStatement pstmt = null;

        try {
            con = getConnection();
            pstmt = con.prepareStatement(sql);
            pstmt.setString(1,memberId);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            log.error("error",e);
            throw e;
        }finally {
            close(con,pstmt,null);
        }
    }




    private void close(Connection con, Statement stmt, ResultSet rs){
        JdbcUtils.closeResultSet(rs);
        JdbcUtils.closeStatement(stmt);
        //주의! 트랜잭션 동기화를 사용하려면 DataSourceUtils를 사용해야 한다.
        DataSourceUtils.releaseConnection(con, dataSource);
    }


    // 데이터베이스 커넥션 획득
    private  Connection getConnection() throws SQLException {
        //주의! 트랜잭션 동기화를 사용하려면 DataSourceUtils를 사용해야 한다.
        Connection con = DataSourceUtils.getConnection(dataSource);
        log.info("get connection={}, class={}",con,con.getClass());
        return con;
    }
}
