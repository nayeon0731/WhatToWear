package com.example.whattowear;

public class WeatherInfoData {
    public int sky, tempature, rainP, humidity, rain;
    public double windSpeed;

    public WeatherInfoData(int sky, int humidity, int tempature, int rainP, double windSpeed, int rain) {
        this.sky = sky;
        this.humidity = humidity;
        this.tempature = tempature;
        this.rainP = rainP;
        this.windSpeed = windSpeed;
        this.rain = rain;
    }

    public int getSky() {
        return sky;
    }

    public int getHumidity() {
        return humidity;
    }

    public int getTempature() {
        return tempature;
    }

    public int getRainP() {
        return rainP;
    }

    public double getWindSpeed() {
        return windSpeed;
    }

    public int getRain() { return rain; }

    public void setSky(int sky) {
        this.sky = sky;
    }

    public void setHumidity(int humidity) {
        this.humidity = humidity;
    }

    public void setTempature(int tempature) {
        this.tempature = tempature;
    }

    public void setRainP(int rainP) {
        this.rainP = rainP;
    }

    public void setWindSpeed(double windSpeed) {
        this.windSpeed = windSpeed;
    }

    public void setRain(int rain) { this.rain = rain; }
}
