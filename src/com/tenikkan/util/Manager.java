package com.tenikkan.util;

import java.util.ArrayList;
import java.util.List;

public class Manager<T extends Identifiable> 
{
    private Object[] types;
    
    public Manager(int size) 
    {
        types = new Object[size];
    }
    
    public int size() { return types.length; }
    
    public void setSize(int size) 
    {
        types = new Object[size];
    }
    
    public void add(T type) 
    {
        if(inBounds(type.getID())) types[type.getID()] = type;
    }
    
    @SuppressWarnings("unchecked")
    public T remove(int id) 
    {
        if(!inBounds(id)) return null;
        T old = (T) types[id];
        types[id] = null;
        return old;
    }
    
    @SuppressWarnings("unchecked")
    public T get(int id) 
    {
        if(!inBounds(id)) return null;
        return (T) types[id];
    }
    
    @SuppressWarnings("unchecked")
    public T get(String name) 
    {
        for(Object o : types) 
        {
            T t = (T)o;
            if(t != null && t.getName().equalsIgnoreCase(name)) return t;
        }
        return null;
    }
    
    @SuppressWarnings("unchecked")
    public int getID(String name) 
    {
        for(int i = 0; i < types.length; i++) 
        {
            if(types[i] != null) 
            {
                if(((T)types[i]).getName().equalsIgnoreCase(name)) return i;
            }
        }
        return -1;
    }
    
    public int getAvailableID() 
    {
        for(int i = 0; i < types.length; i++) 
        {
            if(types[i] == null) 
            {
                return i;
            }
        }
        return -1;
    }
    
    public Object[] toArray() 
    {
        return types;
    }
    
    @SuppressWarnings( "unchecked" )
    public Object[] getAll(String name) 
    {
        List<Object> list = new ArrayList<Object>();
        
        for(Object o : types) 
        {
            T t = (T)o;
            if(t != null && t.getName().equalsIgnoreCase(name)) list.add(t);
        }
        
        return list.toArray(new Object[list.size()]);
    }
    
    private boolean inBounds(int id) 
    {
        return id >= 0 && id < types.length;
    }
}
