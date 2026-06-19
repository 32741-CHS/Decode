package org.firstinspires.ftc.teamcode.utils;

import org.firstinspires.ftc.robotcore.external.Telemetry;

public class TelemetryManager {
    private final Telemetry t;

    public TelemetryManager(Telemetry t) {
        this.t = t;
    }

    public void addData(String caption, Object value) {
        t.addData(caption, value);
    }

    public void addSection(String name) {
        t.addLine();
        t.addData("--- " + name + " ---", "");
    }

    public void update() {
        t.update();
    }
}
