package com.cqebd.student.vo.entity;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * 描述
 * Created by gorden on 2017/12/4.
 */

public class Attachment implements Parcelable{
    private int Id;
    private int ExaminationPapersId;
    private int Status;
    private int Type;
    private int QuestionId;
    private String Name;
    private String MediaTypeName;
    private String CreateDateTime;
    private String Remarks;
    private int AnswerType;//1播放中答、2是必须播放完后可答
    private String Url;
    private String VideoId;
    private int CanWatchTimes;//最多观看次数，0无限制

    public Attachment() {
    }

    public int getId() {
        return Id;
    }

    public void setId(int id) {
        Id = id;
    }

    public int getStatus() {
        return Status;
    }

    public void setStatus(int status) {
        Status = status;
    }

    public int getType() {
        return Type;
    }

    public void setType(int type) {
        Type = type;
    }

    public int getQuestionId() {
        return QuestionId;
    }

    public void setQuestionId(int questionId) {
        QuestionId = questionId;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public String getMediaTypeName() {
        return MediaTypeName;
    }

    public void setMediaTypeName(String mediaTypeName) {
        MediaTypeName = mediaTypeName;
    }

    public String getCreateDateTime() {
        return CreateDateTime;
    }

    public void setCreateDateTime(String createDateTime) {
        CreateDateTime = createDateTime;
    }

    public String getUrl() {
        return Url;
    }

    public void setUrl(String url) {
        Url = url;
    }

    public String getVideoId() {
        return VideoId;
    }

    public void setVideoId(String videoId) {
        VideoId = videoId;
    }

    public int getCanWatchTimes() {
        return CanWatchTimes;
    }

    public void setCanWatchTimes(int canWatchTimes) {
        CanWatchTimes = canWatchTimes;
    }

    public int getAnswerType() {
        return AnswerType;
    }

    public void setAnswerType(int answerType) {
        AnswerType = answerType;
    }

    public int getExaminationPapersId() {
        return ExaminationPapersId;
    }

    public void setExaminationPapersId(int examinationPapersId) {
        ExaminationPapersId = examinationPapersId;
    }

    public String getRemarks() {
        return Remarks;
    }

    public void setRemarks(String remarks) {
        Remarks = remarks;
    }

    protected Attachment(Parcel in) {
        Id = in.readInt();
        ExaminationPapersId = in.readInt();
        Status = in.readInt();
        Type = in.readInt();
        QuestionId = in.readInt();
        Name = in.readString();
        MediaTypeName = in.readString();
        CreateDateTime = in.readString();
        Remarks = in.readString();
        AnswerType = in.readInt();
        Url = in.readString();
        VideoId = in.readString();
        CanWatchTimes = in.readInt();
    }

    public static final Creator<Attachment> CREATOR = new Creator<Attachment>() {
        @Override
        public Attachment createFromParcel(Parcel in) {
            return new Attachment(in);
        }

        @Override
        public Attachment[] newArray(int size) {
            return new Attachment[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(Id);
        dest.writeInt(ExaminationPapersId);
        dest.writeInt(Status);
        dest.writeInt(Type);
        dest.writeInt(QuestionId);
        dest.writeString(Name);
        dest.writeString(MediaTypeName);
        dest.writeString(CreateDateTime);
        dest.writeString(Remarks);
        dest.writeInt(AnswerType);
        dest.writeString(Url);
        dest.writeString(VideoId);
        dest.writeInt(CanWatchTimes);
    }

    @Override
    public String toString() {
        return "Attachment{" +
                "Id=" + Id +
                ", ExaminationPapersId=" + ExaminationPapersId +
                ", Status=" + Status +
                ", Type=" + Type +
                ", QuestionId=" + QuestionId +
                ", Name='" + Name + '\'' +
                ", MediaTypeName='" + MediaTypeName + '\'' +
                ", CreateDateTime='" + CreateDateTime + '\'' +
                ", Remarks='" + Remarks + '\'' +
                ", AnswerType=" + AnswerType +
                ", Url='" + Url + '\'' +
                ", VideoId='" + VideoId + '\'' +
                ", CanWatchTimes=" + CanWatchTimes +
                '}';
    }
}
