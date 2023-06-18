# Maven Homework
For this homework you are going to write a simple yet interesting maven plugin.

## Plugin description
1. Plugin name: style-check
2. Plugin should only have one goal named: check 
3.  This goal by default should be executed during ‘validate’ phase 
4. Our goal should check every .java file in a project and check:
     * If every method name only_uses_snake_case
     * If every variable and argument declarations onlyUseCamelCase
     * If every class name OnlyUsesPascalCase 
5. If a, b or c is not valid then the build should break with the message: ‘code style violated: <canonical name of the class>’, <name of the method/variable/class>


