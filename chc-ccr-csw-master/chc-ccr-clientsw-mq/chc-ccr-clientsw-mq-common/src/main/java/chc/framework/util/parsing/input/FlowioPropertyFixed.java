package chc.framework.util.parsing.input;

/**
 * property con tracciato a posizioni fisse, colonna inizio/fine
 * @author mek
 *
 */
public class FlowioPropertyFixed extends FlowioProperty{

	private int columnStart;
	
	private int columnEnd;
	
	public FlowioPropertyFixed(String name, int columnStart, int columnEnd) {
		super(name);
		this.columnStart = columnStart;
		this.columnEnd = columnEnd;
	}
	
	public int getColumnStart() {
		return columnStart;
	}

	public int getColumnEnd() {
		return columnEnd;
	}

}
