package com.tenikkan.escape.state;

import com.tenikkan.escape.graphics.Display;
import com.tenikkan.util.GameLoop;
import com.tenikkan.util.Manager;

public class StateBasedGame extends GameLoop
{
    private Manager<GameState> states;
    private int curID;
    private Display display;
    
    public StateBasedGame(double fps, double ticks, Display display)
    {
        super(fps, ticks);
        states = new Manager<GameState>(256);
        this.display = display;
    }
    
    public void addState(GameState state) 
    {
        states.add(state);
    } 
    
    public void setState(String name) 
    {
        curID = states.getID(name); 
        reset();
    }
    
    public Display getDisplay() 
    {
        return display;
    }
    
    public void reset() 
    {
        GameState state = states.get(curID);
        if(state != null) state.reset();
    }
    
    @Override
    public void update()
    {
        display.update();
        GameState state = states.get(curID);
        if(state != null) state.update();
    }

    @Override
    public void render()
    {
        GameState state = states.get(curID);
        if(state != null) state.render();
    }

    @Override
    public void init()
    {
        
    }
    
}
