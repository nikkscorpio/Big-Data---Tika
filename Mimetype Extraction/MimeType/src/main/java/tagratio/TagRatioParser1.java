package tagratio;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.logging.Handler;

import org.apache.tika.Tika;
import org.apache.tika.example.ContentHandlerExample;
import org.apache.tika.exception.TikaException;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.parser.AutoDetectParser;
import org.apache.tika.parser.ParseContext;
import org.apache.tika.sax.BodyContentHandler;
import org.apache.tika.sax.ToXMLContentHandler;
import org.xml.sax.ContentHandler;
import org.xml.sax.SAXException;
import org.apache.tika.parser.Parser;

import edu.stanford.nlp.ie.crf.CRFClassifier;
import edu.stanford.nlp.ling.CoreAnnotations;
import edu.stanford.nlp.ling.CoreLabel;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;

public class TagRatioParser1 {

	public static void main(String[] args) throws IOException, SAXException, TikaException {
		// TODO Auto-generated method stub
		TagRatioParser1 trp = new TagRatioParser1();
		String filePath = "9997030953132000764E44A6C870DDA72FE21C92DDE57D6A6225AD54D9C6D331";
		File inputFile = new File(filePath);
		Tika tika = new Tika();

		String type = tika.detect(inputFile);
		if (type.equals("text/html")) {
			trp.tagRatioParser(inputFile);
		}

	}

	public void tagRatioParser(File inputFile) throws IOException, SAXException, TikaException {

		Parser parser = new AutoDetectParser();
		BodyContentHandler handler = new BodyContentHandler();
		Metadata metadata = new Metadata();
		FileInputStream inputstream = new FileInputStream(inputFile);
		ParseContext context = new ParseContext();

		// parsing the file
		parser.parse(inputstream, handler, metadata, context);
		String handlerArr = handler.toString();
		System.out.println(identifyNER(handlerArr,"english.conll.4class.distsim.crf.ser.gz").toString());

	}

	public void extractMeasurements(String[] handlerArr) {

	}

	public static LinkedHashMap<String, LinkedHashSet<String>> identifyNER(String text, String model) {

		text = "Sachin Ramesh Tendulkar (Listeni/ˌsətʃɪn tɛnˈduːlkər/; Marathi: "
				+ " सचिन रमेश तेंडुलकर; born 24 April 1973) is an Indian former cricketer widely "
				+ " acknowledged as the greatest batsman of the modern generation, popularly holds the title \"God of Cricket\" among his fans [2] He is also acknowledged as the greatest cricketer of all time.[6][7][8][9] He took up cricket at the age of eleven, made his Test debut against Pakistan at the age of sixteen, and went on to represent Mumbai domestically and India internationally for close to twenty-four years. He is the only player to have scored one hundred international centuries, the first batsman to score a Double Century in a One Day International, and the only player to complete more than 30,000 runs in international cricket.[10] In October 2013, he became the 16th player and first Indian to aggregate "
				+ " 50,000 runs in all recognized cricket " + " First-class, List A and Twenty20 combined)";
		LinkedHashMap<String, LinkedHashSet<String>> map = new LinkedHashMap();
		String serializedClassifier = model;
		System.out.println(serializedClassifier);
		try{
		CRFClassifier<CoreLabel> classifier = CRFClassifier.getClassifierNoExceptions(serializedClassifier);
		List<List<CoreLabel>> classify = classifier.classify(text);
		for (List<CoreLabel> coreLabels : classify) {
			for (CoreLabel coreLabel : coreLabels) {

				String word = coreLabel.word();
				String category = coreLabel.get(CoreAnnotations.AnswerAnnotation.class);
				if (!"O".equals(category)) {
					if (map.containsKey(category)) {
						// key is already their just insert in arraylist
						map.get(category).add(word);
					} else {
						LinkedHashSet<String> temp = new LinkedHashSet<String>();
						temp.add(word);
						map.put(category, temp);
					}
					System.out.println(word + ":" + category);
				}

			}

		}
		}catch(Exception e){
			System.out.println(e);
			e.printStackTrace();
		}
		return map;
	}

}
