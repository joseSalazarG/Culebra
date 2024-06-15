package component;

import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.entity.component.Component;
import javafx.geometry.Point2D;

import java.util.ArrayList;
import java.util.List;

import static com.almasb.fxgl.dsl.FXGL.spawn;

public class CulebritaLogic extends Component {

    // head - body - ...
    private List<Entity> bodyParts = new ArrayList<>();

    private Point2D direction = new Point2D(1, 0);

    @Override
    public void onAdded() {
        bodyParts.add(entity);

        entity.setProperty("prevPos", entity.getPosition());
    }

    @Override
    public void onUpdate(double tpf) {
        entity.setProperty("prevPos", entity.getPosition());
        entity.translate(direction.multiply(32));

        for (int i = 1; i < bodyParts.size(); i++) {
            var prevPart = bodyParts.get(i - 1);
            var part = bodyParts.get(i);

            Point2D prevPos = prevPart.getObject("prevPos");

            part.setProperty("prevPos", part.getPosition());
            part.setPosition(prevPos);
        }
    }

    public void crecer() {

        var lastBodyPart = bodyParts.get(bodyParts.size() - 1);

        Point2D pos = lastBodyPart.getObject("prevPos");

        var body = spawn("cola", pos);

        bodyParts.add(body);
    }
}
