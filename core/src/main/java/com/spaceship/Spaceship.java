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
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import  com.badlogic.gdx.graphics.Color;


import java.util.Iterator;

/** {@link com.badlogic.gdx.ApplicationListener} implementation shared by all platforms. */
public class Spaceship extends ApplicationAdapter {
    private SpriteBatch batch;
    private Texture image, imgNave, imgEnemy, imgMissil;
    private  Sprite nave;
    private float posX, posY, velocity;
    private Array<Rectangle> misseis;
    private Array<Rectangle> enemies;
    private long lastEnemyTime;
    private int score, power;
    private boolean gameOver;

    private FreeTypeFontGenerator generator;
    private FreeTypeFontGenerator.FreeTypeFontParameter parameter;
    private BitmapFont bitmapFont;

    @Override
    public void create() {
        batch = new SpriteBatch();
        image = new Texture("bg.png");
        imgNave = new Texture("spaceship.png");
        nave = new Sprite(imgNave);
        gameOver = false;
        posX = 0;
        posY = 0;
        velocity = 10;
        imgMissil = new Texture("missile.png");
        misseis = new Array<Rectangle>();
        imgEnemy = new Texture("enemy.png");
        enemies = new Array<Rectangle>();
        lastEnemyTime = 0;
        score = 0;
        power = 3;

        generator = new FreeTypeFontGenerator(Gdx.files.internal("font.ttf"));
        parameter = new FreeTypeFontGenerator.FreeTypeFontParameter();

        parameter.size = 30;
        parameter.borderWidth = 1;
        parameter.borderColor = Color.GREEN;
        parameter.color = Color.WHITE;
        bitmapFont = generator.generateFont(parameter);
    }

    @Override
    public void render() {
        this.movaNave();
        this.moveMissil();
        this.moveEnemy();
        ScreenUtils.clear(0.15f, 0.15f, 0.2f, 1f);
        batch.begin();
        batch.draw(image, 0, 0);

        if(!gameOver){
            for (Rectangle missil : misseis){
                batch.draw(imgMissil, missil.x, missil.y);
            }

            batch.draw(nave, posX, posY);

            for (Rectangle enemy : enemies){
                batch.draw(imgEnemy, enemy.x, enemy.y);
            }
            bitmapFont.draw(batch, "Score: " + score, 20, Gdx.graphics.getHeight() - 20);
            bitmapFont.draw(batch, "Power: " + power, image.getWidth() - 150, Gdx.graphics.getHeight() - 20);
        } else {
            bitmapFont.draw(batch, "Score: " + score, 20, Gdx.graphics.getHeight() - 20);
            bitmapFont.draw(batch, "GAME OVER", image.getWidth() - 150, Gdx.graphics.getHeight() - 20);

            if (Gdx.input.isKeyPressed(Input.Keys.ENTER)){
                score = 0;
                power = 3;
                posY = 0;
                posX = 0;
                enemies.clear();
                gameOver = false;
            }

        }
        batch.end();
    }

    @Override
    public void dispose() {
        batch.dispose();
        image.dispose();
        imgNave.dispose();
        imgEnemy.dispose();
        imgMissil.dispose();
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
            Rectangle missil = new Rectangle(posX + nave.getWidth() / 2, posY + nave.getHeight() / 2 - 11, imgMissil.getWidth(), imgMissil.getHeight());
            misseis.add(missil);
        }

        for (Iterator<Rectangle> i = misseis.iterator(); i.hasNext(); ) {
            Rectangle missil = i.next();
            missil.x += 20;
            if (missil.x + imgMissil.getWidth() > Gdx.graphics.getWidth()) {
                i.remove();
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

        // Verifica a colisão entre os inimigos e os misseis, um por um, para garantir que todos sigam a logica da colisão
        for(Iterator<Rectangle> i = enemies.iterator(); i.hasNext();){
            Rectangle enemy = i.next();
            enemy.x -= 400 * Gdx.graphics.getDeltaTime();
            if(colisao(enemy.x, enemy.y, enemy.width, enemy.height, posX, posY, imgNave.getWidth(), imgNave.getHeight())){
                if (power > 0){
                --power;
                i.remove();
                }else{
                    gameOver = true;
                }

            }

            for(Iterator<Rectangle> j = misseis.iterator(); j.hasNext();){
                Rectangle missil = j.next();

                if (colisao(enemy.x, enemy.y, enemy.width, enemy.height,
                    missil.x, missil.y, missil.width, missil.height)){
                    ++score;
                    i.remove();
                    j.remove();
                    break;
                }
            }

            if (enemy.x + imgEnemy.getWidth() < 0){
                i.remove();
            }
        }
    }

    public boolean colisao(float posX1, float posY1, float w1, float h1, float posX2, float posY2, float w2, float h2){
        return posX1 + w1 > posX2 && posX1 < posX2 + w2 &&
            posY1 + h1 > posY2 && posY1 < posY2 + h2;
    }


}
