package edu.kpi.mapreduce.repository;

import edu.kpi.mapreduce.entity.Task;
import edu.kpi.mapreduce.entity.TaskState;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TaskRepository extends JpaRepository<Task, Long> {

    List<Task> findByState(final TaskState state);
}
