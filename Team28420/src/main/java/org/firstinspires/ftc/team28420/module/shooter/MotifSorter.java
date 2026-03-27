package org.firstinspires.ftc.team28420.module.shooter;

import org.firstinspires.ftc.team28420.config.ShooterConf;
import org.firstinspires.ftc.team28420.processors.BallDetection;

import java.util.HashMap;

public class MotifSorter {
    private String curMotif = "";
    private boolean correctMotif = false;
    private HashMap<String, Integer> sortSeqMap = null;

    public MotifSorter() {
        initSortSeq();
    }

    // SETTERS
    public void resetMotif() {
        correctMotif = false;
        curMotif = "";
    }
    public void appendBallToMotif(BallDetection.BallColor color) {
        curMotif += (color == BallDetection.BallColor.PURPLE) ? 'P' : 'G';
    }

    public void appendBallToMotif(char color) {
        curMotif += color;
    }
    public void dropLastBall() {
        curMotif = curMotif.substring(0, curMotif.length() - 1);
    }

    public void setCurMotif(String motif) {
        curMotif = motif;
    }

    public void setCorrectMotif(boolean b) {
        correctMotif = b;
    }


    // GETTERS
    public int getMoveSlots() {
        int currentIndex = sortSeqMap.getOrDefault(curMotif, 0);
        int targetIndex = sortSeqMap.getOrDefault(ShooterConf.TARGET_MOTIF, 0);

        int moveSlots = (targetIndex - currentIndex + 3) % 3;

        return moveSlots;
    }
    public String getCurMotif() {
        return curMotif;
    }
    public boolean isCorrectMotif() {
        return correctMotif;
    }
    public boolean isMotifFull() {
        return curMotif.length() == 3;
    }

    private void initSortSeq() {
        sortSeqMap = new HashMap<String, Integer>();
        sortSeqMap.put("PPG", 0);
        sortSeqMap.put("GPP", 1);
        sortSeqMap.put("PGP", 2);
    }

    public boolean isValid() {
        long g = curMotif.chars().filter(ch -> ch == 'G').count();
        long p = curMotif.chars().filter(ch -> ch == 'P').count();
        return (g == 1 && p == 2);
    }

}
