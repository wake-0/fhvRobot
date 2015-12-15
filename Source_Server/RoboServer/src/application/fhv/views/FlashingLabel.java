package views;


import javafx.animation.FadeTransition;
import javafx.animation.FillTransition;
import javafx.animation.Transition;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.control.Label;
import javafx.scene.control.TableCell;
import javafx.scene.paint.Color;
import javafx.util.Duration;

public class FlashingLabel extends Label
    {
        private FadeTransition animation;

        public FlashingLabel()
        {
            animation = new FadeTransition(Duration.millis(1000), this);
            animation.setFromValue(1.0);
            animation.setToValue(0);
            animation.setCycleCount(1);
            animation.setAutoReverse(true);
            animation.play();
            textProperty().addListener(new ChangeListener<String>()
            {
                public void changed(ObservableValue<? extends String> source, String oldValue, String newValue)
                {
                    if (!oldValue.equals(newValue))
                    {
		                setStyle("-fx-text-fill: #ff2222; -fx-stroke: black; -fx-stroke-width: 1; -fx-font-size: 14px;");
                        animation.playFromStart();
                        setText("\u2665"); // Black circle would be\u25CF");
                    }
                }
            });
            
        }
    }