package edu.kpi.mapreduce.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import lombok.Data;

import java.util.List;

@Entity(name = "problems")
@Data
public class Problem {

    @Id
    @GeneratedValue
    private Long id;
    @OneToMany(mappedBy = "problem", fetch = FetchType.EAGER)
    private List<Stage> stages;
    @Column(columnDefinition = "TEXT")
    private String result;
    private boolean solved;
}
