import java.io.FileInputStream;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.rdf.api.Graph;
import org.apache.commons.rdf.api.IRI;
import org.apache.commons.rdf.rdf4j.RDF4J;
import org.eclipse.rdf4j.model.Model;
import org.eclipse.rdf4j.model.Statement;
import org.eclipse.rdf4j.rio.RDFFormat;
import org.eclipse.rdf4j.rio.Rio;

import fr.inria.lille.shexjava.GlobalFactory;
import fr.inria.lille.shexjava.schema.Label;
import fr.inria.lille.shexjava.schema.ShexSchema;
import fr.inria.lille.shexjava.schema.parsing.GenParser;
import fr.inria.lille.shexjava.validation.RecursiveValidation;
import fr.inria.lille.shexjava.validation.RecursiveValidationWithMemorization;
import fr.inria.lille.shexjava.validation.RefineValidation;
import fr.inria.lille.shexjava.validation.ValidationAlgorithm;

public class Main {

	public static void main(String[] args) throws Exception {
		Path schemaFile = Paths.get("src", "main", "resources", "simple.shex"); //schema.shex
		Path dataFile = Paths.get("src", "main", "resources", "simple-data.ttl");
		List<Path> importDirectories = Collections.emptyList();
		
		RDF4J factory = new RDF4J();
		GlobalFactory.RDFFactory = factory;
		
		// load and create the shex schema
		System.out.println("Reading schema");
		ShexSchema schema = GenParser.parseSchema(schemaFile,importDirectories);
		for (Label label:schema.getRules().keySet())
			System.out.println(label+": "+schema.getRules().get(label));
		
		 // load the model
		System.out.println("Reading data");
		String baseIRI = "http://example.org/";
		Model data = Rio.parse(new FileInputStream(dataFile.toFile()), baseIRI, RDFFormat.TURTLE);
		Iterator<Statement> ite = data.iterator();
		while (ite.hasNext())
			System.out.println(ite.next());
		
		// create the graph
		Graph dataGraph = factory.asGraph(data);

		//focus node
//		IRI focusNode = factory.createIRI("https://graph.mindlab.ai/tvb-mayrhofen/feratel/25f9649a-c37c-4dc9-a0b2-1a2fabcc3a84"); //to change with what you want 
//		Label shapeLabel = new Label(factory.createIRI("http://example.org/TouristAttraction")); //to change with what you want 

		// choose focus node and shapelabel
		IRI focusNode = factory.createIRI("http://example.org/1655809"); //to change with what you want 
		Label shapeLabel = new Label(factory.createIRI("http://example.org/TouristAttraction")); //to change with what you want 
		
		System.out.println();
		System.out.println("Refine validation:");
		// create the validation algorithm
		ValidationAlgorithm validation = new RefineValidation(schema, dataGraph);  
		//validate
		validation.validate(focusNode, shapeLabel);
		//check the result
		boolean result = validation.getTyping().isConformant(focusNode, shapeLabel);
		System.out.println("Does "+focusNode+" has shape "+shapeLabel+"? "+result);
		
		System.out.println();
		System.out.println("Recursive validation:");
		validation = new RecursiveValidation(schema, dataGraph);
		validation.validate(focusNode, shapeLabel);
		//check the result
		result = validation.getTyping().isConformant(focusNode, shapeLabel);
		System.out.println("Does "+focusNode+" has shape "+shapeLabel+"? "+result);
		
		System.out.println();
		System.out.println("Recursive validation with memorization:");
		validation = new RecursiveValidationWithMemorization(schema, dataGraph);
		validation.validate(focusNode, shapeLabel);
		//check the result
		result = validation.getTyping().isConformant(focusNode, shapeLabel);
		System.out.println("Does "+focusNode+" has shape "+shapeLabel+"? "+result);
	}

}
