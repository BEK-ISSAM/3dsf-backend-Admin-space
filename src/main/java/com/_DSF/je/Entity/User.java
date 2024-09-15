package com._DSF.je.Entity;

import com._DSF.je.Enumeration.Role;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;
import java.util.Set;
@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String username;
    private String password;
    private String email;

    @Enumerated(EnumType.STRING)
    private Role role;

    @ManyToMany
    @JoinTable(
            name = "user_courses",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "course_id")
    )
    @JsonIgnore // Avoid serialization issues

    private Set<Course> courses;

    @OneToMany(mappedBy = "teacher")
    @JsonIgnore // Avoid serialization issues
    private Set<Course> taughtCourses;

    @OneToMany(mappedBy = "student")
    @JsonIgnore // Avoid serialization issues
    private Set<Grade> grades;
    @OneToMany(mappedBy = "user")
    private Set<FAQ> faqs;
}
