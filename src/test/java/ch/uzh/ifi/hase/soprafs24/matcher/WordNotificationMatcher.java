package ch.uzh.ifi.hase.soprafs24.matcher;

import org.mockito.ArgumentMatcher;
import ch.uzh.ifi.hase.soprafs24.websocket.dto.WordNotification;

public class WordNotificationMatcher implements ArgumentMatcher<WordNotification> {
  private final String expectedWord;

  public WordNotificationMatcher(String expectedWord) {
    this.expectedWord = expectedWord;
  }

  @Override
  public boolean matches(WordNotification wn) {
    if (expectedWord == null) {
      return true;
    }
    return expectedWord.equals(wn.getWord());
  }
}
