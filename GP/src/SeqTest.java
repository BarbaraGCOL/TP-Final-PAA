import java.util.ArrayList;
import java.util.List;

import org.jgap.InvalidConfigurationException;
import org.jgap.gp.CommandGene;
import org.jgap.gp.GPProblem;
import org.jgap.gp.function.Add;
import org.jgap.gp.function.Divide;
import org.jgap.gp.function.Exp;
import org.jgap.gp.function.Log;
import org.jgap.gp.function.Multiply;
import org.jgap.gp.function.Pow;
import org.jgap.gp.function.Sine;
import org.jgap.gp.function.Subtract;
import org.jgap.gp.impl.DeltaGPFitnessEvaluator;
import org.jgap.gp.impl.GPConfiguration;
import org.jgap.gp.impl.GPGenotype;
import org.jgap.gp.terminal.Terminal;
import org.jgap.gp.terminal.Variable;

/**
 * @author carlos
 *
 */
public class SeqTest extends GPProblem {
	
	private List<double[]> INPUTS;
	
	private String[] SEQS;
	
	private static double[] OUTPUT;
	
    List<Variable> variables = new ArrayList<Variable>(); 
    
    private static String seq = "teste";
    
    public SeqTest() throws InvalidConfigurationException {
        super(new GPConfiguration());

        GPConfiguration config = getGPConfiguration();

        Data data = new Data(seq+".csv");
        
        SEQS = data.getVariables();
        INPUTS = data.getInputs();
        OUTPUT = data.getOutputs();
        
        data = null;
        
        for(int i = 0; i < SEQS.length - 1; i++){
        	Variable newVariable = Variable.create(config, SEQS[i], CommandGene.DoubleClass);
        	variables.add(newVariable);
        }
        
        config.setGPFitnessEvaluator(new DeltaGPFitnessEvaluator());
        config.setMaxInitDepth(4);
        config.setPopulationSize(1000);
        config.setMaxCrossoverDepth(1000);
        config.setFitnessFunction(new SeqFitnessFunction(INPUTS, OUTPUT, variables));
        config.setStrictProgramCreation(true);
    }

    @SuppressWarnings("rawtypes")
	@Override
    public GPGenotype create() throws InvalidConfigurationException {
        GPConfiguration config = getGPConfiguration();

        // The return type of the GP program.
        Class[] types = { CommandGene.DoubleClass };

        // Arguments of result-producing chromosome: none
        Class[][] argTypes = { {}, };//{CommandGene.DoubleClass, CommandGene.DoubleClass, CommandGene.DoubleClass}};

        // Next, we define the set of available GP commands and terminals to
        // use.
        int size = variables.size();
        
        CommandGene[] cgs = new CommandGene[size + 4];
        
        int i = 0;
        for(Variable v: variables){
        	cgs[i] = v;
        	i++;
        }

        variables = null;
        
        Add add = new Add(config, CommandGene.DoubleClass);
        Multiply m = new Multiply(config, CommandGene.DoubleClass);
        Terminal t = new Terminal(config, CommandGene.DoubleClass, 0.0, 10.0, true);
        Subtract sub = new Subtract(config, CommandGene.DoubleClass);
//        Log log = new Log(config, CommandGene.DoubleClass);
//        Divide div = new Divide(config, CommandGene.DoubleClass);
//        Sine s = new Sine(config, CommandGene.DoubleClass);
//        Exp exp= new Exp(config, CommandGene.DoubleClass);
//        Pow p = new Pow(config, CommandGene.DoubleClass);
//        ADF adf = new ADF(config, 1 , 3);
        
        cgs[size] = add; 
        cgs[size + 1] = m;
        cgs[size + 2] = t;
        cgs[size + 3] = sub;
//        cgs[size + 4] = div;
//        cgs[size + 5] = log;
//        cgs[size + 6] = s;
//        cgs[size + 7] = p;
//        cgs[size + 9] = adf;
//        cgs[size + 10] = exp;
        
        CommandGene[][] nodeSets = {
            cgs
        };
                
        int[] minDepth = new int[] {4};   
        int[] maxDepth = new int[] {8};   
        int maxNodes = 3000;   
        boolean[] fullMode = new boolean[] {true};   
        
        GPGenotype result = GPGenotype.randomInitialGenotype(config, types, argTypes, nodeSets,   
            minDepth, maxDepth, maxNodes, fullMode, true);
        
//        GPGenotype result = GPGenotype.randomInitialGenotype(config, types, argTypes,
//                nodeSets, 20, true);

        return result;
    }

    public static void main(String[] args) throws Exception {
        GPProblem problem = new SeqTest();

        GPGenotype gp = problem.create();
        gp.setVerboseOutput(true);
        gp.evolve(30);

        System.out.println("Formulaiscover:");
        gp.outputSolution(gp.getAllTimeBest());
        
        problem.showTree(gp.getAllTimeBest(), seq+"_result.png");
    }

}