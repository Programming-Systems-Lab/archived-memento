package psl.memento.server.frax;

// jdk imports
import java.io.*;
import java.net.URI;
import java.util.*;

// non-jdk imports
import com.hp.hpl.mesa.rdf.jena.model.*;
import com.hp.hpl.mesa.rdf.jena.common.SeqImpl;
import de.hunsicker.antlr.collections.AST;
import de.hunsicker.jalopy.*;
import de.hunsicker.jalopy.language.*;
import psl.memento.server.frax.FraxException;
import psl.memento.server.frax.vocabulary.JavaSourceVocab;
import psl.memento.server.frax.vocabulary.ResourceVocab;

public class JavaSourcePlug extends Plug {
  private static final String kErrorAddingProperty =
    "Error adding RDF Property object.";
  private static final String kErrorParsingJavaSource = 
    "Could not parse java source.";
  
  public void extractContentMetadata(InputStream iSource, Resource iTarget)
      throws FraxException {
    try {
      Jalopy jalopy = new Jalopy();
      
      jalopy.setInput(iSource, "psl-frax.jar");
      JavaNode node = jalopy.parse();
      
      Jalopy.State s = jalopy.getState();
      if (s != Jalopy.State.PARSED) {
        throw new FraxException(kErrorParsingJavaSource);
      }
      
      JSMD jsmd = new JSMD();
      
      TreeWalker t = new MyTreeWalker(jsmd);
      t.walk(node);
      
      iTarget.addProperty(JavaSourceVocab.kPackage.getProperty(),
        jsmd.mPackage);
      
      Model mod = iTarget.getModel();
      
      Seq importsSeq = mod.createSeq();
      for (int i = 0, n = jsmd.mImports.size(); i < n; i++) {
        importsSeq.add(jsmd.mImports.get(i));
      }
      iTarget.addProperty(JavaSourceVocab.kImports.getProperty(),
        importsSeq);
      
      for (int i = 0, n = jsmd.mClasses.size(); i < n; i++) {
        Clazz c = (Clazz) jsmd.mClasses.get(i);
        Resource cr = mod.createResource();

        cr.addProperty(JavaSourceVocab.kModifierMask.getProperty(),
          c.mModifierMask);
        cr.addProperty(ResourceVocab.kName.getProperty(),
          c.mName);
      }
      
      for (int i = 0, n = jsmd.mMethods.size(); i < n; i++) {
        Method m = (Method) jsmd.mMethods.get(i);
        Resource mr = mod.createResource();

        mr.addProperty(JavaSourceVocab.kModifierMask.getProperty(),
          m.mModifierMask);
        mr.addProperty(ResourceVocab.kName.getProperty(), m.mName);

        for (int k = 0, y = m.mParams.size(); k < y; k++) {
          Param p = (Param) m.mParams.get(k);
          Resource pr = mod.createResource();

          pr.addProperty(JavaSourceVocab.kModifierMask.getProperty(),
            p.mModifierMask);
          pr.addProperty(JavaSourceVocab.kType.getProperty(), p.mTypeName);
          pr.addProperty(ResourceVocab.kName.getProperty(), p.mName);

          mr.addProperty(JavaSourceVocab.kParam.getProperty(), pr);
        }

        if (m.mIsConstructor) {          
          iTarget.addProperty(JavaSourceVocab.kConstructor.getProperty(), mr);
        } else {
          mr.addProperty(JavaSourceVocab.kReturnType.getProperty(),
            m.mReturnType);
          iTarget.addProperty(JavaSourceVocab.kMethod.getProperty(), mr);
        }
      }
    } catch (RDFException ex) {
      throw new FraxException(kErrorAddingProperty, ex);
    }
  }
  
  static class JSMD {
    String mPackage;
    List mImports;
    List mClasses;
    List mMethods;
    
    JSMD() {
      mImports = new ArrayList();
      mClasses = new ArrayList();
      mMethods = new ArrayList();
    }
  }
  
  class MyTreeWalker extends TreeWalker {
    private JSMD mJSMD;    
    
    MyTreeWalker(JSMD iJSMD) {
      mJSMD = iJSMD;      
    }
    
    public void visit(AST iNode) {
      switch(iNode.getType()) {
        case JavaTokenTypes.CTOR_DEF:          
          mJSMD.mMethods.add(new Method(iNode, true));
          break;
        case JavaTokenTypes.METHOD_DEF:          
          mJSMD.mMethods.add(new Method(iNode, false));
          break;
        case JavaTokenTypes.CLASS_DEF:
          mJSMD.mClasses.add(new Clazz(iNode));
          break;
        case JavaTokenTypes.INTERFACE_DEF:
          break;
        case JavaTokenTypes.PACKAGE_DEF:
          mJSMD.mPackage = JavaNodeHelper.getDottedName(iNode.getFirstChild());
          break;
        case JavaTokenTypes.IMPORT:
          mJSMD.mImports.add(iNode.getText());
          break;
      }
    }
  }
  
  static class Method {
    String mName;
    int mModifierMask;
    List mParams = new ArrayList();
    String mReturnType;
    boolean mIsConstructor;
    
    Method(AST iNode, boolean iIsConstructor) {
      mIsConstructor = iIsConstructor;
      
      for (AST i = iNode.getFirstChild(); i != null; i = i.getNextSibling()) {
        switch (i.getType()) {
          case JavaTokenTypes.MODIFIERS:
            mModifierMask = JavaNodeModifier.valueOf(i);
            break;
          case JavaTokenTypes.IDENT:
            mName = i.getText();
            break;
          case JavaTokenTypes.PARAMETERS:
            AST p = i.getFirstChild();
            if (p != null) {
              for (AST j = p; j != null; j = j.getNextSibling()) {
                mParams.add(new Param(j));
              }
            }
            break;
          case JavaTokenTypes.TYPE:
            mReturnType = i.getFirstChild().getText();
            break;
        }
      }
    }
  }
  
  static class Clazz {
    String mName;
    int mModifierMask;
    
    Clazz(AST iNode) {
      mModifierMask = JavaNodeModifier.valueOf(
        JavaNodeHelper.getFirstChild(iNode, JavaTokenTypes.MODIFIERS));
      mName = JavaNodeHelper.getFirstChild(iNode, JavaTokenTypes.IDENT)
        .getText();
    }
  }
  
  static class Param {
    String mName;
    String mTypeName;
    int mModifierMask;
    
    Param(AST iNode) {
      for (AST i = iNode.getFirstChild(); i != null; i = i.getNextSibling()) {
        switch (i.getType()) {
          case JavaTokenTypes.MODIFIERS:
            mModifierMask = JavaNodeModifier.valueOf(i);
            break;
          case JavaTokenTypes.TYPE:
            mTypeName = i.getFirstChild().getText();
            break;
          case JavaTokenTypes.IDENT:
            mName = i.getText();
            break;
        }
      }
    }
  }
}