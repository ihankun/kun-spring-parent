package com.ihankun.core.base.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 文件后缀
 * @author hankun
 */

@Getter
@AllArgsConstructor
public enum FileType {

    TXT("txt"),
    DOC("doc"),
    DOCX("docx"),
    XLS("xls"),
    XLSX("xlsx"),
    PPTX("pptx"),
    ACCDB("accdb"),
    PDF("pdf"),
    HTM("htm"),
    HTML("html"),
    JPG("jpg"),
    PNG("png"),
    GIF("gif"),
    PSD("psd"),
    WAV("wav"),
    MP3("mp3"),
    WMA("wma"),
    MPEG("mpeg"),
    AVI("avi"),
    MP4("mp4"),
    ZIP("zip"),
    RAR("rar"),
    CAB("cab"),
    EXE("exe"),
    COM("com"),
    REG("reg"),
    BAT("bat"),
    DAT("dat"),
    INI("ini");

    private String type;
}
