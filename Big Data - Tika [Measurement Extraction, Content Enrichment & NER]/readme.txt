Homework: Scientific Content Enrichment in the Text Retrieval Conference (TREC) Polar Dynamic Domain Dataset

This document explains the steps to explore, compile and run the data-analysis project. The data-analysis project
is a Maven project that contains Java source files that parse and analyse data. The project can also be found on
https://www.github.com/shriramkm


Steps for Execution of PolarTREC data analysis:
(Assumptions - 1. Apache Tika server running on port 9998, 2. Lucene Geo Gazetteer server running on port 8765
3. Geotopic server is running, 4. GROBID Journal Parsing REST server is up and running on port 8080)
1. Import data-analysis in Eclipse as a Maven Project. The pom.xml contains all the dependencies.
2. Place scholar.py in a path, say $HOME/dir. Open ScholarAPI.java and change line numbers 28 and 29 to
   some temporary files. Change the second argument of function ProcessBuilder in line 27 to point to 
   $HOME/dir/scholar.py.
3. Build the project with the goals as "compile install package"
4. Open the ContentAnalysis.java. The path to the polar dataset can be modified at line 53. Run the
   ContentAnalysis.java
5. Open the JoinJSONFiles.java. The path to the polar dataset can be modified at line 45 and must be the
   same as in line 53 of ContentAnalysis.java. Run the JoinJSONFiles.java
6. Open the JSONtoSOLR.java. The path to the polar dataset can be modified at line 45 and must be the
   same as in line 47 of ContentAnalysis.java. Run the JSONtoSOLR.java.

   
Description of the resources:
A) Package edu.usc.polar 
1. ContentAnalysis.java - Class that performs the analysis of PolarTREC data. The analysis
  is comprised of a sequence of the following 6 tasks in this order
  for each file that is processed from the dataset:
  1. Tag Ratio Parsing to identify sections of text
  2. Generating a unique short URL for the file
  3. GROBID Jounal Parsing for PDF files to identify related publications
  4. Geotopic Parsing to identify references of geographic regions in the file
  5. SWEET Concenpts Extraction to identify references of SWEET concepts in the file
  6. Metadata Score Generation to evaluate metadata quality of the file
  For each step, the class generates the results with the help of the
  corresponding Parser class and stores these results as a {filename}_x.json
  where { filename } stands for the name of the file and x denotes that the 
  JSON file is the result of the corresponding task number (1-6).
  
2. TagRatioParser.java - This class contains methods that perform tag ratio parsing and identify
  areas of text in files of all the different mimetypes. Measurement
  extraction is applied to all these sections of text and the resulting 
  measurements are stored in a JSON file
  {inputFile path}/{inputFile name}_1.json.
  
3. URLShortener.java - Class that contains methods to generate a unique short URL corresponding 
  to a file by making HTTP requests to TinyURL. Once generated, this class
  has methods to write this unique short URL to a JSON file
  {inputFile path}/{inputFile name}_2.json. 
 
4. GrobidParser.java - Class that has methods which send the content text to the GROBID REST 
  Parser of Tika in the form of an HTTP PUT request where the 
  request payload is the text extracted from the file and also writes
  the response of this GROBID REST Parser to a JSON file. This class also
  has methods that sends the title of the current file to ScholarAPI.java
  helper class which in turn invokes the Google Scholar API(A Python script)
  whose response contains publications/authors that are related to the 
  input file
 
5. GeotopicParser.java - Class that has a method which send the content text to the Geotopic 
	  Parser of Tika in the form of an HTTP request to the Tika Server as 
	  a file with Content-Type set to application/geotopic and the 
	  request payload being the text extracted from the file. It also
	  has a method that would write the HTTP response back to a JSON file
	 
6. SWEETParser.java - Class that contains methods to extract concepts of the SWEET Ontology
  that are relevant to each file from the input dataset. Once extracted,
  these concepts are written to {inputFile path}/{inputFile name}_5.json.
 
7. MetadataEvaluator.java - Class that evaluates the quality of metadata of each of the input
  files in the dataset and writes the score to a JSON file
  {inputFile path}/{inputFile name}_6.json
 
8. ScholarAPI.java - Helper class containing a helper method which is used by
  GrobidParser.java to run the Google Scholar API through a Python
  file(scholar.py) installed in the system. The helper method after 
  running the scholar.py also picks up its JSON response and wraps
  it around a JSON Object and returns it to GrobidParser.java. This
  JSON object contains related publications/authors of a publication.
 
9. JoinJSONFiles.java - Utility class that aids in combining and flattening all the
  individual JSON files corresponding to results of each parser's
  work on an input file from the dataset. This combining and flattening
  process ensures that data is in a format eligible for consumption
  by Apache Solr for index creation. The class takes in 
  {inputFile path}/{inputFile name}_1.json,
  {inputFile path}/{inputFile name}_2.json,
  {inputFile path}/{inputFile name}_3.json,
  {inputFile path}/{inputFile name}_4.json,
  {inputFile path}/{inputFile name}_5.json and
  {inputFile path}/{inputFile name}_6.json and combines and flattens these
  to form {inputFile path}/{inputFile name}_solr.json
 
10. JSONtoSOLR.java - Class that takes in all the {inputFile path}/{inputFile name}_solr.json
  for the input files in the dataset and uploads them onto Apache Solr 
  in the form of HTTP Client Requests to the Apache Solr Collection.
  These requests update the Solr Collection with the file's features 
  present in the JSON.
  

B) src/main/resources
1. englishPCFG.ser.gz - The grammar used for Stanford CoreNLP.
2. SWEET OWL set.txt - The list of all SWEET Ontology Concepts.

C) Other resources(Inside Other files folder)
1. Assignment 2 - Visualizations.zip - Contains the 5 visualizations that we have developed
2. output.csv - CSV file used for Tika Similarity clustering
3. cluster.txt - Sample cluster generated by Tika Similarity
3. bv-burgers-tiff-tp-pdf_720.jpg - Input to Tesseract(Extra credit)
4. out.txt.txt - Output of Tesseract(Extra credit)
   
Steps to run Tika Similarity to form clusters:

1. apache tika-python..install edit distance and tika-img-similarity ..
2. Using edit distance comparison on the folder gives us and output csv that has x y coordinates and a similarity score
3. python edit-value-similarity.py --inputDir D:/Finalversion1 --outCSV D:/Finalversion1/output.csv --accept pdf
4. Then pass this output csv generated to the following command
5. python edit-cosine-cluster.py D:\Finalversion1\output.csv
These steps will generate clusters

EXTRA CREDIT:
We have run the Tesseract OCR parser within Tika:

These are the commands that we ran:
brew install tesseract --all-languages
tesseract -psm 3 Desktop/BV-BURGERS-TIFF-TP-PDF.tiff out.txt
java -jar target/tika-server-1.12-SNAPSHOT.jar
curl -T Desktop/BV-BURGERS-TIFF-TP-PDF.tiff http://localhost:9998/tika --header "Content-type: image/tiff"