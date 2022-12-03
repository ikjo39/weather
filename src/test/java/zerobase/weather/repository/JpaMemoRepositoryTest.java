package zerobase.weather.repository;

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import zerobase.weather.domain.Memo;

@SpringBootTest
@Transactional
class JpaMemoRepositoryTest {

	@Autowired
	JpaMemoRepository jpaMemoRepository;

	@Test
	void insertMemoTest() {
	    // given
		Memo newMemo = new Memo(1, "This is my memo");
	    // when
		jpaMemoRepository.save(newMemo);
	    // then
		List<Memo> memoList = jpaMemoRepository.findAll();
		assertTrue(memoList.size() > 0);
	}

	// 실패 이유
	// 키 값이 mysql에서 자동 생성되기 때문임
	// save를 하고 나면 return 값이 Memo임
	// null 값을 test해도 되겠다.
	@Test
	void findById() {
	    // given
		Memo newMemo = new Memo(11, "jpa");
		// when
		Memo memo = jpaMemoRepository.save(newMemo);
		System.out.println(memo.getId());
		// then
		Optional<Memo> result = jpaMemoRepository.findById(memo.getId());
		assertEquals(result.get().getText(), "jpa");
	}

}