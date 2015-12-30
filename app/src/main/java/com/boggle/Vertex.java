package com.boggle;

import java.util.ArrayList;

public class Vertex {
    public final String item;
    public ArrayList<Vertex> neighbors = new ArrayList<Vertex>();
    boolean visited;

    public Iterable<Vertex> getVertices(){
        return neighbors;
    }

    public boolean isVisited(){
        return visited;
    }

    public boolean inNeighbor(Vertex v)
    {
        if(neighbors.contains(v))
            return true;
        else
            return false;
    }

    public String visit(){
        return item;
    }

    public Vertex(String item) {
        this.item = item;
    }

    public void addNeighbor(Vertex neighbor) {
        neighbors.add(neighbor);
    }

    public String printnb ()
    {
        String s="";
        for(Vertex v: neighbors)
        {
            s +=v.item;
        }
        return s;
    }

    public String toString()
    {



        return "Vertex:"+item+" neighbors"+printnb()+"\n";
    }
}
