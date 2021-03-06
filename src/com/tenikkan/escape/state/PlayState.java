package com.tenikkan.escape.state;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.event.KeyEvent;

import com.tenikkan.escape.Camera;
import com.tenikkan.escape.Physics;
import com.tenikkan.escape.Resource;
import com.tenikkan.escape.entity.EnemyProjectile;
import com.tenikkan.escape.entity.Entity;
import com.tenikkan.escape.entity.Player;
import com.tenikkan.escape.entity.ShootingEnemyEntity;
import com.tenikkan.escape.entity.SimpleEnemyEntity;
import com.tenikkan.escape.graphics.Display;
import com.tenikkan.escape.graphics.Renderer;
import com.tenikkan.escape.input.IController;
import com.tenikkan.escape.input.UserController;
import com.tenikkan.escape.level.BasicTile;
import com.tenikkan.escape.level.EndTile;
import com.tenikkan.escape.level.Level;
import com.tenikkan.math.Vector2f;

public class PlayState extends GameState
{
    private Camera camera; 
    private Player player;
    private Level level;
    private Renderer render;
    private IController playerController;
    
    public PlayState(int id, StateBasedGame game) 
    {
        super("play_state", id, game);
        
        init();
    }
    
    public void reset() 
    {
        getDisplay().setCursor(Resource.CROSSHAIR);
        
        numE = 5;
        width = 500;
        height = 800;
        enemyHealth = 50;
        levelNum = 0;
        killed = 0;
        
        skyR = 0x66;
        skyG = 0xbb;
        skyB = 0xff;
        
        init();
    }
    
    private void init() 
    {
        initManagers();
        
        camera = new Camera(0, 0, 16);
        
        render = new Renderer(camera, getDisplay().getWidth(), getDisplay().getHeight());
        
        level = new Level(width, height);
        
        playerController = new UserController(getKeyboard(), getMouse(), render, false);
        
        player = new Player("player", level.getEntities().getAvailableID(), 10000,
                 new Vector2f(0, level.getTopY(0)), playerController);
        
        setupLevel();
        
        positionCamera();
    }
    
    private void positionCamera() 
    {
        Display display = getDisplay();
        
        Vector2f camPos = player.getPosition().add(player.getWidth()/2, player.getHeight()/2);
        camPos.setY(height / 2f);
        camera.setPosition(camPos);
        
        if(camera.getPosition().getX() < display.getWidth() / 2 / camera.getPixelsPerUnit()) 
        {
            camera.getPosition().setX(display.getWidth() / 2 / camera.getPixelsPerUnit());
        }
        
        if(camera.getPosition().getX() > level.getWidth() - display.getWidth() / 2 / camera.getPixelsPerUnit()) 
        {
            camera.getPosition().setX(level.getWidth() - display.getWidth() / 2 / camera.getPixelsPerUnit());
        }
        
//        if(camera.getPosition().getY() < display.getHeight() / 2 / camera.getPixelsPerUnit()) 
//        {
//            camera.getPosition().setY(display.getHeight() / 2 / camera.getPixelsPerUnit());
//        }
//        
//        if(camera.getPosition().getY() > level.getHeight() -  display.getHeight() / 2 / camera.getPixelsPerUnit()) 
//        {
//            camera.getPosition().setY(level.getHeight() -  display.getHeight() / 2 / camera.getPixelsPerUnit());
//        }
    }
    
    private void initManagers() 
    {
        Resource.getTileManager().add(new BasicTile("air", 0, false, null, 0x66bbff));
        Resource.getTileManager().add(new BasicTile("grass", 1, true, "res/grass1.png", 0x11aa33));
        Resource.getTileManager().add(new BasicTile("dirt", 2, true, "res/dirt1.png", 0x7f7f00));
        Resource.getTileManager().add(new BasicTile("grass", 11, true, "res/grass2.png", 0x11aa33));
        Resource.getTileManager().add(new BasicTile("dirt", 12, true, "res/dirt2.png", 0x7f7f00));
        Resource.getTileManager().add(new BasicTile("grass", 21, true, "res/grass3.png", 0x11aa33));
        Resource.getTileManager().add(new BasicTile("dirt", 22, true, "res/dirt3.png", 0x7f7f00));
        Resource.getTileManager().add(new BasicTile("stone", 3, true, "res/stone1.png", 0x7f7f7f));
        Resource.getTileManager().add(new BasicTile("stone", 13, true, "res/stone2.png", 0x7f7f7f));
        Resource.getTileManager().add(new BasicTile("stone", 23, true, "res/stone3.png", 0x7f7f7f));
        Resource.getTileManager().add(new EndTile  ("end_tile", 4, "res/gold.png", 0xffd700));
        Resource.getTileManager().add(new BasicTile("boundry", 255, true, null, 0x3399cc));
    }
    
