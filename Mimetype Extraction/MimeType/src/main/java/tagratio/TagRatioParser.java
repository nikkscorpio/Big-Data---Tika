package tagratio;

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
import org.apache.commons.io.FilenameUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import edu.stanford.nlp.process.DocumentPreprocessor;
import edu.stanford.nlp.ling.HasWord;
import edu.stanford.nlp.trees.*;
import edu.stanford.nlp.parser.lexparser.LexicalizedParser;

class TagRatioParser {

	public static void main(String[] args) throws IOException, SAXException, TikaException {
		String parserModel = "src/test/resources/englishPCFG.ser.gz";
		if (args.length > 0) {
			parserModel = args[0];
		}
		LexicalizedParser lp = LexicalizedParser.loadModel(parserModel);

		String inputFileName = "1424621728000.html";
		File inputFile = new File(inputFileName);

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
			System.out.println(sentence);
			Tree parse = lp.apply(sentence);
			// parse.pennPrint();

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
						System.out.println(measurement + " " + measurementUnit);

					}
				}

			}

		}
		jsonObj.put("Measurements", jsonArr);
		/*
		 * {"Measurements":[{"5":"degrees celsius"},{"seven":"degrees Celsius"
		 * },{"10":"lbs "},{"1983":"July Station"},{"-89.2":"July Station"
		 * },{"-128.6":"F C"}],"FileName":"Official Fight Song _ About USC.html"
		 * }
		 */

		FileWriter file = new FileWriter((inputFile.getParent() != null ? inputFile.getParent() : "")
				+ FilenameUtils.getBaseName(inputFile.getName()) + "_output_json.json");
		file.write(jsonObj.toString());
		file.close();

	}

	public static String tagRatioParser(File inputFile) throws IOException, SAXException, TikaException {

		Parser parser = new AutoDetectParser();
		BodyContentHandler handler = new BodyContentHandler(-1);
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

