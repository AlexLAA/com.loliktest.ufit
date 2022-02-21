package com.loliktest.ufit.chromeDevTools;

import org.openqa.selenium.devtools.v97.performance.Performance;
import org.openqa.selenium.devtools.v97.performance.model.Metric;

import java.util.*;
import java.util.stream.Collectors;

import static org.openqa.selenium.devtools.v97.performance.Performance.EnableTimeDomain.TIMETICKS;
import static org.openqa.selenium.devtools.v97.performance.Performance.getMetrics;

public class PerformanceUfit {

    org.openqa.selenium.devtools.DevTools devTools;

    public PerformanceUfit(org.openqa.selenium.devtools.DevTools devTools) {
        this.devTools = devTools;
    }

    public Map<String, Number> capturingPerformanceMetrics(Performance.EnableTimeDomain timeDomain) {
        devTools.send(Performance.enable(Optional.of(timeDomain)));
        return devTools.send(getMetrics()).stream().collect(Collectors.toMap(Metric::getName, Metric::getValue));
    }

    public Map<String, Number> capturingPerformanceMetrics() {
        return capturingPerformanceMetrics(TIMETICKS);
    }

    public void disablePerformance() {
        devTools.send(Performance.disable());
    }

}
