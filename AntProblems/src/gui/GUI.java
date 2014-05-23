package gui;

import java.util.LinkedList;
import java.util.Queue; 
/**
 * Hoofdklasse van de grafische interface. Gebruik deze klasse om de mier te laten zien.
 * Er zijn public methodes die acties in een wachtrij zetten. De interface laat de volgende
 * actie pas zien als de laatste bijna klaar is. Je kan dus een simulatie draaien op je eigen 
 * grid en bij elke mier actie ook de GUI aanroepen. Je simulatie zal veel eerder klaar zijn
 * maar de GUI laat dan nog wel zien wat er is gebeurd. 
 * 
 * Je mag deze klasse aanpassen.
 * 
 * @author Bas Testerink
 *
 */
public class GUI implements Runnable{
	public static final int MOVE = 0, ROTATE_LEFT = 1, ROTATE_RIGHT = 2, SKIP = 3;
	private Queue<Integer> queue;  // Queue met alle acties die afgespeeld worden
	private int fps = 30; // Frames per second
	private GUIGrid grid;
	private boolean running = true; // Of de GUI nog moet door gaan

	public GUI(){
		queue = new LinkedList<Integer>();
		grid = new GUIGrid();
	}

	/**
	 * Voeg een actie toe aan de queue om uit te voeren.
	 * @param action Actie om uit te voeren.
	 * @throws InterruptedException
	 */
	private synchronized void enqueue(int action) throws InterruptedException {
		queue.add(action);
		notify();
	} 

	/**
	 * Pak en verwijder de volgende actie uit dequeue. De thread wacht totdat 
	 * er minstens 1 actie in de queue staat. 
	 * @return De volgende actie; 
	 * @throws InterruptedException
	 */
	private synchronized int getNextAction() throws InterruptedException {
		notify();
		while(queue.isEmpty()) wait(); 
		return queue.remove();  
	}

	/**
	 * De run methode probeert continu de volgende actie uit te voeren.
	 */
	public void run(){
		try{
			while(running) { 
				execute(getNextAction());
			}
		} catch(InterruptedException e){
			System.out.println("FOUT: interruptie in GUI run methode.");
			e.printStackTrace();
		}
	}

	/**
	 * Voer de volgende actie uit.
	 * @param action Actie om uit te voeren.
	 */
	private void execute(int action){
		grid.execute(action);
		while(!grid.isFinished()){
			grid.tick();
			grid.repaint();
			try {
				Thread.sleep(1000/fps);
			} catch (InterruptedException e) { 
				System.out.println("Fout bij slapen tussen frame updates.");
				e.printStackTrace();
			}
		}
	}

	/**
	 * Voeg een actie toe aan de queue. Deze methode checked of de actie geldig
	 * is. 
	 * @param action De actie om toe te voegen (MOVE, ROTATE_LEFT, ROTATE_RIGHT, SKIP).
	 */
	private void addAction(int action){
		try {
			if(action==MOVE || action==ROTATE_LEFT ||
					action==ROTATE_RIGHT || action==SKIP){
				enqueue(action);
			}
			else {
				System.out.println("FOUT: ongeldige actie: "+action);
			}
		} catch (InterruptedException e) { 
			System.out.println("FOUT: actie kon niet enqueued worden.");
			System.out.print("Actie is = " + action); 
			e.printStackTrace();
		}
	}

	////////////////////////////////////////////////
	/////////// Belangrijkste methoden om te gebruiken
	////////////////////////////////////////////////
	/** Zet een stap naar voren. */
	public void move(){ addAction(MOVE); }
	/** Draai linksom. */
	public void rotateLeft(){ addAction(ROTATE_LEFT); }
	/** Draai rechtsom. */
	public void rotateRight(){ addAction(ROTATE_RIGHT); }
	/** Doe niets. */
	public void skip(){ addAction(SKIP); }
	/** Stop animatie. */
	public void stop(){ running = false; }
	/** Bepaal de start lokatie. */
	public void setStartXY(int x, int y){ grid.setStartXY(x, y); }
	/** Wijs naar het noorden.. */
	public void setStartOrientationNorth(){ grid.setOrientation(GUIGrid.NORTH); }
	/** Wijs naar het zuiden. */
	public void setStartOrientationSouth(){ grid.setOrientation(GUIGrid.SOUTH); }
	/** Wijs naar het oosten. */
	public void setStartOrientationEast(){ grid.setOrientation(GUIGrid.EAST); }
	/** Wijs naar het westen. */
	public void setStartOrientationWest(){ grid.setOrientation(GUIGrid.WEST); }
	/** Teken de grid klein. */
	public void setGridToSmallSize(){ grid.setSize(GUIGrid.SMALL_CELL); }
	/** Teken de grid middelmatig groot.  */
	public void setGridToMediumSize(){ grid.setSize(GUIGrid.MEDIUM_CELL); }
	/** Teken de grid groot. */
	public void setGridToLargeSize(){ grid.setSize(GUIGrid.LARGE_CELL); }
	/** Vernieuw de grootte van de grid. */
	public void setGridDimensions(int width, int height){ grid.setGridDimensions(width, height); }
	/** Snelle animatie. */
	public void setSpeedHigh(){ fps = 50; }
	/** Matig snelle animatie. */
	public void setSpeedMedium(){ fps = 30; }
	/** Slome animatie. */
	public void setSpeedSlow(){ fps = 10; }
	/** Voeg voedsel toe op positie (x,y). */
	public void addFood(int x, int y){ grid.addFood(x, y); }
}
