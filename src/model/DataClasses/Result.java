package model.DataClasses;

import java.util.ArrayList;

public class Result<TypeContent> {
    public ResultStatus Status;
    public TypeContent Result;
    public ArrayList<String> Comment;
    public Result(ResultStatus status, TypeContent result){
        this.Status = status;
        this.Result = result;
        this.Comment = new ArrayList<String>();
    }
    public Result(ResultStatus status, TypeContent result, ArrayList<String> comment){
        this.Status = status;
        this.Result = result;
        this.Comment = comment;
    }
}
