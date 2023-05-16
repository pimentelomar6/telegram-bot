package com.pimentelprojects.productivitymaster.models;

import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Entity
@Table(name = "users")
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@Setter
public class UserEntity {

    @Id
    private Long id;
    private String username;
    private List<String> tasks;
}
