//******************************************************************************
// Copyright (C) 2019 University of Oklahoma Board of Trustees.
//******************************************************************************
// Last modified: Tue Jan 28 09:28:34 2020 by Chris Weaver
//******************************************************************************
// Major Modification History:
//
// 20190203 [weaver]:	Original file.
// 20190220 [weaver]:	Adapted from swingmvc to fxmvc.
//
//******************************************************************************
//
//******************************************************************************

package edu.ou.cs.hci.assignment.prototypea.pane;

//import java.lang.*;

import edu.ou.cs.hci.assignment.prototypea.Model;
import edu.ou.cs.hci.resources.Resources;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.event.*;
import javafx.geometry.*;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.scene.text.*;
import javafx.scene.layout.*;
import javafx.scene.image.ImageView;
import edu.ou.cs.hci.assignment.prototypea.Controller;

import java.util.function.UnaryOperator;

//******************************************************************************

/**
 * The <CODE>EditorPane</CODE> class.
 *
 * @author Chris Weaver
 * @version %I%, %G%
 */
public final class EditorPane extends AbstractPane {
    //**********************************************************************
    // Private Class Members
    //**********************************************************************

    private static final String NAME = "Editor";
    private static final String HINT = "Movie Metadata Editor";

    //**********************************************************************
    // Private Class Members (Effects)
    //**********************************************************************

    private static final Font FONT_LARGE =
            Font.font("Serif", FontPosture.ITALIC, 24.0);

    private static final Font FONT_SMALL =
            Font.font("Serif", FontPosture.ITALIC, 18.0);

    //**********************************************************************
    // Private Members
    //**********************************************************************

    private TextField titleField;

    private TextField directorField;

    private Slider runtimeSlider;

    private TextField runtimeDisplay;

    private ListView<String> genreList;

    private CheckBox isAnimated, isColor;

    private TextField yearField;

    private ToggleGroup ratingGroup;

    private RadioButton gButton, pgButton, pg13Button, rButton;

    private Button imgSelect;

    private CheckBox awardPicture, awardDirecting, awardCinematography, awardActing;

    private TextField imgPath;

    private TextField userRatings, userAverage;

    private TextArea summaryField;

    // Handlers
    private final ActionHandler actionHandler;

    private final GenreChanger genreChanger;

    private final AgeRatingChanger ageRatingChanger;

    private final TextAreaHandler textAreaHandler;

    //**********************************************************************
    // Constructors and Finalizer
    //**********************************************************************

    public EditorPane(Controller controller) {
        super(controller, NAME, HINT);

        actionHandler = new ActionHandler();

        genreChanger = new GenreChanger();

        ageRatingChanger = new AgeRatingChanger();

        textAreaHandler = new TextAreaHandler();

        setBase(buildPane());
    }

    //**********************************************************************
    // Public Methods (Controller)
    //**********************************************************************

    // TODO #4: Write code to initialize the widgets in your layout with the
    // data attribute values from the model when the UI first appears.

    // The controller calls this method when it adds a view.
    // Set up the nodes in the view with data accessed through the controller.
    public void initialize() {
        // Widget Gallery, Slider

        titleField.setText((String) controller.get("title"));

        directorField.setText((String) controller.get("director"));

        runtimeSlider.setValue((Integer) controller.get("runtime"));

        yearField.setText(String.format("%04d", (int) controller.get("year")));

        selectAgeRating((String) controller.get("ageRating"));

    }

    // TODO #10: Write code to take any changes to data attribute values in the
    // model and update the corresponding widgets. The Model and Controller
    // classes help by calling the update() method below whenever there is a
    // data attribute value. Use the same key (name) from TODO #0 to figure out
    // which data attribute has changed, and update the corresponding widget to
    // show the new value.

