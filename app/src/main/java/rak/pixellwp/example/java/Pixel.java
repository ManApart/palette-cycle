package rak.pixellwp.example.java;

public class Pixel {
    private float x;
    private float y;
    private int index;

    public Pixel(float x, float y, int index){
        this.x = x;
        this.y = y;
        this.index = index;
    }

    public float getX(){
        return this.x;
    }

    public float getY(){
        return this.y;
    }

    public int getIndex(){
        return this.index;
    }
}
