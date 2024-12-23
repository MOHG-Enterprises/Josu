package com.Josu.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.ScreenUtils;

public class GameScreen implements Screen {
    private final Josu game;
    private SpriteBatch batch;
    private Texture backgroundImage;
    private Texture circleImage;
    private Texture circleImage2;
    private Texture[] countdownImages; // Array para as imagens do contador
    private BitmapFont font;
    private float circleX, circleY;
    private float circleRadius;
    private int countdownIndex; // Índice para o contador
    private float countdownTimer; // Controla o tempo do contador
    private boolean circleVisible;
    private float circleTimer; // Controla o tempo do círculo
    private int points;
    private int circleColorIndex; // Índice para controlar as cores
    private final float[][] circleColors = {
        {0.5f, 0f, 0.5f, 1f}, // Roxo
        {0f, 0.5f, 0f, 1f},   // Verde
        {0f, 0f, 1f, 1f},     // Azul
        {1f, 0f, 0f, 1f},     // Vermelho
        {1f, 0.5f, 0f, 1f}    // Laranja
    };

    public GameScreen(Josu game) {
        this.game = game;
    }

    @Override
    public void show() {
        circleColorIndex = 0;
        
        Gdx.graphics.setWindowedMode(1920, 1080);
        batch = new SpriteBatch();
        backgroundImage = new Texture("backgroundOsu.png");
        circleImage = new Texture("circle.png");
        circleImage2 = new Texture("hitcircle.png");

        // Inicializa as imagens do contador
        countdownImages = new Texture[4];
        countdownImages[0] = new Texture("numbers/3.png");
        countdownImages[1] = new Texture("numbers/2.png");
        countdownImages[2] = new Texture("numbers/1.png");
        countdownImages[3] = new Texture("numbers/GO.png");

        font = new BitmapFont();
        font.getData().setScale(2);

        // Inicializa as variáveis
        circleRadius = circleImage.getWidth() / 2f;
        circleX = (Gdx.graphics.getWidth() - circleImage.getWidth()) / 2f;
        circleY = (Gdx.graphics.getHeight() - circleImage.getHeight()) / 2f;
        countdownIndex = 0; // Começa no "3"
        countdownTimer = 1f; // Cada imagem do contador dura 1 segundo
        circleVisible = false;
        circleTimer = 3f; // O círculo dura 3 segundos
        points = 0;
    }

    @Override
    public void render(float delta) {
        ScreenUtils.clear(0.15f, 0.15f, 0.2f, 1f);
        batch.begin();
    
        // Desenha o fundo
        float scale = Math.min(
            Gdx.graphics.getWidth() / (float) backgroundImage.getWidth(),
            Gdx.graphics.getHeight() / (float) backgroundImage.getHeight()
        );
        float x = (Gdx.graphics.getWidth() - backgroundImage.getWidth() * scale) / 2;
        float y = (Gdx.graphics.getHeight() - backgroundImage.getHeight() * scale) / 2;
        batch.draw(backgroundImage, x, y, backgroundImage.getWidth() * scale, backgroundImage.getHeight() * scale);
    
        // Camada escura semi-transparente
        batch.setColor(0, 0, 0, 0.5f);
        batch.draw(backgroundImage, x, y, backgroundImage.getWidth() * scale, backgroundImage.getHeight() * scale);
        batch.setColor(1, 1, 1, 1); // Reseta para branco
    
        // Contador
        if (countdownIndex < countdownImages.length) {
            Texture currentImage = countdownImages[countdownIndex];
            float imageWidth = currentImage.getWidth();
            float imageHeight = currentImage.getHeight();
            float centerX = (Gdx.graphics.getWidth() - imageWidth) / 2;
            float centerY = (Gdx.graphics.getHeight() - imageHeight) / 2;
            batch.draw(currentImage, centerX, centerY);
    
            // Atualiza o contador
            countdownTimer -= delta;
            if (countdownTimer <= 0) {
                countdownTimer = 1f; // Reinicia o timer
                countdownIndex++; // Avança para a próxima imagem
            }
        } else if (circleVisible) {
            // Exibe o círculo com cor dinâmica
            batch.draw(circleImage, circleX, circleY);
    
            // Define a cor dinâmica para o circleImage2
            float[] color = circleColors[circleColorIndex];
            batch.setColor(color[0], color[1], color[2], color[3]);
            batch.draw(circleImage2, circleX, circleY);
            batch.setColor(1, 1, 1, 1); // Reseta para branco
    
            // Atualiza o tempo de exibição do círculo
            circleTimer -= delta;
            if (circleTimer <= 0) {
                circleVisible = false; // Esconde o círculo
                circleColorIndex = (circleColorIndex + 1) % circleColors.length; // Atualiza o índice da cor
            }
        } else if (!circleVisible && countdownIndex >= countdownImages.length) {
            // Faz o círculo aparecer após o contador
            circleVisible = true;
            circleTimer = 3f; // Reseta o tempo do círculo
            generateRandomCirclePosition(); // Gera nova posição aleatória
        }
    
        // Exibe os pontos
        font.draw(batch, "Pontos: " + points, 20, Gdx.graphics.getHeight() - 20);
    
        batch.end();
    
        // Detecta clique no círculo
        if (Gdx.input.justTouched() && circleVisible) {
            float mouseX = Gdx.input.getX();
            float mouseY = Gdx.graphics.getHeight() - Gdx.input.getY(); // Converte coordenadas
            if (isCircleClicked(mouseX, mouseY)) {
                points++; // Adiciona um ponto
                circleVisible = false; // Esconde o círculo
                circleColorIndex = (circleColorIndex + 1) % circleColors.length; // Próxima cor
            }
        }
    }
    
    // Gera uma posição aleatória para o círculo
    private void generateRandomCirclePosition() {
        int screenWidth = Gdx.graphics.getWidth();
        int screenHeight = Gdx.graphics.getHeight();
    
        // Garante que o círculo fique totalmente visível dentro da tela
        circleX = (float) (Math.random() * (screenWidth - circleImage.getWidth()));
        circleY = (float) (Math.random() * (screenHeight - circleImage.getHeight()));
    }    

    private boolean isCircleClicked(float x, float y) {
        float dx = x - (circleX + circleRadius);
        float dy = y - (circleY + circleRadius);
        return (dx * dx + dy * dy) <= (circleRadius * circleRadius);
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
        backgroundImage.dispose();
        circleImage.dispose();
        circleImage2.dispose();
        for (Texture texture : countdownImages) {
            texture.dispose();
        }
        font.dispose();
    }
}
