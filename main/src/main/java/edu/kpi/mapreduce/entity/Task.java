package edu.kpi.mapreduce.entity;

import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import lombok.Data;

import java.util.List;

@Entity(name = "tasks")
@Data
public class Task {

    @Id
    @GeneratedValue
    private Long id;
    @ElementCollection
    @Column(columnDefinition = "TEXT")
    private List<String> input;
    @ElementCollection(fetch = FetchType.EAGER)
    @Column(columnDefinition = "TEXT")
    private List<String> output;
    private boolean solved;
    @ManyToOne
    private Stage stage;
}
