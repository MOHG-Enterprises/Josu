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
    private Texture[] countdownImages; // Array para as imagens do contador
    private BitmapFont font;
    private float circleX, circleY;
    private float circleRadius;
    private int countdownIndex; // Índice para o contador
    private float countdownTimer; // Controla o tempo do contador
    private boolean circleVisible;
    private float circleTimer; // Controla o tempo do círculo
    private int points;

    public GameScreen(Josu game) {
        this.game = game;
    }

    @Override
    public void show() {
        Gdx.graphics.setWindowedMode(1920, 1080);
        batch = new SpriteBatch();
        backgroundImage = new Texture("backgroundOsu.png");
        circleImage = new Texture("circle.png");

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
        float scale = Math.min(Gdx.graphics.getWidth() / (float) backgroundImage.getWidth(),
                               Gdx.graphics.getHeight() / (float) backgroundImage.getHeight());
        float x = (Gdx.graphics.getWidth() - backgroundImage.getWidth() * scale) / 2;
        float y = (Gdx.graphics.getHeight() - backgroundImage.getHeight() * scale) / 2;
        batch.draw(backgroundImage, x, y, backgroundImage.getWidth() * scale, backgroundImage.getHeight() * scale);

        // Desenha uma camada escura semi-transparente sobre o fundo
        batch.setColor(0, 0, 0, 0.5f); // Cor preta com 50% de opacidade
        batch.draw(backgroundImage, x, y, backgroundImage.getWidth() * scale, backgroundImage.getHeight() * scale);
        batch.setColor(1, 1, 1, 1); // Reseta a cor para branco total (default)
    
        // Exibe o contador no centro da tela
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
                countdownIndex++; // Vai para a próxima imagem
            }
        } else if (circleVisible) {
            // Exibe o círculo
            batch.draw(circleImage, circleX, circleY);
            circleTimer -= delta;
    
            // Verifica se o tempo do círculo acabou
            if (circleTimer <= 0) {
                circleVisible = false;
            }
        } else if (!circleVisible && countdownIndex >= countdownImages.length) {
            // Faz o círculo aparecer quando o contador termina
            circleVisible = true;
            circleTimer = 3f; // Reseta o tempo do círculo
    
            // Gera uma posição aleatória para o círculo
            generateRandomCirclePosition();
        }
    
        // Exibe os pontos no canto superior esquerdo
        font.draw(batch, "Pontos: " + points, 20, Gdx.graphics.getHeight() - 20);
    
        batch.end();
    
        // Verifica clique no círculo
        if (Gdx.input.justTouched() && circleVisible) {
            float mouseX = Gdx.input.getX();
            float mouseY = Gdx.graphics.getHeight() - Gdx.input.getY(); // Converte para coordenadas de tela
            if (isCircleClicked(mouseX, mouseY)) {
                points++; // Adiciona um ponto
                circleVisible = false; // Esconde o círculo
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
        for (Texture texture : countdownImages) {
            texture.dispose();
        }
        font.dispose();
    }
}
