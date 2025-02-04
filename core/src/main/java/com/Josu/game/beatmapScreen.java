package com.Josu.game;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonValue;
import com.badlogic.gdx.files.FileHandle;

import java.util.ArrayList;
import java.util.List;

public class beatmapScreen implements Screen {
    private final Josu game;
    private SpriteBatch batch;
    private Texture circleTexture;
    private Texture hitCircleTexture;
    private Texture approachCircleTexture;
    private Music music;
    private Sound hitSound;
    private List<BeatCircle> circles;
    private Texture backgroundTexture;
    private final float[][] circleColors = {
        {0.5f, 0f, 0.5f, 1f}, // Roxo
        {0f, 0.5f, 0f, 1f},   // Verde
        {0f, 0f, 1f, 1f},     // Azul
        {1f, 0f, 0f, 1f},     // Vermelho
        {1f, 0.5f, 0f, 1f}    // Laranja
    };

    public beatmapScreen(Josu game) {
        this.game = game;
    }
    
    @Override
    public void show() {
        batch = new SpriteBatch();
        circleTexture = new Texture("circle.png");
        hitCircleTexture = new Texture("hitcircle.png");
        approachCircleTexture = new Texture("approachCircle.png");
        music = Gdx.audio.newMusic(Gdx.files.internal("music.mp3"));
        hitSound = Gdx.audio.newSound(Gdx.files.internal("sounds/drum-hitnormal.ogg"));
        circles = new ArrayList<>();
        backgroundTexture = new Texture("background.png");
        loadBeatmap("beatmap.json");
        music.play();
    }

    private void loadBeatmap(String filePath) {
        Json json = new Json();
        FileHandle file = Gdx.files.internal(filePath);
        Beatmap beatmap = json.fromJson(Beatmap.class, file);
    
        for (Beatmap.CircleData circleData : beatmap.notes) {
            // Aqui, vamos definir o tamanho dos círculos (pode ser modificado de acordo com a lógica desejada)
            float size = 150;  // Tamanho padrão
            float[] color = circleColors[(int)(Math.random() * circleColors.length)];
            circles.add(new BeatCircle(circleData.x, circleData.y, circleData.time, size, color));
        }
    }

    @Override
public void render(float delta) {
    Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
    batch.begin();
    
    // Desenhar o fundo com um filtro de cor escura (escurecer o fundo)
    batch.setColor(0.1f, 0.1f, 0.1f, 0.9f); // Cor escura, ajustando o valor de alpha para 0.5f (50% de opacidade)
    batch.draw(backgroundTexture, 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight()); // Desenha o fundo

    float currentTime = music.getPosition();

    for (BeatCircle circle : circles) {
        if (!circle.clicked) {
            float timeUntilHit = circle.time - currentTime; // Tempo restante até o círculo precisar ser clicado

            if (timeUntilHit < -0.05f) { 
                // Já passou do tempo de acerto + margem de erro (0.3s), então some e dá erro.
                System.out.println("não clicou");
                circle.clicked = true;
                continue;
            }

            // O Approach Circle só começa quando faltar 1 segundo para o círculo aparecer
            if (timeUntilHit <= 1f) {
                float approachProgress = timeUntilHit / 0.7f;
                circle.approachSize = Math.max(circle.size, circle.size * 2 * approachProgress);
            } else {
                circle.approachSize = circle.size * 2; // Mantém grande até 1 segundo antes do tempo correto
            }

            // Aplica fade in quando estiver próximo de aparecer
            if (timeUntilHit <= 1f && circle.alpha < 1f) {
                circle.alpha += delta * 2;
                if (circle.alpha > 1f) circle.alpha = 1f;
            }

            // Desenha o Approach Circle (sempre visível)
            batch.setColor(1, 1, 1, circle.alpha);
            batch.draw(approachCircleTexture, circle.x - (circle.approachSize - circle.size) / 2, 
                    circle.y - (circle.approachSize - circle.size) / 2, circle.approachSize, circle.approachSize);

            // Desenha o hitCircle (parte colorida de dentro) somente quando o approachCircle começar a fechar
            if (timeUntilHit <= 1f) {
                batch.setColor(circle.color[0], circle.color[1], circle.color[2], circle.alpha);
                batch.draw(hitCircleTexture, circle.x, circle.y, circle.size, circle.size);
                
                // Desenha a borda branca do círculo
                batch.setColor(1, 1, 1, 1);
                batch.draw(circleTexture, circle.x, circle.y, circle.size, circle.size);
            }
        }
    }

    batch.end();

    checkClick(); // Verifica cliques
}

private void checkClick() {
    if (Gdx.input.justTouched()) {
        float clickX = Gdx.input.getX();
        float clickY = Gdx.graphics.getHeight() - Gdx.input.getY(); // Ajustar coordenadas

        float currentTime = music.getPosition();

        for (BeatCircle circle : circles) {
            if (!circle.clicked) {
                float timeDifference = Math.abs(currentTime - circle.time); // Diferença de tempo

                // Verifica se o clique foi dentro da área do círculo
                if (clickX >= circle.x && clickX <= circle.x + circle.size &&
                    clickY >= circle.y && clickY <= circle.y + circle.size) {
                    
                    // Classificação do clique baseado na margem de erro
                    if (timeDifference <= 0.35f) {
                        System.out.println("perfeito");
                        hitSound.play();
                    } else if (timeDifference <= 0.6f) {
                        System.out.println("bom");
                        hitSound.play();
                    } else if (timeDifference <= 0.8f) {
                        System.out.println("ruim");
                        hitSound.play();
                    } else {
                        System.out.println("X");
                    }

                    circle.clicked = true; // Remove o círculo
                    break; // Para de verificar após encontrar o primeiro círculo válido
                }
            }
        }
    }
}
    
    
    // Verifica se o clique foi dentro do círculo
    private boolean isInsideCircle(float mouseX, float mouseY, BeatCircle circle) {
        float dx = mouseX - (circle.x + circle.size / 2);
        float dy = mouseY - (circle.y + circle.size / 2);
        return dx * dx + dy * dy <= (circle.size / 2) * (circle.size / 2);
    }
    
    


    @Override
    public void resize(int width, int height) {}
    @Override
    public void pause() {}
    @Override
    public void resume() {}
    @Override
    public void hide() {}
    @Override
    public void dispose() {
        batch.dispose();
        circleTexture.dispose();
        music.dispose();
        backgroundTexture.dispose();
    }
}

class BeatCircle {
    float x, y, time, size;
    float[] color;
    float alpha; // Para fade in
    boolean clicked;
    float approachSize; // Tamanho do Approach Circle
    boolean canBeClicked; // Define se o círculo pode ser clicado

    public BeatCircle(float x, float y, float time, float size, float[] color) {
        this.x = x;
        this.y = y;
        this.time = time;
        this.size = size;
        this.color = color;
        this.alpha = 0f;
        this.clicked = false;
        this.approachSize = size * 2; // Começa maior
        this.canBeClicked = false;
    }
}

// Classe para carregar o beatmap
class Beatmap {
    public String song;
    public int bpm;
    public float offset;
    public CircleData[] notes;

    static class CircleData {
        public float time;
        public float x;
        public float y;
        public float size;
    }
}
