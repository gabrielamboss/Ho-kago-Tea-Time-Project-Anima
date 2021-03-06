package puzzle;

import static java.lang.Math.random;
import static java.lang.Math.sin;
import java.util.List;
import javafx.animation.*;
import javafx.beans.binding.DoubleBinding;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Point2D;
import javafx.scene.Node;
import javafx.scene.paint.Color;
import javafx.scene.shape.Ellipse;
import javafx.scene.transform.Rotate;
import javafx.util.Duration;
import static puzzle.Util.addChildToParent;


public class Animator implements Runnable {

    public static final Duration BRANCH_GROWING_DURATION = Duration.seconds(2);
    public static final Duration GRASS_BECOME_GREEN_DURATION = Duration.seconds(5);
    public static final Duration GRASS_BECOME_YELLOW_DURATION = Duration.seconds(5);
    public static final Duration LEAF_BECOME_YELLOW_DURATION = Duration.seconds(5);
    public static final Duration WIND_CYCLE_DURATION = Duration.seconds(5);
    public static final Duration LEAF_APPEARING_DURATION = Duration.seconds(2);
    public static final Duration FLOWER_APPEARING_DURATION = Duration.seconds(1);
    private TreeGenerator treeGenerator;

    Animator(TreeGenerator treeGenerator) {
        this.treeGenerator = treeGenerator;
    }

    public void run() {

        Tree tree = treeGenerator.generateTree();
        SequentialTransition branchGrowingAnimation = new SequentialTransition();
        ParallelTransition treeWindAnimation = new ParallelTransition();

        for (int i = 0; i < tree.generations.size(); i++) {
            List<Branch> branchGeneration = tree.generations.get(i);
            branchGrowingAnimation.getChildren().add(animateBranchGrowing(branchGeneration, i, BRANCH_GROWING_DURATION)); //create animation for current crown
            treeWindAnimation.getChildren().add(animateTreeWind(branchGeneration, i, WIND_CYCLE_DURATION));
        }


        final Transition all = new ParallelTransition(treeWindAnimation, new SequentialTransition(branchGrowingAnimation, seasonsAnimation(tree)));
        all.play();

    }

    private Animation animateBranchGrowing(List<Branch> branchGeneration, int depth, Duration duration) {

        ParallelTransition sameDepthBranchAnimation = new ParallelTransition();
        for (final Branch branch : branchGeneration) {
            Timeline branchGrowingAnimation = new Timeline(new KeyFrame(duration, new KeyValue(branch.base.endYProperty(), branch.length)));//line is growing by changinh endY from 0 to brunch.length
            sameDepthBranchAnimation.getChildren().add(
                    new SequentialTransition(

                    PauseTransitionBuilder.create().duration(Duration.ONE).onFinished(new EventHandler<ActionEvent>() {

                public void handle(ActionEvent t) {
                    branch.base.setStrokeWidth(branch.length / 25);
                }
            }).build(),
                    branchGrowingAnimation));

        }
        return sameDepthBranchAnimation;

    }

    private Animation animateTreeWind(List<Branch> branchGeneration, int depth, Duration duration) {
        ParallelTransition wind = new ParallelTransition();
        for (final Branch brunch : branchGeneration) {
            final Rotate rotation = new Rotate(0);
            brunch.getTransforms().add(rotation);
            wind.getChildren().add(TimelineBuilder.create().keyFrames(new KeyFrame(duration,
                    new KeyValue(rotation.angleProperty(), depth * 2))).autoReverse(true).cycleCount(Animation.INDEFINITE).build());
        }
        return wind;
    }

    private Transition seasonsAnimation(final Tree tree) {

        Transition spring = animateSpring(tree.leafage);
        Transition flowers = animateFlowers(tree.flowers);
        Transition autumn = animateAutumn(tree.leafage);
        return SequentialTransitionBuilder.create().children(spring, flowers, autumn).cycleCount(Animation.INDEFINITE).build();
    }

    private Transition animateSpring(List<Leaf> leafage) {
        ParallelTransition springAnimation = new ParallelTransition();
        for (Leaf leaf : leafage) {
            springAnimation.getChildren().add(ScaleTransitionBuilder.create().toX(1).toY(1).node(leaf).duration(LEAF_APPEARING_DURATION).build());
        }
        return springAnimation;
    }

