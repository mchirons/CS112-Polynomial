package poly;

import java.io.*;
import java.util.StringTokenizer;

/**
 * This class implements a term of a polynomial.
 * 
 * @author runb-cs112
 *
 */
class Term {
	/**
	 * Coefficient of term.
	 */
	public float coeff;
	
	/**
	 * Degree of term.
	 */
	public int degree;
	
	/**
	 * Initializes an instance with given coefficient and degree.
	 * 
	 * @param coeff Coefficient
	 * @param degree Degree
	 */
	public Term(float coeff, int degree) {
		this.coeff = coeff;
		this.degree = degree;
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	public boolean equals(Object other) {
		return other != null &&
		other instanceof Term &&
		coeff == ((Term)other).coeff &&
		degree == ((Term)other).degree;
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
		if (degree == 0) {
			return coeff + "";
		} else if (degree == 1) {
			return coeff + "x";
		} else {
			return coeff + "x^" + degree;
		}
	}
}

/**
 * This class implements a linked list node that contains a Term instance.
 * 
 * @author runb-cs112
 *
 */
class Node {
	
	/**
	 * Term instance. 
	 */
	Term term;
	
	/**
	 * Next node in linked list. 
	 */
	Node next;
	
	/**
	 * Initializes this node with a term with given coefficient and degree,
	 * pointing to the given next node.
	 * 
	 * @param coeff Coefficient of term
	 * @param degree Degree of term
	 * @param next Next node
	 */
	public Node(float coeff, int degree, Node next) {
		term = new Term(coeff, degree);
		this.next = next;
	}
}

/**
 * This class implements a polynomial.
 * 
 * @author runb-cs112
 *
 */
public class Polynomial {
	
	/**
	 * Pointer to the front of the linked list that stores the polynomial. 
	 */ 
	Node poly;
	
	/** 
	 * Initializes this polynomial to empty, i.e. there are no terms.
	 *
	 */
	public Polynomial() {
		poly = null;
	}
	
	/**
	 * Reads a polynomial from an input stream (file or keyboard). The storage format
	 * of the polynomial is:
	 * <pre>
	 *     <coeff> <degree>
	 *     <coeff> <degree>
	 *     ...
	 *     <coeff> <degree>
	 * </pre>
	 * with the guarantee that degrees will be in descending order. For example:
	 * <pre>
	 *      4 5
	 *     -2 3
	 *      2 1
	 *      3 0
	 * </pre>
	 * which represents the polynomial:
	 * <pre>
	 *      4*x^5 - 2*x^3 + 2*x + 3 
	 * </pre>
	 * 
	 * @param br BufferedReader from which a polynomial is to be read
	 * @throws IOException If there is any input error in reading the polynomial
	 */
	public Polynomial(BufferedReader br) throws IOException {
		String line;
		StringTokenizer tokenizer;
		float coeff;
		int degree;
		
		poly = null;
		
		while ((line = br.readLine()) != null) {
			tokenizer = new StringTokenizer(line);
			coeff = Float.parseFloat(tokenizer.nextToken());
			degree = Integer.parseInt(tokenizer.nextToken());
			poly = new Node(coeff, degree, poly);
		}
	}
	
	
	/**
	 * Returns the polynomial obtained by adding the given polynomial p
	 * to this polynomial - DOES NOT change this polynomial
	 * 
	 * @param a Polynomial to be added
	 * @return A new polynomial which is the sum of this polynomial and p.
	 */
	public Polynomial add(Polynomial p) {
		
		if (p.poly == null && poly == null){                    //both polynomials are empty
			return p;
		}
		if (p.poly == null){									//p is empty
			return this;
		}
		if (poly == null){										//this is empty
			return p;
		}
		Polynomial a = copy(p);
		Node thisPtr = poly; 									//holds ptr of current this node
		Node aPtr = a.poly; 									//holds ptr of current p node
		Node temp;												//for creating new nodes
		/*
		 * For the first additions of nodes
		 */
		if (a.poly.term.degree > poly.term.degree){ 
			temp = new Node(poly.term.coeff, poly.term.degree, a.poly); 
			a.poly = temp;
			thisPtr = thisPtr.next;
		}
		Node aPtrPrev = a.poly;									//holds the address of previous node
		while (aPtr != null && thisPtr != null){  				
			if (aPtr.term.degree < thisPtr.term.degree){ 		//no operand of p that matches degree of this
				aPtrPrev = aPtr;
				aPtr = aPtr.next;
			}
			else if (aPtr.term.degree > thisPtr.term.degree){    //no operand of p that matches degree of this, sum is this.degree
				temp = new Node(thisPtr.term.coeff, thisPtr.term.degree, aPtr);   	//adds new node to p with data from this node
				aPtrPrev.next = temp;
				aPtrPrev = temp;
				thisPtr = thisPtr.next;
			}
			else if (aPtr.term.coeff + thisPtr.term.coeff == 0){ //sum of coeff is 0
				if (aPtr == a.poly){							 //makes front of p next p node
					a.poly = a.poly.next;
					aPtr = a.poly;
					aPtrPrev = a.poly;
					thisPtr = thisPtr.next;
				}
				else {											//deletes p node
					aPtrPrev.next = aPtr.next;
					thisPtr = thisPtr.next;
				}
			}
			else {   											 //adds coefficients together, moves on to next nodes
				aPtr.term.coeff = thisPtr.term.coeff + aPtr.term.coeff;
				aPtrPrev = aPtr;
				thisPtr = thisPtr.next;
				aPtr = aPtr.next;
			}
		}
		while (thisPtr != null){    							//adds nodes to p if this had more higher degree coeff to add
			aPtr = new Node(thisPtr.term.coeff, thisPtr.term.degree, null);
			aPtrPrev.next = aPtr;
			aPtrPrev = aPtr;
			thisPtr = thisPtr.next;
		}
		return a;												//returns the sum polynomial
	}
	
