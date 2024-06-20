package steps;

import com.almasb.fxgl.app.GameApplication;
import com.almasb.fxgl.app.GameSettings;
import com.almasb.fxgl.audio.Sound;
import com.almasb.fxgl.core.math.FXGLMath;
import com.almasb.fxgl.dsl.FXGL;
import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.entity.GameWorld;
import com.almasb.fxgl.input.Input;
import com.almasb.fxgl.input.UserAction;

import component.CulebritaLogic;
import javafx.scene.input.KeyCode;
import javafx.util.Pair;

import static com.almasb.fxgl.dsl.FXGL.*;
import static com.almasb.fxgl.dsl.FXGL.onKeyDown;
import static steps.CulebritaFactory.EntityType.*;

public class Hooks extends GameApplication {

    //private Serpiente jugador;
    private Entity jugador;
    private final int anchoPantalla = 1400;
    private final int altoPantalla = 700;
    private final int anchoJugador = 40;
    private final int altoJugador = 40;
    int vidas = 5;

    private final CulebritaFactory culebritaFactory = new CulebritaFactory();

    @Override
    protected void initSettings(GameSettings settings) {
        settings.setWidth(anchoPantalla);
        settings.setHeight(altoPantalla);
        settings.setTitle("Culebrita");
        settings.setVersion("0.1");
        settings.setTicksPerSecond(50);
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
            comida.setPosition(FXGLMath.random(90, 1250), FXGLMath.random(30, 600));
            getAudioPlayer().playSound(comer);
            jugador.getComponent(CulebritaLogic.class).crecer(jugador.getComponent(CulebritaLogic.class).ubicacionAnterior);
        });

        FXGL.onCollisionBegin(JUGADOR, MURO, (jugador, muro) -> {
            getAudioPlayer().playSound(morir);
            vidas--;
            jugador.setPosition(500,150);
            if (vidas == 0) {
                jugador.removeFromWorld();
            }
        });
    }

    //mueve al jugador
    @Override
    protected void initInput() {

        onKeyDown(KeyCode.LEFT, "Mover hacia la izquierda" ,() -> {
            if (jugador.getX() > 0) {
                jugador.translateX(-40); // se mueve a la izquierda
                jugador.setRotation(180);
                jugador.getComponent(CulebritaLogic.class).guardarUbicacion();
            }
        });

        onKeyDown(KeyCode.RIGHT, "Mover hacia la derecha", () -> {
            if (jugador.getX() < anchoPantalla - anchoJugador) {
                jugador.translateX(40); // se mueve a la derecha
                jugador.setRotation(0);
                jugador.getComponent(CulebritaLogic.class).guardarUbicacion();
            }
        });

        onKeyDown(KeyCode.UP, "Mover hacia arriba", () -> {
            if (jugador.getY() > 0) {
                jugador.translateY(-40); // se mueve hacia arriba
                jugador.setRotation(270);
                jugador.getComponent(CulebritaLogic.class).guardarUbicacion();
            }
        });

        onKeyDown(KeyCode.DOWN, "Mover hacia abajo", () -> {
            if (jugador.getY() < altoPantalla - altoJugador) {
                jugador.translateY(40); // se mueve hacia abajo
                jugador.setRotation(90);
                jugador.getComponent(CulebritaLogic.class).guardarUbicacion();
            }
        });
    }
}
