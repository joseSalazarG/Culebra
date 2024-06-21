package steps;

import com.almasb.fxgl.app.GameApplication;
import com.almasb.fxgl.app.GameSettings;
import com.almasb.fxgl.audio.Sound;
import com.almasb.fxgl.core.math.FXGLMath;
import com.almasb.fxgl.dsl.FXGL;
import static com.almasb.fxgl.dsl.FXGL.getAssetLoader;
import static com.almasb.fxgl.dsl.FXGL.getAudioPlayer;
import static com.almasb.fxgl.dsl.FXGL.onKey;
import static com.almasb.fxgl.dsl.FXGL.spawn;
import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.entity.GameWorld;

import component.CulebritaLogic;
import javafx.scene.input.KeyCode;
import static steps.CulebritaFactory.EntityType.COMIDA;
import static steps.CulebritaFactory.EntityType.JUGADOR;
import static steps.CulebritaFactory.EntityType.MURO;

public class Hooks extends GameApplication {

    //private Serpiente jugador;
    private Entity jugador;
    private final int anchoPantalla = 1400;
    private final int altoPantalla = 700;
    // private final int anchoJugador = 40;
    // private final int altoJugador = 40;

    private final CulebritaFactory culebritaFactory = new CulebritaFactory();

    @Override
    protected void initSettings(GameSettings settings) {
        settings.setWidth(anchoPantalla);
        settings.setHeight(altoPantalla);
        settings.setTitle("Culebrita");
        settings.setVersion("0.1");
        settings.setTicksPerSecond(7); //fps
        //settings.setDefaultLanguage(Language.SPANISH);
    }

    @Override
    protected void initGame() {

        GameWorld mapa = FXGL.getGameWorld();
        mapa.addEntityFactory(culebritaFactory);

        spawn("bosque");
        spawn("muroSuperior");
        spawn("muroInferior");
        spawn("muroIzquierdo");
        spawn("muroDerecho");

        this.jugador = spawn("jugador", 500, 150);
        spawn("comida");
    }

    //colision
    @Override
    protected void initPhysics() {

        Sound comer = getAssetLoader().loadSound("necoarc.mp3");
        Sound morir = getAssetLoader().loadSound("muerte.mp3");

        //colision con comida
        FXGL.onCollisionBegin(JUGADOR, COMIDA, (jugador, comida) -> {
            comida.setPosition(FXGLMath.random(90, 1250), FXGLMath.random(60, 600));
            getAudioPlayer().playSound(comer);
            jugador.getComponent(CulebritaLogic.class).grow();
        });

        FXGL.onCollisionBegin(JUGADOR, MURO, (jugador, muro) -> {
            getAudioPlayer().playSound(morir);
            //morir, respawnear, eliminar cola
            jugador.getComponent(CulebritaLogic.class).die();
        });
    }

    //mueve al jugador
    @Override
    protected void initInput() {

        onKey(KeyCode.LEFT, "Mover hacia la izquierda" ,() -> {
            jugador.getComponent(CulebritaLogic.class).izquierda();
        });

        onKey(KeyCode.RIGHT, "Mover hacia la derecha", () -> {
            jugador.getComponent(CulebritaLogic.class).derecha();
        });

        onKey(KeyCode.UP, "Mover hacia arriba", () -> {
            jugador.getComponent(CulebritaLogic.class).arriba();
        });

        onKey(KeyCode.DOWN, "Mover hacia abajo", () -> {
            jugador.getComponent(CulebritaLogic.class).abajo();
        });
    }
}