    private int skyR = 0x66;
    private int skyG = 0xbb;
    private int skyB = 0xff;
    
    private int numE = 5;
    private int width = 500;
    private int height = 800;
    private int enemyHealth = 50;
    private int levelNum = 0;
    private Font font = new Font(Font.MONOSPACED, Font.BOLD, 20);
    private int killed = 0;
    
    @Override
    public void update()
    {
        render.setWidth(getDisplay().getWidth());
        render.setHeight(getDisplay().getHeight());
        
        if(getKeyboard().isKeyDown(KeyEvent.VK_ESCAPE)) 
        {
            reset();
            getGame().setState("title_state");
        }
        
        killed += level.update(); 
        
        positionCamera();
        
        if(player.isTouchingEndTile()) 
        {
            setupLevel();
        }
        
        Object[] enemies = level.getEntities().getAll("simple_enemy");
        attackPlayer(enemies);
        enemies = level.getEntities().getAll("enemy_arrow");
        attackPlayer(enemies);
    }
    
    private void attackPlayer(Object[] enemies) 
    {
        for(Object o : enemies) 
        {
            Entity e = (Entity)o;
            if(e != null) 
            {
                if(Physics.collideEntities(player, e)) 
                {
                    int oldHealth = player.getHealth();
                    
                    player.changeHealth(-e.getDamage()); 
                    
                    if(oldHealth != player.getHealth()) player.applyKnockback(e);
                    
                    if(player.getHealth() <= 0) 
                    {
                        reset();
                        getGame().setState("title_state");
                    }
                    
                    if(e instanceof EnemyProjectile) e.flagForDelete();
                }
            }
        }
    }

    @Override
    public void render()
    {
        Display display = getDisplay();
        display.clear();
        
        camera.setPixelsPerUnit(display.getWidth() / 50);
        
        Graphics g = display.getGraphics();
        
        render.drawLevel(g, level);
        render.drawEntity(g, player);
        for(Object o : level.getEntities().toArray()) 
        {
            Entity e = (Entity)o;
            if(!(e == null))
                render.drawEntity(g, e); 
        }
        
        g.setColor(Color.BLACK);
        g.setFont(font);
        g.drawString("Level:          " + levelNum, 5, 20);
        g.drawString("Shots Fired:    " + player.getShotsFired(), 5, 40);
        g.drawString("Enemies Killed: " + killed, 5, 60);
        
        display.swapBuffers();
    }
    
    private void setupLevel() 
    {
        levelNum++;
        
        int col = skyR<<16|skyG<<8|skyB;
        Resource.getTileManager().get("air").setColorCode(col);
        Resource.getTileManager().get("boundry").setColorCode(col);
        
        level = new Level(width, level.getHeight());
        level.getEntities().setSize(1000);
        
        player.changeEnergy(player.getMaxEnergy() / 2);
        player.changeHealth(player.getMaxHealth() / 2);
        player.getVelocity().set(0, 0);
        player.getPosition().set(1, level.getTopY(1));
        
        player.setRechargeAmount((int)Math.ceil((levelNum+1)/2f));
        
        level.getEntities().add(player);
        
        player = (Player) level.getEntities().get(player.getID());
        
        for(int i = 0; i < numE; i++) 
        {
            int x = (int)(Math.random() * (level.getWidth() - 100)) + 100;
            int y = level.getTopY(x) + 2;
            Vector2f pos = new Vector2f(x, y);
            Entity e = new SimpleEnemyEntity(level.getEntities().getAvailableID(), enemyHealth, 1400, 0.8f, pos, level);
            level.getEntities().add(e);
        }
        
        for(int i = 0; i < levelNum/2 + 1; i++) 
        {
            int x = -(int)(Math.random() * 100) + level.getWidth() - 3;
            int y = level.getTopY(x) + 2;
            Vector2f pos = new Vector2f(x, y);
            Entity e = new ShootingEnemyEntity(level.getEntities().getAvailableID(), enemyHealth * 3, 1400, 0.8f, 240, pos, level);
            level.getEntities().add(e);
        }
        
        numE =  (int)(numE + 3);
        enemyHealth = (int)(enemyHealth + 50);
        
        skyR += 5;
        skyG -= 40;
        skyB -= 40;
        skyR = Math.min(255, skyR);
        skyG = Math.max(0, skyG);
        skyB = Math.max(0, skyB);
    }
}
