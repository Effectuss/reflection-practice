package edu.school21.ex00.classes;

import lombok.NonNull;

import java.util.StringJoiner;

public class Car {
    private String carBrand;
    private String carModel;
    private boolean isLeftHandDrive;
    private long carPrice;

    public Car() {
        this.carBrand = "Mercedes";
        this.carModel = "CLS";
        this.isLeftHandDrive = true;
        this.carPrice = 3000000;
    }

    public Car(String carBrand, String carModel, boolean isLeftHandDrive, long carPrice) {
        this.carBrand = carBrand;
        this.carModel = carModel;
        this.isLeftHandDrive = isLeftHandDrive;
        this.carPrice = carPrice;
    }

    public long changePrice(long changeValue) {
        if (changeValue < 0) {
            throw new IllegalArgumentException("The change value cant be negative!");
        }

        carPrice += changeValue;
        return carPrice;
    }

    public void changeBrandAndModel(@NonNull String carBrand, @NonNull String carModel) {
        this.carBrand = carBrand;
        this.carModel = carModel;
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", Car.class.getSimpleName() + "[", "]")
                .add("carBrand='" + carBrand + "'")
                .add("carModel='" + carModel + "'")
                .add("isLeftHandDrive='" + isLeftHandDrive + "'")
                .add("carPrice='" + carPrice + "'")
                .toString();
    }
}