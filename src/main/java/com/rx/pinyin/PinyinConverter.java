package com.rx.pinyin;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sun.xml.bind.api.impl.NameConverter;
import net.sourceforge.pinyin4j.PinyinHelper;
import net.sourceforge.pinyin4j.format.HanyuPinyinCaseType;
import net.sourceforge.pinyin4j.format.HanyuPinyinOutputFormat;
import net.sourceforge.pinyin4j.format.HanyuPinyinToneType;
import net.sourceforge.pinyin4j.format.HanyuPinyinVCharType;
import net.sourceforge.pinyin4j.format.exception.BadHanyuPinyinOutputFormatCombination;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.*;

/**
 * Created by hejianjun on 2016/8/4.
 */

public class PinyinConverter implements NameConverter {

    private final Map<String, List<String>> hanyu = new HashMap<>();

    private Map<Character, Map<String, List<String>>> tong = new TreeMap<>();
    private final HanyuPinyinOutputFormat format = new HanyuPinyinOutputFormat();
    private final File file = new File("tong.json");

    public PinyinConverter() {
        format.setCaseType(HanyuPinyinCaseType.LOWERCASE);
        format.setToneType(HanyuPinyinToneType.WITHOUT_TONE);
        format.setVCharType(HanyuPinyinVCharType.WITH_V);
    }


    public String toClassName(String token) {
        //return toMixedCaseName(token, 0);
        return standard.toClassName(toMixedCaseName(token, 2));
    }

    public String toInterfaceName(String token) {
        return standard.toInterfaceName(toMixedCaseName(token, 0));
    }

    public String toPropertyName(String token) {
        //System.out.println(token);
        //System.out.println(standard.toClassName(token));
        //System.out.println(toMixedCaseName(token, 0));
        //System.out.println(toMixedCaseName(token, 1));
        return standard.toPropertyName(toMixedCaseName(token, 1));
    }

    public String toConstantName(String token) {
        return standard.toConstantName(toMixedCaseName(token, 0));
    }

    public String toVariableName(String token) {
        return standard.toVariableName(toMixedCaseName(token, 0));
    }

    public String toPackageName(String namespaceUri) {
        return standard.toPackageName(namespaceUri);
    }

    private String toMixedCaseName(String token, int pingyin) {
        if (pingyin == 1) {
            return toHanyuPinyin(token, false);
        } else if (pingyin == 2) {
            return toHanyuPinyin(token, true);
        }
        return token;
    }

    public String toHanyuPinyin(String token, boolean initial) {
        String[] ss = token.split("\\.");
        StringBuilder sb = new StringBuilder();
        for (String s : ss) {
            List<String> list = hanyu.get(s);
            if (list == null) {
                list = toHanyuPinyin(s);
                hanyu.put(s, list);
            }
            for (String s1 : list) {
                if (initial) {
                    sb.append(s1.charAt(0));

                } else {
                    char[] cs = s1.toCharArray();
                    if (cs[0] > 'a' && cs[0] < 'z')
                        cs[0] -= 32;
                    sb.append(String.valueOf(cs));
                }
            }
            sb.append(".");
        }
        return sb.toString();
    }

    private List<String> toHanyuPinyin(String token) {
        List<String> list = new ArrayList<>();
        for (char c : token.toCharArray()) {
            if (128 < c) {
                try {
                    String string = toHanyuPinyin(token, c);
                    list.add(string);
                } catch (BadHanyuPinyinOutputFormatCombination e) {
                    e.printStackTrace();
                }
            } else {
                list.add(Character.toString(c));
            }

        }
        return list;
    }

    private String toHanyuPinyin(String token, char c) throws BadHanyuPinyinOutputFormatCombination {
        String[] strings = PinyinHelper.toHanyuPinyinStringArray(c, format);
        if (strings.length > 1) {
            String pinyin = getTong(c, token);
            if (pinyin == null) {
                pinyin = getListTong(c, token, strings);
                putTong(c, token, pinyin);
            }
            return pinyin;
        } else {
            return strings[0];
        }
    }

    private String getListTong(char c, String token, String[] strings) {
        switch (c) {
            case '调':
                if (token.contains("调解") || token.contains("协调"))
                    return strings[1];
            case '行':
                if (token.contains("银行"))
                    return strings[1];
            case '长':
                if (token.contains("延长"))
                    return strings[1];
            case '的':
                if (token.contains("目的") || token.contains("标的"))
                    return strings[1];
            case '没':
                if (token.contains("没收"))
                    return strings[1];
            case '重':
                if (token.contains("重审") || token.contains("重新") || token.contains("重整"))
                    return strings[1];
            default:
                return strings[0];
        }
    }

    private String getUserTong(char c, String token, String[] strings) {
        Scanner sc = new Scanner(System.in);
        StringBuilder print = new StringBuilder(token)
                .append("\n")
                .append(c).append(" [");
        boolean allSame = true;
        String string = null;
        for (int i = 0; i < strings.length; i++) {
            if (!(string == null || strings[i].equals(string))) {
                allSame = false;
            }
            string = strings[i];
            if (i == strings.length - 1) {
                print.append(i).append(":").append(string);
            } else {
                print.append(i).append(":").append(string).append(",");
            }
        }
        print.append("]");
        if (allSame) {
            return strings[0];
        } else {
            System.out.println(print);
            String is = sc.nextLine();
            try {
                int i = Integer.parseInt(is);
                return strings[i];
            } catch (Exception e) {
                return strings[0];
            }
        }
    }


    private void putTong(char c, String token, String pinyin) {
        Map<String, List<String>> hanyuMap = tong.get(c);
        if (hanyuMap == null) {
            hanyuMap = new TreeMap<>();
        }
        List<String> hanyuList = hanyuMap.get(pinyin);
        if (hanyuList == null) {
            hanyuList = new ArrayList<>();
        }
        hanyuList.add(token);
        hanyuList.sort(String::compareTo);
        hanyuMap.put(pinyin, hanyuList);
        tong.put(c, hanyuMap);
    }

    private String getTong(char c, String token) {
        Map<String, List<String>> hanyuMap = tong.get(c);
        if (hanyuMap == null) {
            return null;
        }
        for (String pinyin : hanyuMap.keySet()) {
            List<String> hanyuList = hanyuMap.get(pinyin);
            if (hanyuList.indexOf(token) != -1) {
                return pinyin;
            }
        }
        return null;
    }

    public Map load() throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        try {
            tong = mapper.readValue(file, new TypeReference<Map<Character, Map<String, List<String>>>>() {
            });
        } catch (FileNotFoundException e) {
            System.out.println("找不到同音字配置文件，可能需要手动输入");
        }
        return tong;
    }

    public void save() throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        mapper.writeValue(file, tong);
    }
}