	/**
	 * Returns the polynomial obtained by multiplying the given polynomial p
	 * with this polynomial - DOES NOT change this polynomial
	 * 
	 * @param p Polynomial with which this polynomial is to be multiplied
	 * @return A new polynomial which is the product of this polynomial and p.
	 */
	public Polynomial multiply(Polynomial p) {
		if (p.poly == null){								 // if one polynomial is 0 than the product is 0
			return p;
		}
		if (poly == null){
			return this;
		}
		Polynomial temp = new Polynomial();  								//holds the "product to be added" polynomial to be added
		Polynomial answer = new Polynomial();								//holds final result
		Node pPtr = p.poly;   												
		Node thisPtr = poly;  
		Node newNode = null;												//to create and append nodes to temp
		Node prev = temp.poly;
		while (pPtr != null){
			while(thisPtr != null){
				if (temp.poly == null){ 									//to establish "product to be added" polynomial
					newNode = new Node(thisPtr.term.coeff * pPtr.term.coeff, pPtr.term.degree + thisPtr.term.degree, null);
					temp.poly = newNode;            			
					prev = newNode;
					thisPtr = thisPtr.next;
				}
				else {
					newNode = new Node(thisPtr.term.coeff * pPtr.term.coeff, pPtr.term.degree + thisPtr.term.degree, null);
					prev.next = newNode;
					prev = newNode;
					thisPtr = thisPtr.next;
				}
			} 
		answer = answer.add(copy(temp));
		temp.poly = null;
		thisPtr = poly;
		pPtr = pPtr.next;
		}
		return answer;
	}
	
		
	
	
	/**
	 * Evaluates this polynomial at the given value of x
	 * 
	 * @param x Value at which this polynomial is to be evaluated
	 * @return Value of this polynomial at x
	 */
	public float evaluate(float x) {
		if (poly == null){
			return 0;
		}
		Node ptr = poly;
		float total = 0;
		while (ptr != null){
			total = (float)(total + ptr.term.coeff * Math.pow(x, ptr.term.degree));
			ptr = ptr.next;
		}
		return total;
		
		/*
		float total = 0;
		Node ptr = poly;
		if (ptr == null){
			return 0;
		}
		if (ptr.term.degree == 0 && ptr.next == null){
			return poly.term.coeff;
		}
		if (ptr.next == null){
			return ptr.term.coeff;
		}
		if (ptr.term.degree == 0){
			total = poly.term.coeff + evaluate(x);
		}
		ptr = ptr.next;
		return total;
		*/
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
		String retval;
		
		if (poly == null) {
			return "0";
		} else {
			retval = poly.term.toString();
			for (Node current = poly.next ;
			current != null ;
			current = current.next) {
				retval = current.term.toString() + " + " + retval;
			}
			return retval;
		}
	}
	
	private static Polynomial copy(Polynomial p){
		if (p == null){
			return p;
		}
		System.out.println("p is :" + p);
		Polynomial copy = new Polynomial();
		Node firstNode = new Node (p.poly.term.coeff, p.poly.term.degree, null);
		copy.poly = firstNode;
		Node prev = firstNode;
		for (Node ptr = p.poly.next; ptr != null; ptr = ptr.next){
			Node newNode = new Node (ptr.term.coeff, ptr.term.degree, null);
			prev.next = newNode;
			prev = newNode;
		}
		
		return copy;
	}
}