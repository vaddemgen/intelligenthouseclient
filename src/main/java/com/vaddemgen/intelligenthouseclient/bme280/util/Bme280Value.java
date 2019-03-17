package com.vaddemgen.intelligenthouseclient.bme280.util;

import java.io.Serializable;

public class Bme280Value implements Serializable {

    private static final long serialVersionUID = 9114717449881108052L;

    private final double cTemp;
    private final double fTemp;
    private final double pressure;
    private final double humidity;

    public Bme280Value(double cTemp, double fTemp, double pressure, double humidity) {

        this.cTemp = cTemp;
        this.fTemp = fTemp;
        this.pressure = pressure;
        this.humidity = humidity;
    }

    public double getcTemp() {
        return cTemp;
    }

    public double getfTemp() {
        return fTemp;
    }

    public double getPressure() {
        return pressure;
    }

    public double getHumidity() {
        return humidity;
    }

    @Override
    public String toString() {

        // Output data to screen
        return String.format("Temperature in Celsius : %.2f C, ", cTemp) +
            String.format("Temperature in Fahrenheit : %.2f F, ", fTemp) +
            String.format("Pressure : %.2f hPa, ", pressure) +
            String.format("Relative Humidity : %.2f %% RH", humidity);
    }
}
