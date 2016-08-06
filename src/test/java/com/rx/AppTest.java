package com.rx;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import org.apache.maven.model.Model;
import org.apache.maven.model.io.xpp3.MavenXpp3Reader;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.project.MavenProject;
import org.codehaus.plexus.util.xml.pull.XmlPullParserException;
import org.jvnet.mjiip.v_2_2.XJC22Mojo;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Unit test for simple App.
 */
public class AppTest
        extends TestCase {
    /**
     * Create the test case
     *
     * @param testName name of the test case
     */
    public AppTest(String testName) {
        super(testName);
    }

    /**
     * @return the suite of tests being tested
     */
    public static Test suite() {
        return new TestSuite(AppTest.class);
    }

    /**
     * Rigourous Test :-)
     */
    public void testApp() throws IOException, XmlPullParserException, MojoExecutionException {
        // This is the actual test code.
        MavenXpp3Reader r = new MavenXpp3Reader();
        Model model = r.read(new FileReader(new File("pom.xml")));

        XJC22Mojo xjcMojo = new XJC22Mojo();
        xjcMojo.setAddTestCompileSourceRoot(true); // this does nothing because we aren't referencing the main project
        xjcMojo.setSchemaDirectory(new File("D:/xsd2"));
        xjcMojo.setSchemaIncludes(new String[]{
                "_00_结构_复用.xsd",
                "_01_结构_刑事.xsd",
                "_02_结构_民事.xsd",
                "_03_结构_行政.xsd",
                "_04_结构_执行.xsd",
                "_05_结构_国家赔偿与司法救助.xsd",
                "_06_结构_非诉保全.xsd",
                "_07_结构_国际司法协助.xsd",
                "_08_结构_区际司法协助.xsd",
                "_09_结构_司法制裁.xsd",
                "_0A_类型_基础.xsd",
                "_0B_组织机构.xsd",
                "_10_结构_信访.xsd"
        });
        xjcMojo.setGenerateDirectory(new File("D:/generated-sources/xjc"));
        xjcMojo.setVerbose(false);
//		xjcMojo.setCleanPackageDirectories(false);
        xjcMojo.setForceRegenerate(true);
        xjcMojo.setRemoveOldOutput(false);
        List<String> args = new ArrayList<String>();
        //args.add("-extension");
        //args.add("-Xequals");
        //args.add("-XtoString");
        //args.add("-Xannotate");
        //args.add("-XhashCode");
        //args.add("-Xhyperjaxb3-ejb");
        args.add("-nv");
        args.add("-Xpinyin");
        //args.add("-Xmadura-objects");
        //args.add("-Xvalidator");
        xjcMojo.setArgs(args);
        xjcMojo.setProject(new MavenProject(model));
//		xjcMojo.setBuildContext(new DefaultBuildContext());
        xjcMojo.execute();
    }
}
