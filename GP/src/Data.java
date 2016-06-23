import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.math.linear.BigMatrix;
import org.apache.commons.math.linear.MatrixUtils;

import com.opencsv.CSVReader;

@SuppressWarnings("deprecation")
public class Data {

	private String[] variables;
	private List<double[]> inputs;
	private double[] outputs;
	
	public Data(String file){
		try {
			readParameters(file);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
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
