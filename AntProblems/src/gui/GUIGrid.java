package gui; 
import java.awt.Graphics;
import java.awt.Graphics2D; 
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException; 
import javax.imageio.ImageIO; 
import javax.swing.JFrame;
import javax.swing.JPanel;

/**
 * GUIGrid berekent en tekent het uiterlijk van de grid en voert het resultaat van acties uit.
 * 
 * Belangrijk!: Het is niet de bedoeling dat je deze class in het genetisch algoritme gebruikt. 
 * Maak zelf een grid representatie. Sommige functionaliteit zal redundant voorkomen 
 * (zoals het uitvoeren van een actie). Je mag deze klasse wel aanpassen.
 *  
 * @author Bas Testerink
 */
public class GUIGrid extends JPanel{
	// Enkele constanten om de code overzichtelijk te houden
	public final static int SMALL_CELL = 16, MEDIUM_CELL = 48, LARGE_CELL = 96;
	public final static int NORTH=0,EAST=1,SOUTH=2,WEST=3;
	private static double[][] ANGLE_MAP = {
			{Math.PI,3*Math.PI,-Math.PI},
			{1.5*Math.PI,3.5*Math.PI,-0.5*Math.PI},
			{0,2*Math.PI,-2*Math.PI},
			{0.5*Math.PI,2.5*Math.PI,-2.5*Math.PI} };
	
	// Eigenschappen van de grid en de visuele status
	private int width, height, cellSize;
	private int antGridX, antGridY, antPixX, antPixY, orientation;
	private boolean[][] food;
	private double angle;
	private boolean finished = false;
	private BufferedImage antIcon, foodIcon;
	private AntFrame frame;
		
	/**
	 * Een standaard grid heeft een 10x10 afmeting, medium grootte en de mier is 
	 * naar het zuiden georienteerd.
	 */
	public GUIGrid(){
		setGridDimensions(10, 10);
		setSize(MEDIUM_CELL);
		orientation = SOUTH; 
		frame = new AntFrame(this);
	}
	
	/**
	 * Pas de grootte van de grid aan. De mier wordt op (0,0) gezet en het voedsel
	 * wordt verwijderd.
	 * @param width Nieuwe breedte van de grid.
	 * @param height Nieuwe hoogte van de grid.
	 */
	public void setGridDimensions(int width, int height){
		this.width = width;
		this.height = height;
		if(width < 0 || height < 0){
			System.out.println("Fout: Breedte "+width+" en hoogte "+height+
					" zijn geen geldige dimensies.");
			System.out.println("Breedte en hoogte worden op 10 gezet.");
			this.width = 10;
			this.height = 10;
		}
		food = new boolean[this.width][this.height];
		antGridX = 0;
		antGridY = 0;
	}
	
	/**
	 * Verander de afmetingen van cellen.
	 * @param size De nieuwe cell grootte (SMALL_CELL, MEDIUM_CELL, LARGE_CELL).
	 */
	public void setSize(int size){
		cellSize = size;
		if(size!=SMALL_CELL && size!=MEDIUM_CELL && size != LARGE_CELL){
			System.out.println("Fout: Celgrootte ongeldig! Grootte wordt medium.");
			cellSize = MEDIUM_CELL;
		}
		try {
			antIcon = ImageIO.read(new File("./resources/ant"+cellSize+".png"));
			foodIcon = ImageIO.read(new File("./resources/food"+cellSize+".png"));
		} catch (IOException e) { 
			System.out.println("Fout: Probleem bij afbeeldingen laden.");
			e.printStackTrace();
		}
	}
	
	/**
	 * Laat de mier een richting op wijzen.
	 * @param orientation Nieuwe orientatie (NORTH, EAST, SOUTH, WEST).
	 */
	public void setOrientation(int orientation){
		this.orientation = orientation;
		if(orientation!=NORTH && orientation!=SOUTH &&
				orientation!=EAST && orientation!=WEST){
			System.out.println("Fout: Illegale orientatie. Gezet naar noord.");
			this.orientation = NORTH;
		}
	}
	
	/**
	 * Manipuleer de (x,y)-coordinaat van de mier. (0,0) is de links-onder hoek. 
	 * @param x Nieuwe x-coordinaat.
	 * @param y Nieuwe Y-coordinaat.
	 */
	public void setStartXY(int x, int y){
		antGridX = x;
		antGridY = y;
		if(x < 0 || x >= width || y < 0 || y >= height){
			System.out.println("Fout: start coordinaat (" + x + ","+ y+") valt buiten de wereld. Gezet naar (0,0).");
			antGridX = 0;
			antGridY = 0;
		}
	}
	
