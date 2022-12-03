package zerobase.weather.repository;

import java.util.List;
import java.util.Optional;
import javax.sql.DataSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import zerobase.weather.domain.Memo;

@Repository
public class JdbcMemoRepository {

	// 초기값이 설정되지 않았다.
	private final JdbcTemplate jdbcTemplate;

	// properties에 datasource 정보 지정이 dataSource에 담기게 됨
	// @Autowired 해줘야 datasource를 알아서 가져옴
	@Autowired
	public JdbcMemoRepository(DataSource dataSource) {
		jdbcTemplate = new JdbcTemplate(dataSource);
	}

	public Memo save(Memo memo) {
		String sql = "INSERT INTO memo VALUES(?, ?)";
		jdbcTemplate.update(sql, memo.getId(), memo.getText());
		return memo;
	}

	public List<Memo> findAll() {
		String sql = "SELECT * FROM memo";
		return jdbcTemplate.query(sql, memoRowMapper());
	}

	public Optional<Memo> findById(int id) {
		String sql = "SELECT * FROM memo WHERE id = ?";
		return jdbcTemplate.query(sql, memoRowMapper(), id).stream().findFirst();
	}

	private RowMapper<Memo> memoRowMapper() {
		// RowMapper: Jdbc 를 통해 데이터를 가져오면
		// ResultSet 형식이 됨
		// {id = 1, test = 'this is memo~"}
		// ResultSet 을 Memo 라는 형식으로 mapping 해줌
		// rs = ResultSet
		return (rs, rowNum) -> new Memo(
			rs.getInt("id"),
			rs.getString("text")
		);
	}

}
