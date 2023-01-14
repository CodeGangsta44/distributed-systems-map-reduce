package edu.kpi.mapreduce.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import lombok.Data;

import java.util.List;

@Entity(name = "stages")
@Data
public class Stage {

    @Id
    @GeneratedValue
    private Long id;
    private StageType type;
    private String implementation;
    @OneToMany(mappedBy = "stage", fetch = FetchType.EAGER)
    public List<Task> tasks;
    @ManyToOne
    private Problem problem;
    private String reduceInitialIdentity;
}
