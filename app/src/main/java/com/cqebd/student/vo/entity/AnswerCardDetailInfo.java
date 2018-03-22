package com.cqebd.student.vo.entity;

import android.text.TextUtils;


import com.cqebd.student.tools.UtilGson;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by jianghongming on 16/8/26.
 */
public class AnswerCardDetailInfo extends BaseBean {

    private AnswerCardDetailInfo.DataBean    data;
    private List<DataBean.QuestionGroup.Question>   QuestionList;

    public AnswerCardDetailInfo.DataBean getData() {
        return data;
    }

    public void setData(AnswerCardDetailInfo.DataBean data) {
        this.data = data;
    }


    public List<DataBean.QuestionGroup.Question> getQuestionList() {
        if (QuestionList == null)
        {
            QuestionList = new ArrayList<>();
            for (int i = 0; i < data.getQuestionGruop().size();i++)
            {
                DataBean.QuestionGroup group = data.getQuestionGruop().get(i);
                for (int j = 0;j < group.getQuestion().size();j ++)
                {
                    QuestionList.add(group.getQuestion().get(j));
                }

            }


        }
        return QuestionList;
    }

    public class DataBean
    {
        private int     Id;
        private String  Name;
        private int     Count;
        private double  Fraction;

        private List<QuestionGroup> QuestionGruop = new ArrayList<>();

        public List<QuestionGroup> getQuestionGruop() {
            return QuestionGruop;
        }

        public void setQuestionGruop(List<QuestionGroup> questionGruop) {
            QuestionGruop = questionGruop;
        }

        public String getName() {
            return Name;
        }

        public void setName(String name) {
            Name = name;
        }

        public int getCount() {
            return Count;
        }

        public void setCount(int count) {
            Count = count;
        }

        public double getFraction() {
            return Fraction;
        }

        public void setFraction(double fraction) {
            Fraction = fraction;
        }


        public int getId() {
            return Id;
        }

        public void setId(int id) {
            Id = id;
        }

        public class QuestionGroup
        {
            private int     Id;
            private String  Name;
            private List<Question> Question = new ArrayList<>();

            public int getId() {
                return Id;
            }

            public void setId(int id) {
                Id = id;
            }

            public String getName() {
                return Name;
            }

            public void setName(String name) {
                Name = name;
            }

            public List<QuestionGroup.Question> getQuestion() {
                return Question;
            }

            public void setQuestion(List<QuestionGroup.Question> question) {
                Question = question;
            }


            public class Answer{
                private     int     Id;
                private     int     QuestionTypeId;
                private     int     Option;
                private     String  Answer;
                private     String  UserAnswer = "" ;

                public int getId() {
                    return Id;
                }

                public void setId(int id) {
                    Id = id;
                }

                public int getQuestionTypeId() {
                    return QuestionTypeId;
                }


                public JSONObject getUserAnswerJSON(){
                    JSONObject ret = new JSONObject();
                    try {
                        ret.put("TypeId",QuestionTypeId);
                        ret.put("Id",getId());
                        ret.put("Answer",UserAnswer == null ? "":UserAnswer);

                    }catch (Exception e)
                    {

                    }
                    return ret;
                }
                public void setQuestionTypeId(int questionTypeId) {
                    QuestionTypeId = questionTypeId;
                }
                /*
                    判断该答案是否是逻辑题答案
                 */
                public boolean IsLogicAnswer()
                {
                    boolean bRet = false;
                    switch (this.getQuestionTypeId()) {

                        case 1:
                        case 8:
                        case 10:
                        case 12:
                        case 14:          //单选
                        case 4:                                          //判断
                        case 5:                                         //多选

                        case 2:
                        case 9:
                        case 11:
                        case 13:
                        case 15:          //逻辑填空
                            bRet =  true;    break;
                        default:
                            bRet = false;   break;
                    }
                    return bRet;


                }
                /*

                 */
                public int getOption() {
                    return Option;
                }

                public void setOption(int option) {
                    Option = option;
                }

                public String getAnswer() {
                    return Answer;
                }

                public void setAnswer(String answer) {
                    Answer = answer;
                }

