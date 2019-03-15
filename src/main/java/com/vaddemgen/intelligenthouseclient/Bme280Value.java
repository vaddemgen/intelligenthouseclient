package com.vaddemgen.intelligenthouseclient;

class Bme280Value {

    private final double cTemp;
    private final double fTemp;
    private final double pressure;
    private final double humidity;

    Bme280Value(double cTemp, double fTemp, double pressure, double humidity) {

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
        return "Bme280Value{" +
            String.format("Temperature in Celsius : %.2f C %n", cTemp) +
            String.format("Temperature in Fahrenheit : %.2f F %n", fTemp) +
            String.format("Pressure : %.2f hPa %n", pressure) +
            String.format("Relative Humidity : %.2f %% RH %n", humidity) +
            '}';
    }
}
