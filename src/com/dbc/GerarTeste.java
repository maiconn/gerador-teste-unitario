package com.dbc;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.Field;
import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.net.URL;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

public class GerarTeste {
    public static void main(String[] args) throws IOException, ClassNotFoundException {
        final String packageName = "br.com.sicredi.comunidade.projeto.entity";
        Class[] classes = getClasses(packageName);
        for (Class clazz : classes) {
            Field[] allFields = clazz.getDeclaredFields();
            final String simpleName = clazz.getSimpleName();
            StringBuilder testeGerado = new StringBuilder();
            testeGerado.append(
                    "package " + packageName + ";\n\n" +
                            "import org.junit.Test;\n" +
                            "import org.junit.runner.RunWith;\n" +
                            "import org.mockito.junit.MockitoJUnitRunner;\n" +
                            "\n" +
                            "import static org.junit.Assert.*;\n"
            );
            Set<Type> filtrados = new HashSet<>();
            int tamanhoAnterior = 0;
            for(Field field : allFields){
                filtrados.add(field.getType());
                if(filtrados.size() > tamanhoAnterior){
                    if (field.getType() == LocalDateTime.class) {
                        testeGerado.append("import java.time.LocalDateTime;\n");
                    }else if (field.getType() == LocalDate.class) {
                        testeGerado.append("import java.time.LocalDate;\n");
                    }else if (field.getType() == List.class) {
                        testeGerado.append("import java.util.List;\n");
                    }else if (field.getType() == ArrayList.class) {
                        testeGerado.append("import java.util.ArrayList;\n");
                    }else if (field.getType() == Set.class) {
                        testeGerado.append("java.util.Set;\n");
                    }else if (field.getType() == BigDecimal.class) {
                        testeGerado.append("import java.math.BigDecimal;\n");
                    }else if (field.getType() == BigInteger.class) {
                        testeGerado.append("import java.math.BigInteger;\n");
                    }
                    tamanhoAnterior++;
                }
            }

            testeGerado.append("\n" +
                    "@RunWith(MockitoJUnitRunner.class)\n" +
                    "public class " + simpleName + "Test {\n" +
                    "    @Test\n" +
                    "    public void test() {").append(System.lineSeparator());
            testeGerado.append("        " + simpleName + " response = new " + clazz.getSimpleName() + "();").append(System.lineSeparator());
            for (Field field : allFields) {
                final String name = field.getName();
                String cap = name.substring(0, 1).toUpperCase() + name.substring(1);
                if (field.getType() == String.class) {
                    testeGerado.append("        response.set" + cap + "(\"teste\");").append(System.lineSeparator());
                    testeGerado.append("        assertEquals(\"teste\", response.get" + cap + "());").append(System.lineSeparator());
                }else if (field.getType() == Character.class) {
                    testeGerado.append("        response.set" + cap + "('A');").append(System.lineSeparator());
                    testeGerado.append("        assertNotNull(response.get" + cap + "());").append(System.lineSeparator());
                } else if (field.getType() == Double.class) {
                    testeGerado.append("        response.set" + cap + "(0D);").append(System.lineSeparator());
                    testeGerado.append("        assertEquals(0D, response.get" + cap + "(), 1e-15);").append(System.lineSeparator());
                } else if (field.getType() == Long.class) {
                    testeGerado.append("        response.set" + cap + "(1L);").append(System.lineSeparator());
                    testeGerado.append("        assertEquals(1L, response.get" + cap + "().longValue());").append(System.lineSeparator());
                } else if (field.getType() == Integer.class) {
                    testeGerado.append("        response.set" + cap + "(1);").append(System.lineSeparator());
                    testeGerado.append("        assertEquals(1, response.get" + cap + "().intValue());").append(System.lineSeparator());
                } else if (field.getType() == LocalDateTime.class) {
                    testeGerado.append("        response.set" + cap + "(LocalDateTime.now());").append(System.lineSeparator());
                    testeGerado.append("        assertNotNull(response.get" + cap + "());").append(System.lineSeparator());
                }else if (field.getType() == LocalDate.class) {
                    testeGerado.append("        response.set" + cap + "(LocalDate.now());").append(System.lineSeparator());
                    testeGerado.append("        assertNotNull(response.get" + cap + "());").append(System.lineSeparator());
                } else if (field.getType() == List.class) {
                    testeGerado.append("        response.set" + cap + "(new ArrayList<>());").append(System.lineSeparator());
                    testeGerado.append("        assertEquals(0, response.get" + cap + "().size());").append(System.lineSeparator());
                } else if (field.getType() == Set.class) {
                    testeGerado.append("        response.set" + cap + "(new HashSet<>());").append(System.lineSeparator());
                    testeGerado.append("        assertEquals(0, response.get" + cap + "().size());").append(System.lineSeparator());
                }else if (field.getType() == BigDecimal.class) {
                    testeGerado.append("        response.set" + cap + "(new BigDecimal(1L));").append(System.lineSeparator());
                    testeGerado.append("        assertEquals(1L, response.get" + cap + "().longValue());").append(System.lineSeparator());
                }else if (field.getType() == BigInteger.class) {
                    testeGerado.append("        response.set" + cap + "(BigInteger.ONE);").append(System.lineSeparator());
                    testeGerado.append("        assertNotNull(response.get" + cap + "());").append(System.lineSeparator());
                }
                else if (field.getType() == Boolean.class) {
                    testeGerado.append("        response.set" + cap + "(Boolean.TRUE);").append(System.lineSeparator());
                    testeGerado.append("        assertTrue(response.get" + cap + "());").append(System.lineSeparator());
                } else if (field.getType().isEnum()) {
                    String enumName = field.getType().getSimpleName();
                    testeGerado.append("        response.set" + cap + "(" + enumName + ".values()[0]);").append(System.lineSeparator());
                    testeGerado.append("        assertEquals(" + enumName + ".values()[0], response.get" + cap + "());").append(System.lineSeparator());
                } else if (field.getType().isAssignableFrom(field.getType())) {
                    String className = field.getType().getSimpleName();
                    testeGerado.append("        response.set" + cap + "(new " + className + "());").append(System.lineSeparator());
                    testeGerado.append("        assertNotNull(response.get" + cap + "());").append(System.lineSeparator());
                }
            }
            testeGerado.append("   }\n" +
                    "}\n");
            final String fileName = "C:\\Users\\Desktop\\arq Test\\classes\\" + simpleName + "Test.java";
            File file = new File(fileName);
            if (!file.exists()) {
                file.createNewFile();
            }
            FileWriter fileWriter = new FileWriter(file);
            PrintWriter printWriter = new PrintWriter(fileWriter);
            printWriter.print(testeGerado);
            printWriter.close();
        }
    }

