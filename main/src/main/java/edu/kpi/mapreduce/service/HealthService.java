package edu.kpi.mapreduce.service;

import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Service
public class HealthService {

    private final Map<String, Date> healthMap = new HashMap<>();

    public void ping(final String guid) {

        healthMap.put(guid, new Date());
    }

    public Optional<Date> getLastHealthyDate(final String guid) {

        return Optional.ofNullable(healthMap.get(guid));
    }
}
