package zerobase.weather.config;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

	// 예상 못한 예외처리를 도와줘서 소중함을 느낌
	// 500번대 error (서버의 문제)
	@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)

	@ExceptionHandler(Exception.class)
	public Exception handleAllException() {
//		예외를 logger 에 저장, 누군가에게 알람, db 재시작 로직 도입
		System.out.println("error from GlobalExceptionHandler");
		return new Exception();
	}
}
