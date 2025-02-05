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
            { 0.5f, 0f, 0.5f, 1f }, // Roxo
            { 0f, 0.5f, 0f, 1f }, // Verde
            { 0f, 0f, 1f, 1f }, // Azul
            { 1f, 0f, 0f, 1f }, // Vermelho
            { 1f, 0.5f, 0f, 1f } // Laranja
    };

    private float overallDifficulty = 5f; // Default OD; could be read from beatmap's [Difficulty] section.
    private int count300 = 0;
    private int count100 = 0;
    private int count50 = 0;
    private int countMiss = 0;

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

        float playfieldSize = Math.min(Gdx.graphics.getWidth(), Gdx.graphics.getHeight()) * 0.8f;
        float playfieldX = (Gdx.graphics.getWidth() - playfieldSize) / 2f;
        float playfieldY = (Gdx.graphics.getHeight() - playfieldSize) / 2f;

        float approachDuration = 600f;

        Iterator<BeatmapParser.HitObject> schedIterator = scheduledHitObjects.iterator();
        while (schedIterator.hasNext()) {
            BeatmapParser.HitObject hitObject = schedIterator.next();
            if (hitObject.time - approachDuration <= gameTime) {
                float circleX = playfieldX + (hitObject.x / 512f) * playfieldSize - circleTexture.getWidth() / 2f;
                float circleY = playfieldY + ((384 - hitObject.y) / 384f) * playfieldSize
                        - circleTexture.getHeight() / 2f;

                boolean overlapping;
                do {
                    overlapping = false;
                    float newCenterX = circleX + circleTexture.getWidth() / 2f;
                    float newCenterY = circleY + circleTexture.getHeight() / 2f;

                    for (Circle c : activeCircles) {
                        float existingCenterX = c.getX() + c.getWidth() / 2f;
                        float existingCenterY = c.getY() + c.getHeight() / 2f;
                        if (Math.abs(newCenterX - existingCenterX) < 1f
                                && Math.abs(newCenterY - existingCenterY) < 1f) {
                            overlapping = true;
                            circleX += 8f;
                            circleY -= 8f;
                            break;
                        }
                    }
                } while (overlapping);

                int numberIndex = spawnCount % 9;
                int groupIndex = (spawnCount / 9) % circleColors.length;
                float[] color = circleColors[groupIndex];

                Circle circle = new Circle(circleTexture, overlayTexture, approachTexture, numberTextures[numberIndex],
                        circleX, circleY, color, hitObject.time);
                spawnCount++;

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
        int totalHits = count300 + count100 + count50 + countMiss;
        float accuracy = totalHits > 0 ? (300 * count300 + 100 * count100 + 50 * count50) / (300f * totalHits) * 100f : 100f;
        font.draw(batch, "Pontos: " + score, 20, Gdx.graphics.getHeight() - 20);
        font.draw(batch, "Acc: " + String.format("%.2f", accuracy) + "%", 20, Gdx.graphics.getHeight() - 50);
        batch.end();

        Iterator<Circle> activeIterator = activeCircles.iterator();
        while (activeIterator.hasNext()) {
            Circle circle = activeIterator.next();
            circle.update(delta);
            if (!circle.isActive()) {
                if (!circle.wasHit()) {
                    countMiss++;
                    score = 0; // reset score on miss (if desired)
                }
                activeIterator.remove();
            }
        }

        handleInput();
    }

    private void handleInput() {
        boolean keyPressed = Gdx.input.isKeyJustPressed(Input.Keys.Z) || Gdx.input.isKeyJustPressed(Input.Keys.X);

        if (Gdx.input.justTouched() || keyPressed) {
            float mouseX = Gdx.input.getX();
            float mouseY = Gdx.graphics.getHeight() - Gdx.input.getY();
            for (Circle circle : activeCircles) {
                if (circle.isClicked(mouseX, mouseY)) {
                    // Compute timing offset:
                    float offset = Math.abs(gameTime - circle.getScheduledHitTime());
                    // Calculate hit windows based on OD:
                    float threshold300 = 80 - 6 * overallDifficulty;
                    float threshold100 = 140 - 8 * overallDifficulty;
                    float threshold50 = 200 - 10 * overallDifficulty;
                    if (offset <= threshold300) {
                        count300++;
                        score++;
                    } else if (offset <= threshold100) {
                        count100++;
                        score++;
                    } else if (offset <= threshold50) {
                        count50++;
                        score++;
                    } else {
                        countMiss++;
                        score = 0; // reset score on a poor hit
                    }
                    circle.hit();
                    break; // Process one circle per input.
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
        font.dispose();
    }
}
