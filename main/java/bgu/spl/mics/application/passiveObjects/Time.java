package bgu.spl.mics.application.passiveObjects;

public class Time {
    private int speed;
    private int duration;

    public Time(int speed, int duration) {
        this.speed = speed;
        this.duration = duration;
    }

    public int getDuration() {
        return duration;
    }

    public int getSpeed() {
        return speed;
    }
}
