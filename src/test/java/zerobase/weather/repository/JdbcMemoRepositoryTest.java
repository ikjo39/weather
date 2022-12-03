package zerobase.weather.repository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import zerobase.weather.domain.Memo;

@SpringBootTest
@Transactional	// testCode에 붙히면 무조건 test 진행 후 Rollback
public class JdbcMemoRepositoryTest {

	@Autowired
	JdbcMemoRepository jdbcMemoRepository;

	@Test
	void insertMemoTest() {
	    // given
		Memo newMemo = new Memo(2, "insertMemoTest");
	    // when
		jdbcMemoRepository.save(newMemo);
		// then
		Optional<Memo> result = jdbcMemoRepository.findById(2);
		assertEquals(result.get().getText(),"insertMemoTest");
	}


	@Test
	void findAllMemoTest() {
	    // given
		List<Memo> memoList = jdbcMemoRepository.findAll();
	    // when
	    // then
		System.out.println(memoList);
		assertNotNull(memoList);
	}
}