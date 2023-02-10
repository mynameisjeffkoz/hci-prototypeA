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
import javafx.beans.Observable;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.event.*;
import javafx.geometry.*;
import javafx.scene.control.*;
import javafx.scene.text.*;
import javafx.scene.layout.*;
import edu.ou.cs.hci.assignment.prototypea.Controller;

import java.util.ArrayList;
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

	// Handlers
	private final ActionHandler actionHandler;

	private final GenreChanger genreChanger;

	//**********************************************************************
	// Constructors and Finalizer
	//**********************************************************************

	public EditorPane(Controller controller) {
		super(controller, NAME, HINT);

		actionHandler = new ActionHandler();

		genreChanger = new GenreChanger();

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

		runtimeSlider.setValue((Integer)controller.get("runtime"));

		yearField.setText(String.format("%04d", (int) controller.get("year")));

	}

	// TODO #5: Write code to detach widgets from any model properties (or other
	// resources) it has been using, in preparation for removing and destroying
	// the widget object. For Prototype A there's nothing to do for this step
	// since we only detach a widget when its window closes or the program ends.

	// TODO #6: Write code to remove widgets from the layout hierarchy. For
	// Prototype A there's nothing to do for this step since we only remove a
	// widget when its window closes or the program ends, and in those cases
	// JavaFX does the necessary cleanup automatically.

	// TODO #7: Write code to unregister each widget from any event listeners
	// and/or property change handlers it was registered with in TODO #2.

	// TODO #8: Write code to actually destroy the widget objects. There is
	// nothing to do here (in Prototype A or otherwise) since Java uses garbage
	// collection to destroy objects and reclaim any memory allocated for them.

	// The controller calls this method when it removes a view.
	// Unregister event and property listeners for the nodes in the view.


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
			System.out.println("List is " + list_copy.toString());
			genreList.getSelectionModel().clearSelection();
			System.out.println("Clear model");
			System.out.println("List is " + list.toString());
			System.out.println("list copy is " + list_copy.toString());
			for (int num:list_copy) {
				genreList.getSelectionModel().select(num);
			}
			genreList.getSelectionModel().getSelectedIndices().addListener(genreChanger);
			System.out.println("Reenable listener");
		}
		else if ("isAnimated".equals(key))
			 isAnimated.setSelected((Boolean) value);
		else if ("isColor".equals(key))
			 isColor.setSelected((Boolean) value);
		else if ("year".equals(key))
			 yearField.setText(Integer.toString((int) value));

	}

	//**********************************************************************
	// Private Methods (Layout)
	//**********************************************************************

	// TODO #3: Write code to organize the widgets, labels, etc. in your design
	// into a hierarchical layout of panes. Refer to the javafx.scene.layout
	// package in the JavaFX APIs to learn about available pane classes. You are
	// likely to find BorderPane, GridPane, HBox, StackPane, VBox most useful. 

	private Pane buildPane() {

		GridPane pane = new GridPane();

		pane.setAlignment(Pos.TOP_LEFT);
		pane.setHgap(8.0);
		pane.setVgap(8.0);

		Label titleLabel = new Label("Title:");
		Label directorLabel = new Label("Director:");
		Label runtimeLabel = new Label("Runtime:");
		Label genreLabel = new Label("Genre:");

		pane.add(titleLabel, 0, 0);
		pane.add(directorLabel, 0, 1);
		pane.add(runtimeLabel, 0, 2);
		pane.add(genreLabel, 0, 3);

		pane.add(createTitle(), 1, 0);
		pane.add(createDirector(), 1, 1);
		pane.add(createRuntimeSlider(), 1, 2);
		pane.add(createGenrePane(), 1, 3);

		pane.add(createYear(), 2, 0);
		pane.add(createAnimated(),2,1);
		pane.add(createColor(),2,2);
		pane.add(createRating(),2,3);
		return pane;
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

		FlowPane pane = new FlowPane(Orientation.HORIZONTAL,8.0,8);
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

	private Pane createRating() {
		Pane pane = new HBox(8);
		pane.getChildren().add(new Label("Rating:"));

		Pane flowPane = new FlowPane(Orientation.VERTICAL,8,8);
		pane.getChildren().add(flowPane);

		ratingGroup = new ToggleGroup();

		RadioButton gButton = new RadioButton("G");
		gButton.setToggleGroup(ratingGroup);
		flowPane.getChildren().add(gButton);

		RadioButton pgButton = new RadioButton("PG");
		pgButton.setToggleGroup(ratingGroup);
		flowPane.getChildren().add(pgButton);

		RadioButton pg13Button = new RadioButton("PG-13");
		pg13Button.setToggleGroup(ratingGroup);
		flowPane.getChildren().add(pg13Button);

		RadioButton rButton = new RadioButton("R");
		rButton.setToggleGroup(ratingGroup);
		flowPane.getChildren().add(rButton);

		return pane;
	}

	private ListView<String> createGenrePane() {

		genreList = new ListView<String>();
		ObservableList<String> genres = FXCollections.observableArrayList("Action", "Comedy", "Documentary"
				, "Drama", "Fantasy", "Horror", "Romance", "Sci-Fi", "Thriller", "Western");
		genreList.setItems(genres);
		genreList.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
		genreList.getSelectionModel().getSelectedIndices().addListener(genreChanger);

		return genreList;
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
		Pane pane = new FlowPane(Orientation.HORIZONTAL,8,8);

		pane.getChildren().add(new Label("Year:"));

		yearField = new TextField();
		yearField.setPrefColumnCount(3);
		yearField.setTextFormatter(new TextFormatter<String>(yearFilter));
		yearField.setOnAction(actionHandler);

		pane.getChildren().add(yearField);
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

	UnaryOperator<TextFormatter.Change> yearFilter = change -> {
		String input = change.getText();
		if (input.matches("[0-9]*"))
			return change;
		return null;
	};


	private final class GenreChanger implements ListChangeListener {

		public void onChanged(Change change) {
			while (change.next()) {
				if (change.wasAdded() || change.wasRemoved()) {
					controller.set("genre", genreList.getSelectionModel().getSelectedIndices());
				}
			}
		}
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
			}
		}
	}


}

//******************************************************************************
