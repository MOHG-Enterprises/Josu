package com.josu.game.standard.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.josu.game.Josu;
import com.josu.game.standard.beatmap_parser.BeatmapParser;
import com.josu.game.standard.entities.Circle;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class GameScreen implements Screen {
    private final Josu game;
    private OrthographicCamera camera;
    private SpriteBatch batch;

    private ArrayList<Circle> activeCircles;
    private List<BeatmapParser.HitObject> scheduledHitObjects;
    private Music backgroundMusic;

    private Texture circleTexture, overlayTexture, approachTexture;
    private Texture[] numberTextures;

    private float gameTime;
    private boolean songStarted = false;

    private int score;
    private BitmapFont font;

    private int spawnCount = 0;

    private final float[][] circleColors = {
        {0.5f, 0f, 0.5f, 1f}, // Roxo
        {0f, 0.5f, 0f, 1f},   // Verde
        {0f, 0f, 1f, 1f},     // Azul
        {1f, 0f, 0f, 1f},     // Vermelho
        {1f, 0.5f, 0f, 1f}    // Laranja
    };

    public GameScreen(Josu game) {
        this.game = game;
        camera = new OrthographicCamera(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        camera.setToOrtho(false);

        batch = new SpriteBatch();
        activeCircles = new ArrayList<>();

        circleTexture = new Texture("images/circle.png");
        overlayTexture = new Texture("images/hitcircle.png");
        approachTexture = new Texture("images/approachcircle.png");

        numberTextures = new Texture[9];
        for (int i = 0; i < 9; i++) {
            numberTextures[i] = new Texture("images/count/default-" + (i + 1) + ".png");
        }

        // Load the beatmap
        BeatmapParser.BeatmapData beatmap = BeatmapParser.parse("beatmaps/test/test.osu");
        scheduledHitObjects = beatmap.hitObjects;

        // Load the song
        if (!beatmap.audioFilename.isEmpty()) {
            backgroundMusic = Gdx.audio.newMusic(Gdx.files.internal("beatmaps/test/" + beatmap.audioFilename));
            backgroundMusic.setLooping(false);
        }

        gameTime = 0f;
        score = 0;
        font = new BitmapFont();
    }

    @Override
    public void show() {
        // Play the song when the screen is shown
        if (backgroundMusic != null && !songStarted) {
            backgroundMusic.setVolume(0.2f);
            backgroundMusic.play();
            songStarted = true;
        }
    }

    @Override
    public void render(float delta) {
        if (backgroundMusic != null) {
            gameTime = backgroundMusic.getPosition() * 1000f;
        } else {
            gameTime += delta * 1000f;
        }

        Gdx.gl.glClearColor(0f, 0f, 0f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        camera.update();
        batch.setProjectionMatrix(camera.combined);

        float scaleX = Gdx.graphics.getWidth() / 512f;
        float scaleY = Gdx.graphics.getHeight() / 384f;

        float approachDuration = 600f;

        Iterator<BeatmapParser.HitObject> schedIterator = scheduledHitObjects.iterator();
        while (schedIterator.hasNext()) {
            BeatmapParser.HitObject hitObject = schedIterator.next();
            // If you use an approach offset, subtract it here:
            if (hitObject.time - approachDuration <= gameTime) {
                float circleX = hitObject.x * scaleX - circleTexture.getWidth() / 2f;
                float circleY = (384 - hitObject.y) * scaleY - circleTexture.getHeight() / 2f;
                
                // Calculate the proper number texture index (0–8) and group color:
                int numberIndex = spawnCount % 9;          // cycles through 0 to 8
                int groupIndex = (spawnCount / 9) % circleColors.length;
                float[] color = circleColors[groupIndex];
                
                // Use the correct number texture for this circle
                Circle circle = new Circle(circleTexture, overlayTexture, approachTexture, numberTextures[numberIndex],
                        circleX, circleY, color);
                spawnCount++; // increment after each spawn
                
                activeCircles.add(circle);
                schedIterator.remove();
            } else {
                break;
            }
        }


        batch.begin();
        for (Circle circle : activeCircles) {
            circle.render(batch);
        }
        font.draw(batch, "Pontos: " + score, 20, Gdx.graphics.getHeight() - 20);
        batch.end();

        Iterator<Circle> activeIterator = activeCircles.iterator();
        while (activeIterator.hasNext()) {
            Circle circle = activeIterator.next();
            circle.update(delta);
            if (!circle.isActive()) {
                if (circle.wasHit()) {
                    score++;
                } else {
                    score = 0;
                }
                activeIterator.remove();
            }
        }

        handleInput();
    }

    private void handleInput() {
        boolean keyPressed = false;

        if (Gdx.input.isKeyJustPressed(Input.Keys.Z) || Gdx.input.isKeyJustPressed(Input.Keys.X)) {
            keyPressed = true;
        }

        if (Gdx.input.justTouched() || keyPressed) {
            float mouseX = Gdx.input.getX();
            float mouseY = Gdx.graphics.getHeight() - Gdx.input.getY();

            for (Circle circle : activeCircles) {
                if (circle.isClicked(mouseX, mouseY)) {
                    circle.hit();
                    break;
                }
            }
        }
    }

    @Override
    public void resize(int width, int height) {
        camera.setToOrtho(false, width, height);
    }

    @Override
    public void pause() {
        // Handle pause if needed.
    }

    @Override
    public void resume() {
        // Handle resume if needed.
    }

    @Override
    public void hide() {
        dispose();
    }

    @Override
    public void dispose() {
        batch.dispose();
        for (Circle circle : activeCircles) {
            circle.dispose();
        }
        for (Texture t : numberTextures) {
            t.dispose();
        }
        if (backgroundMusic != null) {
            backgroundMusic.dispose();
        }
        circleTexture.dispose();
        overlayTexture.dispose();
        approachTexture.dispose();
    }
}
