package ca.grasley.spaceshooter;

import com.badlogic.gdx.Game;
import java.util.Random;

public class MyGame extends Game {
    public static Random random = new Random();
    private boolean[] unlockedLevels = {true, false, false};

    @Override
    public void create() {
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
        super.dispose();
    }
}
