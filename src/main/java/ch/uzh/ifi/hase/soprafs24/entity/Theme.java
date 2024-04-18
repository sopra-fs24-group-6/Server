package ch.uzh.ifi.hase.soprafs24.entity;

import javax.persistence.*;
import java.io.Serializable;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;


@Entity
@Table(name = "THEME")
public class Theme implements Serializable {

  private static final long serialVersionUID = 1L;

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(nullable = false, unique = true)
  private String name;

  @OneToMany(mappedBy = "theme")
  private Set<WordPair> wordPairs = new HashSet<>();

    //For testing purposes
    @Override
    public String toString() {
        return "Theme{" +
                "\n\tid=" + id +
                ",\n\tname='" + name + '\'' +
                ",\n\twordPairs=" + wordPairs.stream()
                .map(WordPair::toString)
                .collect(Collectors.joining(", ", "[", "]")) +
                "\n}";
    }
  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

}

