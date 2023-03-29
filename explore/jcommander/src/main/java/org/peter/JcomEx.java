package org.peter;

import com.beust.jcommander.FuzzyMap;
import com.beust.jcommander.JCommander;
import com.beust.jcommander.Parameter;
import com.beust.jcommander.ParameterDescription;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;

public class JcomEx {
    @Parameter
    private List<String> parameters = new ArrayList<String>();

    @Parameter(names = {"-log1", "-verbose"}, description = "Level of verbosity" ,required = true)
    private Integer verbose = 1;

    @Parameter(names = "-groups", description = "Comma-separated list of group names to be run")
    private String groups;

    @Parameter(names = "--help", help = true)
    private boolean help;

    public static void main(String[] args) {
        JcomEx obj = new JcomEx();
        String[] argv = {"--helap"};
        var ob = JCommander.newBuilder().addObject(obj).build();
        ob.parse(argv);
//        System.out.println(obj.verbose);
//        System.out.println(obj.groups);
//        System.out.println(obj.help);
        ob.usage();
    }
}
