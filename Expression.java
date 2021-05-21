package app;

import java.io.*;
import java.util.*;
import java.util.regex.*;

import structures.Stack;

public class Expression {

	public static String delims = " \t*+-/()[]";
			
    /**
     * Populates the vars list with simple variables, and arrays lists with arrays
     * in the expression. For every variable (simple or array), a SINGLE instance is created 
     * and stored, even if it appears more than once in the expression.
     * At this time, values for all variables and all array items are set to
     * zero - they will be loaded from a file in the loadVariableValues method.
     * 
     * @param expr The expression
     * @param vars The variables array list - already created by the caller
     * @param arrays The arrays array list - already created by the caller
     */
    public static void 
    makeVariableLists(String expr, ArrayList<Variable> vars, ArrayList<Array> arrays) {
    	
    	/** COMPLETE THIS METHOD **/
    	/** DO NOT create new vars and arrays - they are already created before being sent in
    	 ** to this method - you just need to fill them in.
    	 **/
    	String delims = "\\+|-|\\*|/|\\(|\\)|\\[|\\]" ;
    	String[] tokens = expr.replace(" ", "" ).replace("\t", "" ).split( delims ) ;
    	
    	for (int i = 0; i < tokens.length; i++ ) {
    		String token = tokens[i] ;
    		if ( token.isEmpty()== false && Character.isLetter(token.charAt(0) ) ) {
    			Variable var = new Variable( token ) ;
    			Array arr = new Array( token ) ;
    			int varI = vars.indexOf( var ) ;
    			int arrI = arrays.indexOf( arr ) ;
    			if ( varI >= 0 || arrI >= 0 ) continue ;
    			
    			int idx = expr.indexOf( token ) + token.length();
    			if ( idx < expr.length() && expr.charAt( idx ) == '[' ) {
    				arrays.add( arr ) ;
    			} else {
    				vars.add( var ) ;
    			}
    		}
    	}
    }
    
    /**
     * Loads values for variables and arrays in the expression
     * 
     * @param sc Scanner for values input
     * @throws IOException If there is a problem with the input 
     * @param vars The variables array list, previously populated by makeVariableLists
     * @param arrays The arrays array list - previously populated by makeVariableLists
     */
    public static void 
    loadVariableValues(Scanner sc, ArrayList<Variable> vars, ArrayList<Array> arrays) 
    throws IOException {
        while (sc.hasNextLine()) {
            StringTokenizer st = new StringTokenizer(sc.nextLine().trim());
            int numTokens = st.countTokens();
            String tok = st.nextToken();
            Variable var = new Variable(tok);
            Array arr = new Array(tok);
            int vari = vars.indexOf(var);
            int arri = arrays.indexOf(arr);
            if (vari == -1 && arri == -1) {
            	continue;
            }
            int num = Integer.parseInt(st.nextToken());
            if (numTokens == 2) { // scalar symbol
                vars.get(vari).value = num;
            } else { // array symbol
            	arr = arrays.get(arri);
            	arr.values = new int[num];
                // following are (index,val) pairs
                while (st.hasMoreTokens()) {
                    tok = st.nextToken();
                    StringTokenizer stt = new StringTokenizer(tok," (,)");
                    int index = Integer.parseInt(stt.nextToken());
                    int val = Integer.parseInt(stt.nextToken());
                    arr.values[index] = val;              
                }
            }
        }
    }
    
    private static boolean higher( char op1, char op2 ) {
    	if ( op2 == '+' || op2 == '-' ) {
    		return true ;
    	} else {
    		return false ;
    	}
    }
    
    private static void count ( Stack<Float> stkVals, Stack<Character> stkOps ) {
    	char op = stkOps.pop() ;
    	double right = stkVals.pop() ;
    	double left = stkVals.pop() ;
    	double val = 0 ;
    	if ( op == '+' ) val = left + right ;
    	else if ( op == '-' ) val = left - right ;
    	else if ( op == '*' ) val = left * right ;
    	else if ( op == '/' ) val = left / right ;
    	stkVals.push( new Float( val ) ) ;
    }
    
    private static String varName( String expr, int start ) {
    	int len = expr.length() ;
    	int end = start + 1 ;
    	while ( end != len ) {
    		boolean isFound = delims.indexOf(expr.charAt(end) ) >= 0 ;
    		if ( isFound == true ) break ;
    		end++ ;
    	}
    	
    	return expr.substring( start, end ) ;
    }
    
    /**
     * Evaluates the expression.
     * 
     * @param vars The variables array list, with values for all variables in the expression
     * @param arrays The arrays array list, with values for all array items
     * @return Result of evaluation
     */
    public static float 
    evaluate(String expr, ArrayList<Variable> vars, ArrayList<Array> arrays) {
    	/** COMPLETE THIS METHOD **/
    	// following line just a placeholder for compilation
    	expr = expr.replace(" ",  "" ).replace("\t", "" ) ;
    	
    	Stack<Float> stkVals = new Stack<Float>() ;
    	Stack<Character> stkOps = new Stack<Character>() ;
    	
    	int i = 0 ; 
    	int len = expr.length() ;
    	while (i < len) {
    		char ch = expr.charAt( i ) ;
    		if ( ch == '+' || ch == '-' || ch == '*' || ch == '/' ) {
    			while (stkOps.size() > 0 && higher( stkOps.peek(), ch) ) {
    				count( stkVals, stkOps ) ;
    			}
    			stkOps.push(new Character(ch) ) ;
    			i++ ;
    		} else if ( ch == '(' ) {
    			int end = -1 ;
    			int para = 1 ;
    			for ( int k = i + 1; k < len; k++ ) {
    				if ( expr.charAt(k) == '(' ) para++ ;
    				if ( expr.charAt(k) == ')' ) para-- ;
    				if ( para == 0 ) {
    					end = k ;
    					break ;
    				}
    			}
    			String sub = expr.substring( i + 1, end ) ;
    			double val = evaluate( sub, vars, arrays ) ;
    			stkVals.push( new Float( val) ) ;
    			i = end + 1 ;
    		} else if ( Character.isDigit(ch) || Character.isLetter(ch) ) {
    			String name = varName( expr, i ) ;
    			Variable var = new Variable( name ) ;
    			Array arr = new Array( name ) ;
    			int varI = vars.indexOf( var ) ;
    			int arrI = arrays.indexOf( arr ) ;
    			if( varI >= 0 ) { // scalar
    				Variable v = vars.get( varI ) ;
    				stkVals.push( new Float( v.value ) ) ; 
    				i += name.length();
    			} else if ( arrI >= 0 ) { // array
    				int start = expr.indexOf('[', i ) ;
    				int end = -1 ;
    				int cnt = 1 ;
    				for ( int k = start + 1; k < len; k++ ) {
    					if ( expr.charAt(k) == '[' ) cnt ++ ;
    					if ( expr.charAt(k) == ']' ) cnt-- ;
    					if (cnt == 0 ) {
    						end = k ;
    						break ;
    					}
    				}
    				String sub = expr.substring( start + 1 , end) ;
    				int idx = (int)evaluate(sub, vars, arrays ) ;
    				Array a = arrays.get( arrI ) ;
    				stkVals.push( new Float(a.values[idx])) ;
    				i = end + 1 ;
    				
    			} else { // number
    				stkVals.push( new Float(name) ) ;
    				i += name.length();
    			}
    		} else {
    			i ++ ;
    		}
    	}
    	while (stkOps.size() > 0) count( stkVals, stkOps ) ;
    	
    	return stkVals.pop() ;
    }
}
