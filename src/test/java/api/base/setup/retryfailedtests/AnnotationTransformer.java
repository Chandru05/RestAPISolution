package api.base.setup.retryfailedtests;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import org.testng.IAnnotationTransformer;
import org.testng.annotations.ITestAnnotation;

/**
 *  Annotatiion Transformer for calling Run time failure retry.
 */
public class AnnotationTransformer implements IAnnotationTransformer {
  
  public void transform(ITestAnnotation annotation, Class testClass, Constructor testConstructor, Method testMethod) {
    annotation.setRetryAnalyzer(RetryAnalyzer.class);
  }

}
