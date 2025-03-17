package com.spaceship;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class Missel {
    private Sprite sprite;
    private float misX;
    private float misY;
    private float velocidade;

    public Missel(Texture texture, float misX, float misY){
        this.sprite = new Sprite(texture);
        this.misX = misX;
        this.misY = misY;
        this.velocidade = 20;
    }

    public void atualizar(){
        misX += velocidade;
    }

    public float getMisX(){
        return misX;
    }
    public float getMisY(){
        return misY;
    }

    public void desenhar(SpriteBatch batch){
        batch.draw(sprite, misX, misY);
    }
}
