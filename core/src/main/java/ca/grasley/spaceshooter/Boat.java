package ca.grasley.spaceshooter;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;

public class Boat {
    public Rectangle boundingBox;
    public Rectangle collisionBox;
    private TextureRegion texture;
    private float collisionOffsetX;
    private float collisionOffsetY;
    private float movementSpeed;

    public Boat(float x, float y, float width, float height,
                float movementSpeed, TextureRegion texture) {
        this.movementSpeed = 700f;
        this.texture = texture;
        this.boundingBox = new Rectangle(x, y, width, height);

        // Hitbox do barco
        float collisionWidth = width * 0.55f;
        float collisionHeight = height * 0.9f;
        this.collisionOffsetX = (width - collisionWidth) / 2;
        this.collisionOffsetY = height * 0.1f;

        this.collisionBox = new Rectangle(
            x + collisionOffsetX,
            y + collisionOffsetY,
            collisionWidth,
            collisionHeight
        );
    }

    public float getMovementSpeed() {
        return movementSpeed;
    }

    public void draw(Batch batch) {
        batch.draw(texture,
            boundingBox.x, boundingBox.y,
            boundingBox.width, boundingBox.height);
    }

    public void drawDebug(ShapeRenderer renderer) {
        renderer.rect(collisionBox.x, collisionBox.y,
            collisionBox.width, collisionBox.height);
    }

    public void drawBoundingBox(ShapeRenderer renderer) {
        renderer.rect(boundingBox.x, boundingBox.y,
            boundingBox.width, boundingBox.height);
    }

    public void updatePosition(float x, float y) {
        boundingBox.setPosition(x, y);
        collisionBox.setPosition(
            x + collisionOffsetX,
            y + collisionOffsetY
        );
    }

    public boolean intersects(Rectangle other) {
        return collisionBox.overlaps(other);
    }
}