                public String getUserAnswer() {
                    return UserAnswer;
                }

                public void setUserAnswer(String userAnswer) {
                    UserAnswer = userAnswer;
                }

                public void initWithJSON(JSONObject obj)
                {
                    try {
                        this.Id = obj.getInt("Id");
                        this.QuestionTypeId = obj.getInt("QuestionTypeId");
                        this.Answer = obj.getString("Answer");
                        if (obj.has("Option"))
                            this.Option = obj.getInt("Option");

                    }catch (Exception e)
                    {



                    }
                }



            }
            /**
             *
             */

            public class Question{
                private int     ExaminationPapersQuestionGruopId;
                private double  Fraction;
                private int     Id;
                private int     Sort;
                private String  Snapshoot;
                private String  StudentsAnswer;
                private List<Answer>    Answers;
                private List<StudentAnswer>    studentAnswers;
                private boolean IsSubmit;                //是否提交
                private boolean IsCurrentFocusQuestion;  //


                public boolean isSubmit() {
                    return IsSubmit;
                }

                public void setIsSubmit(boolean isSubmit) {
                    IsSubmit = isSubmit;
                }

                public boolean isCurrentFocusQuestion() {
                    return IsCurrentFocusQuestion;
                }

                public void setIsCurrentFocusQuestion(boolean isCurrentFocusQuestion) {
                    IsCurrentFocusQuestion = isCurrentFocusQuestion;
                }

                public JSONObject getSubmitParams(){
                    JSONObject result = new JSONObject();
                    try
                    {
                        result.put("Stu_Id",1);

                    }catch(Exception e)
                    {

                    }

                    return result;
                }

                public List<Answer> getAnswers() {
                   if (Answers == null)
                   {
                       Answers = new ArrayList<>();

                       if (!TextUtils.isEmpty(Snapshoot))
                       {
                           try {
                               JSONArray arrays = new JSONArray(Snapshoot);
                               for (int i = 0 ;i < arrays.length() ; i ++)
                               {

                                   Answer ans = new Answer();
                                   ans.initWithJSON(arrays.getJSONObject(i));
                                   Answers.add(ans);

                               }

                           }catch (Exception e)
                           {

                           }
                       }

                   }

                    return Answers;
                }

                public List<StudentAnswer> getStudentAnswers() {
                    if (studentAnswers == null)
                    {
                        studentAnswers = new ArrayList<>();

                        if (!TextUtils.isEmpty(StudentsAnswer))
                        {
                            try {
                                JSONArray arrays = new JSONArray(StudentsAnswer);
                                for (int i = 0 ;i < arrays.length() ; i ++)
                                {

                                    StudentAnswer ans = new StudentAnswer();
                                    ans = UtilGson.getInstance().convertJsonStringToObject(arrays.getJSONObject(i).toString(),StudentAnswer.class);
                                    studentAnswers.add(ans);

                                }

                            }catch (Exception e)
                            {

                            }
                        }

                    }

                    return studentAnswers;
                }

                public void setAnswers(List<Answer> answers) {
                    Answers = answers;
                }

                public int getExaminationPapersQuestionGruopId() {
                    return ExaminationPapersQuestionGruopId;
                }

                public void setExaminationPapersQuestionGruopId(int examinationPapersQuestionGruopId) {
                    ExaminationPapersQuestionGruopId = examinationPapersQuestionGruopId;
                }

                public double getFraction() {
                    return Fraction;
                }

                public void setFraction(double fraction) {
                    Fraction = fraction;
                }

                public int getId() {
                    return Id;
                }

                public void setId(int id) {
                    Id = id;
                }

                public int getSort() {
                    return Sort;
                }

                public void setSort(int sort) {
                    Sort = sort;
                }

                public String getSnapshoot() {
                    return Snapshoot;
                }

                public void setSnapshoot(String snapshoot) {
                    Snapshoot = snapshoot;
                }

                public String getStudentsAnswer() {
                    return StudentsAnswer;
                }

                public void setStudentsAnswer(String studentsAnswer) {
                    StudentsAnswer = studentsAnswer;
                }
            }

        }
    }


}
