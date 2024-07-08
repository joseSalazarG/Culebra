package component;

import java.util.ArrayList;
import java.util.List;

import com.almasb.fxgl.core.serialization.Bundle;
import static com.almasb.fxgl.dsl.FXGL.spawn;
import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.entity.SpawnData;
import com.almasb.fxgl.entity.component.Component;
import com.almasb.fxgl.net.Connection;
import static com.almasb.fxgl.dsl.FXGL.getService;
import com.almasb.fxgl.multiplayer.MultiplayerService;
import com.almasb.fxgl.net.Connection;

import javafx.geometry.Point2D;

public class CulebritaLogic extends Component {

    public Point2D direction = new Point2D(1, 0);

    // cabeza - cola - ... - cola
    private final List<Entity> cuerpo = new ArrayList<>();

    @Override
    public void onAdded() {
        cuerpo.add(entity);

        entity.setProperty("prevPos", entity.getPosition());
    }

    @Override
    public void onUpdate(double tpf) {
        entity.setProperty("prevPos", entity.getPosition());
        // separacion de los gatos y cantidad de pixeles que se mueven
        entity.translate(direction.multiply(40));

        for (int i = 1; i < cuerpo.size(); i++) {
            var prevPart = cuerpo.get(i - 1);
            var part = cuerpo.get(i);

            Point2D prevPos = prevPart.getObject("prevPos");

            part.setProperty("prevPos", part.getPosition());
            part.setPosition(prevPos);
        }
    }

    public void arriba() {
        direction = new Point2D(0, -1);
    }

    public void abajo() {
        direction = new Point2D(0, 1);
    }

    public void derecha() {
        direction = new Point2D(1, 0);
    }

    public void izquierda() {
        direction = new Point2D(-1, 0);
    }

    public Point2D returnDirection() {
        return direction;
    }

     public void respawnear() {
        // remueve todos los gatos menos la cabeza del mundo
        cuerpo.stream()
                .skip(1)
                .forEach(Entity::removeFromWorld);

        // ahora eliminamos los gatos de la lista
        cuerpo.clear();
        // y agregamos la cabeza
        cuerpo.add(entity);
        // y la movemos a la posicion inicial de nuevo
  
        entity.setPosition(150, 150);
    }

    public void crecer(SpawnData data, Connection<Bundle> conexion) {
        var body = spawn("cola", data);
        body.translate(direction.multiply(-40));
        cuerpo.add(body);
    }

    public void crecer2(SpawnData data, Connection<Bundle> conexion) {

        var body = spawn("cola2", data);
        body.translate(direction.multiply(-40));

        getService(MultiplayerService.class).spawn(conexion, body, "cola2");

        cuerpo.add(body);
    }
}