import java.io.File;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.jgap.Configuration;
import org.jgap.InvalidConfigurationException;
import org.jgap.gp.CommandGene;
import org.jgap.gp.GPProblem;
import org.jgap.gp.IGPProgram;
import org.jgap.gp.function.Add;
import org.jgap.gp.function.Cosine;
import org.jgap.gp.function.Divide;
import org.jgap.gp.function.Log;
import org.jgap.gp.function.Multiply;
import org.jgap.gp.function.Sine;
import org.jgap.gp.function.Subtract;
import org.jgap.gp.impl.DeltaGPFitnessEvaluator;
import org.jgap.gp.impl.GPConfiguration;
import org.jgap.gp.impl.GPGenotype;
import org.jgap.gp.impl.ProgramChromosome;
import org.jgap.gp.terminal.Constant;
import org.jgap.gp.terminal.Terminal;
import org.jgap.gp.terminal.Variable;

/**
 * @author barbara.lopes
 *
 */
public class SeqTest extends GPProblem {

	private List<double[]> INPUTS;

	private String[] SEQS;

	private static double[] OUTPUT;

	List<Variable> variables = new ArrayList<Variable>(); 

	private static String seq;

	public SeqTest() throws InvalidConfigurationException, IOException {
		super(new GPConfiguration());

		GPConfiguration config = getGPConfiguration();
		Configuration.reset();
		Data data = new Data();
		data.readParameters(seq);

		SEQS = data.getVariables();
		INPUTS = data.getInputs();
		OUTPUT = data.getOutputs();

		data = null;

		for(int i = 0; i < SEQS.length - 1; i++){
			Variable newVariable = Variable.create(config, SEQS[i], CommandGene.DoubleClass);
			variables.add(newVariable);
		}

		config.setGPFitnessEvaluator(new DeltaGPFitnessEvaluator());

//		config.setMutationProb(0.07f);
        config.setMaxInitDepth(10);
        config.setMinInitDepth(4);
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
		Class[][] argTypes = { {}, };

		// Next, we define the set of available GP commands and terminals to
		// use.
		int size = variables.size();

		CommandGene[] cgs = new CommandGene[size + 9];

		int i = 0;
		for(Variable v: variables){
			cgs[i] = v;
			i++;
		}

		Add add = new Add(config, CommandGene.DoubleClass);
		Multiply m = new Multiply(config, CommandGene.DoubleClass);
		Terminal t = new Terminal(config, CommandGene.DoubleClass, 0.0, 10.0, true);
		Subtract sub = new Subtract(config, CommandGene.DoubleClass);
		Log log = new Log(config, CommandGene.DoubleClass);
		Divide div = new Divide(config, CommandGene.DoubleClass);
		Sine s = new Sine(config, CommandGene.DoubleClass);
		Cosine cos = new Cosine(config, CommandGene.DoubleClass);
        Constant k = new Constant(config, CommandGene.DoubleClass, 0.01);

		cgs[size] = add; 
		cgs[size + 1] = m;
		cgs[size + 2] = t;
		cgs[size + 3] = sub;
		cgs[size + 4] = div;
		cgs[size + 5] = log;
		cgs[size + 6] = s;
		cgs[size + 7] = cos;
        cgs[size + 8] = k;
        
		CommandGene[][] nodeSets = {
				cgs
		};

		int[] minDepth = new int[] {4};   
		int[] maxDepth = new int[] {10};   
		int maxNodes = 3000;   
		boolean[] fullMode = new boolean[] {true};   

		GPGenotype result = GPGenotype.randomInitialGenotype(config, types, argTypes, nodeSets,   
				minDepth, maxDepth, maxNodes, fullMode, true);

		return result;
	}

