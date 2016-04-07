package edu.usc.polar;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

import org.apache.tika.exception.TikaException;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.parser.AutoDetectParser;
import org.apache.tika.parser.ParseContext;
import org.apache.tika.parser.Parser;
import org.apache.tika.sax.BodyContentHandler;
import org.json.JSONArray;
import org.json.JSONObject;
import org.xml.sax.SAXException;

import edu.stanford.nlp.ling.HasWord;
import edu.stanford.nlp.parser.lexparser.LexicalizedParser;
import edu.stanford.nlp.process.DocumentPreprocessor;
import edu.stanford.nlp.trees.GrammaticalStructure;
import edu.stanford.nlp.trees.GrammaticalStructureFactory;
import edu.stanford.nlp.trees.Tree;
import edu.stanford.nlp.trees.TreebankLanguagePack;
import edu.stanford.nlp.trees.TypedDependency;

/**
 * This class contains methods that perform tag ratio parsing and identify
 * areas of text in files of all the different mimetypes. Measurement
 * extraction is applied to all these sections of text and the resulting 
 * measurements are stored in a JSON file
 * {inputFile path}/{inputFile name}_1.json.
 * 
 * @author shriram
 *
 */
class TagRatioParser {
	private static String parserModel = "src/main/resources/englishPCFG.ser.gz";
	private static LexicalizedParser lp = LexicalizedParser.loadModel(parserModel);
	/**
	 * Method that performs the tag ratio parsing and then the measurement
	 * extraction
	 * 
	 * @param inputFile
	 * @throws IOException
	 * @throws SAXException
	 * @throws TikaException
	 */
	public static void parse(File inputFile) throws IOException, SAXException, TikaException {
		String outFileName = tagRatioParser(inputFile);
		extractMeasurements(lp, outFileName, inputFile);
	}

	/**
	 * extractMeasurements demonstrates turning a file into tokens and then
	 * parse trees. Note that the trees are printed by calling pennPrint on the
	 * Tree object. It is also possible to pass a PrintWriter to pennPrint if
	 * you want to capture the output. This code will work with any supported
	 * language.
	 * 
	 * @throws IOException
	 */
	public static void extractMeasurements(LexicalizedParser lp, String filename, File inputFile) throws IOException {
		// This option shows loading, sentence-segmenting and tokenizing
		// a file using DocumentPreprocessor.
		TreebankLanguagePack tlp = lp.treebankLanguagePack(); // a
																// PennTreebankLanguagePack
																// for English
		GrammaticalStructureFactory gsf = null;
		if (tlp.supportsGrammaticalStructures()) {
			gsf = tlp.grammaticalStructureFactory();
		}
		// You could also create a tokenizer here (as below) and pass it
		// to DocumentPreprocessor

		JSONObject jsonObj = new JSONObject();
		jsonObj.put("FileName", inputFile.getName());
		JSONArray jsonArr = new JSONArray();

		for (List<HasWord> sentence : new DocumentPreprocessor(filename)) {
			Tree parse = lp.apply(sentence);

			if (gsf != null) {
				GrammaticalStructure gs = gsf.newGrammaticalStructure(parse);
				List<TypedDependency> tdl = gs.typedDependenciesCCprocessed();
				String measurement = "";
				String measurementUnit = "";
				// System.out.println(tdl);
				// Check if the dependency is of relation NUMMOD(Number). If so,
				// extract the dependency and Gov from the reln. and also
				// extract
				// all other relns with the Gov as the Dep.
				for (TypedDependency typedDependency : tdl) {
					if (typedDependency.reln().getShortName().equals("nummod")) {
						measurement = typedDependency.dep().originalText();
						measurementUnit = typedDependency.gov().originalText();
						for (TypedDependency typedDependency2 : tdl) {
							if (!measurementUnit.isEmpty() && !(typedDependency2.reln().getShortName().equals("nummod"))
									&& typedDependency2.dep().originalText().equals(measurementUnit)) {
								measurementUnit += " " + typedDependency2.gov().originalText();
							}
						}
						JSONObject jsonObject = new JSONObject();
						jsonObject.put(measurement, measurementUnit);
						jsonArr.put(jsonObject);
					}
				}

			}

		}
		jsonObj.put("Measurements", jsonArr);
		
		String outputFilePath = inputFile.getAbsolutePath()+"_1.json";
		ContentAnalysis.writeJSONToFile(jsonObj, outputFilePath);

	}

	/**
	 * This method parses the inputFile and retrieves the text content
	 * of the file using the tag ratio parsing algorithm
	 * 
	 * @param inputFile
	 * @return
	 * @throws IOException
	 * @throws SAXException
	 * @throws TikaException
	 */
	public static String tagRatioParser(File inputFile) throws IOException, SAXException, TikaException {

		Parser parser = new AutoDetectParser();
		BodyContentHandler handler = new BodyContentHandler();
		Metadata metadata = new Metadata();
		FileInputStream inputstream = new FileInputStream(inputFile);
		ParseContext context = new ParseContext();

		// parsing the file
		parser.parse(inputstream, handler, metadata, context);
		String handlerArr = handler.toString();

		File outFile = new File((inputFile.getParent() != null ? inputFile.getParent() : "") + "output.txt");
		PrintWriter writer;
		writer = new PrintWriter(outFile.getAbsolutePath(), "UTF-8");
		writer.print(handlerArr);
		writer.close();
		return outFile.getAbsolutePath();
	}

}