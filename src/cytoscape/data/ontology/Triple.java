/**
 * 
 */
package cytoscape.data.ontology;

import java.util.Collections;

import org.biojava.bio.Annotation;
import org.biojava.ontology.Ontology;
import org.biojava.ontology.Term;
import org.biojava.utils.AbstractChangeable;

/**
 * 
 * Extract partial graph structure.
 * 
 * @since Cytoscape 2.4
 * @version 0.5
 * @author kono
 * 
 */
public class Triple extends AbstractChangeable implements
		org.biojava.ontology.Triple {

	private final Term object;
	private final Term subject;
	private final Term predicate;
	
	private final String name;
	private final String description;

	public Triple(final Term subject, final Term object, final Term predicate, final String name, final String description) {
		this.object = object;
		this.subject = subject;
		this.predicate = predicate;
		
		this.name = name;
		this.description = description;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.biojava.ontology.Triple#getObject()
	 */
	public Term getObject() {
		return object;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.biojava.ontology.Triple#getPredicate()
	 */
	public Term getPredicate() {
		return predicate;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.biojava.ontology.Triple#getSubject()
	 */
	public Term getSubject() {
		return subject;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.biojava.ontology.Term#addSynonym(java.lang.Object)
	 */
	/**
	 * Triple synonym is not supported.
	 */
	public void addSynonym(Object arg0) {
		throw new UnsupportedOperationException(
				"Cytoscape does not know about triple synonyms.");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.biojava.ontology.Term#getDescription()
	 */
	/**
	 * Description for triple is not supported.
	 */
	public String getDescription() {
		return description;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.biojava.ontology.Term#getName()
	 */
	/**
	 * Returns human-readable name of this triple. 
	 */
	public String getName() {
		return name;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.biojava.ontology.Term#getOntology()
	 */
	/**
	 * Returns ontology which this triple belongs to. 
	 */
	public Ontology getOntology() {
		return object.getOntology();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.biojava.ontology.Term#getSynonyms()
	 */
	/**
	 * Synonym is not supported.
	 */
	public Object[] getSynonyms() {
		return Collections.EMPTY_LIST.toArray();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.biojava.ontology.Term#removeSynonym(java.lang.Object)
	 */
	/**
	 * Not supported.
	 */
	public void removeSynonym(Object arg0) {
		throw new UnsupportedOperationException(
				"Cytoscape does not support triple synonyms.");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.biojava.bio.Annotatable#getAnnotation()
	 */
	/**
	 * Always return empty annotation object.
	 */
	public Annotation getAnnotation() {
		return Annotation.EMPTY_ANNOTATION;
	}

	public String toString() {
		return this.object.getOntology().getName() + ":" + this.predicate + "("
				+ this.subject + "," + this.object + ")";
	}

}