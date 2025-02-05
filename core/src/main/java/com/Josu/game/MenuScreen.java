package com.josu.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.josu.game.standard.screens.GameScreen;

public class MenuScreen implements Screen {
    private final Josu game;
    private ScreenViewport viewport;
    private SpriteBatch batch;
    private ShapeRenderer shapeRenderer;
    private Texture background;
    private BitmapFont font;

    public MenuScreen(Josu game) {
        this.game = game;
    }

    @Override
    public void show() {
        viewport = game.getViewport();
        batch = new SpriteBatch();
        shapeRenderer = new ShapeRenderer();
        
        background = new Texture("images/backgroundOsu.png");
        Pixmap pixmap = new Pixmap(Gdx.files.internal("images/cursor.png"));
        Gdx.graphics.setCursor(Gdx.graphics.newCursor(pixmap, pixmap.getWidth() / 2, pixmap.getHeight() / 2));
        font = new BitmapFont();
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        viewport.apply();
        batch.setProjectionMatrix(game.getCamera().combined);

        batch.begin();
        batch.draw(background, 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        batch.end();

        drawDarkContainer();

        batch.begin();
        font.draw(batch, "1. Josu! Standard Mode", Gdx.graphics.getWidth() / 2f - 100, Gdx.graphics.getHeight() / 2f + 50);
        font.draw(batch, "2. Josu! Catch Mode", Gdx.graphics.getWidth() / 2f - 100, Gdx.graphics.getHeight() / 2f);
        font.draw(batch, "3. Exit", Gdx.graphics.getWidth() / 2f - 100, Gdx.graphics.getHeight() / 2f - 50);
        batch.end();

        handleInput();
    }

    private void drawDarkContainer() {
        Gdx.gl.glEnable(GL20.GL_BLEND);
        shapeRenderer.setProjectionMatrix(game.getCamera().combined);
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        shapeRenderer.setColor(0, 0, 0, 0.5f);

        float containerX = Gdx.graphics.getWidth() / 2f - 150;
        float containerY = Gdx.graphics.getHeight() / 2f - 80;
        float containerWidth = 300;
        float containerHeight = 180;

        shapeRenderer.rect(containerX, containerY, containerWidth, containerHeight);
        shapeRenderer.end();
        Gdx.gl.glDisable(GL20.GL_BLEND);
    }

    private void handleInput() {
        if (Gdx.input.isTouched()) {
            float mouseX = Gdx.input.getX();
            float mouseY = Gdx.graphics.getHeight() - Gdx.input.getY();

            if (mouseY > Gdx.graphics.getHeight() / 2f + 30 && mouseY < Gdx.graphics.getHeight() / 2f + 70) {
                game.setScreen(new GameScreen(game)); // Standard Mode
            } else if (mouseY > Gdx.graphics.getHeight() / 2f - 20 && mouseY < Gdx.graphics.getHeight() / 2f + 20) {
                // game.setScreen(new CatchModeScreen(game)); // Placeholder for Catch Mode
            } else if (mouseY > Gdx.graphics.getHeight() / 2f - 70 && mouseY < Gdx.graphics.getHeight() / 2f - 30) {
                Gdx.app.exit(); // Exit Game
            }
        }

        if (Gdx.input.isKeyPressed(Input.Keys.ESCAPE)) {
            game.setScreen(new MenuScreen(game));
        }
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
        shapeRenderer.dispose();
        background.dispose();
        font.dispose();
    }
}
