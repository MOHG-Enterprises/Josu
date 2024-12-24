package com.Josu.game;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.utils.viewport.FitViewport;

public class Josu extends Game {
    FitViewport viewport;
    OrthographicCamera camera;

    @Override
    public void create() {
        // Inicializa a câmera e o viewport
        camera = new OrthographicCamera();
        viewport = new FitViewport(8, 5, camera); // Define o mundo como 8x5 unidades (ajuste conforme necessário)

        // Configura o jogo para iniciar em tela cheia
        // if (!Gdx.graphics.isFullscreen()) {
        //     Gdx.graphics.setFullscreenMode(Gdx.graphics.getDisplayMode());
        // }

        this.setScreen(new MenuScreen(this)); // Inicia no menu principal
    }

    @Override
    public void render() {
        // Atualiza a câmera
        camera.update();
        super.render(); // Renderiza a tela ativa
    }

    @Override
    public void resize(int width, int height) {
        // Atualiza o viewport para refletir a nova resolução
        viewport.update(width, height, true);
        super.resize(width, height);
    }

    @Override
    public void dispose() {
        super.dispose();
    }

    public FitViewport getViewport() {
        return viewport; // Fornece o viewport para as outras telas
    }

    public OrthographicCamera getCamera() {
        return camera; // Fornece a câmera para as outras telas
    }
}
