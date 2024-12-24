package com.Josu.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.FitViewport;

public class MenuScreen implements Screen {
    private final Josu game;
    private FitViewport viewport;
    private SpriteBatch batch;
    private Texture background;
    private BitmapFont font;
    private Vector2 touchPos;
    private Texture imgCatch;
    private Texture imgStd;

    public MenuScreen(Josu game) {
        this.game = game;
    }

    @Override
    public void show() {
        viewport = new FitViewport(16, 9);
        batch = new SpriteBatch();
        background = new Texture("backgroundOsu.png"); // Fundo do menu
        imgStd = new Texture("circle.png");
        imgCatch = new Texture("fruitCatcher.png");
        font = new BitmapFont(); // Fonte padrão
        touchPos = new Vector2();

        // botando o cursor 
        Pixmap pixmap = new Pixmap(Gdx.files.internal("cursor.png")); // imagem puxada do assets
        Gdx.graphics.setCursor(Gdx.graphics.newCursor(pixmap, pixmap.getWidth() / 2, pixmap.getHeight() / 2)); // configura o cursor, define o hotspot, logo o centro, porq a altura e a largura / 2 resulta no meio, newCursor() é a instancia do mouse com o hotspot.
        pixmap.dispose(); // retira a memória da imagem ??? (boa prática pra evitar erros, imagino)
    }

    @Override
    public void render(float delta) {
        input();
        draw();
    }

    private void input(){
        if (Gdx.input.isTouched()) {
            float mouseX = Gdx.input.getX();
            float mouseY = Gdx.graphics.getHeight() - Gdx.input.getY();

            if (mouseY > Gdx.graphics.getHeight() / 2f + 20 && mouseY < Gdx.graphics.getHeight() / 2f + 60) {
                game.setScreen(new GameScreen(game)); // Vai para o jogo
            } else if (mouseY > Gdx.graphics.getHeight() / 2f - 30 && mouseY < Gdx.graphics.getHeight() / 2f + 10) {
                game.setScreen(new JosuCatch(game));
            } else if (mouseY > Gdx.graphics.getHeight() / 2f - 80 && mouseY < Gdx.graphics.getHeight() / 2f - 40) {
                Gdx.app.exit(); // Sai do jogo
            }
        }
    }

    private void draw(){
        ScreenUtils.clear(Color.BLACK);
        viewport.apply();
        batch.setProjectionMatrix(viewport.getCamera().combined);
        float newWidth = viewport.getWorldWidth() / 5;
        float newHeight = viewport.getWorldHeight() / 5;

        batch.begin();

        batch.draw(background, 0, 0, viewport.getWorldWidth(), viewport.getWorldHeight());
        batch.draw(imgCatch, (viewport.getWorldWidth() - newWidth) / 2, (viewport.getWorldHeight() - newHeight) / 2, newWidth, newHeight);
        // batch.draw(imgStd , 0, 0, viewport.getWorldWidth(), viewport.getWorldHeight());

        batch.end();
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
        batch.dispose();
        background.dispose();
        font.dispose();
    }
}