	/**
	 * Voeg voedsel toe op een coordinaat.
	 * @param x x-coordinaat van het voedsel.
	 * @param y y-coordinaat van het voedsel.
	 */
	public void addFood(int x, int y){ 
		if(x < 0 || x >= width || y < 0 || y >= height){
			System.out.println("Fout: voedsel coordinaat (" + x + ","+ y+") valt buiten de wereld.");
		} else food[x][y]=true;
	}
	
	/**
	 * Voer een actie uit in de grid.
	 * @param action De actie (GUI.MOVE, GUI.ROTATE_LEFT, GUI.ROTATE_RIGHT, GUI.SKIP).
	 */
	public void execute(int action){
		switch(action){
		case GUI.MOVE:
			if(orientation == EAST) antGridX = (antGridX+1)%width;
			else if(orientation == WEST) antGridX = antGridX==0?(width-1):antGridX-1;
			else if(orientation == NORTH) antGridY = (antGridY+1)%height;
			else if(orientation == SOUTH) antGridY = antGridY==0?(height-1):antGridY-1;
			food[antGridX][antGridY] = false;
			break;
		case GUI.ROTATE_LEFT:
			orientation = orientation==NORTH?WEST:(orientation-1);
			break;
		case GUI.ROTATE_RIGHT:
			orientation = (orientation+1)%4;
			break;
		case GUI.SKIP: break;
		default: System.out.println("GUIGrid: Geen geldige actie: "+action);
		}
		finished = false;
	}
	
	/**
	 * Bereken de pixel posities en draaing van de mier voor de volgende frame.
	 */
	public void tick(){
		// Update de (x,y) coordinaat in pixels
		int goalPixX = antGridX*cellSize+cellSize/2;
		int goalPixY = (height-antGridY-1)*cellSize+cellSize/2;
		antPixX = (int)(antPixX + Math.ceil((goalPixX-antPixX)*0.2));
		antPixY = (int)(antPixY + Math.ceil((goalPixY-antPixY)*0.2));
		
		// Update de rotatie hoek
		double goalTheta = ANGLE_MAP[orientation][0]; 
		if(Math.abs(goalTheta-angle) > Math.abs(ANGLE_MAP[orientation][1]-angle))
			goalTheta = ANGLE_MAP[orientation][1]; 
		if(Math.abs(goalTheta-angle) > Math.abs(ANGLE_MAP[orientation][2]-angle))
			goalTheta = ANGLE_MAP[orientation][2]; 
		angle = (angle+(goalTheta-angle)*0.2) % (2*Math.PI);
		if(angle<0) angle += 2*Math.PI;
		
		// Bepaal of de actie bijna klaar is, zo ja, dan kan de volgende actie verwerk worden.
		if(Math.abs(goalPixX-antPixX) < 5 &&
				Math.abs(goalPixY-antPixY) < 5 &&
				Math.abs(goalTheta-angle) < 0.3)
			finished = true; 
	}
	
	/**
	 * Bepaal of de animatie bijna klaar is met de laatste actie.
	 * @return True als de animatie bijna klaar is, false als er nog tick() aanroepen nodig zijn.
	 */
	public boolean isFinished(){
		return finished;
	}
	
	public void paint(Graphics graphics){
		Graphics2D g = (Graphics2D) graphics;
		g.clearRect(0, 0, (1+width)*cellSize, (1+height)*cellSize);

		// Teken de cellen en het voedsel
		for(int x = 0; x < width; x++){
			for(int y = 0; y < height; y++){
				g.drawRect(x*cellSize, (height-y-1)*cellSize, cellSize, cellSize);
				if(food[x][y]) g.drawImage(foodIcon, x*cellSize, (height-y-1)*cellSize, null);
			}
		}
		
		// Teken de mier
		AffineTransform at = new AffineTransform();
        at.translate(antPixX, antPixY);
        at.rotate(angle);
        at.translate(-antIcon.getWidth()/2, -antIcon.getHeight()/2);
        g.drawImage(antIcon, at, null);
	}  
	
	/**
	 * Kleine hulpklasse voor het maken van een JFrame waarin de grid getoond wordt.
	 * 
	 * @author Bas Testerink
	 *
	 */
    public static class AntFrame extends JFrame{
        public AntFrame(JPanel grid){
            setSize(800, 800);
            add(grid);
            setTitle("IAS: Ant GUI");
            setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            setVisible(true);
        }
    }
}
