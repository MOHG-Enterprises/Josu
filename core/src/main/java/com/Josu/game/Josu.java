package com.Josu.game;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.ScreenUtils;

/** {@link com.badlogic.gdx.ApplicationListener} implementation shared by all platforms. */
public class Josu extends ApplicationAdapter {
    private SpriteBatch batch;
    private Texture image;

    @Override
    public void create() {
        Gdx.graphics.setWindowedMode(1920, 1080); // settei para começar em tela cheia já, não sei porq nao está aparecendo a barra de cima ainda
        batch = new SpriteBatch();
        image = new Texture("backgroundOsu.png");
    }

    @Override
    public void render() { // calcular a escala proporcional, mantendo o aspecto da imagem, "codigo de baixo"
        ScreenUtils.clear(0.15f, 0.15f, 0.2f, 1f);
        batch.begin();
        
        float scale = Math.min(Gdx.graphics.getWidth() / (float) image.getWidth(),
                               Gdx.graphics.getHeight() / (float) image.getHeight());
        float x = (Gdx.graphics.getWidth() - image.getWidth() * scale) / 2;
        float y = (Gdx.graphics.getHeight() - image.getHeight() * scale) / 2;
    
        batch.draw(image, x, y, image.getWidth() * scale, image.getHeight() * scale);
        batch.end();
    }

    @Override
    public void dispose() {
        batch.dispose();
        image.dispose();
    }
}
