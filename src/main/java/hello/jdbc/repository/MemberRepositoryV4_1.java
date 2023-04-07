package hello.jdbc.repository;

import hello.jdbc.domain.Member;
import hello.jdbc.repository.ex.MyDbException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.datasource.DataSourceUtils;
import org.springframework.jdbc.support.JdbcUtils;

import javax.sql.DataSource;
import java.sql.*;
import java.util.NoSuchElementException;

/**
 * 예외 누수 문제 해결
 * 체크 예외를 런타임 예외로 변경
 * MemberRepository 인터페이스 사용
 * throws SQLExcepion 제거
 */
@Slf4j
public class MemberRepositoryV4_1 implements MemberRepository {
    private final DataSource dataSource;

    public MemberRepositoryV4_1(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public Member save(Member member){
        // SQL 정의
        String sql = "insert into member(member_id, money) values(?,?)";

        Connection con = null;
        PreparedStatement pstmt = null;

        try {
            con = getConnection();
            pstmt = con.prepareStatement(sql);
            pstmt.setString(1, member.getMemberId());
            pstmt.setInt(2, member.getMoney());
            pstmt.executeUpdate();
            return member;
        } catch (SQLException e) {
            // MyDbException이란 런타임 예외로 감싸줘서 throw SQLException을 지워줄수 있음, MemberRepository 인터페이스에서도 throw 지움
            throw new MyDbException(e);
        }finally {
            // 쿼리 실행 후 리소스 정리, 정리는 역순으로 해야함
            // 리소스 정리는 필수, 정리하지 않으면 리소스 누수가 발생
            close(con,pstmt,null);
        }
    }

    @Override
    public Member findById(String memberId) {
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
           throw new MyDbException(e);
        }finally {
            close(con,pstmt,rs);
        }
    }

    @Override
    public void update(String memberId, int money){
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
            throw new MyDbException(e);
        }finally {
            close(con,pstmt,null);
        }
    }

    @Override
    public void delete(String memberId) {
        String sql = "delete from member where member_id=?";

        Connection con = null;
        PreparedStatement pstmt = null;

        try {
            con = getConnection();
            pstmt = con.prepareStatement(sql);
            pstmt.setString(1,memberId);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            throw new MyDbException(e);
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