    // The controller calls this method whenever something changes in the model.
    // Update the nodes in the view to reflect the change.
    public void update(String key, Object value) {
        //System.out.println("update " + key + " to " + value);
        if ("title".equals(key))
            titleField.setText((String) value);
        else if ("director".equals(key))
            directorField.setText((String) value);
        else if ("runtime".equals(key))
            runtimeSlider.setValue(Double.parseDouble(value.toString()));
        else if ("genre".equals(key)) {
            genreList.getSelectionModel().getSelectedIndices().removeListener(genreChanger);
            ObservableList<Integer> list = (ObservableList<Integer>) value;
            ObservableList<Integer> list_copy = FXCollections.observableArrayList(list);
            genreList.getSelectionModel().clearSelection();
            for (int num : list_copy) {
                genreList.getSelectionModel().select(num);
            }
            genreList.getSelectionModel().getSelectedIndices().addListener(genreChanger);
        } else if ("isAnimated".equals(key))
            isAnimated.setSelected((Boolean) value);
        else if ("isColor".equals(key))
            isColor.setSelected((Boolean) value);
        else if ("year".equals(key))
            yearField.setText(Integer.toString((int) value));
        else if ("ageRating".equals(key))
            selectAgeRating((String) value);
        else if ("posterPath".equals(key))
            imgPath.setText((String) value);
        else if (key.contains("award")) {
            if (key.contains("Picture"))
                setAwardCheck(awardPicture, (Model.Award) value);
            else if (key.contains("Directing"))
                setAwardCheck(awardDirecting, (Model.Award) value);
            else if (key.contains("Cinematography"))
                setAwardCheck(awardCinematography, (Model.Award) value);
            else if (key.contains("Acting"))
                setAwardCheck(awardActing, (Model.Award) value);
        } else if ("numUserRatings".equals(key))
            userRatings.setText(Integer.toString((Integer) value));
        else if ("userRatingAvg".equals(key))
            userAverage.setText(Double.toString((Double) value));
        else if ("summary".equals(key))
            summaryField.setText((String) value);
    }

    //**********************************************************************
    // Private Methods (Layout)
    //**********************************************************************

    // TODO #3: Write code to organize the widgets, labels, etc. in your design
    // into a hierarchical layout of panes. Refer to the javafx.scene.layout
    // package in the JavaFX APIs to learn about available pane classes. You are
    // likely to find BorderPane, GridPane, HBox, StackPane, VBox most useful.

    private Pane buildPane() {

        BorderPane outerPane = new BorderPane();
        BorderStroke outerBorderSroke = new BorderStroke(Color.DARKGRAY, BorderStrokeStyle.SOLID, CornerRadii.EMPTY, BorderStroke.DEFAULT_WIDTHS, new Insets(20));
        Border outerBorder = new Border(outerBorderSroke);
        outerPane.setBorder(outerBorder);

        GridPane upperGrid = new GridPane();

        upperGrid.setAlignment(Pos.CENTER);
        upperGrid.setHgap(8.0);
        upperGrid.setVgap(8.0);

        Label titleLabel = new Label("Title:");
        Label directorLabel = new Label("Director:");
        Label runtimeLabel = new Label("Runtime:");
        Label genreLabel = new Label("Genre:");
        Label awardLabel = new Label("Awards:");

        upperGrid.add(titleLabel, 0, 0);
        upperGrid.add(directorLabel, 0, 1);
        upperGrid.add(runtimeLabel, 0, 2);
        upperGrid.add(genreLabel, 0, 3);
        upperGrid.add(awardLabel, 0, 4);

        upperGrid.add(createTitle(), 1, 0);
        upperGrid.add(createDirector(), 1, 1);
        upperGrid.add(createRuntimeSlider(), 1, 2);
        upperGrid.add(createGenrePane(), 1, 3);
        upperGrid.add(createAwardPane(), 1, 4);

        upperGrid.add(createYear(), 2, 0);
        upperGrid.add(createAnimated(), 2, 1);
        upperGrid.add(createColor(), 2, 2);
        upperGrid.add(createAgeRating(), 2, 3);

        Pane posterPane = createImagePane();
        GridPane.setRowSpan(posterPane, 4);
        upperGrid.add(posterPane, 3, 0);

        Pane lowerRightPane = createLowerRight();
        GridPane.setColumnSpan(lowerRightPane, 2);
        GridPane.setRowSpan(lowerRightPane, 2);
        upperGrid.add(lowerRightPane, 2, 4);

        BorderStroke gridBorderStroke = new BorderStroke(Color.TRANSPARENT, BorderStrokeStyle.SOLID, CornerRadii.EMPTY, BorderStroke.DEFAULT_WIDTHS, new Insets(8));
        Border gridBorder = new Border(gridBorderStroke);
        upperGrid.setBorder(gridBorder);

        upperGrid.setGridLinesVisible(true);

        outerPane.setTop(upperGrid);

        return outerPane;
    }

