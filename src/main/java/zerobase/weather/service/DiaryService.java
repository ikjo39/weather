package zerobase.weather.service;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import zerobase.weather.WeatherApplication;
import zerobase.weather.domain.DateWeather;
import zerobase.weather.domain.Diary;
import zerobase.weather.error.InvalidDate;
import zerobase.weather.repository.DateWeatherRepository;
import zerobase.weather.repository.DiaryRepository;

@Service
@Transactional(readOnly = true)
public class DiaryService {

	// 왜 value로 쓰냐 - 실제 실무 개발시 로컬, 데브, 리얼 3가지 환경이 있음
	// 각자 환경별 db를 각각 분리하게 됨
	// properties 마다 환경을 지정해둠
	@Value("${openweathermap.key}")
	private String apiKey;

	private final DiaryRepository diaryRepository;
	private final DateWeatherRepository dateWeatherRepository;

	private static final Logger logger = LoggerFactory.getLogger(WeatherApplication.class);

	public DiaryService(DiaryRepository diaryRepository,
		DateWeatherRepository dateWeatherRepository) {
		this.diaryRepository = diaryRepository;
		this.dateWeatherRepository = dateWeatherRepository;
	}

	@Transactional // db를 건들기 때문
	@Scheduled(cron = "0 0 1 * * *")
	public void saveWeatherDate() {
		logger.info("오늘도 날씨 데이터 잘 가져옴");
		dateWeatherRepository.save(getWeatherFromApi());
	}

	@Transactional(isolation = Isolation.SERIALIZABLE)
	public void createDiary(LocalDate date, String text) {
		logger.info("started to create diary");
		// 날씨 데이터 가져오기 (API 에서 가져오기 or DB에서 가져오기)
		DateWeather dateWeather = getDateWeather(date);

		// 파싱된 데이터 + 일기 값 우리 db에 저장하기
		Diary nowDiary = new Diary();
		nowDiary.setDateWeather(dateWeather);
		nowDiary.setText(text);
		nowDiary.setDate(date);
		diaryRepository.save(nowDiary);
		logger.info("end to create diary");
		logger.error("문제가 생기면 꼭 나한테 알려줘야해");
		logger.warn("매일 날씨 데이터를 가져왔다.");
	}

	private DateWeather getWeatherFromApi() {
		// private 각각 함수로 나눠주면 재사용성 좋음
		// open weather map 에서 데이터 받아오기
		String weatherData = getWeatherString();
		// 받아온 날씨 데이터 파싱하기
		Map<String, Object> parsedWeather = parseWeather(weatherData);
		DateWeather dateWeather = new DateWeather();
		dateWeather.setDate(LocalDate.now());
		dateWeather.setWeather(parsedWeather.get("main").toString());
		dateWeather.setIcon(parsedWeather.get("icon").toString());
		dateWeather.setTemperature((Double) parsedWeather.get("temp"));
		return dateWeather;
	}

	private DateWeather getDateWeather(LocalDate date) {
		List<DateWeather> dateWeatherListFromDB = dateWeatherRepository.findAllByDate(date);
		if (dateWeatherListFromDB.size() == 0) {
			// 새로 api에서 날씨정보를 가져와야한다.
			// 정책상.... 현재 날씨를 가져오도록 하거나, 날씨 정보 없이 작성하거나
			return getWeatherFromApi();
		} else {
			return dateWeatherListFromDB.get(0);
		}
	}

	@Transactional(readOnly = true)
	public List<Diary> readDiary(LocalDate date) {
		logger.debug("read diary");

//		if (date.isAfter(LocalDate.ofYearDay(3050, 1))) {
//			throw new InvalidDate();
//		}
		return diaryRepository.findAllByDate(date);
	}

	@Transactional(readOnly = true)
	public List<Diary> readDiaries(LocalDate startDate, LocalDate endDate) {
		return diaryRepository.findAllByDateBetween(startDate, endDate);
	}

	public void updateDiary(LocalDate date, String text) {
		// id 값은 그대로 둔채로 save 하면 덮어 쓰게 됨
		Diary nowDiary = diaryRepository.getFirstByDate(date);
		nowDiary.setText(text);
		diaryRepository.save(nowDiary);
	}

	public void deleteDiary(LocalDate date) {
		diaryRepository.deleteAllByDate(date);
	}

	private String getWeatherString() {
		String apiUrl = "https://api.openweathermap.org/data/2.5/weather?q=seoul&appid=" + apiKey;

		try {
			URL url = new URL(apiUrl);
			// URL을 Http로 연결시킴
			HttpURLConnection connection = (HttpURLConnection) url.openConnection();
			// api 호출 시 get으로 부를거임
			connection.setRequestMethod("GET");
			// 응답 결과의 코드를 받아올 수 있음
			// 200, 500 등
			int responseCode = connection.getResponseCode();
			// 에러시 에러 코드가 길거나, 실제 응답 코드가 길을 때 속도 향상,
			BufferedReader br;

			if (responseCode == 200) {
				br = new BufferedReader(new InputStreamReader(connection.getInputStream()));
			} else {
				br = new BufferedReader(new InputStreamReader(connection.getErrorStream()));
			}
			String inputLine;
			StringBuilder response = new StringBuilder();
			while ((inputLine = br.readLine()) != null) {
				response.append(inputLine);
			}
			br.close();

			return response.toString();

		} catch (Exception e) {
			return "failed to get response";
		}
	}

	private Map<String, Object> parseWeather(String jsonString) {
		JSONParser jsonParser = new JSONParser();
		JSONObject jsonObject;

		// 정상적으로 동작 안하는 경우 - 중괄호 열렸는데 안닫혀있거나, 유효한 문자열이 아닌경우
		try {
			jsonObject = (JSONObject) jsonParser.parse(jsonString);
		} catch (ParseException e) {
			throw new RuntimeException();
		}

		Map<String, Object> resultMap = new HashMap<>();

		// main - main.temp
		// (JSONObject) 형변환 해줘야 인식함
		JSONObject mainData = (JSONObject) jsonObject.get("main");
		resultMap.put("temp", mainData.get("temp"));

		// weather - weather.main, weather.icon
		// jsonParsing 시 대괄호 시작이라면 jsonArray임
		JSONArray weatherArray = (JSONArray) jsonObject.get("weather");
		JSONObject weatherData = (JSONObject) weatherArray.get(0);
		resultMap.put("main", weatherData.get("main"));
		resultMap.put("icon", weatherData.get("icon"));

		return resultMap;

	}


}
