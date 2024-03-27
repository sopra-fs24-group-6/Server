package ch.uzh.ifi.hase.soprafs24.repository;

import ch.uzh.ifi.hase.soprafs24.entity.WordPair;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository("wordPairRepository")
public interface WordPairRepository extends JpaRepository<WordPair, Long> {
  List<WordPair> findByTheme_Id(Long themeId);

  List<WordPair> findByTheme_Name(String themeName);

}
