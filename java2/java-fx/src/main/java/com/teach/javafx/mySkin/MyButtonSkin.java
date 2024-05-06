package com.teach.javafx.mySkin;

import javafx.animation.Animation;
import javafx.animation.FadeTransition;
import javafx.animation.Interpolator;
import javafx.animation.Transition;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.skin.ButtonSkin;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.paint.Color;
import javafx.util.Duration;

public class MyButtonSkin extends ButtonSkin {
    private double originWidth;
    private double originHeight;
    private boolean isInit = false;

    static private Color getColor(Button button){
        Background bg = button.getBackground();
        BackgroundFill bf = bg.getFills().get(0);
        return (Color) bf.getFill();
    }

    static private void setColor(Button button, Color color){
        button.setBackground(new Background(new BackgroundFill(color, CornerRadii.EMPTY, Insets.EMPTY)));
    }

    public MyButtonSkin(Button button) {
        super(button);

        button.addEventHandler(MouseEvent.ANY, new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                if(!isInit){
                    Button target = (Button) event.getTarget();
                    originWidth = target.getWidth();
                    originHeight = target.getHeight();
                    isInit = true;
                    System.out.println("背景填充对象个数: " + target.getBackground().getFills().size());
                }
                if(event.getEventType() == MouseEvent.MOUSE_ENTERED){
                    final Animation animation = new Transition() {
                        {
                            setCycleDuration(Duration.millis(300));
                            setInterpolator(Interpolator.EASE_OUT);
                        }
                        @Override
                        protected void interpolate(double v) {
                            double newWidth = button.getWidth() + v * 10;
                            double newHeight = button.getHeight() + v * 3.33;
                            if(newWidth < originWidth + 30){
                                button.setPrefWidth(newWidth);
                            }
                            else{
                                button.setPrefWidth(originWidth+30);
                            }
                            if(newHeight < originHeight + 10){
                                button.setPrefHeight(newHeight);
                            }
                            else{
                                button.setPrefHeight(originHeight+10);
                            }
                        }
                    };
                    animation.play();
                }
                else if(event.getEventType() == MouseEvent.MOUSE_EXITED){
                    final Animation animation = new Transition() {
                        {
                            setCycleDuration(Duration.millis(300));
                            setInterpolator(Interpolator.EASE_OUT);
                        }
                        @Override
                        protected void interpolate(double v) {
                            double newWidth = button.getWidth() - v * 10;
                            double newHeight = button.getHeight() - v * 3.33;
                            if(newWidth > originWidth){
                                button.setPrefWidth(newWidth);
                            }
                            else{
                                button.setPrefWidth(originWidth);
                            }
                            if(newHeight > originHeight){
                                button.setPrefHeight(newHeight);
                            }
                            else {
                                button.setPrefHeight(originHeight);
                            }
                        }
                    };
                    animation.play();
                }
            }
        });
    }
}