    /**
     * Scans all classes accessible from the context class loader which belong to the given package and subpackages.
     *
     * @param packageName The base package
     * @return The classes
     * @throws ClassNotFoundException
     * @throws IOException
     */
    private static Class[] getClasses(String packageName)
            throws ClassNotFoundException, IOException {
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        assert classLoader != null;
        String path = packageName.replace('.', '/');
        Enumeration<URL> resources = classLoader.getResources(path);
        List<File> dirs = new ArrayList<>();
        while (resources.hasMoreElements()) {
            URL resource = resources.nextElement();
            dirs.add(new File(resource.getFile()));
        }
        ArrayList<Class> classes = new ArrayList<Class>();
        for (File directory : dirs) {
            classes.addAll(findClasses(directory, packageName));
        }
        return classes.toArray(new Class[classes.size()]);
    }

    /**
     * Recursive method used to find all classes in a given directory and subdirs.
     *
     * @param directory   The base directory
     * @param packageName The package name for classes found inside the base directory
     * @return The classes
     * @throws ClassNotFoundException
     */
    private static List<Class> findClasses(File directory, String packageName) throws ClassNotFoundException {
        List<Class> classes = new ArrayList<Class>();
        if (!directory.exists()) {
            return classes;
        }
        File[] files = directory.listFiles();
        for (File file : files) {
            if (file.isDirectory()) {
                assert !file.getName().contains(".");
                classes.addAll(findClasses(file, packageName + "." + file.getName()));
            } else if (file.getName().endsWith(".class")) {
                classes.add(Class.forName(packageName + '.' + file.getName().substring(0, file.getName().length() - 6)));
            }
        }
        return classes;
    }
}
