package zerobase.weather.controller;

import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import java.time.LocalDate;
import java.util.List;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.format.annotation.DateTimeFormat.ISO;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import zerobase.weather.domain.Diary;
import zerobase.weather.service.DiaryService;

// 기본 Controller + 200/300/400 지정해서 상태도 내려줄 수 있음
@RestController
public class DiaryController {

	private final DiaryService diaryService;

	public DiaryController(DiaryService diaryService) {
		this.diaryService = diaryService;
	}

	// 어떤 API를 제공해야할지 생각 해보기
	// API 경로를 지정해야함
	// Get - 조회, Post - 저장
	// date는 여러 형식이므로 정책을 정해야한다.
	@ApiOperation(value = "일기 텍스트와 날씨를 이용해서 DB에 일기 저장") // 안쓰면 기본 함수명임
	@PostMapping("/create/diary")
	void createDiary(
		@RequestParam @DateTimeFormat(iso = ISO.DATE) @ApiParam(value = "생성할 날짜", example = "2022-01-01") LocalDate date,
		@RequestBody String text
	) {
		diaryService.createDiary(date, text);
	}

	@ApiOperation("선택한 날짜의 모든 일기 데이터를 가져옵니다.")
	@GetMapping("/read/diary")
	List<Diary> readDiary(@RequestParam @DateTimeFormat(iso = ISO.DATE) @ApiParam(value = "조회할 날짜", example = "2022-01-01") LocalDate date) {
		return diaryService.readDiary(date);
	}

	@ApiOperation("선택한 기간 중 모든 일기 데이터를 가져옵니다.")
	@GetMapping("/read/diaries")
	List<Diary> readDiaries(@RequestParam @DateTimeFormat(iso = ISO.DATE) @ApiParam(value = "조회할 기간의 첫번째 날", example = "2022-01-01") LocalDate startDate,
		@RequestParam @DateTimeFormat(iso = ISO.DATE) @ApiParam(value = "조회할 기간의 마지막날", example = "2022-12-31") LocalDate endDate) {
		return diaryService.readDiaries(startDate, endDate);
	}

	// 그 날짜에 제일 첫번째 일기 수정으로 제한
	@ApiOperation("일기 텍스트와 날씨를 이용해서 DB에 일기 수정")
	@PutMapping("/update/diary")
	void updateDiary(@RequestParam @DateTimeFormat(iso = ISO.DATE) @ApiParam(value = "수정할 날짜", example = "2022-01-01") LocalDate date,
		@RequestBody String text) {
		diaryService.updateDiary(date, text);
	}

	@ApiOperation("일기 텍스트와 날씨를 이용해서 DB에 일기 삭제")
	@DeleteMapping("/delete/diary")
	void deleteDiary(@RequestParam @DateTimeFormat(iso = ISO.DATE) @ApiParam(value = "삭제할 날짜", example = "2022-01-01") LocalDate date) {
		diaryService.deleteDiary(date);
	}

	// 테스트 코드 2가지
	// 1. testCode, 실제 요청보내 test
	// 브라우저에 localhost/8080/링크 으로 함 보통은
	// 1) Get 으로만 인식, Post는 적합하지 않음
	// 2) 브라우저는 기본적으로 caching을 함, 항상 받아온 웹서버를 빨리빨리 하기 위해
	// 		그리기 위해 apiTest시 요청값을 정확히 보고 싶은데 caching에 영향을 받아
	// 		다른 결과가 나올 수도 있음
	// 		PostMan이라는 프로그램을 씀
	/**
	 * Postman 장점
	 * 1. 지금은 로컬호스트지만 때에따라 실제로 운영에 나가있는 로직이랑
	 * 		지금 막 로컬에서 개발하고 있는 로직의 차이점을 보고 싶을때
	 * 		로컬 호스트 8080을 {{host}} 라는 url Path 변수에 넣어줄 수 있음
	 *
	 */
}