    //**********************************************************************
    // Private Methods (Widget Pane Creators)
    //**********************************************************************

    // TODO #1: Write methods to create the widgets used for editing,
    // showing, labeling, etc. the various data attributes in the model.
    // (Note that the example methods below put their widget inside a titled
    // pane and return that pane instead of the widget itself. For your design,
    // you probably won't want to box and label your widgets that way.)

    // TODO #2a: In your methods, include code to register each widget with the
    // appropriate event listeners and/or property change handlers. Refer to the
    // javafx.scene.control package in the JavaFX APIs to learn about the events
    // and properties utilized by each widget type.


    // Create the title entry field
    private TextField createTitle() {

        Pane pane = new FlowPane(Orientation.HORIZONTAL, 8.0, 8.0);

        Label label = new Label("Title:");

        titleField = new TextField();
        titleField.setPrefColumnCount(20);
        titleField.setOnAction(actionHandler);

        pane.getChildren().add(label);
        pane.getChildren().add(titleField);

        return titleField;
    }

    // Create the director entry field
    private TextField createDirector() {
        Pane pane = new FlowPane(Orientation.HORIZONTAL, 8.0, 8.0);

        Label label = new Label("Director:");
        directorField = new TextField();
        directorField.setOnAction(actionHandler);

        pane.getChildren().add(label);
        pane.getChildren().add(directorField);

        return directorField;
    }

    private Pane createRuntimeSlider() {

        FlowPane pane = new FlowPane(Orientation.HORIZONTAL, 8.0, 8);
        pane.setPrefWidth(titleField.getWidth());

        int runtime_min = 1;
        int runtime_max = 360;

        runtimeSlider = new Slider(runtime_min, runtime_max, 120);
        runtimeSlider.setShowTickLabels(true);
        runtimeSlider.setMajorTickUnit(runtime_max - runtime_min);
        runtimeSlider.setBlockIncrement(1);
        runtimeSlider.valueProperty().addListener(this::changeRuntime);

        pane.getChildren().add(runtimeSlider);

        runtimeDisplay = new TextField();
        runtimeDisplay.setEditable(false);
        runtimeDisplay.setPrefColumnCount(3);

        pane.getChildren().add(runtimeDisplay);

        return pane;
    }

    private Pane createAgeRating() {
        Pane pane = new HBox(8);
        pane.getChildren().add(new Label("Rating:"));

        Pane flowPane = new FlowPane(Orientation.VERTICAL, 8, 8);
        pane.getChildren().add(flowPane);

        ratingGroup = new ToggleGroup();
        ratingGroup.selectedToggleProperty().addListener(ageRatingChanger);

        gButton = new RadioButton("G");
        gButton.setToggleGroup(ratingGroup);
        flowPane.getChildren().add(gButton);

        pgButton = new RadioButton("PG");
        pgButton.setToggleGroup(ratingGroup);
        flowPane.getChildren().add(pgButton);

        pg13Button = new RadioButton("PG-13");
        pg13Button.setToggleGroup(ratingGroup);
        flowPane.getChildren().add(pg13Button);

        rButton = new RadioButton("R");
        rButton.setToggleGroup(ratingGroup);
        flowPane.getChildren().add(rButton);

        pane.setMaxHeight(100);

        return pane;
    }

    private ListView<String> createGenrePane() {

        genreList = new ListView<String>();
        ObservableList<String> genres = FXCollections.observableArrayList("Action", "Comedy", "Documentary"
                , "Drama", "Fantasy", "Horror", "Romance", "Sci-Fi", "Thriller", "Western");
        genreList.setItems(genres);
        genreList.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        genreList.getSelectionModel().getSelectedIndices().addListener(genreChanger);
        genreList.setMaxHeight(100);

        return genreList;
    }