	public static void main(String[] args) throws Exception {
		double smallestFitness, fitness;

		Map<String, Set<String>> adjacencyList = new HashMap<String, Set<String>>();
		Set<String>regulators;
		Map<String, Integer> countFrequency; 
		Set<String> expressions;
		String seqAlvo = "";
		List<String> fittestSolutions = null;
		List<String> fittestSolutionsFinal = new LinkedList<String>();
		
		String pasta = "Cluster6";

		String dir = System.getProperty("user.dir")+"\\"+pasta;
		File f = new File(dir);
		File[] files = f.listFiles(); 
		String[] nomes = f.list();

		int countSeqs = 0;
		
		DecimalFormat df = new DecimalFormat("0.##");
		
		for(int file = 0; file < files.length; file++){
			fittestSolutions = new ArrayList<String>();
			countFrequency = new HashMap<String, Integer>(); 
			smallestFitness = Double.MAX_VALUE; 
			fitness = Double.MAX_VALUE;
			
			seq = dir+"\\"+nomes[file];

			seqAlvo = nomes[file];
			seqAlvo = seqAlvo.replaceAll("\\(t - 1\\)", "");
			seqAlvo = seqAlvo.replaceAll("seq", "");
			seqAlvo = seqAlvo.replaceAll(".csv", "");
			String[]s = seqAlvo.split("_");
			seqAlvo = s[1];

			expressions = new HashSet<String>();
			String expression;
			
			regulators = new LinkedHashSet<String>();
			
			countSeqs++;
			
			for(int count = 0; count < 30; count++){
				
				long tempoInicial = System.currentTimeMillis();
				
				GPProblem problem = new SeqTest();
				GPGenotype gp = problem.create();
				gp.setVerboseOutput(false);
				gp.evolve(30);

//				System.out.println("Formulaiscover:");

				IGPProgram fittest = gp.getFittestProgramComputed();//getFittestProgram();
				expression = fittest.toStringNorm(0);

//				gp.outputSolution(fittest);
				fitness = fittest.getFitnessValue();

				String register = "";
				
				if(!expressions.contains(expression)){
					
					if(fitness <= smallestFitness){
						if(fitness < smallestFitness){
//							expressions = new HashSet<String>();
							regulators = new LinkedHashSet<String>();
							smallestFitness = fitness;
							fittestSolutions = new ArrayList<String>();
						}
						String roundedFitness = df.format(fittest.getFitnessValue());
						register = seqAlvo+" = "+expression+";"+roundedFitness+";";
					}
					

					expressions.add(expression);

					int size = fittest.getChromosome(0).size();
					ProgramChromosome chromossome = fittest.getChromosome(0);

					String sequence;

					for(int i = 0; i < size; i++){
						sequence = chromossome.getGene(i).getName();
						if(sequence.contains("seq")){
							sequence = sequence.replaceAll("\\(t - 1\\)", "");
							sequence = sequence.replaceAll("seq", "");
							s = sequence.split("_");

							if(countFrequency.get(s[1]) == null){
								countFrequency.put(s[1], 1);
							}
							else{
								int countF = countFrequency.get(s[1]);
								countF++;
								countFrequency.remove(s[1]);
								countFrequency.put(s[1], countF);
							}

							if(fitness <= smallestFitness){
								regulators.add(s[1]);
							}
						}
					}
				}
				long tempoFinal = System.currentTimeMillis();
				if(!register.isEmpty()){
					register+=(tempoFinal - tempoInicial);
					fittestSolutions.add(register);
				}
				
			}

			for(String r: regulators){
				if(adjacencyList.get(r) == null){
					adjacencyList.put(r, new HashSet<String>());
				}
				adjacencyList.get(r).add(seqAlvo);
			}

			int maior = 0, c = 0;
			String seqMaior = null;
			for(String r: countFrequency.keySet()){
				c = countFrequency.get(r);
				if(c > maior && maior > 1 && !regulators.contains(r)){
					maior = c;
					seqMaior = r;
				}
			}

			if(seqMaior != null){
				if(adjacencyList.get(seqMaior) == null){
					adjacencyList.put(seqMaior, new HashSet<String>());
				}
				adjacencyList.get(seqMaior).add(seqAlvo);
			}
			
			System.out.println("Seqs Computadas: "+countSeqs);
			
			fittestSolutionsFinal.addAll(fittestSolutions);
		}			
		Data data = new Data();
		data.writeAdjacencyList(adjacencyList, pasta);
		data.writeExpressions(fittestSolutionsFinal, pasta);
	}
}