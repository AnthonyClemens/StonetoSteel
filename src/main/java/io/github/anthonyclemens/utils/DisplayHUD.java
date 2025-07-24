package io.github.anthonyclemens.utils;

import org.newdawn.slick.Color;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;

import io.github.anthonyclemens.GameObjects.SingleTileObjects.Items;
import io.github.anthonyclemens.Logic.Calender;
import io.github.anthonyclemens.Logic.DayNightCycle;
import io.github.anthonyclemens.Player.Player;
import io.github.anthonyclemens.Rendering.SpriteManager;

public class DisplayHUD {
    private final int startY = 256;
    public void renderHUD(GameContainer container, Graphics g, Calender calender, DayNightCycle env, Player player) {
        int width = container.getWidth();
        g.setColor(Color.black);
        g.drawString("Date: " + calender.toString(), width - 200f, 0);
        g.drawString("Time: " + env.toString(), width - 200f, 16);


        SpriteManager.getSpriteSheet("main").getSprite(0, 11).draw(width-70f, startY, 2);
        g.drawString(player.getPlayerInventory().getItemCount(Items.ITEM_WOOD)+ "x", width - 70f + 32, startY+8f);
        SpriteManager.getSpriteSheet("main").getSprite(1, 10).draw(width-70f, startY+32f, 2);
        g.drawString(player.getPlayerInventory().getItemCount(Items.ITEM_FISH)+ "x", width - 70f + 32, startY+40f);
        SpriteManager.getSpriteSheet("main").getSprite(0, 12).draw(width-70f, startY+64f, 2);
        g.drawString(player.getPlayerInventory().getItemCount(Items.ITEM_SEED)+ "x", width - 70f + 32, startY+72f);
    }
}