    private Pane createAwardPane() {

        Pane pane = new FlowPane(Orientation.VERTICAL, 8, 8);

        awardPicture = new CheckBox("Picture");
        awardPicture.setAllowIndeterminate(true);
        awardPicture.setOnAction(actionHandler);
        pane.getChildren().add(awardPicture);

        awardDirecting = new CheckBox("Directing");
        awardDirecting.setAllowIndeterminate(true);
        awardDirecting.setOnAction(actionHandler);
        pane.getChildren().add(awardDirecting);

        awardCinematography = new CheckBox("Cinematography");
        awardCinematography.setAllowIndeterminate(true);
        awardCinematography.setOnAction(actionHandler);
        pane.getChildren().add(awardCinematography);

        awardActing = new CheckBox("Acting");
        awardActing.setAllowIndeterminate(true);
        awardActing.setOnAction(actionHandler);
        pane.getChildren().add(awardActing);

        pane.setMaxHeight(100);

        return pane;
    }

    private CheckBox createAnimated() {
        isAnimated = new CheckBox("Animated");
        isAnimated.setOnAction(actionHandler);
        return isAnimated;
    }

    private CheckBox createColor() {
        isColor = new CheckBox("Color");
        isColor.setOnAction(actionHandler);
        return isColor;
    }

    private Pane createYear() {
        Pane pane = new FlowPane(Orientation.HORIZONTAL, 8, 8);

        pane.setMaxWidth(100);
        pane.getChildren().add(new Label("Year:"));

        yearField = new TextField();
        yearField.setPrefColumnCount(3);
        yearField.setTextFormatter(new TextFormatter<String>(integerFilter));
        yearField.setOnAction(actionHandler);

        pane.getChildren().add(yearField);
        return pane;
    }

    private Pane createImagePane() {
        FlowPane pane = new FlowPane(Orientation.VERTICAL, 8, 8);
        pane.setColumnHalignment(HPos.CENTER);

        //pane.setMaxHeight(230);

        ImageView posterView = new ImageView(Resources.getFXImage("Avatar The Way of Water poster.jpg"));
        posterView.setPreserveRatio(true);
        posterView.setFitWidth(110);
        pane.getChildren().add(posterView);

        imgSelect = new Button("Select Image");
        imgSelect.setOnAction(actionHandler);
        pane.getChildren().add(imgSelect);

        imgPath = new TextField();
        imgPath.setOnAction(actionHandler);
        pane.getChildren().add(imgPath);

        pane.setMaxHeight(230);

        return pane;

    }

    private Pane createLowerRight() {
        GridPane pane = new GridPane();
        pane.setAlignment(Pos.TOP_LEFT);
        pane.setGridLinesVisible(true);
        pane.setHgap(8);
        pane.setVgap(8);

        pane.add(new Label("Ratings:"), 0, 0);
        userRatings = new TextField();
        userRatings.setPrefColumnCount(4);
        userRatings.setOnAction(actionHandler);
        userRatings.setTextFormatter(new TextFormatter<String>(integerFilter));
        pane.add(userRatings, 1, 0);

        pane.add(new Label("Average:"), 2, 0);
        userAverage = new TextField();
        userAverage.setPrefColumnCount(3);
        userAverage.setOnAction(actionHandler);
        pane.add(userAverage, 3, 0);

        pane.add(new Label("Summary:"), 0, 1);
        summaryField = new TextArea();
        summaryField.setPrefColumnCount(15);
        summaryField.setPrefHeight(200);
        summaryField.setWrapText(true);
        ScrollPane scroll = new ScrollPane();
        scroll.setContent(summaryField);
        scroll.setFitToWidth(true);
        scroll.setPrefHeight(120);
        GridPane.setColumnSpan(scroll, 3);
        summaryField.setOnMouseExited(textAreaHandler);
        //summaryField.setOnAction(actionHandler);
        pane.add(scroll, 1, 1);

        pane.setMaxWidth(300);


        return pane;
    }

    //**********************************************************************
    // Private Methods (Property Change Handlers)
    //**********************************************************************

    // TODO #2b: Add any additional methods needed to register change listening
    // for the properties of the widgets you created for your layout.

    // TODO #9a: In the methods you added, implement code to pass the modified
    // value of the observed property to the corresponding data attribute value
    // in the model.

    private void changeRuntime(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
        if (observable == runtimeSlider.valueProperty()) {
            controller.set("runtime", newValue.intValue());
            runtimeDisplay.setText(Integer.toString(newValue.intValue()));
        }
    }

    //**********************************************************************
    // Inner Classes (Event Handling)
    //**********************************************************************

