package edu.kpi.mapreduce;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class MapReduceWorkerApplication {

	public static void main(String[] args) {
		SpringApplication.run(MapReduceWorkerApplication.class, args);
	}
}
