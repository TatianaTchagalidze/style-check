package org.example;

import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.body.VariableDeclarator;
import org.apache.commons.io.FileUtils;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;

import java.io.File;
import java.util.Collection;

@Mojo(name = "check", defaultPhase = LifecyclePhase.VALIDATE)
public class StyleCheckMojo extends AbstractMojo {

  @Parameter(defaultValue = "${project}", required = true, readonly = true)
  private MavenProject mavenProject;

  @Override
  public void execute() throws MojoExecutionException, MojoFailureException {
    Collection<File> files = FileUtils.listFiles(mavenProject.getBasedir(), new String[]{"java"}, true);
    for (File file : files) {
      checkStyleFile(file);
    }
  }

  private void checkStyleFile(File javaFile) throws MojoExecutionException {
    try {
      CompilationUnit compilationUnit = StaticJavaParser.parse(javaFile);

      String canonicalPath = javaFile.getCanonicalPath();
      for (MethodDeclaration methodDeclaration : compilationUnit.findAll(MethodDeclaration.class)) {
        if (!isSnakeCase(methodDeclaration.getNameAsString())) {
          invalidate(canonicalPath, methodDeclaration.getNameAsString());
        }
      }

      for (VariableDeclarator variableDeclarator : compilationUnit.findAll(VariableDeclarator.class)) {
        if (!isCamelCase(variableDeclarator.getNameAsString())) {
          invalidate(canonicalPath, variableDeclarator.getNameAsString());
        }
      }

      for (ClassOrInterfaceDeclaration classOrInterfaceDeclaration : compilationUnit.findAll(ClassOrInterfaceDeclaration.class)) {
        if (!isPascalCase(classOrInterfaceDeclaration.getNameAsString())) {
          invalidate(canonicalPath, classOrInterfaceDeclaration.getNameAsString());
        }
      }

    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }

  private boolean isCamelCase(String name) {
    String camelCasePattern = "([a-z]+[a-zA-Z0-9]*)+";
    return name.matches(camelCasePattern);
  }

  private boolean isPascalCase(String name) {
    String pascalCasePattern = "([A-Z]+[a-zA-Z0-9]*)+";
    return name.matches(pascalCasePattern);
  }

  private boolean isSnakeCase(String name) {
    String snakeCasePattern = "^[a-z]+(?:_[a-z]+)*$";
    return name.matches(snakeCasePattern);
  }

  private void invalidate(String classCanonicalName, String name) throws MojoExecutionException {
    throw new MojoExecutionException("Code style violated: " + classCanonicalName + ", " + name);
  }
}