    private Transition animateFlowers(List<Flower> flowers) {

        ParallelTransition flowersAppearAndFallDown = new ParallelTransition();

        for (int i = 0; i < flowers.size(); i++) {
            final Flower flower = flowers.get(i);
            for (Ellipse pental : flower.getPetals()) {
                flowersAppearAndFallDown.getChildren().add(new SequentialTransition(
                        FadeTransitionBuilder.create().delay(FLOWER_APPEARING_DURATION.divide(3).multiply(i + 1)).duration(FLOWER_APPEARING_DURATION).node(pental).toValue(1).build(), fakeFallDownAnimation(pental)));
            }
        }
        return flowersAppearAndFallDown;
    }

    private Transition animateAutumn(List<Leaf> leafage) {
        ParallelTransition autumn = new ParallelTransition();

        ParallelTransition yellowLeafage = new ParallelTransition();
        ParallelTransition dissappearLeafage = new ParallelTransition();

        for (final Leaf leaf : leafage) {

            final FillTransition toYellow = FillTransitionBuilder.create().shape(leaf).toValue(leaf.AUTUMN_COLOR).duration(LEAF_BECOME_YELLOW_DURATION).build();
            yellowLeafage.getChildren().add(toYellow);

            Animation fakeLeafageDown = fakeFallDownEllipseAnimation((Ellipse) leaf, leaf.AUTUMN_COLOR, new HideMethod() {

                public void hide(Node node) {
                    node.setScaleX(0);
                    node.setScaleY(0);
                }
            });
            dissappearLeafage.getChildren().add(new SequentialTransition(
                    fakeLeafageDown,
                    FillTransitionBuilder.create().shape(leaf).toValue((Color) leaf.getFill()).duration(Duration.ONE).build()));
        }

        ParallelTransition grassBecomeYellowAnimation = new ParallelTransition();

        autumn.getChildren().addAll(grassBecomeYellowAnimation, new SequentialTransition(yellowLeafage, dissappearLeafage));
        return autumn;
    }

    private Animation fakeFallDownAnimation(final Ellipse pentalOld) {
        return fakeFallDownEllipseAnimation(pentalOld, null, new HideMethod() {

            public void hide(Node node) {
                node.setOpacity(0);
            }
        });
    }

    private Animation fakeFallDownEllipseAnimation(final Ellipse sourceEllipse, Color fakeColor, final HideMethod hideMethod) {

        final Ellipse fake = copyEllipse(sourceEllipse, fakeColor);
        addChildToParent(treeGenerator.content, fake);

        PauseTransition replaceFakeWithSource = PauseTransitionBuilder.create().duration(Duration.ONE).delay(Duration.minutes(0.9 * random() + 0.1)).onFinished(new EventHandler<ActionEvent>() {

            public void handle(ActionEvent t) {

                final Point2D position = treeGenerator.content.sceneToLocal(sourceEllipse.localToScene(0, 0));

                DoubleBinding sinPath = new DoubleBinding() {

                    {
                        bind(fake.translateYProperty());
                    }

                    @Override
                    protected double computeValue() {
                        return 50 * sin((fake.translateYProperty().doubleValue() - position.getY()) / 20);
                    }
                };
                fake.setTranslateY(position.getY());
                fake.setCenterX(0);
                fake.setCenterY(0);
                fake.translateXProperty().bind(sinPath.add(position.getX()));
                fake.rotateProperty().bind(fake.translateYProperty().multiply(2).add(random() * 180));
                //replace source with fake
                fake.setOpacity(1);
                hideMethod.hide(sourceEllipse);
            }
        }).build();

        return new SequentialTransition(replaceFakeWithSource,
                TranslateTransitionBuilder.create().duration(Duration.seconds(30)).toY(random() * 30 + 1).node(fake).build(),
                FadeTransitionBuilder.create().toValue(0).delay(Duration.seconds(5)).duration(Duration.seconds(2)).node(fake).build());


    }

    private Ellipse copyEllipse(Ellipse petalOld, Color color) {
        Ellipse ellipse = new Ellipse();
        ellipse.setRadiusX(petalOld.getRadiusX());
        ellipse.setRadiusY(petalOld.getRadiusY());
        if (color == null) {
            ellipse.setFill(petalOld.getFill());
        } else {
            ellipse.setFill(color);
        }
        ellipse.setRotate(petalOld.getRotate());
        ellipse.setOpacity(0);
        return ellipse;
    }

    private static interface HideMethod {

        void hide(Node node);
    };
}
