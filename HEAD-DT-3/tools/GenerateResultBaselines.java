package tools;

public class GenerateResultBaselines {
	
	private static String source = "/Users/rodrigobarros/Desktop/Datasets/Arrumados/";
	private static String results = "/Users/rodrigobarros/Desktop/";
	
	public static void main(String args[]) throws Exception{
	/*	System.out.println("J48...");
		GenerateJ48Results j48 = new GenerateJ48Results(source,results);
		System.out.println();*/
		System.out.println("SVM...");
		GenerateSVMResults svm = new GenerateSVMResults(source,results);
		System.out.println();
		/*System.out.println("REP...");
		GenerateREPTreeResults rep = new GenerateREPTreeResults(source,results);
		System.out.println();
		System.out.println("CART...");
		GenerateCARTResults cart = new GenerateCARTResults(source,results);
		System.out.println();*/
	}

}