    // TODO #2c: Add any additional inner classes needed to register event
    // handling for the widgets you created for your layout.

    // TODO #9b: In the classes you added, implement the event handling method
    // to get the modified information in the relevant widget and use it to
    // update the corresponding data attribute value in the model.

    UnaryOperator<TextFormatter.Change> integerFilter = change -> {
        String input = change.getText();
        if (input.matches("[0-9]*"))
            return change;
        return null;
    };

    private final void selectAgeRating(String rating) {
        ObservableList<Toggle> toggles = ratingGroup.getToggles();
        for (Toggle toggle : toggles) {
            RadioButton button = (RadioButton) toggle;
            if (rating != null && button.getText().equals(rating))
                ratingGroup.selectToggle(button);
        }
    }

    private final class GenreChanger implements ListChangeListener {

        public void onChanged(Change change) {
            while (change.next()) {
                if (change.wasAdded() || change.wasRemoved()) {
                    controller.set("genre", genreList.getSelectionModel().getSelectedIndices());
                }
            }
        }
    }

    private final class AgeRatingChanger implements ChangeListener<Toggle> {

        @Override
        public void changed(ObservableValue<? extends Toggle> observable, Toggle oldValue, Toggle newValue) {
            if (ratingGroup.getSelectedToggle() != null) {
                RadioButton selcted = (RadioButton) ratingGroup.getSelectedToggle();
                controller.set("ageRating", selcted.getText());
            }

        }
    }

    private void updateAwardState(CheckBox source) {
        String name = source.toString();
        String key = "award";

        if (name.contains("Picture"))
            key += "Picture";
        else if (name.contains("Directing"))
            key += "Directing";
        else if (name.contains("Cinematography"))
            key += "Cinematography";
        else if (name.contains("Acting"))
            key += "Acting";

        controller.set(key, readAwardCheck(source));
    }

    private Model.Award readAwardCheck(CheckBox source) {
        if (source.isIndeterminate())
            return Model.Award.NOMINATED;
        else if (source.isSelected())
            return Model.Award.TRUE;
        else
            return Model.Award.FALSE;
    }

    private void setAwardCheck(CheckBox check, Model.Award state) {
        if (state.equals(Model.Award.NOMINATED))
            check.setIndeterminate(true);
        else
            check.setIndeterminate(false);
        check.setSelected(state.equals(Model.Award.TRUE));
    }

    private final class ActionHandler
            implements EventHandler<ActionEvent> {
        public void handle(ActionEvent e) {
            Object source = e.getSource();

            if (source == titleField)
                controller.set("title", titleField.getText());
            else if (source == directorField)
                controller.set("director", directorField.getText());
            else if (source == isAnimated)
                controller.set("isAnimated", isAnimated.isSelected());
            else if (source == isColor)
                controller.set("isColor", isColor.isSelected());
            else if (source == yearField) {
                int year = Integer.parseInt(yearField.getText());
                if (year < 1960)
                    controller.set("year", 1960);
                else if (year > 2040)
                    controller.set("year", 2040);
                else
                    controller.set("year", year);
            } else if (source == imgSelect) {
                String text = imgSelect.getText();
                if (text.startsWith("Clicked")) {
                    imgSelect.setText(text + "*");
                } else
                    imgSelect.setText("Clicked");
            } else if (source == imgPath) {
                controller.set("posterPath", imgPath.getText());
            } else if (source == awardPicture || source == awardDirecting
                    || source == awardCinematography || source == awardActing) {
                updateAwardState((CheckBox) source);
            } else if (source == userRatings)
                controller.set("numUserRatings", Integer.parseInt(userRatings.getText()));
            else if (source == userAverage) {
                Double avg;
                try {
                    avg = Double.parseDouble(userAverage.getText());
                    if (avg < 0)
                        controller.set("userRatingAvg", 0.0);
                    else if (avg > 10)
                        controller.set("userRatingAvg", 10.0);
                    else
                        controller.set("userRatingAvg", avg);
                } catch (NumberFormatException exception) {
                }
            }
        }
    }

    private final class TextAreaHandler implements EventHandler<MouseEvent> {

        @Override
        public void handle(MouseEvent event) {
            Object source = event.getSource();
            if (source == summaryField)
                controller.set("summary", summaryField.getText());
        }
    }


}

//******************************************************************************
