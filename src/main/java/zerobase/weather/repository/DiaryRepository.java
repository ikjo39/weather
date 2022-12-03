package zerobase.weather.repository;

import java.time.LocalDate;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import zerobase.weather.domain.Diary;

@Repository
public interface DiaryRepository extends JpaRepository<Diary, Long> {
	List<Diary> findAllByDate(LocalDate date);
	List<Diary> findAllByDateBetween(LocalDate startDate, LocalDate endDate);

	Diary getFirstByDate(LocalDate date);

	@Transactional // 이거 안붙히면 예외 발생
	void deleteAllByDate(LocalDate date);
}
