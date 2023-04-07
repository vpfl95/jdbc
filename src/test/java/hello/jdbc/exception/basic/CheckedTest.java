package hello.jdbc.exception.basic;

import lombok.extern.slf4j.Slf4j;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.*;

@Slf4j
public class CheckedTest {

    @Test
    void checked_catch(){
        MyChecekdException.Service service= new MyChecekdException.Service();
        service.callCatch();
    }

    @Test
    void checked_throw(){
        MyChecekdException.Service service= new MyChecekdException.Service();
        assertThatThrownBy(()->service.callThrow())
                .isInstanceOf(MyChecekdException.class);
    }

    static class MyChecekdException extends Exception{
        /**
         * Exception을 상속받은 예외는 체크예외가 된다.
         */
        public MyChecekdException(String message) {
            super(message);
        }

        /**
         * Checked 예외는
         * 예외를 잡아서 처리하거나, 던지거나 둘중 하나를 필수로 선택해야함
         */
        static class Service{
            Repository repository = new Repository();
            /**
             * 예외를 잡아서 처리하는 코드
             */
            public void callCatch(){
                try {
                    repository.call();
                } catch (MyChecekdException e) {
                    //예외 처리 로직 : 예외잡고 정상 흐름으로 리턴
                    log.info("예외처리, message={}",e.getMessage(), e);
                }
            }

            /**
             *체크 예외를 밖으로 던지는 코드
             * 체크 예외는 예외를 잡지 않고 밖으로 던지려면 throws 예외를 메서드에 필수로 선언해야함
             */
            public void callThrow() throws MyChecekdException {
                repository.call();
            }
        }

        static class Repository{
            public void call() throws MyChecekdException {
                throw new MyChecekdException("ex");
            }
        }
    }
}
