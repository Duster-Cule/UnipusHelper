package org.unipus.unipus;

public enum QuestionType {
    READING,            //仅读文章，无题目
    WATCHING,           //仅看视频，无题目
    LISTENING,          //听音频回答问题
    FILLINGBLANKS,      //填空
    COMMENT,            //评论（默认不做）
    VOCABULARY,         //生词
    CHOOSING,           //选择
    SHORTANSWER,        //简答题
    BLANKEDCLOZE,       //选词填空
    MATCH,              //匹配
    REVIEWANDCHECK,     //review & check
    UNITPROJECT,        //unit project(要上传照片)
    UNKNOWN             //未知类型
}
