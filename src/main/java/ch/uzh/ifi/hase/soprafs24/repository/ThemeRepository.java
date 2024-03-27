package ch.uzh.ifi.hase.soprafs24.repository;

import ch.uzh.ifi.hase.soprafs24.entity.Theme;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository("themeRepository")
public interface ThemeRepository extends JpaRepository<Theme, Long> {
  Optional<Theme> findByName(String name);

}
