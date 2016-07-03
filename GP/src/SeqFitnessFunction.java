import java.util.ArrayList;
import java.util.List;

import org.jgap.gp.GPFitnessFunction;
import org.jgap.gp.IGPProgram;
import org.jgap.gp.terminal.Variable;

/**
 * @author barbara.lopes
 *
 */
public class SeqFitnessFunction extends GPFitnessFunction {

    private static final long serialVersionUID = 1L;
    
	private List<double[]> _inputs;
    private double[] _output;
    private List<Variable> _variables = new ArrayList<Variable>();

    private static Object[] NO_ARGS = new Object[0];

    public SeqFitnessFunction(List<double[]> inputs,
            double output[], List<Variable> variables) {
    	
        _inputs = inputs;
        _output = output;
        _variables = variables;
    }

    @Override
    protected double evaluate(final IGPProgram program) {
        double result = 0.0f;

        int size = _inputs.get(0).length;
        
        for (int i = 0; i < size; i++) {
        	for(int b = 0; b < _inputs.size(); b++){
        		_variables.get(b).set(_inputs.get(b)[i]);
        	}
        	double value = program.execute_double(0, NO_ARGS);
        	result += Math.abs(value - _output[i]);
        }
             
        return result;
    }
}