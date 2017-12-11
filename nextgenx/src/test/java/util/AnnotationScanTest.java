
package util;

import io.github.lukehutch.fastclasspathscanner.FastClasspathScanner;
import io.github.lukehutch.fastclasspathscanner.matchprocessor.ClassAnnotationMatchProcessor;

public class AnnotationScanTest {


    private static String appendedString = "";

    public static void main(String args[]) throws ClassNotFoundException {



        new FastClasspathScanner("com")
                // Optional, in case you want to debug any issues with scanning.
                // Add this right after the constructor to maximize the amount of log info.
                //.verbose()
                .matchClassesWithAnnotation(Deprecated.class, new ClassAnnotationMatchProcessor() {
                    @Override
                    public void processMatch(Class<?> matchingClass) {
                        String className = matchingClass.getName();
                        if (((className.contains("com.btfin") || className.contains("com.bt")))
                                && !className.contains("package-info")) {
                            String slashName = className.replace(".","/");
                            String mavenExclusion = "**/" + slashName + "*";
                            appendString(mavenExclusion);
                        }
                    }
                })
                // Actually perform the scan (nothing will happen without this call)
                .scan();

        System.out.println(appendedString);
    }

    private static void appendString(String theClassName) {
        appendedString += ","+theClassName;
    }

}
