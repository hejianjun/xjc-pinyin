package com.rx.pinyin;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.sun.codemodel.JCodeModel;
import com.sun.codemodel.JDefinedClass;
import com.sun.tools.xjc.BadCommandLineException;
import com.sun.tools.xjc.Options;
import com.sun.tools.xjc.Plugin;
import com.sun.tools.xjc.outline.Outline;
import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXException;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


/**
 * Created by hejianjun on 2016/7/28.
 */
public class PinyinPlugin extends Plugin {
    PinyinConverter pc;

    @Override
    public String getOptionName() {
        return "Xpinyin";
    }

    @Override
    public String getUsage() {
        return "转化类名为拼音";
    }

    @Override
    public void onActivated(Options opts) throws BadCommandLineException {
        System.out.println("开始转换");
        pc = new PinyinConverter();
        try {
            pc.load();
        } catch (IOException e) {
            e.printStackTrace();
           throw new BadCommandLineException(e.getMessage());
        }
        opts.setNameConverter(pc, this);
    }

    @Override
    public boolean run(Outline outline, Options opt, ErrorHandler errorHandler) throws SAXException {
        try {
            pc.save();
        } catch (IOException e) {
            e.printStackTrace();
        }
        /*
        Map<String, List<JDefinedClass>> classByName = outline.getClasses().stream().map(co -> co.implClass).collect(Collectors.groupingBy(cl -> {
            String name = cl.name();
            //String[] split = name.split("(?<!^)(?=[A-Z])");
            String[] split = name.split("_");
            return split[split.length - 1];
        }));
        classByName.forEach((k, v) -> {
            System.out.print(k + ":");
            System.out.println(v.size());
        });
        System.out.println("classByName:"+classByName.size());
        //throw new SAXException();
        */
        return true;
    }


}
