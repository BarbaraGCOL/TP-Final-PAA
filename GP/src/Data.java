import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.math.linear.BigMatrix;
import org.apache.commons.math.linear.MatrixUtils;

import com.opencsv.CSVReader;

/**
 * @author barbara.lopes
 *
 */
@SuppressWarnings("deprecation")
public class Data {

	private String[] variables;
	private List<double[]> inputs;
	private double[] outputs;
	
	private static final String COMMA_DELIMITER = ";";
	private static final String NEW_LINE_SEPARATOR = "\n";
	
	public Data(){
		
	}
//ucinet	
	public void readParameters(String file) throws IOException{
		
		CSVReader csvReader = new CSVReader(new FileReader(file), ',', '\'', 1);
		List<String[]> list = csvReader.readAll();

		csvReader = new CSVReader(new FileReader(new File(file)));
		variables = csvReader.readNext();

		csvReader.close();
		
		// Convert to 2D array
		String[][] dataArr = new String[list.size()][];
		dataArr = list.toArray(dataArr);

		BigMatrix matrix = MatrixUtils.createBigMatrix(dataArr);

		inputs = new ArrayList<double[]>();
		double[] column;
		
		int qtdColumns = variables.length;
		
		for(int i = 0; i < qtdColumns - 1; i++){
			column = matrix.getColumnAsDoubleArray(i);
			inputs.add(column);
		}

		outputs = matrix.getColumnAsDoubleArray(qtdColumns - 1);

		//		RealMatrix rm = new Array2DRowRealMatrix(dataArr);
		//		rm.getColumn(0);
	}
	
	public void writeAdjacencyList(Map<String, Set<String>> adjacencyList, String cluster) throws IOException{
		String directory = System.getProperty("user.dir")+"\\Clusters";

		new File(directory).mkdirs();

		FileWriter writer = new FileWriter(directory+"\\"+cluster+".csv");
		Set<String> adjs;
		
		for(String no: adjacencyList.keySet()){
			writer.append(no+"");
			writer.append(COMMA_DELIMITER);
			adjs = adjacencyList.get(no);
			
			for(String adj: adjs){
				writer.append(adj+"");
				writer.append(COMMA_DELIMITER);
			}
			writer.append(NEW_LINE_SEPARATOR);
			writer.flush();
		}
		writer.close();
	}

	public void writeExpressions(List<String> expressions, String cluster) throws IOException{
		String directory = System.getProperty("user.dir")+"\\Clusters";

		new File(directory).mkdirs();

		FileWriter writer = new FileWriter(directory+"\\"+cluster+"_expressions.csv");
		
		writer.append("EXPRESSION");
		writer.append(COMMA_DELIMITER);
		writer.append("FITNESS");
		writer.append(COMMA_DELIMITER);
		writer.append("TIME (Milliseconds)");
		writer.append(COMMA_DELIMITER);
		writer.append(NEW_LINE_SEPARATOR);
		
		for(String e: expressions){
			writer.append(e);
			
			writer.append(NEW_LINE_SEPARATOR);
			writer.flush();
		}
		writer.close();
	}
	
	public String[] getVariables() {
		return variables;
	}

	public void setVariables(String[] variables) {
		this.variables = variables;
	}

	public List<double[]> getInputs() {
		return inputs;
	}

	public void setInputs(List<double[]> inputs) {
		this.inputs = inputs;
	}

	public double[] getOutputs() {
		return outputs;
	}

	public void setOutputs(double[] outputs) {
		this.outputs = outputs;
	}
}
