package org.firstinspires.ftc.teamcode.NonOpModes.depreciated;

import android.graphics.Color;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.NormalizedColorSensor;
import com.qualcomm.robotcore.hardware.NormalizedRGBA;

@Autonomous(name = "DualNormalizedColorSensorTest")
public class DrumColorTest extends LinearOpMode {

    private NormalizedColorSensor colorSensor1;
    private NormalizedColorSensor colorSensor2;

    int color = 0; // 1 = green, 2 = purple, 0 = other

    @Override
    public void runOpMode() {

        colorSensor1 = hardwareMap.get(NormalizedColorSensor.class, "colorSensor1");
        colorSensor2 = hardwareMap.get(NormalizedColorSensor.class, "colorSensor2");

        telemetry.addData("Status", "Initialized");
        telemetry.update();

        waitForStart();

        // Adjust gain if needed
        colorSensor1.setGain(5);
        colorSensor2.setGain(5);

        while (opModeIsActive()) {

            String combinedColor = getCombinedColor();

            telemetry.addData("FINAL COLOR", combinedColor);
            telemetry.addData("color int", color);
            telemetry.update();
        }
    }

    /**FUNCTION TO COMBINE BOTH COLOR SENSORS*/
    private String getCombinedColor() {

        float[] hsv1 = new float[3];
        float[] hsv2 = new float[3];

        // Sensor 1
        NormalizedRGBA colors1 = colorSensor1.getNormalizedColors();
        Color.colorToHSV(colors1.toColor(), hsv1);
        float hue1 = hsv1[0];

        // Sensor 2
        NormalizedRGBA colors2 = colorSensor2.getNormalizedColors();
        Color.colorToHSV(colors2.toColor(), hsv2);
        float hue2 = hsv2[0];

        // Average hue
        float avgHue = (hue1 + hue2) / 2f;
        String combinedColor = getColorName(avgHue);

        // Optional sensor telemetry (can remove to reduce spam)
        telemetry.addLine("SENSOR 1");
        telemetry.addData("Hue1", "%.1f", hue1);
        telemetry.addData("Detected1", getColorName(hue1));

        telemetry.addLine("SENSOR 2");
        telemetry.addData("Hue2", "%.1f", hue2);
        telemetry.addData("Detected2", getColorName(hue2));

        telemetry.addLine("COMBINED");
        telemetry.addData("Avg Hue", "%.1f", avgHue);
        telemetry.addData("Combined Color", combinedColor);

        // leave update() to caller (runOpMode)
        return combinedColor;
    }

    // --- ✨ FUNCTION TO CLASSIFY COLOR BASED ON HUE ✨ ---
    private String getColorName(float hue) {
        if (hue >= 140 && hue <= 160) {
            color = 1;
            return "GREEN";

        } else if (hue >= 215 && hue <= 245) {
            color = 2;
            return "PURPLE";

        } else {
            color = 0;
            return "OTHER";
        }
    }
}
