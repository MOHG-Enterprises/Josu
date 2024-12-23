package com.Josu.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.Screen;

public class JosuCatch implements Screen {
    private final Josu game; // Referência ao jogo principal
    private SpriteBatch spriteBatch;
    private FitViewport viewport;
    private Texture backgroundTexture;
    private Texture fruitCatcherTexture;
    private Texture dropTexture;
    private Sound dropSound;
    private Music music;
    private Sprite fruitCatcherSprite;
    private Array<Sprite> dropSprites;
    private Vector2 touchPos;
    private Rectangle fruitCatcherRectangle;
    private Rectangle dropRectangle;
    private float dropTimer;

    public JosuCatch(Josu game) {
        this.game = game;
    }

    @Override
    public void show() {
        spriteBatch = new SpriteBatch();
        viewport = new FitViewport(8, 5);
        backgroundTexture = new Texture("background.png");
        fruitCatcherTexture = new Texture("fruitCatcher.png");
        dropTexture = new Texture("drop.png");
        dropSound = Gdx.audio.newSound(Gdx.files.internal("drop.mp3"));
        music = Gdx.audio.newMusic(Gdx.files.internal("music.mp3"));
        music.setLooping(true);
        music.setVolume(0.5f);
        music.play();

        fruitCatcherSprite = new Sprite(fruitCatcherTexture);
        fruitCatcherSprite.setSize(1, 1);
        touchPos = new Vector2();
        dropSprites = new Array<>();
        fruitCatcherRectangle = new Rectangle();
        dropRectangle = new Rectangle();
    }

    @Override
    public void render(float delta) {
        input();
        logic(delta);
        draw();

        // Voltar ao menu principal
        if (Gdx.input.isKeyPressed(Input.Keys.ESCAPE)) {
            game.setScreen(new MenuScreen(game));
        }
    }

    private void input() {
        float speed = 4f;
        float delta = Gdx.graphics.getDeltaTime();

        if (Gdx.input.isKeyPressed(Input.Keys.RIGHT)) {
            fruitCatcherSprite.translateX(speed * delta);
        }
        if (Gdx.input.isKeyPressed(Input.Keys.LEFT)) {
            fruitCatcherSprite.translateX(-speed * delta);
        }
        if (Gdx.input.isTouched()) {
            touchPos.set(Gdx.input.getX(), Gdx.input.getY());
            viewport.unproject(touchPos);
            fruitCatcherSprite.setCenterX(touchPos.x);
        }
    }

    private void logic(float delta) {
        float worldWidth = viewport.getWorldWidth();
        float fruitCatcherWidth = fruitCatcherSprite.getWidth();

        // Manter o sprite do catcher dentro da tela
        fruitCatcherSprite.setX(MathUtils.clamp(fruitCatcherSprite.getX(), 0, worldWidth - fruitCatcherWidth));

        // Atualizar a posição e detectar colisões
        fruitCatcherRectangle.set(
            fruitCatcherSprite.getX(),
            fruitCatcherSprite.getY(),
            fruitCatcherSprite.getWidth(),
            fruitCatcherSprite.getHeight()
        );

        for (int i = dropSprites.size - 1; i >= 0; i--) {
            Sprite dropSprite = dropSprites.get(i);
            dropSprite.translateY(-2f * delta);

            dropRectangle.set(dropSprite.getX(), dropSprite.getY(), dropSprite.getWidth(), dropSprite.getHeight());

            if (dropSprite.getY() < -dropSprite.getHeight()) {
                dropSprites.removeIndex(i);
            } else if (fruitCatcherRectangle.overlaps(dropRectangle)) {
                dropSprites.removeIndex(i);
                dropSound.play();
            }
        }

        dropTimer += delta;
        if (dropTimer > 0.22f) {
            dropTimer = 0;
            createDroplet();
        }
    }

    private void draw() {
        ScreenUtils.clear(Color.BLACK);
        viewport.apply();
        spriteBatch.setProjectionMatrix(viewport.getCamera().combined);

        spriteBatch.begin();
        spriteBatch.draw(backgroundTexture, 0, 0, viewport.getWorldWidth(), viewport.getWorldHeight());
        fruitCatcherSprite.draw(spriteBatch);

        for (Sprite dropSprite : dropSprites) {
            dropSprite.draw(spriteBatch);
        }
        spriteBatch.end();
    }

    private void createDroplet() {
        float dropWidth = 1;
        float dropHeight = 1;
        float worldWidth = viewport.getWorldWidth();

        Sprite dropSprite = new Sprite(dropTexture);
        dropSprite.setSize(dropWidth, dropHeight);
        dropSprite.setX(MathUtils.random(0f, worldWidth - dropWidth));
        dropSprite.setY(viewport.getWorldHeight());
        dropSprites.add(dropSprite);
    }

    @Override
    public void resize(int width, int height) {
        viewport.update(width, height, true);
    }

    @Override
    public void pause() {}

    @Override
    public void resume() {}

    @Override
    public void hide() {
        dispose();
    }

    @Override
    public void dispose() {
        spriteBatch.dispose();
        backgroundTexture.dispose();
        fruitCatcherTexture.dispose();
        dropTexture.dispose();
        dropSound.dispose();
        music.dispose();
    }
}
