// Arsen Cui
// ICS4U1-01
// September 30, 2019
// Mr. Radulovic
// ICS4U Review Assignment
/*This program is a simulator. Using coordinates from a sample function or list of coordinates, the
program is able to approximate the values of these coordinates. It creates lines that are spinning
at different frequencies, all connected head to tail using vector addition. The end point of the 
last line will draw a path that draws out the approximated function.*/

import javafx.application.Application;
import javafx.scene.Group;
import javafx.stage.*;
import javafx.scene.Scene;
import javafx.scene.shape.Line;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Scanner;
import javafx.animation.AnimationTimer;

public class Assignment1 extends Application {

	private Group sceneMain; // Group storing the main scene 
	private Group sceneLines; // Group storing the spinning lines
	private Group sceneFLines; // Group storing the drawn lines of the function
	
	private double center; // coordinates for the center of the screen
	
	private Circle c; // dot used to track drawing location at the end of the spinning lines
	
	// variable used to track the sample coordinates range
	private double endX;
	
	// variables used to help calculate the coefficients and coordinate values
	
	private double t; // variable used to track time elapsed and help calculate coordinate values
	private double deltaT; // amount to increment t by each time
	private int i; // variable used to run through the sample coordinates in the original function
	
	// variables used to store the coordinates of the function being drawn
	private double x; 	
	private double y;
	
	private int numLines; // how many spinning lines there are in total
	
	private boolean draw; // boolean used to track whether the function has finished drawing
	
	private ArrayList<double[]> cf; // ArrayList storing the values of cfx and cfy
	
	// ArrayList storing the coordinates of the spinning lines
	private ArrayList<double[]> lineCoordinates;
	
	// ArrayList storing the coordinates of the original sample function
	private ArrayList<double[]> coordinates_o;
	
	// variables used to temporarily store coordinates to draw the function
	private double drawx0;
	private double drawy0;
	private double drawx1;
	private double drawy1;
	
	private AnimationTimer timer; // Animation timer used to animate the function drawing

	// Method used to load coordinates of a function from a text file
	public ArrayList<double[]> loadFunction(String fileName) throws FileNotFoundException
	{
		File file = new File(fileName);
		Scanner scan = new Scanner(file);
		
		ArrayList<double[]> coordinates = new ArrayList<double[]>();
		
		// Reads the values from the file and stores them in an arraylist
		while (scan.hasNextLine())
		{
			String line = scan.nextLine();
			String[] pointstring = line.split(",");
			double[] point = {Double.parseDouble(pointstring[0]), 
							  Double.parseDouble(pointstring[1])};
			
			coordinates.add(point);
		}
		
		scan.close();
		
		return coordinates;
	}
	
	// Method used to load coordinates of a function from a hard coded equation
	public ArrayList<double[]> loadFunction() throws FileNotFoundException
	{
		ArrayList<double[]> coordinates = new ArrayList<double[]>();
		
		endX = 15.0;
		double t = -endX;
		double t_increment = 0.1;
		
		/*Goes through all the x values within the range specified, 
		calculates the coordinate values, and stores them in an arraylist*/
		while (t <= endX)
		{
			double x = t;
			double y = t*t; // Function of a parabola
			
			double[] point = {x, y};
			
			coordinates.add(point);
			
			t += t_increment; // increments x value to calculate the coordinates for the next point
		}
		
		return coordinates;
	}
	
	// Method used to draw the original sample function
	public void drawFunction()
	{
		// Runs through all of the coordinates, drawing from each point to the next
		for (int i = 0; i < coordinates_o.size() - 1; i += 1)
		{
			Line l = new Line(coordinates_o.get(i)[0] + center, center - coordinates_o.get(i)[1], 
					coordinates_o.get(i + 1)[0] + center, center - coordinates_o.get(i + 1)[1]);
			l.setStroke(Color.RED);
			
			sceneFLines.getChildren().add(l);
		}
		
	}
	
	// Method used to update the spinning lines every frame
	public void drawLines() throws FileNotFoundException
	{	
		/*Add the center of the screen as the first 
		coordinate to draw the spinning lines from center*/
		double[] origin = {center, center};
		lineCoordinates.add(origin);
		
		// Variables used to store the spinning line coordinates
		double currentx = origin[0];
		double currenty = origin[1];
		double newx = 0;
		double newy = 0;
		
		// Runs through all the spinning lines, updating their coordinates in the current frame
		for (int f = -numLines/2, i = 0; f <= numLines/2; f += 1, i += 1)
		{
			x = cf.get(i)[0] * Math.cos(2.0*Math.PI*f*t) - 
					(cf.get(i)[1] * Math.sin(2.0*Math.PI*f*t));
			
			y = cf.get(i)[0] * Math.sin(2.0*Math.PI*f*t) + 
					(cf.get(i)[1] * Math.cos(2.0*Math.PI*f*t));
			
			/*Updates the new coordinates of the spinning lines by adding the
			newly calculated values onto the previous coordinate values*/
			newx = x + currentx;
			newy = currenty - y;
			
			double[] point = {newx, newy};
			
			// Set the new coordinates as the previous coordinate for the next line to draw from
			currentx = newx;
			currenty = newy;
			
			lineCoordinates.add(point);
		}
		
		// Draws all of the spinning lines
		for (int i = 0; i < lineCoordinates.size() - 1; i += 1)
		{
			Line line = new Line(lineCoordinates.get(i)[0], lineCoordinates.get(i)[1], 
								 lineCoordinates.get(i + 1)[0], lineCoordinates.get(i + 1)[1]);
			sceneLines.getChildren().add(line);
		}
	}
	
