package ch.uzh.ifi.hase.soprafs24.entity;

import javax.persistence.*;
import java.io.Serializable;


@Entity
@Table(name = "WORD_PAIR")
public class WordPair implements Serializable {

  private static final long serialVersionUID = 1L;

  @Id
  @GeneratedValue
  private Long id;

  @ManyToOne
  @JoinColumn(name = "theme_id")
  private Theme theme;

  @Column(nullable = false)
  private String firstWord;

  @Column(nullable = false)
  private String secondWord;


  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public Theme getTheme() {
    return theme;
  }

  public void setTheme(Theme theme) {
    this.theme = theme;
  }

  public String getFirstWord() {
    return firstWord;
  }

  public void setFirstWord(String firstWord) {
    this.firstWord = firstWord;
  }

  public String getSecondWord() {
    return secondWord;
  }

  public void setSecondWord(String secondWord) {
    this.secondWord = secondWord;
  }

}
