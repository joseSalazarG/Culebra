package steps;

import com.almasb.fxgl.app.GameApplication;
import com.almasb.fxgl.app.GameSettings;
import com.almasb.fxgl.dsl.FXGL;
import com.almasb.fxgl.dsl.components.AutoRotationComponent;
import com.almasb.fxgl.entity.Entity;
import javafx.scene.input.KeyCode;
// sugeridos
import com.almasb.fxgl.input.Input;
import com.almasb.fxgl.input.UserAction;

import static com.almasb.fxgl.dsl.FXGL.play;
import static com.almasb.fxgl.dsl.FXGL.texture;

public class Hooks extends GameApplication {

    //private Serpiente jugador;
    private Entity jugador;
    private final int anchoPantalla = 1400;
    private final int altoPantalla = 700;
    private final int anchoJugador = 40;
    private final int altoJugador = 40;

    @Override
    protected void initSettings(GameSettings settings) {
        settings.setWidth(anchoPantalla);
        settings.setHeight(altoPantalla);
        settings.setTitle("Culebrita");
        settings.setVersion("0.1");
        //settings.setDefaultLanguage(Language.SPANISH);
    }

    @Override
    protected void initGame() {

        FXGL.play("gato.mp3");

        jugador = FXGL.entityBuilder()
                .at(150, 150)
                //.view(new Rectangle(anchoJugador, altoJugador, Color.LIGHTGREEN))
                .viewWithBBox(texture("neko.png", 100, 100))
                //.viewWithBBox(new Rectangle(20, 60, Color.LIGHTGRAY))
                .collidable()
                .with(new AutoRotationComponent())
                .buildAndAttach();
    }

    // mueve al jugador
    @Override
    protected void initInput() {

        Input input = FXGL.getInput();

        input.addAction(new UserAction("Mover hacia arriba") {
            @Override
            protected void onAction() {
                if (jugador.getY() > 0) {
                    jugador.translateY(-2.5); // se mueve arriba
                };
            }
        }, KeyCode.UP);

        input.addAction(new UserAction("Mover hacia abajo") {
            @Override
            protected void onAction() {
                if (jugador.getY() < altoPantalla-altoJugador) {
                    jugador.translateY(2.5); // se mueve arriba
                };
            }
        }, KeyCode.DOWN);

        input.addAction(new UserAction("Mover hacia la izquierda") {
            @Override
            protected void onAction() {
                if (jugador.getX() > 0) {
                    jugador.translateX(-2.5); // se mueve arriba
                };
            }
        }, KeyCode.LEFT);

        input.addAction(new UserAction("Mover hacia la derecha") {
            @Override
            protected void onAction() {
                if (jugador.getX() < anchoPantalla-anchoJugador) {
                    jugador.translateX(2.5); // se mueve arriba
                };
            }
        }, KeyCode.RIGHT);

    }
}
