/*
 * ModelerRule.java
 *
 * Created on February 8, 2003, 12:27 PM
 */

package psl.memento.server.vem;

import org.apache.oro.text.regex.*;
/**
 *
 * @author  vlad
 */
public class ModelerRule {
    
    private String type;
    private Pattern pattern;
    
    private static Perl5Compiler compiler;
    private static Perl5Matcher matcher;
    
    /** Creates a new instance of ModelerRule */
    public ModelerRule(String patternStr, String t) {
	type = t;
	
	if (patternStr == null) {
	    pattern = null;
	} else {
	    if (compiler == null) {
		compiler = new Perl5Compiler();
		matcher = new Perl5Matcher();
	    }

	    try {
		pattern = compiler.compile(patternStr);
	    } catch (MalformedPatternException mpe) {}
	}
    }
    
    public boolean matches(String objectName) {
	return matcher.contains(objectName, pattern);
    }
    
    public String getMatch() {
	return matcher.getMatch().toString();
    }
    
    public String getType() {
	return (type == null) ? getMatch() : type;
    }
    
    public static String escape(String in) {
	return Perl5Compiler.quotemeta(in);
    }
}
