package io.github.anthonyclemens.utils;

import org.newdawn.slick.Color;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;

import io.github.anthonyclemens.Logic.Calender;
import io.github.anthonyclemens.Logic.DayNightCycle;

public class DisplayHUD {
    public void renderHUD(GameContainer container, Graphics g, Calender calender, DayNightCycle env) {
        g.setColor(Color.black);
        g.drawString("Date: " + calender.toString(), container.getWidth() - 200f, 0);
        g.drawString("Time: " + env.toString(), container.getWidth() - 200f, 16);
    }
}
