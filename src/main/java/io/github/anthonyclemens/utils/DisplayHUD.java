package io.github.anthonyclemens.utils;

import org.newdawn.slick.Color;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;

import io.github.anthonyclemens.GameObjects.Items;
import io.github.anthonyclemens.Logic.Calender;
import io.github.anthonyclemens.Logic.DayNightCycle;
import io.github.anthonyclemens.Player.Player;
import io.github.anthonyclemens.Rendering.SpriteManager;

public class DisplayHUD {
    public void renderHUD(GameContainer container, Graphics g, Calender calender, DayNightCycle env, Player player) {
        int width = container.getWidth();
        g.setColor(Color.black);
        g.drawString("Date: " + calender.toString(), width - 200f, 0);
        g.drawString("Time: " + env.toString(), width - 200f, 16);

        SpriteManager.getSpriteSheet("main").getSprite(0, 11).draw(width-70f, 256, 2);
        g.drawString(player.getPlayerInventory().getItemCount(Items.ITEM_WOOD)+ "x", width - 70f + 32, 264);
    }
}
