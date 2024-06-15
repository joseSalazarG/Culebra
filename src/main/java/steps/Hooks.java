package steps;

import com.almasb.fxgl.app.GameApplication;
import com.almasb.fxgl.app.GameSettings;
import com.almasb.fxgl.audio.Sound;
import com.almasb.fxgl.core.math.FXGLMath;
import com.almasb.fxgl.dsl.FXGL;
import com.almasb.fxgl.dsl.components.AutoRotationComponent;
import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.entity.GameWorld;
import com.almasb.fxgl.input.Input;
import com.almasb.fxgl.input.UserAction;

import component.CulebritaLogic;
import javafx.scene.input.KeyCode;
import javafx.scene.shape.Rectangle;

import static com.almasb.fxgl.dsl.FXGL.*;
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
            jugador.getComponent(CulebritaLogic.class).crecer();
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

        Input input = FXGL.getInput();

        input.addAction(new UserAction("Mover hacia arriba") {
            @Override
            protected void onAction() {
                if (jugador.getY() > 0) {
                    jugador.translateY(-2.5); // se mueve arriba
                }
            }
        }, KeyCode.UP);

        input.addAction(new UserAction("Mover hacia abajo") {
            @Override
            protected void onAction() {
                if (jugador.getY() < altoPantalla-altoJugador) {
                    jugador.translateY(2.5); // se mueve abajo
                }
            }
        }, KeyCode.DOWN);

        input.addAction(new UserAction("Mover hacia la izquierda") {
            @Override
            protected void onAction() {
                if (jugador.getX() > 0) {
                    jugador.translateX(-2.5); // se mueve a la izquierda
                }
            }
        }, KeyCode.LEFT);

        input.addAction(new UserAction("Mover hacia la derecha") {
            @Override
            protected void onAction() {
                if (jugador.getX() < anchoPantalla-anchoJugador) {
                    jugador.translateX(2.5); // se mueve a la derecha
                }
            }
        }, KeyCode.RIGHT);
    }
}
