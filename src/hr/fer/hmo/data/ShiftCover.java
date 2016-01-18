package hr.fer.hmo.data;

public class ShiftCover {

    private int day;
    private String shiftId;
    private int requirement;
    private int weightUnder;
    private int weightOver;

    public ShiftCover(int day, String shiftId, int requirement, int weightUnder, int weightOver) {
        this.day = day;
        this.shiftId = shiftId;
        this.requirement = requirement;
        this.weightUnder = weightUnder;
        this.weightOver = weightOver;
    }

    public int getDay() {
        return day;
    }

    public String getShiftId() {
        return shiftId;
    }

    public int getRequirement() {
        return requirement;
    }

    public int getWeightUnder() {
        return weightUnder;
    }

    public int getWeightOver() {
        return weightOver;
    }
}
