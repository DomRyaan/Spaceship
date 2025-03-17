package com.spaceship;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Input;

import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.TimeUtils;

import java.util.Iterator;

/** {@link com.badlogic.gdx.ApplicationListener} implementation shared by all platforms. */
public class Spaceship extends ApplicationAdapter {
    SpriteBatch batch;
    Texture image, imgNave, imgEnemy, tMissil;
    Sprite nave;
    float posX, posY, velocity;
    private Array<Missel> misseis = new Array<>();
    private Array<Rectangle> enemies;
    private long lastEnemyTime;

    @Override
    public void create() {
        batch = new SpriteBatch();
        image = new Texture("bg.png");
        imgNave = new Texture("spaceship.png");
        nave = new Sprite(imgNave);
        posX = 0;
        posY = 0;
        velocity = 10;
        tMissil = new Texture("missile.png");
        imgEnemy = new Texture("enemy.png");
        enemies = new Array<Rectangle>();
        lastEnemyTime = 0;
    }

    @Override
    public void render() {
        this.movaNave();
        this.moveMissil();
        this.moveEnemy();
        ScreenUtils.clear(0.15f, 0.15f, 0.2f, 1f);
        batch.begin();
        batch.draw(image, 0, 0);
        if(misseis != null){
            for (Missel missel : misseis){
                missel.desenhar(batch);
            }
        }
        batch.draw(nave, posX, posY);
        for (Rectangle enemy : enemies){
            batch.draw(imgEnemy, enemy.x, enemy.y);
        }
        batch.end();
    }

    @Override
    public void dispose() {
        batch.dispose();
        image.dispose();
        imgNave.dispose();
        imgEnemy.dispose();
        tMissil.dispose();
    }

    public void movaNave(){
        if (Gdx.input.isKeyPressed(Input.Keys.D)) {
            if (posX < Gdx.graphics.getWidth() - nave.getWidth()) {
                posX += velocity;
            }
        }
        if (Gdx.input.isKeyPressed(Input.Keys.A)) {
            if (posX > 0) {
                posX -= velocity;
            }
        }
        if (Gdx.input.isKeyPressed(Input.Keys.W)) {
            if (posY < Gdx.graphics.getHeight() - nave.getHeight()) {
                posY += velocity;
            }
        }
        if (Gdx.input.isKeyPressed(Input.Keys.S)) {
            if (posY > 0) {
                posY -= velocity;
            }
        }
    }

    public void moveMissil(){
        if (Gdx.input.isKeyJustPressed(Input.Keys.ENTER)) {
            Missel novoMissil = new Missel(tMissil, posX + nave.getWidth() / 2, posY + nave.getHeight() / 2 - 11);
            misseis.add(novoMissil);
        }
        for(int i = misseis.size - 1; i >= 0; i--){
            Missel missil = misseis.get(i);
            missil.atualizar();
            if(missil.getMisX() > image.getWidth()){
                misseis.removeIndex(i);
                missil = null;
            }
        }
    }

    public void spawmEnemy(){
        Rectangle enemy = new Rectangle(image.getWidth(), MathUtils.random(0, image.getHeight() - imgEnemy.getHeight()), imgEnemy.getWidth(), imgEnemy.getHeight());
        enemies.add(enemy);
        lastEnemyTime = TimeUtils.nanoTime();
    }

    public void moveEnemy(){
        if (TimeUtils.nanoTime() - lastEnemyTime > 1000000000) {
            this.spawmEnemy();
        }
        for(Iterator<Rectangle> i = enemies.iterator(); i.hasNext();){
            Rectangle enemy = i.next();
            enemy.x -= 400 * Gdx.graphics.getDeltaTime();
            if (enemy.x + imgEnemy.getWidth() < 0){
                i.remove();
            }
        }
    }
}
