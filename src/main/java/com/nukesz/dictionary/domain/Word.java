package com.nukesz.dictionary.domain;


import javax.persistence.*;
import java.io.Serializable;
import java.util.Objects;

/**
 * A Word.
 */
@Entity
@Table(name = "word")
public class Word implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(name = "swedish")
    private String swedish;

    @Column(name = "hungarian")
    private String hungarian;

    @Column(name = "english")
    private String english;

    @Column(name = "comment")
    private String comment;

    @ManyToOne
    private Type type;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getSwedish() {
        return swedish;
    }

    public Word swedish(String swedish) {
        this.swedish = swedish;
        return this;
    }

    public void setSwedish(String swedish) {
        this.swedish = swedish;
    }

    public String getHungarian() {
        return hungarian;
    }

    public Word hungarian(String hungarian) {
        this.hungarian = hungarian;
        return this;
    }

    public void setHungarian(String hungarian) {
        this.hungarian = hungarian;
    }

    public String getEnglish() {
        return english;
    }

    public Word english(String english) {
        this.english = english;
        return this;
    }

    public void setEnglish(String english) {
        this.english = english;
    }

    public String getComment() {
        return comment;
    }

    public Word comment(String comment) {
        this.comment = comment;
        return this;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public Type getType() {
        return type;
    }

    public Word type(Type type) {
        this.type = type;
        return this;
    }

    public void setType(Type type) {
        this.type = type;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Word word = (Word) o;
        if(word.id == null || id == null) {
            return false;
        }
        return Objects.equals(id, word.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

    @Override
    public String toString() {
        return "Word{" +
            "id=" + id +
            ", swedish='" + swedish + "'" +
            ", hungarian='" + hungarian + "'" +
            ", english='" + english + "'" +
            ", comment='" + comment + "'" +
            '}';
    }
}
