package com.game.entity;

import javax.persistence.*;
import java.sql.Date;

@Entity
public class Player {
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    @Column(name = "id")
    private Long id;
    @Basic
    @Column(name = "name")
    private String name;
    @Basic
    @Column(name = "title")
    private String title;
    @Basic
    @Column(name = "race")
    @Convert(converter = RaceConverter.class)
    private Race race;
    @Basic
    @Column(name = "profession")
    @Convert(converter = ProfessionConverter.class)
    private Profession profession;
    @Basic
    @Column(name = "birthday")
    @Convert(converter = LongConverter.class)
    private Long birthday;
    @Basic
    @Column(name = "banned")
    private Boolean banned;
    @Basic
    @Column(name = "experience")
    private Integer experience;
    @Basic
    @Column(name = "level")
    private Integer level;
    @Basic
    @Column(name = "untilNextLevel")
    private Integer untilNextLevel;

    public Long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Race getRace() {
        return race;
    }

    public void setRace(Race race) {
        this.race = race;
    }

    public Profession getProfession() {
        return profession;
    }

    public void setProfession(Profession profession) {
        this.profession = profession;
    }

    public Long getBirthday() {
        return birthday;
    }

    public void setBirthday(Long birthday) {
        this.birthday = birthday;
    }

    public Boolean getBanned() {
        if (banned == null) {
            banned = false;
        }
        return banned;
    }

    public void setBanned(Boolean banned) {
        this.banned = banned;
    }

    public Integer getExperience() {
        return experience;
    }

    public void setExperience(Integer experience) {
        this.experience = experience;
    }

    public Integer getLevel() {
        return level;
    }

    public void setLevel(Integer level) {
        this.level = level;
    }

    public Integer getUntilNextLevel() {
        return untilNextLevel;
    }

    public void setUntilNextLevel(Integer untilNextLevel) {
        this.untilNextLevel = untilNextLevel;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Player player = (Player) o;

        if (id != player.id) return false;
        if (name != null ? !name.equals(player.name) : player.name != null) return false;
        if (title != null ? !title.equals(player.title) : player.title != null) return false;
        if (race != null ? !race.equals(player.race) : player.race != null) return false;
        if (profession != null ? !profession.equals(player.profession) : player.profession != null) return false;
        if (birthday != null ? !birthday.equals(player.birthday) : player.birthday != null) return false;
        if (banned != null ? !banned.equals(player.banned) : player.banned != null) return false;
        if (experience != null ? !experience.equals(player.experience) : player.experience != null) return false;
        if (level != null ? !level.equals(player.level) : player.level != null) return false;
        if (untilNextLevel != null ? !untilNextLevel.equals(player.untilNextLevel) : player.untilNextLevel != null)
            return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = (int) (id ^ (id >>> 32));
        result = 31 * result + (name != null ? name.hashCode() : 0);
        result = 31 * result + (title != null ? title.hashCode() : 0);
        result = 31 * result + (race != null ? race.hashCode() : 0);
        result = 31 * result + (profession != null ? profession.hashCode() : 0);
        result = 31 * result + (birthday != null ? birthday.hashCode() : 0);
        result = 31 * result + (banned != null ? banned.hashCode() : 0);
        result = 31 * result + (experience != null ? experience.hashCode() : 0);
        result = 31 * result + (level != null ? level.hashCode() : 0);
        result = 31 * result + (untilNextLevel != null ? untilNextLevel.hashCode() : 0);
        return result;
    }
}
