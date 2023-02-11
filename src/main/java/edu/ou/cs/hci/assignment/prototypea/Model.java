//******************************************************************************
// Copyright (C) 2019-2020 University of Oklahoma Board of Trustees.
//******************************************************************************
// Last modified: Wed Feb 20 19:34:56 2019 by Chris Weaver
//******************************************************************************
// Major Modification History:
//
// 20190203 [weaver]:	Original file.
// 20190220 [weaver]:	Adapted from swingmvc to fxmvc.
//
//******************************************************************************
//
//******************************************************************************

package edu.ou.cs.hci.assignment.prototypea;

//import java.lang.*;
import java.util.HashMap;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

//******************************************************************************

/**
 * The <CODE>Model</CODE> class.
 *
 * @author  Chris Weaver
 * @version %I%, %G%
 */
public final class Model
{
	//**********************************************************************
	// Private Members
	//**********************************************************************

	// Master of the program, manager of the data, mediator of all updates
	private final Controller				controller;

	// Easy, extensible way to store multiple simple, independent parameters
	private final HashMap<String, Object>	properties;

	//**********************************************************************
	// Constructors and Finalizer
	//**********************************************************************

	public Model(Controller controller)
	{
		this.controller = controller;

		properties = new HashMap<String, Object>();

		// Create a property for each of the 17 editable data
		// attributes from the table in Design A. (The table has 19 rows, but
		// 2 of them are merely for helper widgets to load and show the poster.)
		properties.put("userRatingAvg", 0.0);
		properties.put("awardPicture", Award.FALSE);
		properties.put("awardDirecting", Award.FALSE);
		properties.put("awardCinematography", Award.FALSE);
		properties.put("awardActing", Award.FALSE);
		properties.put("comments", "Comments");
		properties.put("director", "Director");
		properties.put("genre", FXCollections.observableArrayList());
		properties.put("isAnimated", false);
		properties.put("isColor", false);
		properties.put("numUserRatings",0);
		properties.put("posterPath", "Path");
		properties.put("ageRating", "NR");
		properties.put("runtime", 120);
		properties.put("summary", "Summary");
		properties.put("title","Title");
		properties.put("year", 0000);

	}

	//**********************************************************************
	// Public Methods (Controller)
	//**********************************************************************

	public Object	getValue(String key)
	{
		return properties.get(key);
	}

	public void	setValue(String key, Object value)
	{
		if (properties.containsKey(key) &&
			properties.get(key).equals(value))
		{
			System.out.println("  model: value not changed");
			return;
		}

		Platform.runLater(new Updater(key, value));
	}

	public void	trigger(String name)
	{
		System.out.println("  model: (not!) calculating function: " + name);
	}

	//**********************************************************************
	// Inner Classes (Updater)
	//**********************************************************************

	private class Updater
		implements Runnable
	{
		private final String	key;
		private final Object	value;

		public Updater(String key, Object value)
		{
			this.key = key;
			this.value = value;
		}

		public void	run()
		{
			properties.put(key, value);
			controller.update(key, value);
		}
	}

	public enum Award {FALSE, NOMINATED, TRUE};
}

//******************************************************************************
