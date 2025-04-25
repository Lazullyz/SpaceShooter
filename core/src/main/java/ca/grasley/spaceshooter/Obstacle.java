package ca.grasley.spaceshooter;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;

public class Obstacle {
    public Rectangle boundingBox;
    public Rectangle collisionBox;
    private TextureRegion texture;
    private String type;
    private float speed = 250;

    public Obstacle(float x, float y, float width, float height,
                    TextureRegion texture, String type) {
        this.texture = texture;
        this.type = type;
        this.boundingBox = new Rectangle(x, y, width, height);

        // Hitbox para cada tipo
        switch (type) {
            case "Tronco":
                // Hitbox Tronco
                setupCollisionBox(width * 0.9f, height * 0.3f, width * 0.05f, height * 0.35f);
                break;
            case "Metal":
                // Hitbox Metal
                setupCollisionBox(width * 0.7f, height * 0.7f, width * 0.15f, height * 0.15f);
                break;
            case "RedePesca":
                // Hitbox Rede de Pesca
                setupCollisionBox(width * 0.85f, height * 0.6f, width * 0.075f, height * 0.18f);
                break;
        }
    }

    private void setupCollisionBox(float width, float height, float offsetX, float offsetY) {
        this.collisionBox = new Rectangle(
            boundingBox.x + offsetX,
            boundingBox.y + offsetY,
            width,
            height
        );
    }

    public void draw(Batch batch) {
        batch.draw(texture, boundingBox.x, boundingBox.y, boundingBox.width, boundingBox.height);
    }

    public void drawDebug(ShapeRenderer renderer) {
        renderer.rect(collisionBox.x, collisionBox.y, collisionBox.width, collisionBox.height);
    }

    public void drawBoundingBox(ShapeRenderer renderer) {
        renderer.rect(boundingBox.x, boundingBox.y, boundingBox.width, boundingBox.height);
    }

    public void update(float delta) {
        boundingBox.y -= speed * delta;
        collisionBox.y -= speed * delta;
    }
}
