package org.firstinspires.ftc.team28420.config;

import com.acmerobotics.dashboard.config.Config;

import org.opencv.core.Scalar;

@Config
public class BallDetectionConf {
    public static Scalar lowGreen = new Scalar(35, 50, 50);
    public static Scalar highGreen = new Scalar(85, 255, 255);
    public static Scalar lowPurple = new Scalar(125, 50, 50);
    public static Scalar highPurple = new Scalar(160, 255, 255);
    public static double MIN_AREA = 15000; // минимальный размер объекта
    public static double kP = 0.0025;
}
