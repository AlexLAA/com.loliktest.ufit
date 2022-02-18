package com.loliktest.ufit.ChromeDevTools;

import org.openqa.selenium.devtools.v97.performance.Performance;
import org.openqa.selenium.devtools.v97.performance.model.Metric;

import java.util.*;
import java.util.stream.Collectors;

import static org.openqa.selenium.devtools.v97.performance.Performance.EnableTimeDomain.TIMETICKS;
import static org.openqa.selenium.devtools.v97.performance.Performance.getMetrics;

public class PerformanceUfit {

    public Map<String, Number> capturingPerformanceMetrics(Performance.EnableTimeDomain timeDomain) {
        ChromeDevTools.devToolsLocal.get().send(Performance.enable(Optional.of(timeDomain)));
        return ChromeDevTools.devToolsLocal.get().send(getMetrics()).stream().collect(Collectors.toMap(Metric::getName, Metric::getValue));
    }

    public Map<String, Number> capturingPerformanceMetrics() {
        return capturingPerformanceMetrics(TIMETICKS);
    }

    public void disablePerformance() {
        ChromeDevTools.devToolsLocal.get().send(Performance.disable());
    }

}
