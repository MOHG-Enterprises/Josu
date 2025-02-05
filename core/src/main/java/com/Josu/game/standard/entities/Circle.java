package com.josu.game.standard.entities;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;

public class Circle {
    private Texture texture;
    private Texture overlayTexture;
    private Texture approachCircleTexture;
    private Texture numberTexture;
    private Vector2 position;
    private float radius;
    private float[] color;
    private float approachScale;
    private float approachTimer;
    private Sound hitSound;
    private boolean active; // Indicates if the circle is still active (clickable/visible)
    private boolean hit = false;
    
    // Constant for the hit sound path
    private static final String HIT_SOUND_PATH = "sounds/drum-hitnormal.ogg";

    /**
     * Constructs a Circle.
     * @param texture the main circle texture
     * @param overlayTexture the overlay texture (for hit effects)
     * @param approachCircleTexture the texture for the approach circle
     * @param numberTexture the texture for the initial number
     * @param screenX the x-coordinate (in screen space) for the circle
     * @param screenY the y-coordinate (in screen space) for the circle
     * @param color a float array representing RGBA values for tinting the overlay
     */
    public Circle(Texture texture, Texture overlayTexture, Texture approachCircleTexture, Texture numberTexture,
                  float screenX, float screenY, float[] color) {
        this.texture = texture;
        this.overlayTexture = overlayTexture;
        this.approachCircleTexture = approachCircleTexture;
        this.numberTexture = numberTexture;
        this.position = new Vector2(screenX, screenY);
        this.radius = texture.getWidth() / 2f; // Working in screen pixels
        this.color = color;
        this.approachScale = 2.0f;
        this.approachTimer = 0f;
        this.hitSound = Gdx.audio.newSound(Gdx.files.internal(HIT_SOUND_PATH));
        this.active = true;
    }

    /**
     * Renders the circle and its associated graphics if active.
     */
    public void render(SpriteBatch batch) {
        if (!active) {
            return; // Do not render if the circle is inactive
        }
        
        // Calculate dimensions and position for the approach circle
        float approachCircleWidth = approachCircleTexture.getWidth() * approachScale;
        float approachCircleHeight = approachCircleTexture.getHeight() * approachScale;
        float approachCircleX = position.x + (texture.getWidth() - approachCircleWidth) / 2;
        float approachCircleY = position.y + (texture.getHeight() - approachCircleHeight) / 2;

        // Draw the approach circle, main circle, overlay, and number texture
        batch.draw(approachCircleTexture, approachCircleX, approachCircleY, approachCircleWidth, approachCircleHeight);
        batch.draw(texture, position.x, position.y);

        batch.setColor(color[0], color[1], color[2], color[3]);
        batch.draw(overlayTexture, position.x, position.y);
        batch.setColor(1, 1, 1, 1); // Reset color to white

        float numberX = position.x + (texture.getWidth() - numberTexture.getWidth()) / 2;
        float numberY = position.y + (texture.getHeight() - numberTexture.getHeight()) / 2;
        batch.draw(numberTexture, numberX, numberY);
    }

    /**
     * Updates the approach circle's scale and marks the circle inactive if the user misses.
     * @param delta the time in seconds since the last frame
     */
    public void update(float delta) {
        if (!active) {
            return;
        }
        
        approachTimer += delta;
        float progress = approachTimer / 0.75f;
        approachScale = 2.0f - progress;
        if (approachScale <= 1.0f) {
            approachScale = 1.0f;
            // The approach circle reached the circle border; mark as inactive (missed)
            active = false;
        }
    }

    /**
     * Resets the approach circle animation.
     */
    public void resetApproachCircle() {
        this.approachScale = 2.0f;
        this.approachTimer = 0f;
    }

    /**
     * Checks whether the given screen coordinates are within the circle.
     * @param x the x-coordinate in screen space
     * @param y the y-coordinate in screen space
     * @return true if the point is inside the circle and the circle is active, false otherwise
     */
    public boolean isClicked(float x, float y) {
        if (!active) return false;
        float dx = x - (position.x + radius);
        float dy = y - (position.y + radius);
        return (dx * dx + dy * dy) <= (radius * radius);
    }
    
    /**
     * Plays the hit sound at a reduced volume, resets the approach circle, and deactivates the circle.
     */
    public boolean wasHit() {
        return hit;
    }

    public void hit() {
        // Play hit sound at reduced volume (0.2f)
        hitSound.play(0.2f);
        resetApproachCircle();
        hit = true;
        active = false;
    }

    /**
     * Updates the displayed number texture.
     * @param newTexture the new number texture to display
     */
    public void setNumberTexture(Texture newTexture) {
        this.numberTexture = newTexture;
    }

    public float getWidth() {
        return texture.getWidth();
    }

    public float getHeight() {
        return texture.getHeight();
    }

    public Texture getNumberTexture() {
        return this.numberTexture;
    }

    public void setPosition(float x, float y) {
        position.set(x, y);
    }

    public float getX() {
        return position.x;
    }

    public float getY() {
        return position.y;
    }
    
    /**
     * Returns whether the circle is still active (visible/clickable).
     */
    public boolean isActive() {
        return active;
    }

    /**
     * Disposes of all assets used by the circle.
     */
    public void dispose() {
        texture.dispose();
        overlayTexture.dispose();
        approachCircleTexture.dispose();
        numberTexture.dispose();
        hitSound.dispose();
    }
}
