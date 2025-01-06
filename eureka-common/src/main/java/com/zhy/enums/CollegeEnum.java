package com.zhy.enums;

public enum CollegeEnum {
    MKSZY("MKSZY","马克思主义学院"),
    CSJS("CSJS","城市建设学院"),
    CSJJ("CSJJ","城市经济学院"),
    CSGL("CSGL","城市管理学院"),
    CSHJ("CSHJ","城市环境学院"),
    PLJXGC("PLJXGC","培黎机械工程学院"),
    PLSYGC("PLSYGC","培黎石油工程学院"),
    XXGC("XXGC","信息工程学院"),
    DZGC("DZGC","电子工程学院"),
    HXGC("HXGC","化学工程学院"),
    WS("WS","文史学院"),
    WGY("WGY","外国语学院"),
    JY("JY","教育学院"),
    YESF("YESF","幼儿师范学院"),
    CM("CM","传媒学院"),
    YY("YY","音乐学院"),
    YSSJ("YSSJ","艺术设计学院"),
    TY("TY","体育学院");

    public String collegeCode;
    public String collegeName;

    CollegeEnum(String collegeCode, String collegeName) {
        this.collegeCode = collegeCode;
        this.collegeName = collegeName;
    }

    public  String getCollegeCode() {
        return collegeCode;
    }

    public String getCollegeName() {
        return collegeName;
    }

    public static String getCollegeName(String collegeCode) {
        for (CollegeEnum collegeEnum : CollegeEnum.values()) {
            if (collegeEnum.getCollegeCode().equals(collegeCode)) {
                return collegeEnum.getCollegeName();
            }
        }
        return null;
    }
    public static String getCollegeCode(String collegeName) {
        for (CollegeEnum collegeEnum : CollegeEnum.values()) {
            if (collegeEnum.getCollegeName().equals(collegeName)) {
                return collegeEnum.getCollegeCode();
            }
        }
        return null;
    }
}
