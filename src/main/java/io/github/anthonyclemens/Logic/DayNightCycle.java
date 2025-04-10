package io.github.anthonyclemens.Logic;

import java.io.Serializable;

import org.newdawn.slick.Color;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;

public class DayNightCycle implements Serializable{
    private float timeOfDay = 8.0f; // Initial time of day (8AM)
    private final Calender calender;
    private final float totalCycleSeconds;
    private float sunriseTime;
    private float sunsetTime;
    private final Color sunriseColor = new Color(255, 94, 19, 100);  // Orange tint
    private final Color sunsetColor = new Color(255, 117, 24, 100);  // Reddish tint
    private final Color nightColor = new Color(15, 15, 64, 150);     // Dark blue tint
    private Color currentOverlayColor = Color.black; // Default to night
    private float lastUpdateTime = 0.0f;


    public DayNightCycle(float dayLength, float riseTime, float setTime, Calender calender){
        this.totalCycleSeconds=dayLength*60f;
        this.sunriseTime=riseTime;
        this.sunsetTime=setTime;
        this.calender=calender;
    }

    // Function to update time of day
    public void updateDayNightCycle(float delta) {
        // Increment timeOfDay based on elapsed time (delta) and scale to a 24-hour cycle
        timeOfDay += ((delta/1000.0f) / totalCycleSeconds) * 24.0f;
        if (timeOfDay >= 24.0f) {
            timeOfDay -= 24.0f; // Reset to the next day
            calender.incrementDay();
        }
        updateOverlayColor();
    }

    private Color calculateOverlayColor() {
        if (timeOfDay >= (sunriseTime - 1.5f) && timeOfDay < sunriseTime) {
            // Sunrise: Blend from nightColor to sunriseColor
            float factor = (timeOfDay - (sunriseTime - 1.5f)) / 1.5f;
            return interpolateColor(nightColor, sunriseColor, factor);
        } else if (timeOfDay >= sunriseTime && timeOfDay < (sunriseTime + 1.5f)) {
            // Sunrise to Day: Blend from sunriseColor to transparent
            float factor = (timeOfDay - sunriseTime) / 1.5f;
            return interpolateColor(sunriseColor, new Color(1.0f, 1.0f, 1.0f, 0.0f), factor);
        } else if (timeOfDay >= sunriseTime && timeOfDay <= sunsetTime) {
            // Daytime: Fully transparent white
            return new Color(1.0f, 1.0f, 1.0f, 0.0f);
        } else if (timeOfDay >= (sunsetTime - 1.5f) && timeOfDay < sunsetTime) {
            // Sunset to Night: Blend from transparent to sunsetColor
            float factor = (timeOfDay - (sunsetTime - 1.5f)) / 1.5f;
            return interpolateColor(new Color(1.0f, 1.0f, 1.0f, 0.0f), sunsetColor, factor);
        } else if (timeOfDay >= sunsetTime && timeOfDay < (sunsetTime + 1.5f)) {
            // Sunset: Blend from sunsetColor to nightColor
            float factor = (timeOfDay - sunsetTime) / 1.5f;
            return interpolateColor(sunsetColor, nightColor, factor);
        } else {
            // Nighttime: Dark blue tint
            return nightColor;
        }
    }

    // Interpolate between two colors
    private Color interpolateColor(Color start, Color end, float factor) {
        factor = Math.max(0.0f, Math.min(1.0f, factor)); // Clamp factor to [0, 1]
        int r = (int) (start.getRed() + (end.getRed() - start.getRed()) * factor);
        int g = (int) (start.getGreen() + (end.getGreen() - start.getGreen()) * factor);
        int b = (int) (start.getBlue() + (end.getBlue() - start.getBlue()) * factor);
        int a = (int) (start.getAlpha() + (end.getAlpha() - start.getAlpha()) * factor);
        return new Color(r, g, b, a);
    }

    private void updateOverlayColor() {
        // Update the overlay color at fixed intervals or when timeOfDay changes enough
        if (Math.abs(timeOfDay - lastUpdateTime) > 0.1f) {
            currentOverlayColor = calculateOverlayColor();
            lastUpdateTime = timeOfDay;
        }
    }

    public void renderOverlay(GameContainer container, Graphics g) {
        g.setColor(currentOverlayColor);
        g.fillRect(0, 0, container.getWidth(), container.getHeight());
    }

    public boolean isSunUp() {
        return timeOfDay >= sunriseTime && timeOfDay <= sunsetTime;
    }

    public boolean isSunDown() {
        return timeOfDay < sunriseTime || timeOfDay > sunsetTime;
    }

    public float getSunriseTime() {
        return sunriseTime;
    }

    public void setSunriseTime(float sunriseTime) {
        this.sunriseTime = sunriseTime;
    }

    public float getSunsetTime() {
        return sunsetTime;
    }

    public void setSunsetTime(float sunsetTime) {
        this.sunsetTime = sunsetTime;
    }

    @Override
    public String toString(){
        int hours = (int)timeOfDay;
        int minutes = Math.round((timeOfDay-hours)*60);
        String end = (timeOfDay>12f)? "PM" : "AM";
        hours = (hours % 12 == 0) ? 12 : hours % 12;
        return hours+":"+String.format("%02d", minutes)+" "+end;
    }

}
