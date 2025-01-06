package com.zhy.enums;

import java.util.ArrayList;
import java.util.List;

public enum MajorEnum {
    SXZZJY("SXZZJY", "思想政治教育", "MKSZY"),
    CXGH("CXGH", "城乡规划", "CSJS"),
    TMGC("TMGC", "土木工程", "CSJS"),
    JZX("JZX", "建筑学", "CSJS"),
    KJX("KJX", "会计学", "CSJJ"),
    CWGL("CWGL", "财务管理", "CSJJ"),
    JJX("JJX", "经济学", "CSJJ"),
    SJX("SJX", "审计学", "CSJJ"),
    JDGL("JDGL", "酒店管理", "CSGL"),
    CSG("CSG", "城市管理", "CSGL"),
    SHGZ("SHGZ", "社会工作", "CSGL"),
    DLKX("DLKX", "地理科学", "CSHJ"),
    DLXXKX("DLXXKX", "地理信息科学", "CSHJ"),
    HJGC("HJGC", "环境工程", "CSHJ"),
    JQRGC("JQRGC", "机器人工程", "PLJXGC"),
    JXSJZZJQZDH("JXSJZZJQZDH", "机械设计制造及其自动化", "PLJXGC"),
    HJJSYGC("HJJSYGC", "焊接技术与工程", "PLJXGC"),
    QCFWGC("QCFWGC", "汽车服务工程", "PLJXGC"),
    ZNZZGC("ZNZZGC", "智能制造工程", "PLJXGC"),
    ZYKCGC("ZYKCGC", "资源勘查工程", "PLSYGC"),
    YQCYGC("YQCYGC", "油气储运工程", "PLSYGC"),
    XNYKXYGC("XNYKXYGC", "新能源科学与工程", "PLSYGC"),
    SYGC("SYGC", "石油工程", "PLSYGC"),
    SJKXYDSJJS("SJKXYDSJJS", "数据科学与大数据技术", "XXGC"),
    SXYYYSX("SXYYYSX", "数学与应用数学", "XXGC"),
    RJGC("RJGC", "软件工程", "XXGC"),
    ZNKXYJS("ZNKXYJS", "智能科学与技术", "XXGC"),
    TXGC("TXGC", "通信工程", "DZGC"),
    WLX("WLX", "物理学", "DZGC"),
    WDZKXYGC("WDZKXYGC", "微电子科学与工程", "DZGC"),
    HX("HX", "化学", "HXGC"),
    HXGCYY("HXGCYY", "化学工程与工艺", "HXGC"),
    HYYGJJJ("HYGJJY", "汉语言国际教育", "WS"),
    HYYWX("HYYWX", "汉语言文学", "WS"),
    LSX("LSX", "历史学", "WS"),
    FY("FY", "法语", "WGY"),
    TS("TS", "翻译","WGY"),
    ENG("ENG", "英语", "WGY"),
    XXJY("XXJY", "小学教育", "JY"),
    JYJSX("JYJSX", "教育技术学", "JY"),
    XQJY("XQJY", "学前教育", "YESF"),
    GBDSBD("GBDSBD", "广播电视编导", "CM"),
    BYYZCYS("BYYZCYS", "播音与主持艺术", "CM"),
    WLYXMT("WLYXMT", "网络与新媒体", "CM"),
    XX("XX", "新闻学", "CM"),
    WDBY("WDBY", "舞蹈表演", "YY"),
    HKFWYSYGL("HKFWYSYGL", "航空服务艺术与管理", "YY"),
    YYBY("YYBY", "音乐表演", "YY"),
    YYX("YYX", "音乐学", "YY"),
    HJSJ("HJSJ", "环境设计", "YSSJ"),
    CPSJ("CPSJ", "产品设计", "YSSJ"),
    MSX("MSX", "美术学", "YSSJ"),
    SJCDSJ("SJCD", "视觉传达设计", "YSSJ"),
    GYMS("GYMS", "工艺美术", "YSSJ"),
    TYJY("TYJY", "体育教育", "TY");



    private final String code;
    private final String name;
    private final String collegeCode;

    MajorEnum(String code, String name, String collegeCode) {
        this.code = code;
        this.name = name;
        this.collegeCode = collegeCode;
    }

    public static String getName(String code) {
        for (MajorEnum majorEnum : MajorEnum.values()) {
            if (majorEnum.getCode().equals(code)) {
                return majorEnum.getName();
            }
        }
        return null;
    }

    public static String getCollegeCode(String code) {
        for (MajorEnum majorEnum : MajorEnum.values()) {
            if (majorEnum.getCode().equals(code)) {
                return majorEnum.getCollegeCode();
            }
        }
        return null;
    }

    public static String getNameByCollegeCode(String collegeCode) {
        for (MajorEnum majorEnum : MajorEnum.values()) {
            if (majorEnum.getCollegeCode().equals(collegeCode)) {
                return majorEnum.getName();
            }
        }
        return null;
    }

    public static String getCodeByCollegeCode(String collegeCode) {
        for (MajorEnum majorEnum : MajorEnum.values()) {
            if (majorEnum.getCollegeCode().equals(collegeCode)) {
                return majorEnum.getCode();
            }
        }
        return null;
    }

    public static String getCollegeName(String code) {
        for (MajorEnum majorEnum : MajorEnum.values()) {
            if (majorEnum.getCode().equals(code)) {
                return CollegeEnum.getCollegeName(majorEnum.getCollegeCode());
            }
        }
        return null;
    }

    public static String getCollegeNameByCollegeCode(String collegeCode) {
        return CollegeEnum.getCollegeName(collegeCode);
    }

    public static List<String> getMajorNameList() {
        List<String> majorNameList = new ArrayList<>();
        for (MajorEnum majorEnum : MajorEnum.values()) {
            majorNameList.add(majorEnum.getName());
        }
        return majorNameList;
    }

    public static List<String> getMajorCodeList() {
        List<String> majorCodeList = new ArrayList<>();
        for (MajorEnum majorEnum : MajorEnum.values()) {
            majorCodeList.add(majorEnum.getCode());
        }
        return majorCodeList;
    }

    public static String getCode(String major) {
        for (MajorEnum majorEnum : MajorEnum.values()) {
            if (majorEnum.getName().equals(major)) {
                return majorEnum.getCode();
            }
        }
        return null;
    }


    public String getCode() {
        return code;
    }

    public String getName() {
        return name;
    }

    public String getCollegeCode() {
        return collegeCode;
    }


}
