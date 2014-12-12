package net.javainthebox.duke;


import java.util.stream.DoubleStream;
import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.paint.Color;
import javafx.scene.shape.CubicCurve;
import javafx.scene.shape.Line;
import javafx.scene.transform.Rotate;
import javafx.stage.Stage;
import net.javainthebox.caraibe.svg.SVGContent;
import net.javainthebox.caraibe.svg.SVGLoader;

public class DukeDeSwing extends Application {

    private static final double XPOSITION = 400.0;
    private static final double LENGTH = 500.0;

    private CubicCurve string = new CubicCurve(
            XPOSITION, 0.0,     // 開始点
            XPOSITION, 100.0,   // 第 1 制御点
            XPOSITION, 300.0,   // 第 2 制御点
            XPOSITION, LENGTH); // 終点

    // Duke の回転用
    private Rotate rotate = new Rotate(0.0, 0.0, 0.0);

    @Override
    public void start(Stage stage) {
        Group root = new Group();

        SVGContent content = SVGLoader.load(getClass().getResource("duke.svg").toString());

        // Duke のロード
        Node duke = content.getNode("duke");
        root.getChildren().add(duke);
        
        // Duke の位置を紐の終点とバインドする
        duke.translateXProperty().bind(string.endXProperty());
        duke.translateYProperty().bind(string.endYProperty());
        duke.getTransforms().add(rotate);
        
        // ブランコの紐
        string.setStroke(Color.PERU);
        string.setStrokeWidth(10.0);
        string.setFill(null);
        root.getChildren().add(string);

        // Duke の手のロード
        Node hand = content.getNode("hand");
        root.getChildren().add(hand);

        // Duke の手の位置を紐の終点とバインドする
        hand.translateXProperty().bind(string.endXProperty());
        hand.translateYProperty().bind(string.endYProperty());
        // 揺れに応じた回転を行わせるための回転を設定
        hand.getTransforms().add(rotate);

        Scene scene = new Scene(root, XPOSITION * 2.0, LENGTH + 100.0);
        scene.setFill(Color.BLACK);

        stage.setScene(scene);
        stage.setTitle("Duke de Swing");
        stage.show();

        startSwingAnimation();
    }

    private void startSwingAnimation() {
        AnimationTimer timer = new AnimationTimer() {
            @Override
            public void handle(long now) {
                double alpha = Math.PI * Math.sin(now / 500_000_000.0) / 6.0;

                // 終点の位置の更新
                double endX = XPOSITION + LENGTH * Math.sin(alpha);
                double endY = LENGTH * Math.cos(alpha);
                string.setEndX(endX);
                string.setEndY(endY);

                // 第2制御点の更新
                double controlX2 = XPOSITION + 300 * Math.sin(alpha * .8);
                double controlY2 = 300 * Math.cos(alpha * .8);
                string.setControlX2(controlX2);
                string.setControlY2(controlY2);

                // 第1制御点の更新
                double controlX1 = XPOSITION + 100 * Math.sin(alpha * .4);
                double controlY1 = 100 * Math.cos(alpha * .4);
                string.setControlX1(controlX1);
                string.setControlY1(controlY1);

                // Dukeの回転の更新
                double theta = 180.0 * Math.atan2(endY - controlY2, endX - controlX2) / Math.PI - 90.0;
                rotate.setAngle(theta);
            }
        };

        timer.start();
    }

    public static void main(String... args) {
        launch(args);
    }

}
