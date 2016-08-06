package com.rx.pinyin;

import junit.framework.TestCase;
import net.sourceforge.pinyin4j.format.exception.BadHanyuPinyinOutputFormatCombination;

import java.lang.reflect.Array;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Created by hejianjun on 2016/8/4.
 */
public class PinyinConverterTest extends TestCase {
    private final PinyinConverter pinyinConverter = new PinyinConverter();

    public void testToHanyuPinyin() throws Exception {
        String[] arr = new String[]{"管辖处理记录1",
                "扣划"
        };
        List<String> strings = Arrays.asList(arr);
        Map<String, List<String[]>> collect = strings.stream().map(s -> {
            String[] ss = new String[2];
            ss[0] = pinyinConverter.toHanyuPinyin(s, true);
            ss[1] = s;
            return ss;
        }).collect(Collectors.groupingBy(s -> s[0]));
        collect.forEach((k, v) -> {
            if (v.size() > 1) {
                System.out.println(k);
                for (String[] strings1 : v) {
                    System.out.println(strings1[1] + ":" + strings1[0]);
                }
            }
        });
    }

    public void testLoad() throws Exception {
        Map load = pinyinConverter.load();
        load.forEach((k, v) -> {
            System.out.println(k.getClass());
            ((Map) v).forEach((vk, vv) -> {
                System.out.println("    "+vk.getClass());
                ((List)vv).forEach(vvv->{
                    System.out.println("        "+vvv.getClass());
                });
            });

        });
    }

    public void testSave() throws Exception {

    }
}