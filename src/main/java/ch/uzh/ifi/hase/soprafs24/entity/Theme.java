package ch.uzh.ifi.hase.soprafs24.entity;

import javax.persistence.Entity;

import java.io.Serializable;
import java.util.List;

import javax.persistence.*;

@Entity
@Table(name = "Theme")
public class Theme implements Serializable {
    @Column
    private String themeName;

    @Column
    private List<String> words;

}
