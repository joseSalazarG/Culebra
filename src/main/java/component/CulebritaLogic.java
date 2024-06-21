package component;

import java.util.ArrayList;
import java.util.List;

import static com.almasb.fxgl.dsl.FXGL.spawn;
import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.entity.component.Component;

import javafx.geometry.Point2D;

public class CulebritaLogic extends Component {

    public Point2D direction = new Point2D(1, 0);

    // head - body - ... - body
    private final List<Entity> bodyParts = new ArrayList<>();

    @Override
    public void onAdded() {
        bodyParts.add(entity);

        entity.setProperty("prevPos", entity.getPosition());
    }

    @Override
    public void onUpdate(double tpf) {
        entity.setProperty("prevPos", entity.getPosition());
        // separacion de los gatos y cantidad de pixeles que se mueven
        entity.translate(direction.multiply(40));

        for (int i = 1; i < bodyParts.size(); i++) {
            var prevPart = bodyParts.get(i - 1);
            var part = bodyParts.get(i);

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

     public void die() {
        // clean up body parts, apart from head
        bodyParts.stream()
                .skip(1)
                .forEach(Entity::removeFromWorld);

        bodyParts.clear();
        bodyParts.add(entity);

        entity.setPosition(150, 150);
    }

    public void grow() {

        var lastBodyPart = bodyParts.get(bodyParts.size() - 1);

        Point2D pos = lastBodyPart.getObject("prevPos");

        var body = spawn("cola", pos);

        body.translate(direction.multiply(-40));

        bodyParts.add(body);
    }

    public void log() {
        bodyParts.forEach(part -> {
            System.out.println(part.getPosition());
            System.out.println(part.getObject("prevPos").toString());
        });
    }
}