package ca.grasley.spaceshooter;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;

public class Trash {
    public Rectangle boundingBox;
    public Rectangle collisionBox;
    private TextureRegion texture;
    private float speed = 200;

    public Trash(float x, float y, float width, float height, TextureRegion texture) {
        this.texture = texture;
        this.boundingBox = new Rectangle(x, y, width, height);

        // Hitbox Lixo
        float size = Math.min(width, height) * 0.5f;
        float offsetX = (width - size) / 2;
        float offsetY = (height - size) / 2;

        this.collisionBox = new Rectangle(
            x + offsetX,
            y + offsetY,
            size,
            size
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
