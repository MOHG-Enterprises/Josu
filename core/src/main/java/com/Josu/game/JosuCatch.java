package com.Josu.game;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.FitViewport;


/** {@link com.badlogic.gdx.ApplicationListener} implementation shared by all platforms. */
public class JosuCatch extends ApplicationAdapter {
    SpriteBatch spriteBatch;
    FitViewport viewport;
    Texture backgroundTexture;
    Texture fruitCatcherTexture;
    Texture dropTexture;
    Sound dropSound;
    Music music;
    Sprite fruitCatcherSprite;
    Vector2 touchPos;
    Array<Sprite> dropSprites;
    float dropTimer;
    Rectangle fruitCatcherRectangle;
    Rectangle dropRectangle;
    
    @Override
    public void create() {
        spriteBatch = new SpriteBatch();
        viewport = new FitViewport(8, 5);
        backgroundTexture = new Texture("background.png");
        fruitCatcherTexture = new Texture("fruitCatcher.png");
        dropTexture = new Texture("drop.png");
        dropSound = Gdx.audio.newSound(Gdx.files.internal("drop.mp3"));
        music = Gdx.audio.newMusic(Gdx.files.internal("music.mp3"));
        fruitCatcherSprite = new Sprite(fruitCatcherTexture);
        fruitCatcherSprite.setSize(1, 1);
        touchPos = new Vector2();
        dropSprites = new Array<>();
        fruitCatcherRectangle = new Rectangle();
        dropRectangle = new Rectangle();
        music.setLooping(true);
        music.setVolume(.5f);
        music.play();
    }

    @Override
    public void render() {
        input();
        logic();
        draw();
    }
    
    private void input() {
        float speed = 4f;
        float delta = Gdx.graphics.getDeltaTime();

        if (Gdx.input.isKeyPressed(Input.Keys.RIGHT)){
            fruitCatcherSprite.translateX(speed * delta);
        }
        if (Gdx.input.isKeyPressed(Input.Keys.LEFT)){
            fruitCatcherSprite.translateX(-speed * delta);
        }
        if (Gdx.input.isTouched()) {
            touchPos.set(Gdx.input.getX(), Gdx.input.getY()); // Get where the touch happened on screen
            viewport.unproject(touchPos); // Convert the units to the world units of the viewport
            fruitCatcherSprite.setCenterX(touchPos.x); // Change the horizontally centered position of the fruitCatcher
        }
    }
    
    private void logic() {
        float worldWidth = viewport.getWorldWidth();
        float worldHeight = viewport.getWorldHeight();
        float fruitCatcherWidth = fruitCatcherSprite.getWidth();
        float fruitCatcherHeight = fruitCatcherSprite.getHeight();
    
        fruitCatcherSprite.setX(MathUtils.clamp(fruitCatcherSprite.getX(), 0, worldWidth - fruitCatcherWidth));
    
        float delta = Gdx.graphics.getDeltaTime();
        // Apply the fruitCatcher position and size to the fruitCatcherRectangle
        fruitCatcherRectangle.set(fruitCatcherSprite.getX(), fruitCatcherSprite.getY(), fruitCatcherWidth, fruitCatcherHeight);
    
        for (int i = dropSprites.size - 1; i >= 0; i--) {
            Sprite dropSprite = dropSprites.get(i);
            float dropWidth = dropSprite.getWidth();
            float dropHeight = dropSprite.getHeight();
    
            dropSprite.translateY(-2f * delta);
            // Apply the drop position and size to the dropRectangle
            dropRectangle.set(dropSprite.getX(), dropSprite.getY(), dropWidth, dropHeight);
    
            if (dropSprite.getY() < -dropHeight) dropSprites.removeIndex(i);
            else if (fruitCatcherRectangle.overlaps(dropRectangle)) { // Check if the fruitCatcher overlaps the drop
                dropSprites.removeIndex(i); // Remove the drop
                dropSound.play();
            }
        }
    
        dropTimer += delta;
        if (dropTimer > .22f) {
            dropTimer = 0;
            createDroplet();
        }
    }

    private void draw() {
        ScreenUtils.clear(Color.BLACK);
        viewport.apply();
        spriteBatch.setProjectionMatrix(viewport.getCamera().combined);

        spriteBatch.begin();
        //{
        
        float worldWidth = viewport.getWorldWidth();
        float worldHeight = viewport.getWorldHeight();

        spriteBatch.draw(fruitCatcherTexture, 0, 0, 1, 1); // draw the fruitCatcher
        spriteBatch.draw(backgroundTexture, 0, 0, worldWidth, worldHeight); // draw the background
        fruitCatcherSprite.draw(spriteBatch);

        for (Sprite dropSprite : dropSprites) {
            dropSprite.draw(spriteBatch);
        }

         //}
        spriteBatch.end();
    }

    private void createDroplet() {
        // create local variables for convenience
        float dropWidth = 1;
        float dropHeight = 1;
        float worldWidth = viewport.getWorldWidth();
        float worldHeight = viewport.getWorldHeight();
        
        // create the drop sprite
        Sprite dropSprite = new Sprite(dropTexture);
        dropSprite.setSize(dropWidth, dropHeight);
        dropSprite.setX(MathUtils.random(0f, worldWidth - dropWidth));
        dropSprite.setY(worldHeight);
        dropSprites.add(dropSprite); // Add it to the list
    }

    @Override
    public void resize(int width, int height) {
        viewport.update(width, height, true); // true centers the camera
    }

    @Override
    public void dispose() {
    }
}