	// Method used to draw the coordinates of the approximated function
	public void drawCoordinates()
	{
		// Increments to the next point in the function
		i += 1;
		
		// If the program has run through all of the coordinates, stop drawing
		if (t >= 1)
		{
			draw = false;
		}
		
		/*if this is the first time generating a coordinate of the function, store the initial 
		start values but don't start drawing until you get a second coordinate*/		
		if (i <= 1)
		{
			drawx0 = lineCoordinates.get(lineCoordinates.size() - 1)[0];
			drawy0 = lineCoordinates.get(lineCoordinates.size() - 1)[1];
		}
		
		/*Draw the approximated function by getting the end point of the spinning lines
		from the previous frame to use as the starting point, and the end point of the
		spinning lines in the current frame to use as the ending point*/
		else if (i > 1)
		{
			drawx1 = lineCoordinates.get(lineCoordinates.size() - 1)[0];
			drawy1 = lineCoordinates.get(lineCoordinates.size() - 1)[1];
			
			Line l = new Line(drawx0, drawy0, drawx1, drawy1);
			l.setStroke(Color.RED);
			
			// Move the drawing dot along with the spinning line end point
			c.setCenterX(drawx1);
			c.setCenterY(drawy1);
			
			// Set the current end coordinates as the new starting coordinates for the next frame
			drawx0 = drawx1;
			drawy0 = drawy1;
			
			sceneFLines.getChildren().add(l);
		}
	}
	
	public static void main(String[] args) {
		launch(args);
	}

	@Override
	public void start(Stage primaryStage) throws Exception {
		
		sceneLines = new Group();
		sceneFLines = new Group();
		sceneMain = new Group();
		
		// Adds the spinning lines and function coordinates to the screen
		sceneMain.getChildren().addAll(sceneLines, sceneFLines);
		
		double sceneparameters = 600; // Size of screen
		Scene scene = new Scene(sceneMain, sceneparameters, sceneparameters);
		
		center = sceneparameters/2;
		
		t = 0;
		numLines = 101;
		
		draw = true;
		
		i = 0;
		
		lineCoordinates = new ArrayList<double[]>();
		
		coordinates_o = loadFunction();
		coordinates_o = loadFunction("coordinates.txt");
		
		cf = new ArrayList<double[]>();
		
		// calculate cfx and cfy for each spinning line, and store them in an arraylist
		for (int f = -numLines/2; f <= numLines/2; f += 1) // Runs through all the line frequencies
		{
			double t_temp = 0; // represents the current t value when calculating cfx and cfy
			double cfx = 0;
			double cfy = 0;
			deltaT = 1.0/(coordinates_o.size() - 1); // size of the t increment
			
			// Calculates cfx and cfy by adding all the values together from t equals 0 to 1
			for (int i = 0; i < coordinates_o.size(); i += 1)
			{
				t_temp = i * deltaT;
				
				// Values of the coordinates from the original sample function
				double originalx = coordinates_o.get(i)[0];
				double originaly = coordinates_o.get(i)[1];
				
				cfx += (originalx * Math.cos(2.0*Math.PI*f*t_temp) + 
						originaly * Math.sin(2.0*Math.PI*f*t_temp)) * deltaT;
				cfy -= (originalx * Math.sin(2.0*Math.PI*f*t_temp) - 
						originaly * Math.cos(2.0*Math.PI*f*t_temp)) * deltaT;
			}
			
			double[] cf_list = {cfx, cfy};
			
			cf.add(cf_list);
		}
		
		// initializes the dot representing the drawing point at the end of the spinning lines
		c = new Circle();
		c.setFill(Color.LIME);
		c.setRadius(5);
		
		sceneMain.getChildren().add(c);
		
		drawFunction(); // draws the original sample function
		
		// Animation timer used to animate the spinning lines and the drawing of coordinates
		timer = new AnimationTimer() {
			
			long oldTime = 0;
			
			@Override
			public void handle(long time) {

				oldTime += 1;
				
				if (time - oldTime >= 1) {
					try {
						upDate();
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					oldTime = time;
				}

			}
		};
		timer.start();
		
		primaryStage.setTitle("Assignment1");
		primaryStage.setScene(scene);
		primaryStage.show();
		
	}
	
	public void upDate() throws Exception {
		
		// if there are still points on the function left to draw
		if (draw == true)
		{
			// Clears the screen of spinning lines
			lineCoordinates.clear();
			sceneLines.getChildren().clear();
			
			//updates the spinning lines and draws the next coordinate on the approximated function
			drawLines();
			drawCoordinates();
		}
		
		// If the program has run through all of the coordinates, stop drawing
		if (draw == false)
		{
			timer.stop();
		}
		
		t = i * deltaT; // Increments t to calculate new coordinate values next frame
		
	}

}
