package com.Josu.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.ScreenUtils;

public class MenuScreen implements Screen {
    private final Josu game;
    private SpriteBatch batch;
    private Texture background;
    private BitmapFont font;

    public MenuScreen(Josu game) {
        this.game = game;
    }

    @Override
    public void show() {
        Gdx.graphics.setWindowedMode(1920, 1080); // settei para começar em tela cheia já, não sei porq nao está aparecendo a barra de cima ainda
        batch = new SpriteBatch();
        background = new Texture("backgroundOsu.png"); // Fundo do menu
        font = new BitmapFont(); // Fonte padrão

        // botando o cursor 
        Pixmap pixmap = new Pixmap(Gdx.files.internal("cursor.png")); // imagem puxada do assets
        Gdx.graphics.setCursor(Gdx.graphics.newCursor(pixmap, pixmap.getWidth() / 2, pixmap.getHeight() / 2)); // configura o cursor, define o hotspot, logo o centro, porq a altura e a largura / 2 resulta no meio, newCursor() é a instancia do mouse com o hotspot.
        pixmap.dispose(); // retira a memória da imagem ??? (boa prática pra evitar erros, imagino)
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0.1f, 0.1f, 0.15f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        batch.begin();
        batch.draw(background, 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        font.draw(batch, "1. Josu!Standart", Gdx.graphics.getWidth() / 2f - 50, Gdx.graphics.getHeight() / 2f + 40);
        font.draw(batch, "2. Josu!Catch", Gdx.graphics.getWidth() / 2f - 50, Gdx.graphics.getHeight() / 2f - 10);
        font.draw(batch, "3. Sair", Gdx.graphics.getWidth() / 2f - 50, Gdx.graphics.getHeight() / 2f - 60);
        batch.end();

        if (Gdx.input.isTouched()) {
            float mouseX = Gdx.input.getX();
            float mouseY = Gdx.graphics.getHeight() - Gdx.input.getY();

            if (mouseY > Gdx.graphics.getHeight() / 2f + 20 && mouseY < Gdx.graphics.getHeight() / 2f + 60) {
                game.setScreen(new GameScreen(game)); // Vai para o jogo
            } else if (mouseY > Gdx.graphics.getHeight() / 2f - 30 && mouseY < Gdx.graphics.getHeight() / 2f + 10) {
                System.out.println("josucatch");
            } else if (mouseY > Gdx.graphics.getHeight() / 2f - 80 && mouseY < Gdx.graphics.getHeight() / 2f - 40) {
                Gdx.app.exit(); // Sai do jogo
            }
        }
    }

    @Override
    public void resize(int width, int height) {}

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
