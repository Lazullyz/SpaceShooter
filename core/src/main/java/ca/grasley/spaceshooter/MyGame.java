package ca.grasley.spaceshooter;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.Gdx;

import java.util.Random;

public class MyGame extends Game {
    public static Random random = new Random();
    private boolean[] unlockedLevels = {true, false, false};

    // 🔥 Fonte global
    public BitmapFont font;

    @Override
    public void create() {
        // 🔤 Carregar fonte personalizada
        FreeTypeFontGenerator generator = new FreeTypeFontGenerator(Gdx.files.internal("VeniceClassic.ttf"));
        FreeTypeFontGenerator.FreeTypeFontParameter params = new FreeTypeFontGenerator.FreeTypeFontParameter();
        params.size = 72;
        params.color = Color.WHITE;
        params.borderWidth = 3;
        params.borderColor = Color.BLACK;

        font = generator.generateFont(params);
        generator.dispose();

        setScreen(new MenuScreen(this));
    }

    public void unlockNextLevel(int currentLevel) {
        if (currentLevel < unlockedLevels.length) {
            unlockedLevels[currentLevel] = true;
        }
    }

    public boolean isLevelUnlocked(int level) {
        return level >= 1 && level <= unlockedLevels.length ? unlockedLevels[level-1] : false;
    }

    @Override
    public void dispose() {
        font.dispose();
        super.dispose();
    }
}
