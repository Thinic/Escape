/**
 * 
 */
package com.tenikkan.arcana.entity;

import com.tenikkan.math.Vector2f;

/**
 * @author Nicholas Hamilton
 *
 */
public class Player extends Entity
{   
    public Player(String name, Vector2f position) 
    {
        super(name, 0xff00ff, 2.0f, 3.0f, 0.4f, position, new Vector2f(0, 0));
    }
}