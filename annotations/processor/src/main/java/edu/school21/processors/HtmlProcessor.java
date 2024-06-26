package edu.school21.processors;

import com.google.auto.service.AutoService;
import edu.school21.annotations.HtmlForm;
import edu.school21.annotations.HtmlInput;

import javax.annotation.processing.*;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import javax.tools.Diagnostic;
import javax.tools.FileObject;
import javax.tools.StandardLocation;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashSet;
import java.util.Set;

@SupportedAnnotationTypes({"edu.school21.annotations.HtmlForm", "edu.school21.annotations.HtmlInput"})
@SupportedSourceVersion(SourceVersion.RELEASE_17)
@AutoService(Processor.class)
public class HtmlProcessor extends AbstractProcessor {

    private static final Set<String> GENERATED_FILES = new HashSet<>();

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        for (Element element : roundEnv.getElementsAnnotatedWith(HtmlForm.class)) {
            HtmlForm htmlForm = element.getAnnotation(HtmlForm.class);
            String fileName = htmlForm.fileName();

            if (GENERATED_FILES.contains(fileName)) {
                fileName = generateUniqueFileName(fileName);
            }

            GENERATED_FILES.add(fileName);

            StringBuilder html = new StringBuilder();

            html.append("<form action=\"")
                    .append(htmlForm.action())
                    .append("\" method=\"")
                    .append(htmlForm.method())
                    .append("\">\n");

            for (Element enclosedElement : element.getEnclosedElements()) {
                HtmlInput htmlInput = enclosedElement.getAnnotation(HtmlInput.class);
                if (htmlInput != null) {
                    html.append("\t<input type=\"").append(htmlInput.type())
                            .append("\" name=\"").append(htmlInput.name())
                            .append("\" placeholder=\"").append(htmlInput.placeholder()).append("\" />\n");
                }
            }

            html.append("\t<input type=\"submit\" value=\"Send\" />\n").append("</form>");

            try {
                FileObject file = processingEnv.getFiler().createResource(StandardLocation.CLASS_OUTPUT, "", fileName);
                try (PrintWriter writer = new PrintWriter(file.openWriter())) {
                    writer.print(html);
                }
            } catch (IOException e) {
                processingEnv.getMessager().printMessage(Diagnostic.Kind.ERROR,
                        "Error creating HTML file: " + e.getMessage());
            }
        }
        return true;
    }

    private String generateUniqueFileName(String fileName) {
        String baseName = fileName.contains(".") ? fileName.substring(0, fileName.lastIndexOf('.')) : fileName;
        String extension = fileName.contains(".") ? fileName.substring(fileName.lastIndexOf('.')) : "";
        int counter = 1;
        String newFileName = baseName + "_" + counter + extension;

        while (GENERATED_FILES.contains(newFileName)) {
            counter++;
            newFileName = baseName + "_" + counter + extension;
        }

        return newFileName;
    }
}
