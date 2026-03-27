package org.firstinspires.ftc.team28420.config;

import com.acmerobotics.dashboard.config.Config;

import org.opencv.core.Scalar;

@Config
public class ShooterConf {
    public static double REVOLVER_VELOCITY = 1331;
    public static double SORT_P = 6.2;
    public static Scalar cslowGreen = new Scalar(70, 0.4, 0.019);
    public static Scalar cshighGreen = new Scalar(160, 1, 0.031);
    public static Scalar cslowPurple = new Scalar(240, 0.3, 0.006);
    public static Scalar cshighPurple = new Scalar(330, 1, 0.04);
    public static double SCANNED_BALL_MS = 70;
    public static String TARGET_MOTIF = null;
    public static double BALL_DETECTION_THRESHOLD = 4;
    public static double SHOOTER_F = 30;
    public static double SHOOTER_I = 0;
    public static double SHOOTER_P = 37;
    public static double SHOOTER_D = 2;
    public static int VELOCITY = 512;
    public static int DRIBBLER_VELOCITY = 2067;
    public static double SORT_MOTOR_TICKS_PER_TURN = 1067.0; // TODO: calibrate
    public static double STALL_THRESHOLD_TPS = 80.0;
    public static double STALL_TIMEOUT_SEC = 0.8;
    public static int BUSY_TOLERANCE_TICKS = 25;
}
