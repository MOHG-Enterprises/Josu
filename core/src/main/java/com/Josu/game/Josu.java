package com.Josu.game;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;

public class Josu extends Game {
    @Override
    public void create() {
        Gdx.graphics.setWindowedMode(1920, 1080); // settei para começar em tela cheia já, não sei porq nao está aparecendo a barra de cima ainda
        this.setScreen(new MenuScreen(this)); // Inicia no menu principal
    }

    @Override
    public void render() {
        super.render(); // Renderiza a tela ativa
    }

    @Override
    public void dispose() {
        super.dispose();
    }
}
